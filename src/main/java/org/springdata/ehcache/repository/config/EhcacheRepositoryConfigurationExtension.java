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

package org.springdata.ehcache.repository.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Repository Configuration Extension
 * 
 * @author Alex Shvid
 * 
 */

public class EhcacheRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

	private static final String CACHE_TEMPLATE_REF = "ehcache-template-ref";

	@Override
	protected String getModulePrefix() {
		return "ehcache";
	}

	public String getRepositoryFactoryClassName() {
		return EhcacheRepositoryFactoryBean.class.getName();
	}

	@Override
	public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {

		Element element = config.getElement();

		ParsingUtils.setPropertyReference(builder, element, CACHE_TEMPLATE_REF, "ehcacheTemplate");

	}

	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {

		AnnotationAttributes attributes = config.getAttributes();

		String ehcacheTemplateRef = attributes.getString("ehcacheTemplateRef");
		if (StringUtils.hasText(ehcacheTemplateRef)) {
			builder.addPropertyReference("ehcacheTemplate", ehcacheTemplateRef);
		}
	}

}
