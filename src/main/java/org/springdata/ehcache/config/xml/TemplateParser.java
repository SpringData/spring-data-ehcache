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

import org.springdata.ehcache.config.EhcacheTemplateFactoryBean;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 
 * @author Alex Shvid
 * 
 */

public class TemplateParser extends AbstractSimpleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return EhcacheTemplateFactoryBean.class;
	}

	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		String id = super.resolveId(element, definition, parserContext);
		return StringUtils.hasText(id) ? id : ConfigConstants.TEMPLATE_DEFAULT_ID;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

		String cacheManagerRef = element.getAttribute("cache-manager-ref");
		if (!StringUtils.hasText(cacheManagerRef)) {
			cacheManagerRef = ConfigConstants.CACHE_MANAGER_DEFAULT_ID;
		}
		builder.addPropertyReference("cacheManager", cacheManagerRef);

		String converterRef = element.getAttribute("converter-ref");
		if (!StringUtils.hasText(converterRef)) {
			converterRef = ConfigConstants.CONVERTER_DEFAULT_ID;
		}
		builder.addPropertyReference("ehcacheConverter", converterRef);

		postProcess(builder, element);
	}

}
