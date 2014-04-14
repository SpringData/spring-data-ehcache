/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springdata.ehcache.convert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdata.ehcache.mapping.EhcachePersistentEntity;
import org.springdata.ehcache.mapping.EhcachePersistentProperty;
import org.springdata.ehcache.mapping.Stringable;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Ehcache converter implementation
 * 
 * @author Alex Shvid
 * 
 */

public class EhcacheMappingConverter extends AbstractEhcacheConverter implements ApplicationContextAware,
		BeanClassLoaderAware {

	protected static final Logger log = LoggerFactory.getLogger(EhcacheMappingConverter.class);

	protected final MappingContext<? extends EhcachePersistentEntity<?>, EhcachePersistentProperty> mappingContext;
	protected ApplicationContext applicationContext;

	private ClassLoader beanClassLoader;

	public EhcacheMappingConverter(
			MappingContext<? extends EhcachePersistentEntity<?>, EhcachePersistentProperty> mappingContext) {
		super(new DefaultConversionService());
		this.mappingContext = mappingContext;
	}

	@Override
	public MappingContext<? extends EhcachePersistentEntity<?>, EhcachePersistentProperty> getMappingContext() {
		return mappingContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public String toKey(Object id) {
		Assert.notNull(id);

		if (id instanceof String) {
			return (String) id;
		}

		if (id instanceof Stringable) {
			Stringable stringable = (Stringable) id;
			return stringable.stringify();
		}

		return conversionService.convert(id, String.class);
	}

	@Override
	public <R> R read(Class<R> clazz, ByteArray source) {

		if (source.getBytes() == null || source.getBytes().length == 0) {
			return null;
		}

		Class<R> beanClassLoaderClass = transformClassToBeanClassLoaderClass(clazz);

		EhcachePersistentEntity<R> entity = (EhcachePersistentEntity<R>) mappingContext
				.getPersistentEntity(beanClassLoaderClass);
		if (entity == null) {
			throw new MappingException("No mapping metadata found for " + source.getClass());
		}

		Deserializer<R> deserializer = entity.getDeserializer();

		try {
			R newInstance = deserializer.deserialize(source.asStream());
			return newInstance;
		} catch (IOException e) {
			throw new MappingException("deserialization fail for " + beanClassLoaderClass, e);
		}
	}

	@Override
	public void write(Object source, ByteArray sink) {

		if (source == null) {
			sink.setBytes(new byte[0]);
			return;
		}

		Class<?> beanClassLoaderClass = transformClassToBeanClassLoaderClass(source.getClass());
		EhcachePersistentEntity<?> entity = mappingContext.getPersistentEntity(beanClassLoaderClass);

		if (entity == null) {
			throw new MappingException("No mapping metadata found for " + source.getClass());
		}

		Serializer<Object> serializer = (Serializer<Object>) entity.getSerializer();

		ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
		try {
			serializer.serialize(source, bytesStream);
			sink.setBytes(bytesStream.toByteArray());
		} catch (IOException e) {
			throw new MappingException("serialization fail for " + beanClassLoaderClass, e);
		}

	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@SuppressWarnings("unchecked")
	private <T> Class<T> transformClassToBeanClassLoaderClass(Class<T> entity) {
		try {
			return (Class<T>) ClassUtils.forName(entity.getName(), beanClassLoader);
		} catch (ClassNotFoundException e) {
			return entity;
		} catch (LinkageError e) {
			return entity;
		}
	}

}
