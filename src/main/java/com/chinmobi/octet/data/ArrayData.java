/**
 * MIT License
 *
 * Copyright (c) 2018 Zhaoping Yu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.chinmobi.octet.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class ArrayData implements OctetData {

	private byte[] array;


	public ArrayData() {
	}

	public ArrayData(final byte[] array) {
		this.array = array;
	}


	public final ArrayData wrap(final byte[] array) {
		this.array = array;
		return this;
	}


	public final byte byteAt(final int index) {
		return this.array[index];
	}

	public final boolean hasBuffer() {
		return false;
	}

	public final ByteBuffer buffer() {
		return null;
	}

	public final boolean hasArray() {
		return true;
	}

	public final byte[] array() {
		return this.array;
	}

	public final int arrayOffset() {
		return 0;
	}

	public final ArrayData setByteAt(final int index, final byte b) {
		this.array[index] = b;
		return this;
	}

	/*
	 * Externalizable methods
	 */

	public final void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(0);

		writeArray(this.array, out);
	}

	public final void readExternal(final ObjectInput in) throws IOException,
			ClassNotFoundException {
		/*int tmp = */in.readInt();

		this.array = readArray(in);
	}


	static final void writeArray(final byte[] array, final ObjectOutput out) throws IOException {

		out.writeInt(array.length);

		out.write(array, 0, array.length);
	}

	static final byte[] readArray(final ObjectInput in) throws IOException,
			ClassNotFoundException {

		final int capacity = in.readInt();

		final byte[] array = new byte[capacity];

		in.read(array, 0, array.length);

		return array;
	}

}
