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
public final class BufferData implements OctetData {

	private ByteBuffer buffer;


	public BufferData() {
	}

	public BufferData(final ByteBuffer buffer) {
		this.buffer = buffer;
	}


	public final BufferData wrap(final ByteBuffer buffer) {
		this.buffer = buffer;
		return this;
	}


	public final byte byteAt(final int index) {
		return this.buffer.get(index);
	}

	public final boolean hasBuffer() {
		return true;
	}

	public final ByteBuffer buffer() {
		return this.buffer;
	}

	public final boolean hasArray() {
		return this.buffer.hasArray();
	}

	public final byte[] array() {
		if (this.buffer.hasArray()) {
			return this.buffer.array();
		}
		return null;
	}

	public final int arrayOffset() {
		if (this.buffer.hasArray()) {
			return this.buffer.arrayOffset();
		}
		return 0;
	}

	public final BufferData setByteAt(final int index, final byte b) {
		this.buffer.put(index, b);
		return this;
	}

	/*
	 * Externalizable methods
	 */

	public final void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(0);

		writeBuffer(this.buffer, out);
	}

	public final void readExternal(final ObjectInput in) throws IOException,
			ClassNotFoundException {
		/*int tmp = */in.readInt();

		this.buffer = readBuffer(in);
	}


	static final void writeBuffer(final ByteBuffer buffer, final ObjectOutput out) throws IOException {

		final int capacity = buffer.capacity();
		out.writeInt(capacity);

		out.writeBoolean(buffer.isDirect());

		final int position = buffer.position();
		final int limit = buffer.limit();

		out.writeInt(position);
		out.writeInt(limit);

		byte[] array = null;

		if (buffer.hasArray()) {
			array = buffer.array();
		}

		if (array != null) {
			out.write(array, buffer.arrayOffset(), capacity);
		} else {
			array = new byte[(capacity > 64) ? 64 : capacity];

			buffer.position(0).limit(capacity);
			try {
				int remaining = capacity;
				while (remaining > 0) {
					final int len = (remaining > array.length) ? array.length : remaining;

					buffer.get(array, 0, len);
					out.write(array, 0, len);

					remaining -= len;
				}
			} finally {
				buffer.position(0).limit(limit).position(position);
			}
		}
	}

	static final ByteBuffer readBuffer(final ObjectInput in) throws IOException,
			ClassNotFoundException {

		final int capacity = in.readInt();

		final boolean isDirect = in.readBoolean();

		final int position = in.readInt();
		final int limit = in.readInt();

		ByteBuffer buffer;

		if (isDirect) {
			buffer = ByteBuffer.allocateDirect(capacity);
		} else {
			buffer = ByteBuffer.allocate(capacity);
		}

		byte[] array = null;

		if (buffer.hasArray()) {
			array = buffer.array();
		}

		if (array != null) {
			in.read(array, buffer.arrayOffset(), capacity);
		} else {
			array = new byte[(capacity > 64) ? 64 : capacity];

			int remaining = capacity;
			while (remaining > 0) {
				int len = (remaining > array.length) ? array.length : remaining;

				len = in.read(array, 0, len);
				if (len > 0) {
					buffer.put(array, 0, len);
					remaining -= len;
				} else {
					break;
				}
			}
		}

		buffer.position(0).limit(limit).position(position);

		return buffer;
	}

}
