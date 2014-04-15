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

import org.springdata.ehcache.config.EhcacheLookupFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Alex Shvid
 * 
 */

@Configuration
@ComponentScan("org.springdata.ehcache.config.service")
public class JavaConfig extends AbstractEhcacheConfiguration {

	@Override
	public String ehcacheFile() {
		return "src/test/resources/test-ehcache.xml";
	}

	@Override
	public String terracottaLicenseFile() {
		return System.getenv("TERRACOTTA_LICENSE_KEY");
	}

	@Bean(name = "TEST_CACHE")
	public EhcacheLookupFactoryBean testCache() {
		return lookupCache();
	}

}
