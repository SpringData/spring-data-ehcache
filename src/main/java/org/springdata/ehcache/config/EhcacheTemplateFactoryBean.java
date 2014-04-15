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

import org.springdata.ehcache.convert.EhcacheConverter;
import org.springdata.ehcache.core.EhcacheTemplate;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 
 * @author Alex Shvid
 * 
 */

public class EhcacheTemplateFactoryBean implements FactoryBean<EhcacheTemplate>, InitializingBean {

	private CacheManager cacheManager;
	private EhcacheConverter ehcacheConverter;

	private EhcacheTemplate ehcacheTemplate;

	@Override
	public void afterPropertiesSet() {

		Assert.notNull(cacheManager, "CacheManager property must be set");
		Assert.notNull(ehcacheConverter, "CacheConverter property must be set");

		ehcacheTemplate = new EhcacheTemplate(cacheManager, ehcacheConverter);

	}

	@Override
	public EhcacheTemplate getObject() {
		return ehcacheTemplate;
	}

	@Override
	public Class<?> getObjectType() {
		return ehcacheTemplate != null ? ehcacheTemplate.getClass() : EhcacheTemplate.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setEhcacheConverter(EhcacheConverter ehcacheConverter) {
		this.ehcacheConverter = ehcacheConverter;
	}

}
