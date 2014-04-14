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

package org.springdata.ehcache.repository.config;

import java.io.Serializable;

import org.springdata.ehcache.core.EhcacheTemplate;
import org.springdata.ehcache.mapping.EhcachePersistentEntity;
import org.springdata.ehcache.mapping.EhcachePersistentProperty;
import org.springdata.ehcache.repository.support.EhcacheEntityInformation;
import org.springdata.ehcache.repository.support.MappingEhcacheEntityInformation;
import org.springdata.ehcache.repository.support.SimpleEhcacheRepository;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.util.Assert;

/**
 * Ehcache repository factory
 * 
 * @author Alex Shvid
 * 
 */

public class EhcacheRepositoryFactory extends RepositoryFactorySupport {

	private final EhcacheTemplate ehcacheTemplate;
	private final MappingContext<? extends EhcachePersistentEntity<?>, EhcachePersistentProperty> mappingContext;

	public EhcacheRepositoryFactory(EhcacheTemplate ehcacheTemplate) {

		Assert.notNull(ehcacheTemplate);

		this.ehcacheTemplate = ehcacheTemplate;
		this.mappingContext = ehcacheTemplate.getConverter().getMappingContext();
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SimpleEhcacheRepository.class;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object getTargetRepository(RepositoryMetadata metadata) {

		EhcacheEntityInformation<?> entityInformation = getEntityInformationOverride(metadata.getDomainType());

		return new SimpleEhcacheRepository(entityInformation, metadata.getRepositoryInterface(), ehcacheTemplate);

	}

	@Override
	protected QueryLookupStrategy getQueryLookupStrategy(Key key) {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		return (EntityInformation<T, ID>) getEntityInformationOverride(domainClass);
	}

	@SuppressWarnings("unchecked")
	public <T> EhcacheEntityInformation<T> getEntityInformationOverride(Class<T> domainClass) {

		EhcachePersistentEntity<?> entity = mappingContext.getPersistentEntity(domainClass);

		if (entity == null) {
			throw new MappingException(String.format("Could not lookup mapping metadata for domain class %s!",
					domainClass.getName()));
		}

		return new MappingEhcacheEntityInformation<T>(ehcacheTemplate, (EhcachePersistentEntity<T>) entity);
	}
}
