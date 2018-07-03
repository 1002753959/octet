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
package com.chinmobi.octet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;

import com.chinmobi.octet.data.OctetData;
import com.chinmobi.octet.io.OctetInputOp;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public abstract class AbstractOctet implements Octet, Externalizable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6614825304047900555L;

	protected OctetData data;

	protected int begin;
	protected int length;


	protected AbstractOctet(final OctetData data) {
		this.data = data;

		this.begin = 0;
		this.length = 0;
	}

	protected AbstractOctet(final AbstractOctet source) {
		this.data = source.data;

		this.begin = source.begin;
		this.length = source.length;
	}


	protected void set(final AbstractOctet source) {
		this.data = source.data;
		this.begin = source.begin;
		this.length = source.length;
	}

	protected void swap(final AbstractOctet another) {
		final OctetData tmpData = this.data;
		this.data = another.data;
		another.data = tmpData;

		int tmp = this.begin;
		this.begin = another.begin;
		another.begin = tmp;

		tmp = this.length;
		this.length = another.length;
		another.length = tmp;
	}


	public final Object lock() {
		return this.data;
	}

	public OctetInputOp inputOp() {
		return new OctetInputOp(this);
	}


	public final byte byteAt(final int index) {
		return this.data.byteAt(index);
	}

	public final int begin() {
		return this.begin;
	}

	public final int end() {
		return this.begin + this.length;
	}

	public final boolean isEmpty() {
		return (this.length <= 0);
	}

	public final int length() {
		return this.length;
	}

	public final boolean hasBuffer() {
		return this.data.hasBuffer();
	}

	public final ByteBuffer buffer() {
		return this.data.buffer();
	}

	public final boolean hasArray() {
		return this.data.hasArray();
	}

	public final byte[] array() {
		return this.data.array();
	}

	public final int arrayOffset() {
		return this.data.arrayOffset();
	}


	/*
	 * Comparable methods
	 */

	public final int compareTo(final Octet another) {
		final int len1 = this.length();
		final int len2 = another.length();
		int n = Math.min(len1, len2);

		int i = this.begin();
		int j = another.begin();

		final byte[] a1 = this.array();
		final byte[] a2 = another.array();

		final ByteBuffer buf1 = this.buffer();
		final ByteBuffer buf2 = another.buffer();

		if (a1 != null && a2 != null) {
			i += this.arrayOffset();
			j += another.arrayOffset();

			while (n-- != 0) {
				final byte b1 = a1[i++];
				final byte b2 = a2[j++];
				if (b1 != b2) {
					return b1 - b2;
				}
			}
		} else if (a1 != null && buf2 != null) {
			i += this.arrayOffset();

			while (n-- != 0) {
				final byte b1 = a1[i++];
				final byte b2 = buf2.get(j++);
				if (b1 != b2) {
					return b1 - b2;
				}
			}
		} else if (a2 != null && buf1 != null) {
			j += another.arrayOffset();

			while (n-- != 0) {
				final byte b1 = buf1.get(i++);
				final byte b2 = a2[j++];
				if (b1 != b2) {
					return b1 - b2;
				}
			}
		} else if (buf1 != null && buf2 != null) {
			while (n-- != 0) {
				final byte b1 = buf1.get(i++);
				final byte b2 = buf2.get(j++);
				if (b1 != b2) {
					return b1 - b2;
				}
			}
		}

		return len1 - len2;
	}

	/*
	 * equals methods
	 */

	public final boolean equals(final Object obj) {
		if (obj != null && obj instanceof Octet) {
			return equals((Octet)obj);
		}
		return false;
	}

	public final boolean equals(final Octet another) {
		if (this == another) {
			return true;
		}
		if (this.length == another.length()) {
			return equals(this.begin, this.length, another);
		}
		return false;
	}

	public final boolean equals(final byte[] bytes, final int offset, final int length) {
		if (bytes != null && length == this.length) {
			return equals(this.begin, this.length, bytes, offset);
		}
		return false;
	}

	public final boolean equals(final byte[] bytes) {
		return equals(bytes, 0, bytes.length);
	}

	/*
	 * startsWith methods
	 */

	public final boolean startsWith(final Octet prefix) {
		if (this.length >= prefix.length()) {
			return equals(this.begin, prefix.length(), prefix);
		}
		return false;
	}

	public final boolean startsWith(final byte[] bytes, final int offset, final int length) {
		if (this.length >= length) {
			return equals(this.begin, length, bytes, offset);
		}
		return false;
	}

	public final boolean startsWith(final byte[] bytes) {
		return startsWith(bytes, 0, bytes.length);
	}

	public final boolean startsWith(final int fromIndex, final Octet prefix) {
		int start = fromIndex;
		if (start < this.begin) {
			start = this.begin;
		}

		if ((end() - start) >= prefix.length()) {
			return equals(start, prefix.length(), prefix);
		}

		return false;
	}

	public final boolean startsWith(final int fromIndex, final byte[] bytes, final int offset, final int length) {
		int start = fromIndex;
		if (start < this.begin) {
			start = this.begin;
		}

		if ((end() - start) >= length) {
			return equals(start, length, bytes, offset);
		}
		return false;
	}

	public final boolean startsWith(final int fromIndex, final byte[] bytes) {
		return startsWith(fromIndex, bytes, 0, bytes.length);
	}

	/*
	 * endsWith methods
	 */

	public final boolean endsWith(final Octet suffix) {
		if (this.length >= suffix.length()) {
			return equals((end() - suffix.length()), suffix.length(), suffix);
		}
		return false;
	}

	public final boolean endsWith(final byte[] bytes, final int offset, final int length) {
		if (this.length >= length) {
			return equals((end() - length), length, bytes, offset);
		}
		return false;
	}

	public final boolean endsWith(final byte[] bytes) {
		return endsWith(bytes, 0, bytes.length);
	}

	/*
	 * indexOf and lastIndexOf byte methods
	 */

	public final int indexOf(final byte b) {
		return indexOf(this.begin, b);
	}

	public final int indexOf(int fromIndex, final byte b) {
		int end = end();

		if (fromIndex < this.begin) {
			fromIndex = this.begin;
		}

		final byte[] array = this.array();
		if (array != null) {
			end += arrayOffset();

			for (int i = fromIndex + arrayOffset(); i < end; ++i) {
				if (array[i] == b) {
					return i - arrayOffset();
				}
			}
		} else {
			final ByteBuffer buffer = this.buffer();
			if (buffer != null) {
				for (int i = fromIndex; i < end; ++i) {
					if (buffer.get(i) == b) {
						return i;
					}
				}
			}
		}

		return -1;
	}

	public final int indexOneOf(final byte[] bytes, final int offset, final int length) {
		return indexOneOf(this.begin, bytes, offset, length);
	}

	public final int indexOneOf(final byte[] bytes) {
		return indexOneOf(this.begin, bytes);
	}

	public final int indexOneOf(int fromIndex, final byte[] bytes, final int offset, final int length) {
		int end = end();

		if (fromIndex < this.begin) {
			fromIndex = this.begin;
		}

		final int limit = offset + length;

		final byte[] array = this.array();
		if (array != null) {
			end += arrayOffset();

			for (int i = fromIndex + arrayOffset(); i < end; ++i) {
				final byte b = array[i];

				for (int j = offset; j < limit; ++j) {
					if (bytes[j] == b) {
						return i - arrayOffset();
					}
				}
			}
		} else {
			final ByteBuffer buffer = this.buffer();
			if (buffer != null) {
				for (int i = fromIndex; i < end; ++i) {
					final byte b = buffer.get(i);

					for (int j = offset; j < limit; ++j) {
						if (bytes[j] == b) {
							return i;
						}
					}
				}
			}
		}

		return -1;
	}

	public final int indexOneOf(int fromIndex, final byte[] bytes) {
		return indexOneOf(fromIndex, bytes, 0, bytes.length);
	}

	public final int lastIndexOf(final byte b) {
		return lastIndexOf(end() - 1, b);
	}

	public final int lastIndexOf(int fromIndex, final byte b) {
		int begin = this.begin;

		if (fromIndex >= end()) {
			fromIndex = end() - 1;
		}

		final byte[] array = this.array();
		if (array != null) {
			begin += arrayOffset();

			for (int i = fromIndex + arrayOffset(); i >= begin; --i) {
				if (array[i] == b) {
					return i - arrayOffset();
				}
			}
		} else {
			final ByteBuffer buffer = this.buffer();
			if (buffer != null) {
				for (int i = fromIndex; i >= begin; --i) {
					if (buffer.get(i) == b) {
						return i;
					}
				}
			}
		}

		return -1;
	}

	/*
	 * indexOf methods
	 */

	public final int indexOf(final Octet octet) {
		if (this.length >= octet.length() && octet.length() > 0) {
			int[] shiftTable = null;
			if (OctetMatchUtils.shouldShiftTable(this.length, octet.length())) {
				shiftTable = OctetMatchUtils.newShiftTable();
			}

			return OctetMatchUtils.find(this, this.begin, this.length, octet, octet.begin(), octet.length(), shiftTable);
		}
		return -1;
	}

	public final int indexOf(final byte[] bytes, final int offset, final int length) {
		if (this.length >= length && length > 0) {
			int[] shiftTable = null;
			if (OctetMatchUtils.shouldShiftTable(this.length, length)) {
				shiftTable = OctetMatchUtils.newShiftTable();
			}

			return OctetMatchUtils.find(this, this.begin, this.length, bytes, offset, length, shiftTable);
		}
		return -1;
	}

	public final int indexOf(final byte[] bytes) {
		return indexOf(bytes, 0, bytes.length);
	}

	public final int indexOf(int fromIndex, final Octet octet) {
		if (fromIndex < this.begin) {
			fromIndex = this.begin;
		}

		final int srcLength = end() - fromIndex;

		if (srcLength >= octet.length() && octet.length() > 0) {
			int[] shiftTable = null;
			if (OctetMatchUtils.shouldShiftTable(srcLength, octet.length())) {
				shiftTable = OctetMatchUtils.newShiftTable();
			}

			return OctetMatchUtils.find(this, fromIndex, srcLength, octet, octet.begin(), octet.length(), shiftTable);
		}
		return -1;
	}

	public final int indexOf(int fromIndex, final byte[] bytes, final int offset, final int length) {
		if (fromIndex < this.begin) {
			fromIndex = this.begin;
		}

		final int srcLength = end() - fromIndex;

		if (srcLength >= length && length > 0) {
			int[] shiftTable = null;
			if (OctetMatchUtils.shouldShiftTable(srcLength, length)) {
				shiftTable = OctetMatchUtils.newShiftTable();
			}

			return OctetMatchUtils.find(this, fromIndex, srcLength, bytes, offset, length, shiftTable);
		}
		return -1;
	}

	public final int indexOf(final int fromIndex, final byte[] bytes) {
		return indexOf(fromIndex, bytes, 0, bytes.length);
	}

	/*
	 * lastIndexOf methods
	 */

	public final int lastIndexOf(final Octet octet) {
		if (this.length >= octet.length() && octet.length() > 0) {
			int[] shiftTable = null;
			if (OctetMatchUtils.shouldShiftTable(this.length, octet.length())) {
				shiftTable = OctetMatchUtils.newShiftTable();
			}

			return OctetMatchUtils.reverseFind(this, this.begin, this.length, octet, octet.begin(), octet.length(), shiftTable);
		}
		return -1;
	}

	public final int lastIndexOf(final byte[] bytes, final int offset, final int length) {
		if (this.length >= length && length > 0) {
			int[] shiftTable = null;
			if (OctetMatchUtils.shouldShiftTable(this.length, length)) {
				shiftTable = OctetMatchUtils.newShiftTable();
			}

			return OctetMatchUtils.reverseFind(this, this.begin, this.length, bytes, offset, length, shiftTable);
		}
		return -1;
	}

	public final int lastIndexOf(final byte[] bytes) {
		return lastIndexOf(bytes, 0, bytes.length);
	}

	public final int lastIndexOf(int fromIndex, final Octet octet) {
		if (fromIndex >= end()) {
			fromIndex = end() - 1;
		}

		final int srcLength = fromIndex - this.begin;

		if (srcLength >= octet.length() && octet.length() > 0) {
			int[] shiftTable = null;
			if (OctetMatchUtils.shouldShiftTable(srcLength, octet.length())) {
				shiftTable = OctetMatchUtils.newShiftTable();
			}

			return OctetMatchUtils.reverseFind(this, this.begin, srcLength, octet, octet.begin(), octet.length(), shiftTable);
		}
		return -1;
	}

	public final int lastIndexOf(int fromIndex, final byte[] bytes, final int offset, final int length) {
		if (fromIndex >= end()) {
			fromIndex = end() - 1;
		}

		final int srcLength = fromIndex - this.begin;

		if (srcLength >= length && length > 0) {
			int[] shiftTable = null;
			if (OctetMatchUtils.shouldShiftTable(srcLength, length)) {
				shiftTable = OctetMatchUtils.newShiftTable();
			}

			return OctetMatchUtils.reverseFind(this, this.begin, srcLength, bytes, offset, length, shiftTable);
		}
		return -1;
	}

	public final int lastIndexOf(final int fromIndex, final byte[] bytes) {
		return lastIndexOf(fromIndex, bytes, 0, bytes.length);
	}

	/*
	 * suboctet methods
	 */

	public final Octet suboctet(final int beginIndex) {
		final int limit = end();

		if (beginIndex > this.begin) {
			if (beginIndex > limit) {
				this.begin = limit;
			} else {
				this.begin = beginIndex;
			}

			this.length = limit - this.begin;
		}

		return this;
	}

	public final Octet suboctet(final int beginIndex, final int endIndex) {
		final int limit = end();

		if (beginIndex > this.begin) {
			if (beginIndex > limit) {
				this.begin = limit;
			} else {
				this.begin = beginIndex;
			}
		}
		if (endIndex < limit) {
			if (endIndex < this.begin) {
				this.length = 0;
			} else {
				this.length = endIndex - this.begin;
			}
		} else {
			this.length = limit - this.begin;
		}

		return this;
	}

	/*
	 * other methods
	 */

	public final byte[] getBytes() {
		final byte[] bytes = new byte[this.length];

		if (this.length > 0) {
			final byte[] array = array();
			if (array != null) {
				System.arraycopy(array, this.begin + arrayOffset(), bytes, 0, this.length);
			} else {
				final ByteBuffer buffer = buffer();
				if (buffer != null) {
					final int position = buffer.position();
					final int limit = buffer.limit();

					buffer.position(0).limit(end()).position(this.begin);
					try {
						buffer.get(bytes);
					} finally {
						buffer.position(0).limit(limit).position(position);
					}
				}
			}
		}

		return bytes;
	}

	@Override
	public int hashCode() {
		int i = this.begin;
		int end = this.end();

		int h = 0;

		final byte[] array = this.array();

		if (array != null) {
			i += this.arrayOffset();
			end += this.arrayOffset();

			for (; i < end; ++i) {
				h = 31 * h + array[i];
			}
		} else {
			final ByteBuffer buffer = this.buffer();
			if (buffer != null) {
				for (; i < end; ++i) {
					h = 31 * h + buffer.get(i);
				}
			}
		}

		return h;
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public void toString(boolean asHexFormat, Appendable appendable) {
		OctetUtils.hexFormat(this, appendable);
	}

	public String toString(boolean asHexFormat) {
		return OctetUtils.hexFormat(this);
	}


	/*
	 * Externalizable methods
	 */

	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeObject(this.data);

		out.writeInt(this.begin);
		out.writeInt(this.length);
	}

	public void readExternal(final ObjectInput in) throws IOException,
			ClassNotFoundException {
		this.data = (OctetData)in.readObject();

		this.begin = in.readInt();
		this.length = in.readInt();
	}

	/*
	 * Internal equals methods
	 */

	protected final boolean equals(final int start, final int length, final Octet another) {
		int i = start;
		int j = another.begin();

		int end = start + length;

		final byte[] anotherArray = another.array();
		if (anotherArray != null) {
			return equals(start, length, anotherArray, j + another.arrayOffset());
		}

		final ByteBuffer anotherBuf = another.buffer();
		if (anotherBuf != null) {

			final byte[] array = this.array();
			if (array != null) {
				end += arrayOffset();

				for (i += arrayOffset(); i < end; ++i, ++j) {
					if (array[i] != anotherBuf.get(j)) {
						return false;
					}
				}
			} else {
				final ByteBuffer buffer = this.buffer();
				if (buffer != null) {
					for (; i < end; ++i, ++j) {
						if (buffer.get(i) != anotherBuf.get(j)) {
							return false;
						}
					}
				} else {
					return false;
				}
			}
		} else {
			return false;
		}

		return true;
	}

	protected final boolean equals(final int start, final int length, final byte[] bytes, final int offset) {
		int i = start;
		int j = offset;

		int end = start + length;

		final byte[] array = this.array();
		if (array != null) {
			end += arrayOffset();

			for (i += arrayOffset(); i < end; ++i, ++j) {
				if (array[i] != bytes[j]) {
					return false;
				}
			}
		} else {
			final ByteBuffer buffer = this.buffer();
			if (buffer != null) {
				for (; i < end; ++i, ++j) {
					if (buffer.get(i) != bytes[j]) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}

}
