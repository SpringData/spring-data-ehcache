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

package org.springdata.ehcache.mapping;

import org.springdata.ehcache.serializer.DataDeserializer;
import org.springdata.ehcache.serializer.DataSerializable;
import org.springdata.ehcache.serializer.DataSerializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

/**
 * Basic Ehcache Persistent Entity implementation
 * 
 * @author Alex Shvid
 * 
 * @param <T>
 */

public class BasicEhcachePersistentEntity<T> extends BasicPersistentEntity<T, EhcachePersistentProperty> implements
		EhcachePersistentEntity<T>, ApplicationContextAware {

	private final String cacheName;
	private final Serializer<T> serializer;
	private final Deserializer<T> deserializer;

	private final SpelExpressionParser parser;
	private final StandardEvaluationContext context;

	@SuppressWarnings("unchecked")
	public BasicEhcachePersistentEntity(TypeInformation<T> typeInformation) {

		super(typeInformation);

		this.parser = new SpelExpressionParser();
		this.context = new StandardEvaluationContext();

		Class<T> rawType = typeInformation.getType();

		if (rawType.isAnnotationPresent(Entity.class)) {
			Entity annotation = rawType.getAnnotation(Entity.class);
			this.cacheName = StringUtils.hasText(annotation.cacheName()) ? annotation.cacheName() : null;
		} else {
			this.cacheName = null;
		}

		if (this.cacheName == null) {
			throw new BeanInitializationException("entity class " + rawType
					+ " does not specify cache name by Cache annotation");
		}

		if (DataSerializable.class.isAssignableFrom(rawType)) {
			serializer = new DataSerializer<T>();
			deserializer = new DataDeserializer<T>(rawType);
		} else {
			serializer = (Serializer<T>) new DefaultSerializer();
			deserializer = (Deserializer<T>) new DefaultDeserializer();
		}

	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		context.addPropertyAccessor(new BeanFactoryAccessor());
		context.setBeanResolver(new BeanFactoryResolver(applicationContext));
		context.setRootObject(applicationContext);
	}

	public String getCacheName() {
		Expression expression = parser.parseExpression(cacheName, ParserContext.TEMPLATE_EXPRESSION);
		return expression.getValue(context, String.class);
	}

	public Serializer<T> getSerializer() {
		return serializer;
	}

	public Deserializer<T> getDeserializer() {
		return deserializer;
	}

}
