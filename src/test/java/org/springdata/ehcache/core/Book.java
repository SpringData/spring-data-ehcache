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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.springdata.ehcache.mapping.Entity;
import org.springdata.ehcache.serializer.DataSerializable;
import org.springframework.data.annotation.Id;

/**
 * 
 * @author Alex Shvid
 * 
 */

@Entity(cacheName = "TEST_CACHE")
public class Book implements DataSerializable {

	private static final long serialVersionUID = 1158452471374775145L;

	@Id
	private long id;
	private String author;
	private double price;

	public Book() {
	}

	public Book(long id, String author, double price) {
		this.id = id;
		this.author = author;
		this.price = price;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		id = in.readLong();
		author = in.readUTF();
		price = in.readDouble();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(id);
		out.writeUTF(author);
		out.writeDouble(price);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		return true;
	}

}
