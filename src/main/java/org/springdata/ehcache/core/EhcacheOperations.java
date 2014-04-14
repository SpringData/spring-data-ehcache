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

import java.util.List;

import net.sf.ehcache.CacheManager;

import org.springdata.ehcache.convert.EhcacheConverter;

/**
 * Ehcache Operations
 * 
 * @author Alex Shvid
 * 
 */

public interface EhcacheOperations {

	String getCacheName(Class<?> entityClass);

	<T> String getKey(T entity);

	<T> T execute(String cacheName, EhcacheCallback<T> cb);

	<T> T get(Class<T> entityClass, String key);

	<T> List<T> getAll(Class<T> entityClass);

	<T> void put(T entity);

	<T> void remove(Class<T> entityClass, String key);

	<T> void remove(T entity);

	void removeAll(Class<?> entityClass);

	long count(Class<?> entityClass);

	boolean exists(Class<?> entityClass, String key);

	CacheManager getCacheManager();

	EhcacheConverter getConverter();

}
