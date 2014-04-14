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

import java.io.FileNotFoundException;

import net.sf.ehcache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Cache Manager Factory Bean
 * 
 * @author Alex Shvid
 * 
 */

public class CacheManagerFactoryBean implements FactoryBean<CacheManager>, InitializingBean, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(CacheManagerFactoryBean.class);

	private String configFile;
	private String terracottaLicenseFile;

	private CacheManager cacheManager;

	@Override
	public void afterPropertiesSet() throws FileNotFoundException {

		Assert.hasText(configFile, "configFile property must be set");

		if (StringUtils.hasText(terracottaLicenseFile)) {
			System.getProperties().setProperty("com.tc.productkey.path", terracottaLicenseFile);
		}

		logger.info("Initializing ehcache ...");
		cacheManager = new CacheManager(configFile);

	}

	@Override
	public void destroy() {
		if (cacheManager != null) {
			logger.info("Destroying ehcache ...");
			cacheManager.shutdown();
			cacheManager = null;
		}
	}

	@Override
	public CacheManager getObject() {
		return cacheManager;
	}

	@Override
	public Class<?> getObjectType() {
		return cacheManager != null ? cacheManager.getClass() : CacheManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public void setTerracottaLicenseFile(String terracottaLicenseFile) {
		this.terracottaLicenseFile = terracottaLicenseFile;
	}

}
