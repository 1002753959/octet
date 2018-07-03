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
package com.chinmobi.text;

import java.nio.ByteBuffer;

import com.chinmobi.octet.Octet;
import com.chinmobi.octet.OctetMatchUtils;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class TextMatchUtils extends OctetMatchUtils {

	private TextMatchUtils() {
		super();
	}

	// -------------------------------------------------------------------------

	private static void initIgnoreCaseShiftTable(int[] shiftTable,
			final byte[] bytes, final int offset, final int length) {
		for (int i = 0; i < shiftTable.length; ++i) {
			shiftTable[i] = length + 1;
		}
		final int end = offset + length;
		for (int i = offset; i < end; ++i) {
			int pos = 0xFF & bytes[i];
			shiftTable[pos] = end - i;

			if (pos <= 0x7A && pos >= 0x41) {
				if (pos >= 0x61) shiftTable[pos - 32] = end - i;
				else
				if (pos <= 0x5A) shiftTable[pos + 32] = end - i;
			}
		}
	}

	private static boolean equalsIgnoreCase(final byte obj, final byte src) {
		if (obj == src) {
			return true;
		} else
		if (obj <= 0x7A && obj >= 0x41) {
			if (obj >= 0x61) return (obj - 32) == src;
			if (obj <= 0x5A) return (obj + 32) == src;
		}

		return false;
	}

	public static int findIgnoreCase(final Octet src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		if (objLength > 0 && objLength <= srcLength) {
			if (shiftTable != null) {
				initIgnoreCaseShiftTable(shiftTable, obj, objOffset, objLength);
			}

			final byte[] array = src.array();
			if (array != null) {
				final int index = doFindIgnoreCase(array, srcOffset + src.arrayOffset(), srcLength,
						obj, objOffset, objLength, shiftTable);
				if (index >= 0) {
					return index - src.arrayOffset();
				}
			} else {
				final ByteBuffer buffer = src.buffer();
				if (buffer != null) {
					return doFindIgnoreCase(buffer, srcOffset, srcLength,
							obj, objOffset, objLength, shiftTable);
				}
			}
		}

		return -1;
	}

	private static int doFindIgnoreCase(final ByteBuffer src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset;
		final int srcEnd = srcOffset + srcLength;
		final int objEnd = objOffset + objLength;
		while ((ptr + objLength) <= srcEnd) {
			int i = objOffset;
			for (int j = ptr; i < objEnd; ++i, ++j) {
				if (!equalsIgnoreCase(obj[i], src.get(j))) {
					if ((ptr + objLength) < srcEnd) {
						if (shiftTable != null) {
							int pos = 0xFF & src.get(ptr + objLength);
							ptr += shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src.get(ptr + objLength);
							for (int k = objOffset; k < objEnd; ++k) {
								if (equalsIgnoreCase(val, obj[k])) {
									shift = objEnd - k;
								}
							}
							ptr += shift;
						}
						break;
					} else {
						return -1;
					}
				}
			}
			if (i >= objEnd) {
				return ptr;
			}
		}

		return -1;
	}

	private static int doFindIgnoreCase(final byte[] src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset;
		final int srcEnd = srcOffset + srcLength;
		final int objEnd = objOffset + objLength;
		while ((ptr + objLength) <= srcEnd) {
			int i = objOffset;
			for (int j = ptr; i < objEnd; ++i, ++j) {
				if (!equalsIgnoreCase(obj[i], src[j])) {
					if ((ptr + objLength) < srcEnd) {
						if (shiftTable != null) {
							int pos = 0xFF & src[ptr + objLength];
							ptr += shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src[ptr + objLength];
							for (int k = objOffset; k < objEnd; ++k) {
								if (equalsIgnoreCase(val, obj[k])) {
									shift = objEnd - k;
								}
							}
							ptr += shift;
						}
						break;
					} else {
						return -1;
					}
				}
			}
			if (i >= objEnd) {
				return ptr;
			}
		}

		return -1;
	}

    // -----------------------------------------------------

	private static void initIgnoreCaseShiftTable(int[] shiftTable,
			final Octet octet, final int offset, final int length) {

		final byte[] array = octet.array();
		if (array != null) {
			initIgnoreCaseShiftTable(shiftTable, array, offset + octet.arrayOffset(), length);
		} else {
			final ByteBuffer buffer = octet.buffer();
			if (buffer != null) {
				initIgnoreCaseShiftTable(shiftTable, buffer, offset, length);
			}
		}
	}

	private static void initIgnoreCaseShiftTable(int[] shiftTable,
			final ByteBuffer buffer, final int offset, final int length) {

		for (int i = 0; i < shiftTable.length; ++i) {
			shiftTable[i] = length + 1;
		}
		final int end = offset + length;
		for (int i = offset; i < end; ++i) {
			int pos = 0xFF & buffer.get(i);
			shiftTable[pos] = end - i;

			if (pos <= 0x7A && pos >= 0x41) {
				if (pos >= 0x61) shiftTable[pos - 32] = end - i;
				else
				if (pos <= 0x5A) shiftTable[pos + 32] = end - i;
			}
		}
	}

	public static int findIgnoreCase(final Octet src, final int srcOffset, final int srcLength,
			final Octet obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		if (objLength > 0 && objLength <= srcLength) {
			if (shiftTable != null) {
				initIgnoreCaseShiftTable(shiftTable, obj, objOffset, objLength);
			}

			final byte[] array = src.array();
			if (array != null) {
				final int index = doFindIgnoreCase(array, srcOffset + src.arrayOffset(), srcLength,
						obj, objOffset, objLength, shiftTable);
				if (index >= 0) {
					return index - src.arrayOffset();
				}
				return index;
			}

			final ByteBuffer buffer = src.buffer();
			if (buffer != null) {

				final byte[] objArray = obj.array();
				if (objArray != null) {
					return doFindIgnoreCase(buffer, srcOffset, srcLength,
							objArray, objOffset + obj.arrayOffset(), objLength, shiftTable);
				} else {
					final ByteBuffer objBuffer = obj.buffer();
					if (objBuffer != null) {
						return doFindIgnoreCase(buffer, srcOffset, srcLength,
								objBuffer, objOffset, objLength, shiftTable);
					}
				}
			}
		}

		return -1;
	}

	private static int doFindIgnoreCase(final byte[] src, final int srcOffset, final int srcLength,
			final Octet obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		final byte[] array = obj.array();
		if (array != null) {
			return doFindIgnoreCase(src, srcOffset, srcLength,
					array, objOffset + obj.arrayOffset(), objLength, shiftTable);
		} else {
			final ByteBuffer buffer = obj.buffer();
			if (buffer != null) {
				return doFindIgnoreCase(src, srcOffset, srcLength,
						buffer, objOffset, objLength, shiftTable);
			}
		}

		return -1;
	}

	private static int doFindIgnoreCase(final byte[] src, final int srcOffset, final int srcLength,
			final ByteBuffer obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset;
		final int srcEnd = srcOffset + srcLength;
		final int objEnd = objOffset + objLength;
		while ((ptr + objLength) <= srcEnd) {
			int i = objOffset;
			for (int j = ptr; i < objEnd; ++i, ++j) {
				if (!equalsIgnoreCase(obj.get(i), src[j])) {
					if ((ptr + objLength) < srcEnd) {
						if (shiftTable != null) {
							int pos = 0xFF & src[ptr + objLength];
							ptr += shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src[ptr + objLength];
							for (int k = objOffset; k < objEnd; ++k) {
								if (equalsIgnoreCase(val, obj.get(k))) {
									shift = objEnd - k;
								}
							}
							ptr += shift;
						}
						break;
					} else {
						return -1;
					}
				}
			}
			if (i >= objEnd) {
				return ptr;
			}
		}

		return -1;
	}

	private static int doFindIgnoreCase(final ByteBuffer src, final int srcOffset, final int srcLength,
			final ByteBuffer obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset;
		final int srcEnd = srcOffset + srcLength;
		final int objEnd = objOffset + objLength;
		while ((ptr + objLength) <= srcEnd) {
			int i = objOffset;
			for (int j = ptr; i < objEnd; ++i, ++j) {
				if (!equalsIgnoreCase(obj.get(i), src.get(j))) {
					if ((ptr + objLength) < srcEnd) {
						if (shiftTable != null) {
							int pos = 0xFF & src.get(ptr + objLength);
							ptr += shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src.get(ptr + objLength);
							for (int k = objOffset; k < objEnd; ++k) {
								if (equalsIgnoreCase(val, obj.get(k))) {
									shift = objEnd - k;
								}
							}
							ptr += shift;
						}
						break;
					} else {
						return -1;
					}
				}
			}
			if (i >= objEnd) {
				return ptr;
			}
		}

		return -1;
	}

	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------

	private static void initReverseIgnoreCaseShiftTable(int[] shiftTable,
			final byte[] bytes, final int offset, final int length) {
		for (int i = 0; i < shiftTable.length; ++i) {
			shiftTable[i] = length + 1;
		}
		for (int i = offset + length - 1; i >= offset; --i) {
			int pos = 0xFF & bytes[i];
			shiftTable[pos] = i - offset + 1;

			if (pos <= 0x7A && pos >= 0x41) {
				if (pos >= 0x61) shiftTable[pos - 32] = i - offset + 1;
				else
				if (pos <= 0x5A) shiftTable[pos + 32] = i - offset + 1;
			}
		}
	}

	public static int reverseFindIgnoreCase(final Octet src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		if (objLength <= srcLength) {
			if (shiftTable != null) {
				initReverseIgnoreCaseShiftTable(shiftTable, obj, objOffset, objLength);
			}

			final byte[] array = src.array();
			if (array != null) {
				final int index = doReverseFindIgnoreCase(array, srcOffset + src.arrayOffset(), srcLength,
						obj, objOffset, objLength, shiftTable);
				if (index >= 0) {
					return index - src.arrayOffset();
				}
			} else {
				final ByteBuffer buffer = src.buffer();
				if (buffer != null) {
					return doReverseFindIgnoreCase(buffer, srcOffset, srcLength,
							obj, objOffset, objLength, shiftTable);
				}
			}
		}

		return -1;
	}

	private static int doReverseFindIgnoreCase(final ByteBuffer src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset + srcLength - objLength;
		final int objEnd = objOffset + objLength - 1;
		while (ptr >= srcOffset) {
			int i = objEnd;
			for (int j = ptr + objLength - 1; i >= objOffset; --i, --j) {
				if (!equalsIgnoreCase(obj[i], src.get(j))) {
					if (ptr > srcOffset) {
						if (shiftTable != null) {
							int pos = 0xFF & src.get(ptr - 1);
							ptr -= shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src.get(ptr - 1);
							for (int k = objOffset + objLength - 1; k >= objOffset; --k) {
								if (equalsIgnoreCase(val, obj[k])) {
									shift = k - objOffset + 1;
								}
							}
							ptr -= shift;
						}
						break;
					} else {
						return -1;
					}
				}
			}
			if (i < objOffset) {
				return ptr;
			}
		}

		return -1;
	}

	private static int doReverseFindIgnoreCase(final byte[] src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset + srcLength - objLength;
		final int objEnd = objOffset + objLength - 1;
		while (ptr >= srcOffset) {
			int i = objEnd;
			for (int j = ptr + objLength - 1; i >= objOffset; --i, --j) {
				if (!equalsIgnoreCase(obj[i], src[j])) {
					if (ptr > srcOffset) {
						if (shiftTable != null) {
							int pos = 0xFF & src[ptr - 1];
							ptr -= shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src[ptr - 1];
							for (int k = objOffset + objLength - 1; k >= objOffset; --k) {
								if (equalsIgnoreCase(val, obj[k])) {
									shift = k - objOffset + 1;
								}
							}
							ptr -= shift;
						}
						break;
					} else {
						return -1;
					}
				}
			}
			if (i < objOffset) {
				return ptr;
			}
		}

		return -1;
	}

    // -----------------------------------------------------

	private static void initReverseIgnoreCaseShiftTable(int[] shiftTable,
			final Octet octet, final int offset, final int length) {

		final byte[] array = octet.array();
		if (array != null) {
			initReverseIgnoreCaseShiftTable(shiftTable, array, offset + octet.arrayOffset(), length);
		} else {
			final ByteBuffer buffer = octet.buffer();
			if (buffer != null) {
				initReverseIgnoreCaseShiftTable(shiftTable, buffer, offset, length);
			}
		}
	}

	private static void initReverseIgnoreCaseShiftTable(int[] shiftTable,
			final ByteBuffer buffer, final int offset, final int length) {

		for (int i = 0; i < shiftTable.length; ++i) {
			shiftTable[i] = length + 1;
		}
		for (int i = offset + length - 1; i >= offset; --i) {
			int pos = 0xFF & buffer.get(i);
			shiftTable[pos] = i - offset + 1;

			if (pos <= 0x7A && pos >= 0x41) {
				if (pos >= 0x61) shiftTable[pos - 32] = i - offset + 1;
				else
				if (pos <= 0x5A) shiftTable[pos + 32] = i - offset + 1;
			}
		}
	}

	public static int reverseFindIgnoreCase(final Octet src, final int srcOffset, final int srcLength,
			final Octet obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		if (objLength <= srcLength) {
			if (shiftTable != null) {
				initReverseIgnoreCaseShiftTable(shiftTable, obj, objOffset, objLength);
			}

			final byte[] array = src.array();
			if (array != null) {
				final int index = doReverseFindIgnoreCase(array, srcOffset + src.arrayOffset(), srcLength,
						obj, objOffset, objLength, shiftTable);
				if (index >= 0) {
					return index - src.arrayOffset();
				}
				return index;
			}

			final ByteBuffer buffer = src.buffer();
			if (buffer != null) {

				final byte[] objArray = obj.array();
				if (objArray != null) {
					return doReverseFindIgnoreCase(buffer, srcOffset, srcLength,
							objArray, objOffset + obj.arrayOffset(), objLength, shiftTable);
				} else {
					final ByteBuffer objBuffer = obj.buffer();
					if (objBuffer != null) {
						return doReverseFindIgnoreCase(buffer, srcOffset, srcLength,
								objBuffer, objOffset, objLength, shiftTable);
					}
				}
			}
		}

		return -1;
	}

	private static int doReverseFindIgnoreCase(final byte[] src, final int srcOffset, final int srcLength,
			final Octet obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		final byte[] array = obj.array();
		if (array != null) {
			return doReverseFindIgnoreCase(src, srcOffset, srcLength,
					array, objOffset + obj.arrayOffset(), objLength, shiftTable);
		} else {
			final ByteBuffer buffer = obj.buffer();
			if (buffer != null) {
				return doReverseFindIgnoreCase(src, srcOffset, srcLength,
						buffer, objOffset, objLength, shiftTable);
			}
		}

		return -1;
	}

	private static int doReverseFindIgnoreCase(final byte[] src, final int srcOffset, final int srcLength,
			final ByteBuffer obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset + srcLength - objLength;
		final int objEnd = objOffset + objLength - 1;
		while (ptr >= srcOffset) {
			int i = objEnd;
			for (int j = ptr + objLength - 1; i >= objOffset; --i, --j) {
				if (!equalsIgnoreCase(obj.get(i), src[j])) {
					if (ptr > srcOffset) {
						if (shiftTable != null) {
							int pos = 0xFF & src[ptr - 1];
							ptr -= shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src[ptr - 1];
							for (int k = objOffset + objLength - 1; k >= objOffset; --k) {
								if (equalsIgnoreCase(val, obj.get(k))) {
									shift = k - objOffset + 1;
								}
							}
							ptr -= shift;
						}
						break;
					} else {
						return -1;
					}
				}
			}
			if (i < objOffset) {
				return ptr;
			}
		}

		return -1;
	}

	private static int doReverseFindIgnoreCase(final ByteBuffer src, final int srcOffset, final int srcLength,
			final ByteBuffer obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset + srcLength - objLength;
		final int objEnd = objOffset + objLength - 1;
		while (ptr >= srcOffset) {
			int i = objEnd;
			for (int j = ptr + objLength - 1; i >= objOffset; --i, --j) {
				if (!equalsIgnoreCase(obj.get(i), src.get(j))) {
					if (ptr > srcOffset) {
						if (shiftTable != null) {
							int pos = 0xFF & src.get(ptr - 1);
							ptr -= shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src.get(ptr - 1);
							for (int k = objOffset + objLength - 1; k >= objOffset; --k) {
								if (equalsIgnoreCase(val, obj.get(k))) {
									shift = k - objOffset + 1;
								}
							}
							ptr -= shift;
						}
						break;
					} else {
						return -1;
					}
				}
			}
			if (i < objOffset) {
				return ptr;
			}
		}

		return -1;
	}

	// -------------------------------------------------------------------------

}
