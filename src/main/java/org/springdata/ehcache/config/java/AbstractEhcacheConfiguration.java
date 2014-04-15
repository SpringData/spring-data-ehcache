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

package org.springdata.ehcache.config.java;

import org.springdata.ehcache.config.CacheManagerFactoryBean;
import org.springdata.ehcache.config.EhcacheLookupFactoryBean;
import org.springdata.ehcache.config.EhcacheTemplateFactoryBean;
import org.springdata.ehcache.config.MappingConverterFactoryBean;
import org.springdata.ehcache.config.xml.ConfigConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Abstract Ehcache Java Config
 * 
 * @author Alex Shvid
 * 
 */

@Configuration
public abstract class AbstractEhcacheConfiguration {

	public abstract String ehcacheFile();

	public String terracottaLicenseFile() {
		return null;
	}

	@Bean(name = ConfigConstants.CACHE_MANAGER_DEFAULT_ID)
	public CacheManagerFactoryBean manager() {

		CacheManagerFactoryBean bean = new CacheManagerFactoryBean();

		bean.setConfigFile(ehcacheFile());
		bean.setTerracottaLicenseFile(terracottaLicenseFile());

		return bean;
	}

	@Bean(name = ConfigConstants.CONVERTER_DEFAULT_ID)
	public MappingConverterFactoryBean converter() {
		return new MappingConverterFactoryBean();
	}

	@Bean(name = ConfigConstants.TEMPLATE_DEFAULT_ID)
	public EhcacheTemplateFactoryBean template() {
		EhcacheTemplateFactoryBean bean = new EhcacheTemplateFactoryBean();
		bean.setCacheManager(manager().getObject());
		bean.setEhcacheConverter(converter().getObject());
		return bean;
	}

	protected EhcacheLookupFactoryBean lookupCache() {
		return lookupCache(null);
	}

	protected EhcacheLookupFactoryBean lookupCache(String cacheName) {

		EhcacheLookupFactoryBean bean = new EhcacheLookupFactoryBean();

		bean.setCacheManager(manager().getObject());
		bean.setName(cacheName);

		return bean;
	}

}
