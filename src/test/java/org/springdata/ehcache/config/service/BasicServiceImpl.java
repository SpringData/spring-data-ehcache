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

package org.springdata.ehcache.config.service;

import javax.annotation.Resource;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.springframework.stereotype.Service;

/**
 * Basic Service implementation
 * 
 * @author Alex Shvid
 * 
 */

@Service
public class BasicServiceImpl implements BasicService {

	@Resource(name = "TEST_CACHE")
	private Ehcache testCache;

	@Override
	public byte[] get(String key) {
		Element element = testCache.get(key);
		if (element == null) {
			return null;
		}
		return (byte[]) element.getValue();
	}

	@Override
	public void put(String key, byte[] value) {
		testCache.put(new Element(key, value));
	}

}
