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
public class OctetMatchUtils {

	private static final int ENABLE_SHIFT_TABLE = 0;


	protected OctetMatchUtils() {
	}


	@SuppressWarnings("unused")
	public static boolean shouldShiftTable(final int srcLength, final int objLength) {
		if (ENABLE_SHIFT_TABLE > 0) {
			return true;
		} else
		if (ENABLE_SHIFT_TABLE < 0) {
			return false;
		}
		return (objLength > 4 && (srcLength * objLength) > 256);
	}

	public static int[] newShiftTable() {
		return new int[256];
	}

    // -------------------------------------------------------------------------

	private static void initShiftTable(int[] shiftTable,
			final byte[] bytes, final int offset, final int length) {
		for (int i = 0; i < 256; ++i) {
			shiftTable[i] = length + 1;
		}
		final int end = offset + length;
		for (int i = offset; i < end; ++i) {
			int pos = 0xFF & bytes[i];
			shiftTable[pos] = end - i;
		}
	}

	public static int find(final Octet src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		if (objLength > 0 && objLength <= srcLength) {

			if (shiftTable != null) {
				initShiftTable(shiftTable, obj, objOffset, objLength);
			}

			final byte[] array = src.array();
			if (array != null) {
				final int index = doFind(array, srcOffset + src.arrayOffset(), srcLength,
						obj, objOffset, objLength, shiftTable);
				if (index >= 0) {
					return index - src.arrayOffset();
				}
			} else {
				final ByteBuffer buffer = src.buffer();
				if (buffer != null) {
					return doFind(buffer, srcOffset, srcLength,
							obj, objOffset, objLength, shiftTable);
				}
			}
		}

		return -1;
	}

	private static int doFind(final ByteBuffer src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset;
		final int srcEnd = srcOffset + srcLength;
		final int objEnd = objOffset + objLength;
		while ((ptr + objLength) <= srcEnd) {
			int i = objOffset;
			for (int j = ptr; i < objEnd; ++i, ++j) {
				if (obj[i] != src.get(j)) {
					if ((ptr + objLength) < srcEnd) {
						if (shiftTable != null) {
							int pos = 0xFF & src.get(ptr + objLength);
							ptr += shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src.get(ptr + objLength);
							for (int k = objOffset; k < objEnd; ++k) {
								if (val == obj[k]) {
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

	private static int doFind(final byte[] src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset;
		final int srcEnd = srcOffset + srcLength;
		final int objEnd = objOffset + objLength;
		while ((ptr + objLength) <= srcEnd) {
			int i = objOffset;
			for (int j = ptr; i < objEnd; ++i, ++j) {
				if (obj[i] != src[j]) {
					if ((ptr + objLength) < srcEnd) {
						if (shiftTable != null) {
							int pos = 0xFF & src[ptr + objLength];
							ptr += shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src[ptr + objLength];
							for (int k = objOffset; k < objEnd; ++k) {
								if (val == obj[k]) {
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

	private static void initShiftTable(int[] shiftTable,
			final Octet octet, final int offset, final int length) {

		final byte[] array = octet.array();
		if (array != null) {
			initShiftTable(shiftTable, array, offset + octet.arrayOffset(), length);
		} else {
			final ByteBuffer buffer = octet.buffer();
			if (buffer != null) {
				initShiftTable(shiftTable, buffer, offset, length);
			}
		}
	}

	private static void initShiftTable(int[] shiftTable,
			final ByteBuffer buffer, final int offset, final int length) {

		for (int i = 0; i < 256; ++i) {
			shiftTable[i] = length + 1;
		}
		final int end = offset + length;
		for (int i = offset; i < end; ++i) {
			int pos = 0xFF & buffer.get(i);
			shiftTable[pos] = end - i;
		}
	}

	public static int find(final Octet src, final int srcOffset, final int srcLength,
			final Octet obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		if (objLength > 0 && objLength <= srcLength) {
			if (shiftTable != null) {
				initShiftTable(shiftTable, obj, objOffset, objLength);
			}

			final byte[] array = src.array();
			if (array != null) {
				final int index = doFind(array, srcOffset + src.arrayOffset(), srcLength,
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
					return doFind(buffer, srcOffset, srcLength,
							objArray, objOffset + obj.arrayOffset(), objLength, shiftTable);
				} else {
					final ByteBuffer objBuffer = obj.buffer();
					if (objBuffer != null) {
						return doFind(buffer, srcOffset, srcLength,
								objBuffer, objOffset, objLength, shiftTable);
					}
				}
			}
		}

		return -1;
	}

	private static int doFind(final byte[] src, final int srcOffset, final int srcLength,
			final Octet obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		final byte[] array = obj.array();
		if (array != null) {
			return doFind(src, srcOffset, srcLength,
					array, objOffset + obj.arrayOffset(), objLength, shiftTable);
		} else {
			final ByteBuffer buffer = obj.buffer();
			if (buffer != null) {
				return doFind(src, srcOffset, srcLength,
						buffer, objOffset, objLength, shiftTable);
			}
		}

		return -1;
	}

	private static int doFind(final byte[] src, final int srcOffset, final int srcLength,
			final ByteBuffer obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset;
		final int srcEnd = srcOffset + srcLength;
		final int objEnd = objOffset + objLength;
		while ((ptr + objLength) <= srcEnd) {
			int i = objOffset;
			for (int j = ptr; i < objEnd; ++i, ++j) {
				if (obj.get(i) != src[j]) {
					if ((ptr + objLength) < srcEnd) {
						if (shiftTable != null) {
							int pos = 0xFF & src[ptr + objLength];
							ptr += shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src[ptr + objLength];
							for (int k = objOffset; k < objEnd; ++k) {
								if (val == obj.get(k)) {
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

	private static int doFind(final ByteBuffer src, final int srcOffset, final int srcLength,
			final ByteBuffer obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset;
		final int srcEnd = srcOffset + srcLength;
		final int objEnd = objOffset + objLength;
		while ((ptr + objLength) <= srcEnd) {
			int i = objOffset;
			for (int j = ptr; i < objEnd; ++i, ++j) {
				if (obj.get(i) != src.get(j)) {
					if ((ptr + objLength) < srcEnd) {
						if (shiftTable != null) {
							int pos = 0xFF & src.get(ptr + objLength);
							ptr += shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src.get(ptr + objLength);
							for (int k = objOffset; k < objEnd; ++k) {
								if (val == obj.get(k)) {
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

	private static void initReverseShiftTable(int[] shiftTable,
			final byte[] bytes, final int offset, final int length) {
		for (int i = 0; i < 256; ++i) {
			shiftTable[i] = length + 1;
		}
		for (int i = offset + length - 1; i >= offset; --i) {
			int pos = 0xFF & bytes[i];
			shiftTable[pos] = i - offset + 1;
		}
	}

	public static int reverseFind(final Octet src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		if (objLength <= srcLength) {
			if (shiftTable != null) {
				initReverseShiftTable(shiftTable, obj, objOffset, objLength);
			}

			final byte[] array = src.array();
			if (array != null) {
				final int index = doReverseFind(array, srcOffset + src.arrayOffset(), srcLength,
						obj, objOffset, objLength, shiftTable);
				if (index >= 0) {
					return index - src.arrayOffset();
				}
			} else {
				final ByteBuffer buffer = src.buffer();
				if (buffer != null) {
					return doReverseFind(buffer, srcOffset, srcLength,
							obj, objOffset, objLength, shiftTable);
				}
			}
		}

		return -1;
	}

	private static int doReverseFind(final ByteBuffer src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset + srcLength - objLength;
		final int objEnd = objOffset + objLength - 1;
		while (ptr >= srcOffset) {
			int i = objEnd;
			for (int j = ptr + objLength - 1; i >= objOffset; --i, --j) {
				if (obj[i] != src.get(j)) {
					if (ptr > srcOffset) {
						if (shiftTable != null) {
							int pos = 0xFF & src.get(ptr - 1);
							ptr -= shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src.get(ptr - 1);
							for (int k = objOffset + objLength - 1; k >= objOffset; --k) {
								if (val == obj[k]) {
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

	private static int doReverseFind(final byte[] src, final int srcOffset, final int srcLength,
			final byte[] obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset + srcLength - objLength;
		final int objEnd = objOffset + objLength - 1;
		while (ptr >= srcOffset) {
			int i = objEnd;
			for (int j = ptr + objLength - 1; i >= objOffset; --i, --j) {
				if (obj[i] != src[j]) {
					if (ptr > srcOffset) {
						if (shiftTable != null) {
							int pos = 0xFF & src[ptr - 1];
							ptr -= shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src[ptr - 1];
							for (int k = objOffset + objLength - 1; k >= objOffset; --k) {
								if (val == obj[k]) {
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

	private static void initReverseShiftTable(int[] shiftTable,
			final Octet octet, final int offset, final int length) {

		final byte[] array = octet.array();
		if (array != null) {
			initReverseShiftTable(shiftTable, array, offset + octet.arrayOffset(), length);
		} else {
			final ByteBuffer buffer = octet.buffer();
			if (buffer != null) {
				initReverseShiftTable(shiftTable, buffer, offset, length);
			}
		}
	}

	private static void initReverseShiftTable(int[] shiftTable,
			final ByteBuffer buffer, final int offset, final int length) {

		for (int i = 0; i < 256; ++i) {
			shiftTable[i] = length + 1;
		}
		for (int i = offset + length - 1; i >= offset; --i) {
			int pos = 0xFF & buffer.get(i);
			shiftTable[pos] = i - offset + 1;
		}
	}

	public static int reverseFind(final Octet src, final int srcOffset, final int srcLength,
			final Octet obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		if (objLength <= srcLength) {
			if (shiftTable != null) {
				initReverseShiftTable(shiftTable, obj, objOffset, objLength);
			}

			final byte[] array = src.array();
			if (array != null) {
				final int index = doReverseFind(array, srcOffset + src.arrayOffset(), srcLength,
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
					return doReverseFind(buffer, srcOffset, srcLength,
							objArray, objOffset + obj.arrayOffset(), objLength, shiftTable);
				} else {
					final ByteBuffer objBuffer = obj.buffer();
					if (objBuffer != null) {
						return doReverseFind(buffer, srcOffset, srcLength,
								objBuffer, objOffset, objLength, shiftTable);
					}
				}
			}
		}

		return -1;
	}

	private static int doReverseFind(final byte[] src, final int srcOffset, final int srcLength,
			final Octet obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		final byte[] array = obj.array();
		if (array != null) {
			return doReverseFind(src, srcOffset, srcLength,
					array, objOffset + obj.arrayOffset(), objLength, shiftTable);
		} else {
			final ByteBuffer buffer = obj.buffer();
			if (buffer != null) {
				return doReverseFind(src, srcOffset, srcLength,
						buffer, objOffset, objLength, shiftTable);
			}
		}

		return -1;
	}

	private static int doReverseFind(final byte[] src, final int srcOffset, final int srcLength,
			final ByteBuffer obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset + srcLength - objLength;
		final int objEnd = objOffset + objLength - 1;
		while (ptr >= srcOffset) {
			int i = objEnd;
			for (int j = ptr + objLength - 1; i >= objOffset; --i, --j) {
				if (obj.get(i) != src[j]) {
					if (ptr > srcOffset) {
						if (shiftTable != null) {
							int pos = 0xFF & src[ptr - 1];
							ptr -= shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src[ptr - 1];
							for (int k = objOffset + objLength - 1; k >= objOffset; --k) {
								if (val == obj.get(k)) {
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

	private static int doReverseFind(final ByteBuffer src, final int srcOffset, final int srcLength,
			final ByteBuffer obj, final int objOffset, final int objLength,
			int[] shiftTable) {

		int ptr = srcOffset + srcLength - objLength;
		final int objEnd = objOffset + objLength - 1;
		while (ptr >= srcOffset) {
			int i = objEnd;
			for (int j = ptr + objLength - 1; i >= objOffset; --i, --j) {
				if (obj.get(i) != src.get(j)) {
					if (ptr > srcOffset) {
						if (shiftTable != null) {
							int pos = 0xFF & src.get(ptr - 1);
							ptr -= shiftTable[pos];
						} else {
							int shift = objLength + 1;
							byte val = src.get(ptr - 1);
							for (int k = objOffset + objLength - 1; k >= objOffset; --k) {
								if (val == obj.get(k)) {
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
