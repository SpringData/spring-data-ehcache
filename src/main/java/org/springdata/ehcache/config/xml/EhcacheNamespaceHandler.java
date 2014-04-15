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

import org.springdata.ehcache.repository.config.EhcacheRepositoryConfigurationExtension;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.data.repository.config.RepositoryBeanDefinitionParser;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/**
 * Ehcache Namespace Handler
 * 
 * @author Alex Shvid
 * 
 */

public class EhcacheNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {

		RepositoryConfigurationExtension extension = new EhcacheRepositoryConfigurationExtension();
		RepositoryBeanDefinitionParser repositoryBeanDefinitionParser = new RepositoryBeanDefinitionParser(extension);
		registerBeanDefinitionParser(ConfigConstants.REPOSITORIES_ELEMENT, repositoryBeanDefinitionParser);

		registerBeanDefinitionParser(ConfigConstants.CACHE_MANAGER_ELEMENT, new CacheManagerParser());
		registerBeanDefinitionParser(ConfigConstants.CONVERTER_ELEMENT, new ConverterParser());
		registerBeanDefinitionParser(ConfigConstants.TEMPLATE_ELEMENT, new TemplateParser());
		registerBeanDefinitionParser(ConfigConstants.CACHE_ELEMENT, new CacheParser());

	}

}
