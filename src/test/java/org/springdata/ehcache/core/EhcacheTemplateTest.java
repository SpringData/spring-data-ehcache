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

package org.springdata.ehcache.core;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Alex Shvid
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-template-context.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class EhcacheTemplateTest {

	@Autowired
	private EhcacheOperations ehcacheOperations;

	@Test
	public void testGetKey() {

		Book book = new Book(123, "alex", 100.0);

		String key = ehcacheOperations.getKey(book);

		Assert.assertEquals("123", key);

	}

	@Test
	public void testPutGet() {

		Book book = new Book(123, "alex", 100.0);

		Assert.assertNull(ehcacheOperations.get(Book.class, "123"));

		ehcacheOperations.put(book);

		Book stored = ehcacheOperations.get(Book.class, "123");

		Assert.assertEquals(book, stored);
	}

}
