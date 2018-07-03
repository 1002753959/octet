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

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public class OctetUtils {

	public static final String CRLF = System.getProperty("line.separator");


	private OctetUtils() {
	}


	public static final void hexFormat(final byte[] array, final int offset, final int length,
			final Appendable appendable) {
		if (offset >= 0 && length > 0 && (offset + length) <= array.length) {
			try {
				doFormat(array, offset, offset + length, appendable);
			} catch (IOException ignore) {
			}
		} else if (length == 0) {
		} else {
			throw new IndexOutOfBoundsException("offset: " + offset + " length: " + length);
		}
	}

	public static final String hexFormat(final byte[] array, final int offset, final int length) {
		final StringBuilder builder = new StringBuilder();
		hexFormat(array, offset, length, builder);
		return builder.toString();
	}

	public static final void hexFormat(final byte[] array, final Appendable appendable) {
		hexFormat(array, 0, array.length, appendable);
	}

	public static final String hexFormat(final byte[] array) {
		return hexFormat(array, 0, array.length);
	}

	public static final void hexFormat(final ByteBuffer buffer, int offset, final int length,
			final Appendable appendable) {
		if (offset >= 0 && length > 0 && (offset + length) <= buffer.capacity()) {
			try {
				if (buffer.hasArray()) {
					final byte[] array = buffer.array();
					offset += buffer.arrayOffset();
					doFormat(array, offset, offset + length, appendable);
				} else {
					doFormat(buffer, offset, offset + length, appendable);
				}
			} catch (IOException ignore) {
			}
		} else if (length == 0) {
		} else {
			throw new IndexOutOfBoundsException("offset: " + offset + " length: " + length);
		}
	}

	public static final String hexFormat(final ByteBuffer buffer, final int offset, final int length) {
		final StringBuilder builder = new StringBuilder();
		hexFormat(buffer, offset, length, builder);
		return builder.toString();
	}

	public static final void hexFormat(final ByteBuffer buffer, final Appendable appendable) {
		hexFormat(buffer, buffer.position(), buffer.limit(), appendable);
	}

	public static final String hexFormat(final ByteBuffer buffer) {
		return hexFormat(buffer, buffer.position(), buffer.limit());
	}

	public static final void hexFormat(final Octet octet, int offset, final int length,
			final Appendable appendable) {
		if (offset >= octet.begin() && length > 0 && (offset + length) <= octet.end()) {
			try {
				final byte[] array = octet.array();
				if (array != null) {
					offset += octet.arrayOffset();
					doFormat(array, offset, offset + length, appendable);
				} else {
					final ByteBuffer buffer = octet.buffer();
					if (buffer != null) {
						doFormat(buffer, offset, offset + length, appendable);
					}
				}
			} catch (IOException ignore) {
			}
		} else if (length == 0) {
		} else {
			throw new IndexOutOfBoundsException("offset: " + offset + " length: " + length);
		}
	}

	public static final String hexFormat(final Octet octet, final int offset, final int length) {
		final StringBuilder builder = new StringBuilder();
		hexFormat(octet, offset, length, builder);
		return builder.toString();
	}

	public static final void hexFormat(final Octet octet, final Appendable appendable) {
		hexFormat(octet, octet.begin(), octet.length(), appendable);
	}

	public static final String hexFormat(final Octet octet) {
		return hexFormat(octet, octet.begin(), octet.length());
	}


	private static final char HEX_MAP[] = {
    	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    	'A', 'B', 'C', 'D', 'E', 'F' };

	private static final void doFormat(final byte[] array, int iter, final int end,
			final Appendable appendable) throws IOException {

		appendable.append(CRLF);

		while (iter < end) {
			int maxCols = end - iter;
			if (maxCols > 16) {
				maxCols = 16;
			}

			int i = 0;
			for (; i < maxCols; ++i) {
				if (i > 0) {
					appendable.append(' ');

					if (i == 8) {
						appendable.append(' ');
					}
				}

				final byte b = array[i + iter];

				appendable.append(HEX_MAP[(b >>> 4) & 0xF]);
				appendable.append(HEX_MAP[b & 0xF]);
			}

			if (i <= 8) {
				appendable.append(' ');
			}
			for (; i < 16; ++i) {
				appendable.append("   ");
			}

			appendable.append("  ");

			for (i = 0; i < maxCols; ++i) {
				final byte b = array[i + iter];

				final char ch = (b > 31 && b < 127) ? ((char)b) : ((char)'.');

				appendable.append(ch);
			}

			appendable.append(CRLF);

			iter += maxCols;
		}
	}

	private static final void doFormat(final ByteBuffer buffer, int iter, final int end,
			final Appendable appendable) throws IOException {

		appendable.append(CRLF);

		while (iter < end) {
			int maxCols = end - iter;
			if (maxCols > 16) {
				maxCols = 16;
			}

			int i = 0;
			for (; i < maxCols; ++i) {
				if (i > 0) {
					appendable.append(' ');

					if (i == 8) {
						appendable.append(' ');
					}
				}

				final byte b = buffer.get(i + iter);

				appendable.append(HEX_MAP[(b >>> 4) & 0xF]);
				appendable.append(HEX_MAP[b & 0xF]);
			}

			if (i <= 8) {
				appendable.append(' ');
			}
			for (; i < 16; ++i) {
				appendable.append("   ");
			}

			appendable.append("  ");

			for (i = 0; i < maxCols; ++i) {
				final byte b = buffer.get(i + iter);

				final char ch = (b > 31 && b < 127) ? ((char)b) : ((char)'.');

				appendable.append(ch);
			}

			appendable.append(CRLF);

			iter += maxCols;
		}
	}

}
