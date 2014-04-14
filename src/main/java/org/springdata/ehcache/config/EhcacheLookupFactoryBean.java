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

package org.springdata.ehcache.config;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Ehcache lookup by name or bean name
 * 
 * @author Alex Shvid
 * 
 */

public class EhcacheLookupFactoryBean implements FactoryBean<Ehcache>, InitializingBean, BeanNameAware {

	private static Logger logger = LoggerFactory.getLogger(EhcacheLookupFactoryBean.class);

	private CacheManager cacheManager;

	private String beanName;

	private String name;

	private Ehcache cache;

	@Override
	public void afterPropertiesSet() {

		Assert.notNull(cacheManager, "CacheManager property must be set");

		String cacheName = StringUtils.hasText(name) ? name : beanName;
		Assert.hasText(cacheName, "Name property must be set or bean name must be defined");

		cache = cacheManager.getCache(cacheName);

		if (cache != null) {
			logger.info("Retrieved cache [" + cacheName + "] from cacheManager");
		} else {
			cache = lookupFallback(cacheManager, cacheName);
		}

	}

	protected Ehcache lookupFallback(CacheManager cacheManager, String cacheName) {
		throw new BeanInitializationException("Cannot find cache [" + cacheName + "] in terracotta " + cacheManager);
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Ehcache getObject() {
		return cache;
	}

	@Override
	public Class<?> getObjectType() {
		return (cache != null ? cache.getClass() : Ehcache.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

}
