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

package org.springdata.ehcache.serializer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.serializer.Deserializer;

public class DataDeserializer<T> implements Deserializer<T> {

	private final Class<T> entityClass;

	public DataDeserializer(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	public T deserialize(InputStream inputStream) throws IOException {

		T obj;
		try {
			obj = entityClass.newInstance();
		} catch (Exception e) {
			throw new IOException(e);
		}

		DataSerializable io = (DataSerializable) obj;
		io.readFields(new DataInputStream(inputStream));

		return obj;
	}

}
