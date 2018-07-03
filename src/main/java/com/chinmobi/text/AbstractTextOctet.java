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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Set;

import com.chinmobi.octet.AbstractOctet;
import com.chinmobi.octet.Octet;
import com.chinmobi.octet.data.OctetData;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public abstract class AbstractTextOctet extends AbstractOctet implements TextOctet {

	/**
	 *
	 */
	private static final long serialVersionUID = -3504273266851912024L;

	protected transient Charset charset;


	protected AbstractTextOctet(final OctetData data) {
		super(data);
	}

	protected AbstractTextOctet(final AbstractTextOctet source) {
		super((AbstractOctet)source);
		this.charset = source.charset;
	}


	protected void set(final AbstractTextOctet source) {
		super.set((AbstractOctet)source);
		this.charset = source.charset;
	}

	protected void swap(final AbstractTextOctet another) {
		super.swap((AbstractOctet)another);

		Charset charsetTmp = this.charset;
		this.charset = another.charset;
		another.charset = charsetTmp;
	}

	/*
	 * Comparable methods
	 */

	public final int compareToIgnoreCase(final TextOctet another) {
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
				if (!equalsIgnoreCase(b1, b2)) {
					return b1 - b2;
				}
			}
		} else if (a1 != null && buf2 != null) {
			i += this.arrayOffset();

			while (n-- != 0) {
				final byte b1 = a1[i++];
				final byte b2 = buf2.get(j++);
				if (!equalsIgnoreCase(b1, b2)) {
					return b1 - b2;
				}
			}
		} else if (a2 != null && buf1 != null) {
			j += another.arrayOffset();

			while (n-- != 0) {
				final byte b1 = buf1.get(i++);
				final byte b2 = a2[j++];
				if (!equalsIgnoreCase(b1, b2)) {
					return b1 - b2;
				}
			}
		} else if (buf1 != null && buf2 != null) {
			while (n-- != 0) {
				final byte b1 = buf1.get(i++);
				final byte b2 = buf2.get(j++);
				if (!equalsIgnoreCase(b1, b2)) {
					return b1 - b2;
				}
			}
		}

		return len1 - len2;
	}

	/*
	 * equals methods
	 */

	public final boolean equalsIgnoreCase(final TextOctet another) {
		if (this == another) {
			return true;
		}
		if (this.length == another.length()) {
			return equalsIgnoreCase(this.begin, this.length, another);
		}
		return false;
	}

	public final boolean equalsIgnoreCase(final byte[] bytes, final int offset, final int length) {
		if (bytes != null && length == this.length) {
			return equalsIgnoreCase(this.begin, this.length, bytes, offset);
		}
		return false;
	}

	public final boolean equalsIgnoreCase(final byte[] bytes) {
		return equalsIgnoreCase(bytes, 0, bytes.length);
	}

	/*
	 * startsWith methods
	 */

	public final boolean startsWith(final boolean ignoreCase, final TextOctet prefix) {
		if (this.length >= prefix.length()) {
			if (ignoreCase) {
				return equalsIgnoreCase(this.begin, prefix.length(), prefix);
			} else {
				return equals(this.begin, prefix.length(), prefix);
			}
		}
		return false;
	}

	public final boolean startsWith(final boolean ignoreCase, final byte[] bytes, final int offset, final int length) {
		if (this.length >= length) {
			if (ignoreCase) {
				return equalsIgnoreCase(this.begin, length, bytes, offset);
			} else {
				return equals(this.begin, length, bytes, offset);
			}
		}
		return false;
	}

	public final boolean startsWith(final boolean ignoreCase, final byte[] bytes) {
		return startsWith(ignoreCase, bytes, 0, bytes.length);
	}

	public final boolean startsWith(final boolean ignoreCase, final int fromIndex, final TextOctet prefix) {
		int start = fromIndex;
		if (start < this.begin) {
			start = this.begin;
		}

		if ((end() - start) >= prefix.length()) {
			if (ignoreCase) {
				return equalsIgnoreCase(start, prefix.length(), prefix);
			} else {
				return equals(start, prefix.length(), prefix);
			}
		}

		return false;
	}

	public final boolean startsWith(final boolean ignoreCase, final int fromIndex, final byte[] bytes, final int offset, final int length) {
		int start = fromIndex;
		if (start < this.begin) {
			start = this.begin;
		}

		if ((end() - start) >= length) {
			if (ignoreCase) {
				return equalsIgnoreCase(start, length, bytes, offset);
			} else {
				return equals(start, length, bytes, offset);
			}
		}
		return false;
	}

	public final boolean startsWith(final boolean ignoreCase, final int fromIndex, final byte[] bytes) {
		return startsWith(ignoreCase, fromIndex, bytes, 0, bytes.length);
	}

	/*
	 * endsWith methods
	 */

	public final boolean endsWith(final boolean ignoreCase, final TextOctet suffix) {
		if (this.length >= suffix.length()) {
			if (ignoreCase) {
				return equalsIgnoreCase((end() - suffix.length()), suffix.length(), suffix);
			} else {
				return equals((end() - suffix.length()), suffix.length(), suffix);
			}
		}
		return false;
	}

	public final boolean endsWith(final boolean ignoreCase, final byte[] bytes, final int offset, final int length) {
		if (this.length >= length) {
			if (ignoreCase) {
				return equalsIgnoreCase((end() - length), length, bytes, offset);
			} else {
				return equals((end() - length), length, bytes, offset);
			}
		}
		return false;
	}

	public final boolean endsWith(final boolean ignoreCase, final byte[] bytes) {
		return endsWith(ignoreCase, bytes, 0, bytes.length);
	}

	/*
	 * indexOf methods
	 */

	public final int indexOf(final boolean ignoreCase, final TextOctet octet) {
		if (this.length >= octet.length() && octet.length() > 0) {
			int[] shiftTable = null;
			if (TextMatchUtils.shouldShiftTable(this.length, octet.length())) {
				shiftTable = TextMatchUtils.newShiftTable();
			}

			if (ignoreCase) {
				return TextMatchUtils.findIgnoreCase(this, this.begin, this.length, octet, octet.begin(), octet.length(), shiftTable);
			} else {
				return TextMatchUtils.find(this, this.begin, this.length, octet, octet.begin(), octet.length(), shiftTable);
			}
		}
		return -1;
	}

	public final int indexOf(final boolean ignoreCase, final byte[] bytes, final int offset, final int length) {
		if (this.length >= length && length > 0) {
			int[] shiftTable = null;
			if (TextMatchUtils.shouldShiftTable(this.length, length)) {
				shiftTable = TextMatchUtils.newShiftTable();
			}

			if (ignoreCase) {
				return TextMatchUtils.findIgnoreCase(this, this.begin, this.length, bytes, offset, length, shiftTable);
			} else {
				return TextMatchUtils.find(this, this.begin, this.length, bytes, offset, length, shiftTable);
			}
		}
		return -1;
	}

	public final int indexOf(final boolean ignoreCase, final byte[] bytes) {
		return indexOf(ignoreCase, bytes, 0, bytes.length);
	}

	public final int indexOf(final boolean ignoreCase, int fromIndex, final TextOctet octet) {
		if (fromIndex < this.begin) {
			fromIndex = this.begin;
		}

		final int srcLength = end() - fromIndex;

		if (srcLength >= octet.length() && octet.length() > 0) {
			int[] shiftTable = null;
			if (TextMatchUtils.shouldShiftTable(srcLength, octet.length())) {
				shiftTable = TextMatchUtils.newShiftTable();
			}

			if (ignoreCase) {
				return TextMatchUtils.findIgnoreCase(this, fromIndex, srcLength, octet, octet.begin(), octet.length(), shiftTable);
			} else {
				return TextMatchUtils.find(this, fromIndex, srcLength, octet, octet.begin(), octet.length(), shiftTable);
			}
		}
		return -1;
	}

	public final int indexOf(final boolean ignoreCase, int fromIndex, final byte[] bytes, final int offset, final int length) {
		if (fromIndex < this.begin) {
			fromIndex = this.begin;
		}

		final int srcLength = end() - fromIndex;

		if (srcLength >= length && length > 0) {
			int[] shiftTable = null;
			if (TextMatchUtils.shouldShiftTable(srcLength, length)) {
				shiftTable = TextMatchUtils.newShiftTable();
			}

			if (ignoreCase) {
				return TextMatchUtils.findIgnoreCase(this, fromIndex, srcLength, bytes, offset, length, shiftTable);
			} else {
				return TextMatchUtils.find(this, fromIndex, srcLength, bytes, offset, length, shiftTable);
			}
		}
		return -1;
	}

	public final int indexOf(final boolean ignoreCase, final int fromIndex, final byte[] bytes) {
		return indexOf(ignoreCase, fromIndex, bytes, 0, bytes.length);
	}

	/*
	 * lastIndexOf methods
	 */

	public final int lastIndexOf(final boolean ignoreCase, final TextOctet octet) {
		if (this.length >= octet.length() && octet.length() > 0) {
			int[] shiftTable = null;
			if (TextMatchUtils.shouldShiftTable(this.length, octet.length())) {
				shiftTable = TextMatchUtils.newShiftTable();
			}

			if (ignoreCase) {
				return TextMatchUtils.reverseFindIgnoreCase(this, this.begin, this.length, octet, octet.begin(), octet.length(), shiftTable);
			} else {
				return TextMatchUtils.reverseFind(this, this.begin, this.length, octet, octet.begin(), octet.length(), shiftTable);
			}
		}
		return -1;
	}

	public final int lastIndexOf(final boolean ignoreCase, final byte[] bytes, final int offset, final int length) {
		if (this.length >= length && length > 0) {
			int[] shiftTable = null;
			if (TextMatchUtils.shouldShiftTable(this.length, length)) {
				shiftTable = TextMatchUtils.newShiftTable();
			}

			if (ignoreCase) {
				return TextMatchUtils.reverseFindIgnoreCase(this, this.begin, this.length, bytes, offset, length, shiftTable);
			} else {
				return TextMatchUtils.reverseFind(this, this.begin, this.length, bytes, offset, length, shiftTable);
			}
		}
		return -1;
	}

	public final int lastIndexOf(final boolean ignoreCase, final byte[] bytes) {
		return lastIndexOf(ignoreCase, bytes, 0, bytes.length);
	}

	public final int lastIndexOf(final boolean ignoreCase, int fromIndex, final TextOctet octet) {
		if (fromIndex >= end()) {
			fromIndex = end() - 1;
		}

		final int srcLength = fromIndex - this.begin;

		if (srcLength >= octet.length() && octet.length() > 0) {
			int[] shiftTable = null;
			if (TextMatchUtils.shouldShiftTable(srcLength, octet.length())) {
				shiftTable = TextMatchUtils.newShiftTable();
			}

			if (ignoreCase) {
				return TextMatchUtils.reverseFindIgnoreCase(this, this.begin, srcLength, octet, octet.begin(), octet.length(), shiftTable);
			} else {
				return TextMatchUtils.reverseFind(this, this.begin, srcLength, octet, octet.begin(), octet.length(), shiftTable);
			}
		}
		return -1;
	}

	public final int lastIndexOf(final boolean ignoreCase, int fromIndex, final byte[] bytes, final int offset, final int length) {
		if (fromIndex >= end()) {
			fromIndex = end() - 1;
		}

		final int srcLength = fromIndex - this.begin;

		if (srcLength >= length && length > 0) {
			int[] shiftTable = null;
			if (TextMatchUtils.shouldShiftTable(srcLength, length)) {
				shiftTable = TextMatchUtils.newShiftTable();
			}

			if (ignoreCase) {
				return TextMatchUtils.reverseFindIgnoreCase(this, this.begin, srcLength, bytes, offset, length, shiftTable);
			} else {
				return TextMatchUtils.reverseFind(this, this.begin, srcLength, bytes, offset, length, shiftTable);
			}
		}
		return -1;
	}

	public final int lastIndexOf(final boolean ignoreCase, final int fromIndex, final byte[] bytes) {
		return lastIndexOf(ignoreCase, fromIndex, bytes, 0, bytes.length);
	}

	/*
	 * subtext methods
	 */

	public final TextOctet subtext(final int beginIndex) {
		super.suboctet(beginIndex);
		return this;
	}

	public final TextOctet subtext(final int beginIndex, final int endIndex) {
		super.suboctet(beginIndex, endIndex);
		return this;
	}

	/*
	 * trim methods
	 */

	public final TextOctet trim() {
		int ptr = this.begin;
		int limit = ptr + this.length;
		int end = limit - 1;

		final byte[] array = this.array();
		final ByteBuffer buffer = this.buffer();
		if (array != null) {
			ptr += this.arrayOffset();
			limit += this.arrayOffset();
			end += this.arrayOffset();
		} else if (buffer == null) {
			return this;
		}

		while (ptr < limit) {
			final byte b = (array != null) ? array[ptr] : buffer.get(ptr);
			switch (b) {
			case ' ': case '\t': case '\r': case '\n':
				++ptr;
				break;

			default:
				limit = -1; // to terminate the loop
				break;
			}
		}

		limit = ptr;

		while (end >= limit) {
			final byte b = (array != null) ? array[end] : buffer.get(end);
			switch (b) {
			case ' ': case '\t': case '\r': case '\n':
				--end;
				break;

			default:
				limit = end + 1; // to terminate the loop
				break;
			}
		}

		++end;

		this.begin = (array != null) ? ptr - this.arrayOffset() : ptr;
		this.length = end - ptr;

		return this;
	}

	/*
	 * charset methods
	 */

	public final AbstractTextOctet setCharset(final Charset charset) {
		this.charset = charset;
		return this;
	}

	/**
	 *
	 * @param charsetName
	 *
	 * @throws IllegalCharsetNameException
	 * @throws IllegalArgumentException
	 * @throws UnsupportedCharsetException
	 */
	public final AbstractTextOctet setCharset(final String charsetName) {
		if (!isSameCharset(charsetName)) {
			this.charset = Charset.forName(charsetName);
		}
		return this;
	}

	public final boolean isSameCharset(final String charsetName) {
		final Charset charset = getCharset();
		if (charset.name().equalsIgnoreCase(charsetName)) {
			return true;
		}

		final Set<String> aliases = charset.aliases();
		for (String name : aliases) {
			if (name.equalsIgnoreCase(charsetName)) {
				return true;
			}
		}

		return false;
	}

	public Charset getCharset() {
		if (this.charset == null) {
			this.charset = Charset.forName("UTF-8");
		}
		return this.charset;
	}

	public final boolean hasCharset() {
		return (this.charset != null);
	}

	protected CharsetDecoder getCharsetDecoder() {
		return getCharsetDecoder(getCharset());
	}

	protected static final CharsetDecoder getCharsetDecoder(final Charset charset) {
		final CharsetDecoder decoder = charset.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPLACE);
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		return decoder;
	}


	/*
	 * append methods
	 */

	public final void appendTo(final Appendable appendable) throws IOException {
		if (this.length > 0) {
			doAppend(getCharsetDecoder(), appendable);
		}
	}

	private final void doAppend(final CharsetDecoder decoder, final Appendable appendable) throws IOException {
		ByteBuffer buffer = buffer();
		int oldPosition = -1;
		int oldLimit = 0;

		if (buffer != null) {
			oldPosition = buffer.position();
			oldLimit = buffer.limit();

			buffer.position(0).limit(end()).position(this.begin);
		} else if (array() != null){
			buffer = ByteBuffer.wrap(array(), this.begin + arrayOffset(), this.length);
		} else {
			return;
		}

		try {
			decodeBuffer(buffer, decoder, appendable);
		} finally {
			if (oldPosition >= 0) {
				buffer.position(0).limit(oldLimit).position(oldPosition);
			}
		}
	}

	private static final void decodeBuffer(final ByteBuffer buffer, final CharsetDecoder decoder, final Appendable appendable)
			throws IOException {

		final CharBuffer chars = CharBuffer.allocate(128);

		decoder.reset();

		for (;;) {
			CoderResult result = decoder.decode(buffer, chars, true);

			if (result.isOverflow()) {
				chars.flip();
				appendable.append(chars);
				chars.clear();
				continue;
			}
			break;
		}

		decoder.flush(chars);
		chars.flip();
		appendable.append(chars);
	}

	/*
	 * other methods
	 */

	public final Reader getReader() {
		return new BufferedReader(new InputStreamReader(inputOp(), getCharset()));
	}


	@Override
	public String toString() {
		return decodeToString();
	}

	@Override
	public final void toString(boolean asHexFormat, Appendable appendable) {
		if (asHexFormat) {
			super.toString(asHexFormat, appendable);
		} else {
			if (this.length > 0) {
				try {
					doAppend(getCharsetDecoder(), appendable);
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public final String toString(boolean asHexFormat) {
		if (asHexFormat) {
			return super.toString(asHexFormat);
		} else {
			return decodeToString();
		}
	}

	private final String decodeToString() {
		if (this.length > 0) {
			final StringBuilder builder = new StringBuilder();

			try {
				doAppend(getCharsetDecoder(), builder);
			} catch (IOException ignore) {
			}

			return builder.toString();

			/*byte[] array;
			final ByteBuffer buffer = buffer();

			if (buffer != null) {
				if (buffer.hasArray()) {
					array = buffer.array();
					return new String(array, this.begin + buffer.arrayOffset(), this.length, getCharset());
				} else {
					final StringBuilder builder = new StringBuilder();

					try {
						doAppend(getCharsetDecoder(), builder);
					} catch (IOException ignore) {
					}

					return builder.toString();
				}
			} else {
				array = array();

				if (array != null) {
					return new String(array, this.begin + arrayOffset(), this.length, getCharset());
				}
			}*/
		}

		return new String();
	}


	/*
	 * Externalizable methods
	 */

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		super.writeExternal(out);

		String charsetName;
		if (hasCharset()) {
			charsetName = getCharset().name();
		} else {
			charsetName = "";
		}
		out.writeUTF(charsetName);
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);

		final String charsetName = in.readUTF();
		if (charsetName.length() > 0) {
			setCharset(charsetName);
		}
	}

	/*
	 * Internal equals methods
	 */

	protected final boolean equalsIgnoreCase(final int start, final int length, final Octet another) {
		int i = start;
		int j = another.begin();

		int end = start + length;

		final byte[] anotherArray = another.array();
		if (anotherArray != null) {
			return equalsIgnoreCase(start, length, anotherArray, j + another.arrayOffset());
		}

		final ByteBuffer anotherBuffer = another.buffer();
		if (anotherBuffer != null) {
			final byte[] array = array();

			if (array != null) {
				end += arrayOffset();

				for (i += arrayOffset(); i < end; ++i, ++j) {
					if (!equalsIgnoreCase(array[i], anotherBuffer.get(j))) {
						return false;
					}
				}
			} else {
				final ByteBuffer buffer = this.buffer();
				if (buffer != null) {
					for (; i < end; ++i, ++j) {
						if (!equalsIgnoreCase(buffer.get(i), anotherBuffer.get(j))) {
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

	protected final boolean equalsIgnoreCase(final int start, final int length,
			final byte[] bytes, final int offset) {
		int i = start;
		int j = offset;

		int end = start + length;

		final byte[] array = array();
		if (array != null) {
			end += arrayOffset();

			for (i += arrayOffset(); i < end; ++i, ++j) {
				if (!equalsIgnoreCase(array[i], bytes[j])) {
					return false;
				}
			}
		} else {
			final ByteBuffer buffer = this.buffer();
			if (buffer != null) {
				for (; i < end; ++i, ++j) {
					if (!equalsIgnoreCase(buffer.get(i), bytes[j])) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
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

}
