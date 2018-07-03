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

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

import com.chinmobi.octet.BufferAllocator;
import com.chinmobi.octet.BufferSettableOctet;
import com.chinmobi.octet.BufferUtils;
import com.chinmobi.octet.ExpandableOctetBuffer;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public class ExpandableTextBuffer extends ExpandableOctetBuffer implements Appendable {

	private static final int TEST_MODE = 0;


	protected ExpandableTextBuffer(final BufferSettableOctet bufferOctet) {
		super(bufferOctet);
	}

	public ExpandableTextBuffer() {
		this(new BufferText());
	}

	protected ExpandableTextBuffer(final BufferAllocator allocator, final BufferSettableOctet bufferOctet) {
		super(allocator, bufferOctet);
	}

	public ExpandableTextBuffer(final int size) {
		super(null, new BufferText(ExpandableOctetBuffer.allocate(null, null, size), 0, 0));
	}

	public ExpandableTextBuffer(final BufferAllocator allocator, final int size) {
		super(allocator, new BufferText(ExpandableOctetBuffer.allocate(allocator, null, size), 0, 0));
	}


	@Override
	public ExpandableTextBuffer clear() {
		super.clear();
		return this;
	}

	@Override
	public String toString() {
		return toString(false);
	}


	public ExpandableTextBuffer load(final String fileName) throws IOException {
		return load(fileName, null);
	}

	public ExpandableTextBuffer load(final String fileName, String charsetName) throws IOException {
		final FileInputStream inputStream = new FileInputStream(fileName);

		try {
			final FileChannel fileChannel = inputStream.getChannel();

			if (fileChannel.size() >= 2) {
				setOutputMode();

				final int offset = buffer().position();

				final String guessedName = guessCharsetName(fileChannel);
				if (guessedName != null) {
					charsetName = guessedName;
				}

				if (charsetName != null) {
					if (bufferText().hasCharset()) {
						if (!bufferText().isSameCharset(charsetName)) {
							final Charset charset = Charset.forName(charsetName);

							super.load(fileChannel);

							convertForCharset(charset, bufferText().getCharset(), offset);

							return this;
						}
					} else {
						bufferText().setCharset(charsetName);
					}
				}
			}

			super.load(fileChannel);
		} finally {
			inputStream.close();
		}

		return this;
	}

	private final String guessCharsetName(final FileChannel fileChannel) throws IOException {
		setOutputMode();

		ByteBuffer buf = buffer();
		final int pos = buf.position();

		final int len = outputOp().transferFrom(fileChannel, 3);
		if (len >= 2) {
			buf = buffer();

			final int b0 = buf.get(pos + 0) & 0xFF;
			final int b1 = buf.get(pos + 1) & 0xFF;

			switch (b0) {
			case 0xFE: if (b1 == 0xFF) { delete(pos, pos + 2); return "UTF-16BE"; }
			case 0xFF: if (b1 == 0xFE) { delete(pos, pos + 2); return "UTF-16LE"; }
			case 0xEF:
				if (b1 == 0xBB && len > 2) {
					final int b2 = buf.get(pos + 2) & 0xFF;
					if (b2 == 0xBF) { delete(pos, pos + 3); return "UTF-8"; }
				}
			}
		}

		return null;
	}

	public void store(final String fileName, final boolean append, final boolean withBOM)
			throws IOException {
		final FileOutputStream outputStream = new FileOutputStream(fileName, append);

		try {
			final FileChannel fileChannel = outputStream.getChannel();
			if (!append) {
				fileChannel.position(0);
			}

			if (fileChannel.position() == 0 && withBOM) {
				writeBOM(fileChannel);
			}

			store(fileChannel);

			fileChannel.truncate(fileChannel.position());
		} finally {
			outputStream.close();
		}
	}

	private final void writeBOM(final FileChannel fileChannel) throws IOException {
		final byte[] array = new byte[8];
		final ByteBuffer buffer = ByteBuffer.wrap(array);
		buffer.clear();

		if (!bufferText().hasCharset() || bufferText().isSameCharset("UTF-8")) {
			buffer.put((byte)0xEF).put((byte)0xBB).put((byte)0xBF);
		} else if (bufferText().isSameCharset("UTF-16BE")) {
			buffer.put((byte)0xFE).put((byte)0xFF);
		} else if (bufferText().isSameCharset("UTF-16LE")) {
			buffer.put((byte)0xFF).put((byte)0xFE);
		}

		buffer.flip();
		if (buffer.hasRemaining()) {
			fileChannel.write(buffer);
		}
	}


	protected final AbstractTextOctet bufferText() {
		return (AbstractTextOctet)this.bufferOctet;
	}


	public final ExpandableTextBuffer toOutputText() {
		super.toOutput();
		return this;
	}

	public final ExpandableTextBuffer toOutputText(final int start, final int end) {
		super.toOutput(start, end);
		return this;
	}

	public final TextOctet toInputText() {
		super.toInput();
		return (TextOctet)this.bufferOctet;
	}


	public final Writer getWriter() {
		setOutputMode();
		return new BufferedWriter(new OutputStreamWriter(outputOp(), getCharset()));
	}

	public final PrintWriter getPrintWriter(final boolean autoFlush) {
		return new PrintWriter(getWriter(), autoFlush);
	}


	/**
	 *
	 * @param charsetName
	 *
	 * @throws IllegalCharsetNameException
	 * @throws IllegalArgumentException
	 * @throws UnsupportedCharsetException
	 */
	public final ExpandableTextBuffer setCharset(final String charsetName) {
		this.bufferText().setCharset(charsetName);
		return this;
	}

	public final Charset getCharset() {
		return bufferText().getCharset();
	}

	public final boolean hasCharset() {
		return bufferText().hasCharset();
	}

	public final boolean isSameCharset(final String charsetName) {
		return bufferText().isSameCharset(charsetName);
	}


	/*
	 * Append methods
	 */

	public final ExpandableTextBuffer append(final char c) {
		setOutputMode();

		final char[] array = new char[1];
		array[0] = c;

		final CharBuffer chars = CharBuffer.wrap(array);
		appendChars(this.bufferOctet.buffer(), chars, getCharsetEncoder());

		return this;
	}

	public final ExpandableTextBuffer append(final CharSequence csq, final int start, final int end) {
		setOutputMode();

		final CharBuffer chars = CharBuffer.wrap(csq, start, end);
		appendChars(this.bufferOctet.buffer(), chars, getCharsetEncoder());

		return this;
	}

	public final ExpandableTextBuffer append(final CharSequence csq) {
		setOutputMode();

		final CharBuffer chars = CharBuffer.wrap(csq);
		appendChars(this.bufferOctet.buffer(), chars, getCharsetEncoder());

		return this;
	}

	public final ExpandableTextBuffer append(final CharBuffer chars, final int start, final int end) {
		setOutputMode();

		final int oldPos = chars.position();
		final int oldLimit = chars.limit();

		chars.position(0).limit(end).position(start);
		try {
			appendChars(this.bufferOctet.buffer(), chars, getCharsetEncoder());
		} finally {
			chars.position(0).limit(oldLimit).position(oldPos);
		}

		return this;
	}

	/*
	 * Replace methods
	 */

	public final ExpandableTextBuffer replace(final int beginIndex, final int endIndex, final CharSequence csq) {
		return replace(beginIndex, endIndex, csq, 0, csq.length());
	}

	public final ExpandableTextBuffer replace(final int beginIndex, final int endIndex,
			final CharSequence csq, final int start, final int end) {
		setOutputMode();

		final CharBuffer chars = CharBuffer.wrap(csq, start, end);
		doReplace(this.bufferOctet.buffer(), beginIndex, endIndex, chars, getCharsetEncoder());

		return this;
	}

	public final ExpandableTextBuffer replace(final int beginIndex, final int endIndex,
			final CharBuffer chars, final int start, final int end) {
		setOutputMode();

		final int oldPos = chars.position();
		final int oldLimit = chars.limit();

		chars.position(0).limit(end).position(start);
		try {
			doReplace(this.bufferOctet.buffer(), beginIndex, endIndex, chars, getCharsetEncoder());
		} finally {
			chars.position(0).limit(oldLimit).position(oldPos);
		}

		return this;
	}

	public final ExpandableTextBuffer replace(final int beginIndex, final int endIndex, final char c) {
		setOutputMode();

		final char[] array = new char[1];
		array[0] = c;

		final CharBuffer chars = CharBuffer.wrap(array);
		doReplace(this.bufferOctet.buffer(), beginIndex, endIndex, chars, getCharsetEncoder());

		return this;
	}

	/*
	 * Insert methods
	 */

	public final ExpandableTextBuffer insert(final int offset, final CharSequence csq) {
		return replace(offset, offset, csq);
	}

	public final ExpandableTextBuffer insert(final int offset,
			final CharSequence csq, final int start, final int end) {
		return replace(offset, offset, csq, start, end);
	}

	public final ExpandableTextBuffer insert(final int offset, final CharBuffer chars, final int start, final int end) {
		return replace(offset, offset, chars, start, end);
	}

	public final ExpandableTextBuffer insert(final int offset, final char c) {
		return replace(offset, offset, c);
	}


	private final ByteBuffer doReplace(ByteBuffer buffer, final int beginIndex, final int endIndex,
			final CharBuffer chars, final CharsetEncoder encoder) {
		int position = buffer.position();

		if (beginIndex >= position) {
			buffer = ensureLength(buffer, beginIndex - position);

			buffer.position(beginIndex);
			buffer = appendChars(buffer, chars, encoder);

			return buffer;
		}

		final int replaces = (endIndex <= position) ? (endIndex - beginIndex) : (position - beginIndex);
		buffer = BufferUtils.expand(buffer, beginIndex, replaces, 0, this);

		final int distance = buffer.position() - beginIndex;

		buffer = appendChars(buffer, chars, encoder);

		position = beginIndex + distance;

		final int length = buffer.position() - position;
		BufferUtils.swap(buffer, beginIndex, 0, position, length);

		return buffer;
	}

	/**
	 *
	 * @param charsetName
	 * @return this HttpExpandableBuffer
	 *
	 * @throws IllegalCharsetNameException
	 * @throws IllegalArgumentException
	 * @throws UnsupportedCharsetException
	 */
	public final ExpandableTextBuffer convert(final String charsetName) {
		if (this.bufferText().isSameCharset(charsetName)) {
			return this;
		}

		final Charset charset = Charset.forName(charsetName);

		convertForCharset(this.bufferText().getCharset(), charset, 0);

		this.bufferText().setCharset(charset);
		return this;
	}

	protected CharsetEncoder getCharsetEncoder() {
		return getCharsetEncoder(this.bufferText().getCharset());
	}

	protected static final CharsetEncoder getCharsetEncoder(final Charset charset) {
		final CharsetEncoder encoder = charset.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPLACE);
		encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		return encoder;
	}

	protected CharsetDecoder getCharsetDecoder() {
		return getCharsetDecoder(this.bufferText().getCharset());
	}

	protected static final CharsetDecoder getCharsetDecoder(final Charset charset) {
		return AbstractTextOctet.getCharsetDecoder(charset);
	}

	private final void convertForCharset(final Charset from, final Charset to, int offset) {

		setInputMode();

		ByteBuffer buffer = buffer();
		buffer.position(offset);

		if (buffer.remaining() > 0) {
			final CharsetDecoder decoder = getCharsetDecoder(from);
			final CharsetEncoder encoder = getCharsetEncoder(to);

			@SuppressWarnings("unused")
			final CharBuffer chars = (TEST_MODE > 0) ? CharBuffer.allocate(4) : CharBuffer.allocate(128);

			buffer = buffer();

			int limit = buffer.limit();

			decoder.reset();

			for (;;) {
				CoderResult result = decoder.decode(buffer, chars, true);

				if (result.isOverflow()) {
					chars.flip();

					final int remaining = buffer.remaining();

					final int position = buffer.position();
					buffer.position(0).limit(limit);
					setOutputMode();
					buffer = doReplace(buffer, offset, position, chars, encoder);

					setInputMode();
					limit = buffer.limit();

					offset = limit - remaining;
					buffer.position(offset);

					chars.clear();
					continue;
				}
				break;
			}

			decoder.flush(chars);
			chars.flip();

			if (chars.hasRemaining()) {
				final int position = buffer.position();
				buffer.position(0).limit(limit);
				setOutputMode();
				doReplace(buffer, offset, position, chars, encoder);
			}
		}
	}

	@SuppressWarnings("unused")
	private final ByteBuffer appendChars(ByteBuffer bytes,
			final CharBuffer chars, final CharsetEncoder encoder) {
		encoder.reset();

		int remaining = chars.remaining();
		if (TEST_MODE > 0) {
			if (remaining > 4) remaining = 4;
		}

		bytes = ensureLength(bytes, remaining);

		for (;;) {
			CoderResult result = encoder.encode(chars, bytes, true);

			if (result.isOverflow()) {
				remaining = chars.remaining();
				if (TEST_MODE > 0) {
					if (remaining > 4) remaining = 4;
				}

				bytes = ensureLength(bytes, remaining);
				continue;
			}
			break;
		}

		encoder.flush(bytes);

		return bytes;
	}

}
