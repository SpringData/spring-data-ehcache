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

package org.springdata.ehcache.core;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.springdata.ehcache.convert.ByteArray;
import org.springdata.ehcache.convert.EhcacheConverter;
import org.springdata.ehcache.mapping.EhcachePersistentEntity;
import org.springdata.ehcache.mapping.EhcachePersistentProperty;
import org.springdata.ehcache.support.EhacheExceptionTranslator;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

/**
 * Ehcache Template
 * 
 * @author Alex Shvid
 * 
 */

public class EhcacheTemplate implements EhcacheOperations {

	private final CacheManager cacheManager;
	private final EhcacheConverter converter;
	private final MappingContext<? extends EhcachePersistentEntity<?>, EhcachePersistentProperty> mappingContext;

	private EhacheExceptionTranslator exceptionTranslator = new EhacheExceptionTranslator();

	public EhcacheTemplate(CacheManager cacheManager, EhcacheConverter converter) {
		Assert.notNull(cacheManager);
		Assert.notNull(converter);
		this.cacheManager = cacheManager;
		this.converter = converter;
		this.mappingContext = converter.getMappingContext();
	}

	@Override
	public String getCacheName(Class<?> entityClass) {
		Assert.notNull(entityClass);
		return getPersistentEntity(entityClass).getCacheName();
	}

	@Override
	public <T> String getKey(T entity) {
		Assert.notNull(entity);

		EhcachePersistentEntity<T> entityMetadata = getPersistentEntity((Class<T>) entity.getClass());

		EhcachePersistentProperty idProperty = entityMetadata.getIdProperty();

		if (idProperty == null) {
			throw new MappingException("entity does not have @Id annotated field " + entity.getClass());
		}

		try {
			Object id = BeanWrapper.create(entity, null).getProperty(idProperty);
			return converter.toKey(id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T get(Class<T> entityClass, final String key) {
		Assert.notNull(entityClass);
		Assert.notNull(key);

		String cacheName = getPersistentEntity(entityClass).getCacheName();

		ByteArray ba = execute(cacheName, new EhcacheCallback<ByteArray>() {

			@Override
			public ByteArray doWithCache(Ehcache cache) {
				Element element = cache.get(key);
				if (element != null) {
					return new ByteArray((byte[]) element.getValue());
				}
				return null;
			}

		});

		if (ba == null) {
			return null;
		}

		return converter.read(entityClass, ba);

	}

	@Override
	public <T> List<T> getAll(Class<T> entityClass) {
		Assert.notNull(entityClass);

		String cacheName = getPersistentEntity(entityClass).getCacheName();

		List<String> allKeys = execute(cacheName, new EhcacheCallback<List<String>>() {

			@Override
			public List<String> doWithCache(Ehcache cache) {
				return (List<String>) cache.getKeys();
			}

		});

		List<T> allEntities = new ArrayList<T>(allKeys.size());

		for (String key : allKeys) {
			T entity = get(entityClass, key);
			if (entity != null) {
				allEntities.add(entity);
			}
		}

		return allEntities;
	}

	@Override
	public <T> void put(T entity) {
		Assert.notNull(entity);

		final String key = getKey(entity);
		Assert.notNull(key);

		final ByteArray ba = new ByteArray();
		converter.write(entity, ba);

		String cacheName = getPersistentEntity(entity.getClass()).getCacheName();

		execute(cacheName, new EhcacheCallback<Object>() {

			@Override
			public Object doWithCache(Ehcache cache) {
				cache.put(new Element(key, ba.getBytes()));
				return null;
			}

		});

	}

	@Override
	public <T> void remove(Class<T> entityClass, final String key) {
		Assert.notNull(entityClass);
		Assert.notNull(key);

		String cacheName = getPersistentEntity(entityClass).getCacheName();

		execute(cacheName, new EhcacheCallback<Object>() {

			@Override
			public Object doWithCache(Ehcache cache) {
				cache.remove(key);
				return null;
			}

		});

	}

	@Override
	public <T> void remove(T entity) {
		Assert.notNull(entity);

		String key = getKey(entity);
		remove(entity.getClass(), key);
	}

	@Override
	public long count(Class<?> entityClass) {
		Assert.notNull(entityClass);

		String cacheName = getPersistentEntity(entityClass).getCacheName();

		return execute(cacheName, new EhcacheCallback<Long>() {

			@Override
			public Long doWithCache(Ehcache cache) {
				return (long) cache.getSize();
			}

		});

	}

	@Override
	public boolean exists(Class<?> entityClass, final String key) {
		Assert.notNull(entityClass);
		Assert.notNull(key);

		String cacheName = getPersistentEntity(entityClass).getCacheName();

		return execute(cacheName, new EhcacheCallback<Boolean>() {

			@Override
			public Boolean doWithCache(Ehcache cache) {
				return cache.isKeyInCache(key);
			}

		});
	}

	@Override
	public void removeAll(Class<?> entityClass) {
		Assert.notNull(entityClass);

		String cacheName = getPersistentEntity(entityClass).getCacheName();

		execute(cacheName, new EhcacheCallback<Object>() {

			@Override
			public Object doWithCache(Ehcache cache) {
				cache.removeAll();
				return null;
			}

		});

	}

	@Override
	public <T> T execute(String cacheName, EhcacheCallback<T> cb) {

		try {

			Ehcache cache = cacheManager.getCache(cacheName);
			return cb.doWithCache(cache);

		} catch (RuntimeException x) {

			DataAccessException dax = exceptionTranslator.translateExceptionIfPossible(x);
			throw dax != null ? dax : x;

		}

	}

	public EhacheExceptionTranslator getExceptionTranslator() {
		return exceptionTranslator;
	}

	public void setExceptionTranslator(EhacheExceptionTranslator exceptionTranslator) {
		this.exceptionTranslator = exceptionTranslator;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public EhcacheConverter getConverter() {
		return converter;
	}

	protected <T> EhcachePersistentEntity<T> getPersistentEntity(Class<T> entityClass) {

		Assert.notNull(entityClass);

		EhcachePersistentEntity<T> entity = (EhcachePersistentEntity<T>) mappingContext.getPersistentEntity(entityClass);

		if (entity == null) {
			throw new MappingException("persistent entity not found for a given class " + entityClass);
		}

		return entity;
	}

}
