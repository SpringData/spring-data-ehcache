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

package org.springdata.ehcache.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springdata.ehcache.core.Book;
import org.springdata.ehcache.core.EhcacheOperations;
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
@ContextConfiguration(locations = { "classpath:test-repo-context.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SimpleRepositoryTest {

	@Autowired
	private EhcacheOperations ehcacheOperations;

	@Autowired
	private BookRepository bookRepository;

	private Book alex, bob;

	@Before
	public void init() {

		alex = new Book(55, "alex", 100.0);
		bob = new Book(77, "bob", 77.0);

		ehcacheOperations.put(alex);
		ehcacheOperations.put(bob);
	}

	@Test
	public void testSave() {

		long was = bookRepository.count();

		Book greg = new Book(999, "greg", 46.0);

		bookRepository.save(greg);

		Assert.assertEquals(was + 1, bookRepository.count());
	}

	@Test
	public void testFindOne() {

		Book found = bookRepository.findOne(Long.toString(alex.getId()));
		Assert.assertEquals(alex, found);

		Book notFound = bookRepository.findOne(alex.getId() + "x");
		Assert.assertNull(notFound);
	}

	@Test
	public void testFindAll() {

		List<Book> all = new ArrayList<Book>();
		for (Book b : bookRepository.findAll()) {
			all.add(b);
		}
		Assert.assertEquals(2, all.size());
		Assert.assertTrue(all.contains(alex));
		Assert.assertTrue(all.contains(bob));

	}

	@Test
	public void testRemove() {

		bookRepository.delete(alex);

		Book notFound = bookRepository.findOne(Long.toString(alex.getId()));
		Assert.assertNull(notFound);

	}

	@Test
	public void testRemoveAll() {

		bookRepository.deleteAll();

		Assert.assertEquals(0, bookRepository.count());

	}

	@Test
	public void testExists() {

		Assert.assertTrue(bookRepository.exists(Long.toString(alex.getId())));

	}

}
