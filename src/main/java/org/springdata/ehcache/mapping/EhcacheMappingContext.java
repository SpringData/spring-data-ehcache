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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

/**
 * Ehcache Mapping Context
 * 
 * @author Alex Shvid
 * 
 */

public class EhcacheMappingContext extends
		AbstractMappingContext<BasicEhcachePersistentEntity<?>, EhcachePersistentProperty> implements
		ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public EhcachePersistentProperty createPersistentProperty(Field field, PropertyDescriptor descriptor,
			BasicEhcachePersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
		return new BasicEhcachePersistentProperty(field, descriptor, owner, simpleTypeHolder);
	}

	@Override
	protected <T> BasicEhcachePersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {

		BasicEhcachePersistentEntity<T> entity = new BasicEhcachePersistentEntity<T>(typeInformation);

		if (context != null) {
			entity.setApplicationContext(context);
		}

		return entity;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}
