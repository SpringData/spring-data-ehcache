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

package org.springdata.ehcache.config.xml;

import org.springdata.ehcache.config.EhcacheLookupFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Cache Element Parser
 * 
 * @author Alex Shvid
 * 
 */

public class CacheParser extends AbstractSimpleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return EhcacheLookupFactoryBean.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

		String name = element.getAttribute("name");
		if (StringUtils.hasText(name)) {
			builder.addPropertyValue("name", name);
		}

		String cacheManagerRef = element.getAttribute("cache-manager-ref");
		if (!StringUtils.hasText(cacheManagerRef)) {
			cacheManagerRef = ConfigConstants.CACHE_MANAGER_DEFAULT_ID;
		}
		builder.addPropertyReference("cacheManager", cacheManagerRef);

		postProcess(builder, element);
	}

}
