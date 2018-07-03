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

import java.nio.ByteBuffer;

import com.chinmobi.octet.data.OctetData;
import com.chinmobi.octet.io.OctetOutputOp;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public abstract class AbstractMutableOctet
	extends AbstractOctet implements MutableOctet {

	/**
	 *
	 */
	private static final long serialVersionUID = 3042382690088668491L;


	protected AbstractMutableOctet(final OctetData data) {
		super(data);
	}

	protected AbstractMutableOctet(final AbstractMutableOctet source) {
		super((AbstractOctet)source);
	}


	protected void set(final AbstractMutableOctet source) {
		super.set((AbstractOctet)source);
	}

	protected void swap(final AbstractMutableOctet another) {
		super.swap((AbstractOctet)another);
	}


	/*
	 * mutable methods
	 */

	public MutableOctet clear() {
		final ByteBuffer buf = buffer();
		if (buf != null) {
			buf.clear();
		}

		this.begin = 0;
		this.length = 0;

		return this;
	}

	public final MutableOctet delete(final int start, final int end) {
		delete(this, start, end);
		return this;
	}

	public final MutableOctet replaceAll(final byte oldByte, final byte newByte) {
		replaceAll(this, oldByte, newByte);
		return this;
	}

	public final MutableOctet setByteAt(final int index, final byte b) {
		if (index >= begin() && index < end()) {
			this.data.setByteAt(index, b);
		} else {
			throw new IndexOutOfBoundsException("index: " + index);
		}
		return this;
	}

	public OctetOutputOp outputOp() {
		return new OutputOp(this, this);
	}


	/*
	 * static methods
	 */

	public static final void delete(final AbstractOctet octet, final int start, final int end) {

		if (start >= octet.begin()) {

			if (end > start) {
				int limit = octet.end();

				final ByteBuffer buffer = octet.buffer();
				final byte[] array = octet.array();

				if (end <= limit) {
					if (buffer != null) {
						limit = buffer.limit();
					}

					if (end < limit) {
						if (array != null) {
							System.arraycopy(array, end + octet.arrayOffset(),
									array, start + octet.arrayOffset(), limit - end);
						} else if (buffer != null) {
							BufferUtils.move(buffer, end, start, limit - end);
						}
					}

					limit = end; // To adjust length.
				}

				if (end == limit) {
					final int deletedLen = end - start;

					octet.length -= deletedLen;

					if (buffer != null) {
						if (buffer.position() > start) {
							limit = buffer.position();

							if (limit >= end) {
								limit -= deletedLen;
							} else {
								limit = start;
							}

							buffer.position(limit);
						}

						limit = buffer.limit();
						limit -= deletedLen;
						buffer.limit(limit);
					}

					return;
				}
			} else if (end == start) {
				// Nothing to do.
				return;
			}
		}

		throw new IndexOutOfBoundsException("start: " + start + " end: " + end);
	}

	public static final void replaceAll(final MutableOctet octet, final byte oldByte, final byte newByte) {
		int end = octet.end();

		final byte[] array = octet.array();
		if (array != null) {
			end += octet.arrayOffset();
			for (int i = octet.begin() + octet.arrayOffset(); i < end; ++i) {
				if (oldByte == array[i]) {
					array[i] = newByte;
				}
			}
		} else {
			final ByteBuffer buf = octet.buffer();
			if (buf != null) {
				for (int i = octet.begin(); i < end; ++i) {
					if (oldByte == buf.get(i)) {
						buf.put(i, newByte);
					}
				}
			}
		}
	}


	static final class OutputOp extends OctetOutputOp {

		private final AbstractMutableOctet mutableOctet;


		OutputOp(final AbstractMutableOctet mutableOctet, final MutableOctet octet) {
			super(octet, false);
			this.mutableOctet = mutableOctet;
			init(octet);
		}


		@Override
		protected final void init(final MutableOctet octet) {
			final ByteBuffer buf = octet.buffer();
			if (buf != null) {
				buf.clear().position(octet.end());
			}
			super.init(octet);
		}

		@Override
		protected final ByteBuffer expandLength(final ByteBuffer buffer, final int requiredLength) {
			return buffer;
		}

		@Override
		protected final void updateBounds(final int newBegin, final int newEnd) {
			this.mutableOctet.begin = newBegin;
			this.mutableOctet.length = newEnd - newBegin;
		}

	}

}
