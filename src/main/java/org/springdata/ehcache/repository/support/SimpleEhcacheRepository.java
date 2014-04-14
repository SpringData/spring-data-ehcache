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

package org.springdata.ehcache.repository.support;

import java.util.ArrayList;
import java.util.List;

import org.springdata.ehcache.core.EhcacheOperations;
import org.springdata.ehcache.core.EhcacheTemplate;
import org.springdata.ehcache.repository.EhcacheRepository;
import org.springframework.util.Assert;

/**
 * Simple Ehcache Repository
 * 
 * @author Alex Shvid
 * 
 * @param <T>
 */

public class SimpleEhcacheRepository<T> implements EhcacheRepository<T> {

	private final EhcacheEntityInformation<T> entityInformation;
	private final Class<?> repositoryInterface;
	private final EhcacheTemplate ehcacheTemplate;

	public SimpleEhcacheRepository(EhcacheEntityInformation<T> entityInformation, Class<?> repositoryInterface,
			EhcacheTemplate ehcacheTemplate) {

		Assert.notNull(entityInformation);
		Assert.notNull(repositoryInterface);
		Assert.notNull(ehcacheTemplate);

		this.entityInformation = entityInformation;
		this.repositoryInterface = repositoryInterface;
		this.ehcacheTemplate = ehcacheTemplate;

	}

	@Override
	public <S extends T> S save(S entity) {
		Assert.notNull(entity, "Entity must not be null!");
		ehcacheTemplate.put(entity);
		return entity;
	}

	@Override
	public <S extends T> List<S> save(Iterable<S> entities) {

		Assert.notNull(entities, "The given Iterable of entities must not be null!");

		List<S> saved = new ArrayList<S>();

		for (S entity : entities) {
			save(entity);
			saved.add(entity);
		}

		return saved;

	}

	@Override
	public T findOne(String key) {
		Assert.notNull(key, "The given id must not be null!");
		return ehcacheTemplate.get(entityInformation.getJavaType(), key);
	}

	@Override
	public boolean exists(String key) {
		Assert.notNull(key, "The given id must not be null!");
		return ehcacheTemplate.exists(entityInformation.getJavaType(), key);
	}

	@Override
	public long count() {
		return ehcacheTemplate.count(entityInformation.getJavaType());
	}

	@Override
	public void delete(String key) {
		Assert.notNull(key, "The given id must not be null!");
		ehcacheTemplate.remove(entityInformation.getJavaType(), key);
	}

	@Override
	public void delete(T entity) {
		Assert.notNull(entity, "The given entity must not be null!");
		ehcacheTemplate.remove(entity);
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		for (T entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		ehcacheTemplate.removeAll(entityInformation.getJavaType());
	}

	@Override
	public List<T> findAll() {
		return ehcacheTemplate.getAll(entityInformation.getJavaType());
	}

	@Override
	public Iterable<T> findAll(Iterable<String> ids) {
		Assert.notNull(ids, "The given Iterable of ids not be null!");
		List<T> collector = new ArrayList<T>();

		for (String id : ids) {
			collector.add(findOne(id));
		}

		return collector;
	}

	protected EhcacheOperations getEhcacheOperations() {
		return ehcacheTemplate;
	}

	protected EhcacheEntityInformation<T> getEntityInformation() {
		return entityInformation;
	}

}
