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

import org.springdata.ehcache.convert.EhcacheConverter;
import org.springdata.ehcache.convert.EhcacheMappingConverter;
import org.springdata.ehcache.mapping.EhcacheMappingContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author Alex Shvid
 * 
 */

public class MappingConverterFactoryBean implements FactoryBean<EhcacheConverter>, InitializingBean {

	private EhcacheConverter converter;

	@Override
	public void afterPropertiesSet() {

		EhcacheMappingContext mappingContext = new EhcacheMappingContext();

		converter = new EhcacheMappingConverter(mappingContext);

	}

	@Override
	public EhcacheConverter getObject() {
		return converter;
	}

	@Override
	public Class<?> getObjectType() {
		return converter != null ? converter.getClass() : EhcacheConverter.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
