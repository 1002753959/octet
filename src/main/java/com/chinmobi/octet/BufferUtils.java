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

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class BufferUtils {

	private BufferUtils() {
	}


	/*
	 * expand method
	 */

	public static final ByteBuffer expand(final ByteBuffer buffer,
			final int offset, final int oldLength, final int newLength,
			final BufferAllocator allocator) {

		final int diff = (oldLength >= newLength) ? (oldLength - newLength) : (newLength - oldLength);
		if (diff == 0) {
			return buffer;
		}

		final int position = buffer.position();

		if (oldLength > newLength) {
			move(buffer, offset + oldLength, offset + newLength, position - offset - oldLength);
			buffer.position(position - diff);
		} else
		if ((position + diff) <= buffer.capacity()) {
			move(buffer, offset + oldLength, offset + newLength, position - offset - oldLength);
			buffer.position(position + diff);
		} else {
			ByteBuffer newBuffer = allocator.allocate(buffer, position + diff);

			if (offset > 0) {
				buffer.position(0).limit(offset);
				newBuffer.put(buffer);
			}

			newBuffer.position(offset + newLength);

			if (position > (offset + oldLength)) {
				buffer.limit(position).position(offset + oldLength);
				newBuffer.put(buffer);
			}

			return newBuffer;
		}

		return buffer;
	}

	/*
	 * move method
	 */

	public static final void move(final ByteBuffer buffer,
			final int srcPos, final int destPos, final int length) {
		if (length <= 0 || srcPos == destPos) {
			return;
		}

		if (buffer.hasArray()) {
			final byte[] array = buffer.array();
			System.arraycopy(array, srcPos + buffer.arrayOffset(),
					array, destPos + buffer.arrayOffset(), length);
		} else {
			if (srcPos < destPos) {
				for (int i = srcPos + length - 1, j = destPos + length - 1; i >= srcPos; --i, --j) {
					buffer.put(j, buffer.get(i));
				}
			} else
			if (srcPos > destPos) {
				final int end = srcPos + length;
				for (int i = srcPos, j = destPos; i < end; ++i, ++j) {
					buffer.put(j, buffer.get(i));
				}
			}
		}
	}

	/*
	 * fill method
	 */

	public static final void fill(final ByteBuffer buffer,
			final int offset, final int length, final byte b) {

		if (buffer.hasArray()) {
			final byte[] array = buffer.array();
			final int end = offset + length + buffer.arrayOffset();
			for (int i = offset + buffer.arrayOffset(); i < end; ++i) {
				array[i] = b;
			}
		} else {
			final int end = offset + length;
			for (int i = offset; i < end; ++i) {
				buffer.put(i, b);
			}
		}
	}

	/*
	 * replaceAll method
	 */

	public static final void replaceAll(final ByteBuffer buffer, int start, int end,
			final byte oldByte, final byte newByte) {
		final byte[] array = buffer.array();
		if (array != null) {
			start += buffer.arrayOffset();
			end += buffer.arrayOffset();
			for (int i = start; i < end; ++i) {
				if (oldByte == array[i]) {
					array[i] = newByte;
				}
			}
		} else {
			for (int i = start; i < end; ++i) {
				if (oldByte == buffer.get(i)) {
					buffer.put(i, newByte);
				}
			}
		}
	}

	/*
	 * swap methods
	 */

	public static void swap(final ByteBuffer buffer,
			int srcOffset, int srcLength, int destOffset, int destLength) {

		if (srcOffset > destOffset) {
			int tmp;
			tmp = srcOffset; srcOffset = destOffset; destOffset = tmp;
			tmp = srcLength; srcLength = destLength; destLength = tmp;
		}
		if ((srcOffset == destOffset) || (srcOffset + srcLength) > destOffset) {
			return;
		}

		// Now: (srcOffset < destOffset) && ((srcOffset + srcLength) <= destOffset)

		int length = (srcLength <= destLength) ? srcLength : destLength;
		swap(buffer, srcOffset, destOffset, length);

		srcOffset += length; destOffset += length;

		if (srcLength == destLength) {
			return;
		} else
		if (srcLength < destLength) {
			length = destLength - length;
			if (length > (destOffset - srcOffset)) {
				srcLength = length;
				length = destOffset - srcOffset;
				destOffset += srcLength;
				destLength = 0;
			}
		} else {
			length = srcLength - length;
			if (length > (destOffset - srcOffset - length)) {
				destLength = length;
				length = destOffset - srcOffset - length;
				destOffset -= length;
				srcLength = 0;
			}
		}

		final int tmpPos = buffer.position();
		int tmpLen = buffer.remaining();

		if (tmpLen <= 0) {
			final byte[] tmpArray = new byte[length];

			if (srcLength < destLength) {
				get(buffer, destOffset, tmpArray, 0, length);
				move(buffer, srcOffset, srcOffset + length, destOffset - srcOffset);
				set(buffer, srcOffset, tmpArray, 0, length);
			} else {
				get(buffer, srcOffset, tmpArray, 0, length);
				move(buffer, srcOffset + length, srcOffset, destOffset - srcOffset - length);
				set(buffer, destOffset - length, tmpArray, 0, length);
			}

		} else
		if (srcLength < destLength) {

			for (; length > 0; ) {
				if (tmpLen > length) {
					tmpLen = length;
				}

				move(buffer, destOffset, tmpPos, tmpLen);
				move(buffer, srcOffset, srcOffset + tmpLen, destOffset - srcOffset);
				move(buffer, tmpPos, srcOffset, tmpLen);

				srcOffset += tmpLen; destOffset += tmpLen;
				length -= tmpLen;
			}

		} else {

			srcOffset += length;

			for (; length > 0; ) {
				if (tmpLen > length) {
					tmpLen = length;
				}

				srcOffset -= tmpLen; destOffset -= tmpLen;

				move(buffer, srcOffset, tmpPos, tmpLen);
				move(buffer, srcOffset + tmpLen, srcOffset, destOffset - srcOffset);
				move(buffer, tmpPos, destOffset, tmpLen);

				length -= tmpLen;
			}
		}
	}

	private static void swap(final ByteBuffer buffer, final int srcPos, final int destPos, final int length) {
		if (length <= 0 || srcPos == destPos) {
			return;
		}

		byte tmp = 0;

		if (buffer.hasArray()) {
			final byte[] array = buffer.array();
			int i = srcPos + buffer.arrayOffset();
			int j = destPos + buffer.arrayOffset();
			final int end = i + length;

			for (; i < end; ++i, ++j) {
				tmp = array[i]; array[i] = array[j]; array[j] = tmp;
			}
		} else {
			final int end = srcPos + length;
			for (int i = srcPos, j = destPos; i < end; ++i, ++j) {
				tmp = buffer.get(i);
				buffer.put(i, buffer.get(j));
				buffer.put(j, tmp);
			}
		}
	}

	private static void get(final ByteBuffer buffer, final int index, final byte[] dest, final int offset, final int length) {
		if (buffer.hasArray()) {
			final byte[] array = buffer.array();
			System.arraycopy(array, index + buffer.arrayOffset(), dest, offset, length);
		} else {
			final int oldPos = buffer.position();
			final int oldLimit = buffer.limit();

			buffer.position(0).limit(index + length).position(index);

			buffer.get(dest, offset, length);

			buffer.position(0).limit(oldLimit).position(oldPos);
		}
	}

	private static void set(final ByteBuffer buffer, final int index, final byte[] src, final int offset, final int length) {
		if (buffer.hasArray()) {
			final byte[] array = buffer.array();
			System.arraycopy(src, offset, array, index + buffer.arrayOffset(), length);
		} else {
			final int oldPos = buffer.position();

			buffer.position(index);

			buffer.put(src, offset, length);

			buffer.position(oldPos);
		}
	}

}
