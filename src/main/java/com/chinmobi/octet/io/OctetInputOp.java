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
package com.chinmobi.octet.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.WritableByteChannel;

import com.chinmobi.octet.Octet;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public class OctetInputOp extends InputStream implements OctetOp, ScatteringByteChannel {

	private static final int LENGTH_PER_TRANSFER = 64;

	private final Octet octet;

	private ByteBuffer buffer;


	public OctetInputOp(final Octet octet) {
		this(octet, true);
	}

	protected OctetInputOp(final Octet octet, final boolean init) {
		this.octet = octet;
		if (init) {
			init(octet);
		}
	}


	protected void init(final Octet octet) {
		final ByteBuffer buf = octet.buffer();

		if (buf != null) {
			this.buffer = buf;
		} else {
			final byte[] tmpArray = octet.array();
			if (tmpArray != null) {
				if (this.buffer == null || this.buffer.array() != tmpArray) {
					this.buffer = ByteBuffer.wrap(tmpArray);
				}
			} else {
				throw new IllegalArgumentException("Null octet buffer or array.");
			}
		}

		this.buffer.position(0).limit(octet.end()).position(octet.begin());

		this.buffer.mark();
	}


	public final Octet octet() {
		return this.octet;
	}

	public final OctetInputOp restart() {
		init(this.octet);
		return this;
	}

	public final Object lock() {
		return this.octet.lock();
	}


	/*
	 * Transfer methods
	 */

	public final int transferTo(final int position, final int count, final OutputStream target)
			throws IOException {
		if (position >= this.octet.begin() && count > 0 && (position + count) <= this.octet.end()) {
			byte[] array = this.octet.array();

			if (array != null) {
				target.write(array, position + this.octet.arrayOffset(), count);
			} else {
				final int oldPosition = this.buffer.position();
				final int oldLimit = this.buffer.limit();

				this.buffer.position(0).limit(position + count).position(position);
				try {
					array = new byte[(count > LENGTH_PER_TRANSFER) ? LENGTH_PER_TRANSFER : count];

					int remaining = count;
					while (remaining > 0) {
						final int len = (remaining > array.length) ? array.length : remaining;

						this.buffer.get(array, 0, len);
						target.write(array, 0, len);

						remaining -= len;
					}
				} finally {
					this.buffer.position(0).limit(oldLimit).position(oldPosition);
				}
			}
		} else if (count == 0) {
		} else {
			throw new IllegalArgumentException("position: " + position + " count: " + count);
		}
		return count;
	}

	public final int transferTo(final OutputStream target, final int length)
			throws IOException {
		if (length > 0 && length <= remaining()) {
			final int position = this.buffer.position();

			byte[] array = this.octet.array();

			if (array != null) {
				target.write(array, position + this.octet.arrayOffset(), length);
			} else {
				final int limit = this.buffer.limit();
				this.buffer.limit(this.buffer.position() + length);
				try {
					array = new byte[(length > LENGTH_PER_TRANSFER) ? LENGTH_PER_TRANSFER : length];

					int remaining = length;
					while (remaining > 0) {
						final int len = (remaining > array.length) ? array.length : remaining;

						this.buffer.get(array, 0, len);
						target.write(array, 0, len);

						remaining -= len;
					}
				} finally {
					this.buffer.limit(limit);
				}
			}

			this.buffer.position(position + length);
		} else if (length == 0) {
		} else {
			throw new IllegalArgumentException("length: " + length);
		}
		return length;
	}

	public final int transferTo(final OutputStream target) throws IOException {
		return transferTo(target, remaining());
	}

	public final int transferTo(final int position, int count, final WritableByteChannel target)
			throws IOException {
		if (position >= this.octet.begin() && count > 0 && (position + count) <= this.octet.end()) {

			int total = 0;

			final int oldPosition = this.buffer.position();
			final int oldLimit = this.buffer.limit();

			this.buffer.position(0).limit(position + count).position(position);
			try {
				while (count > 0) {
					final int n = target.write(this.buffer);

					if (n > 0) {
						total += n;
						count -= n;
					} else {
						break;
					}
				}
			} finally {
				this.buffer.position(0).limit(oldLimit).position(oldPosition);
			}

			return total;

		} else if (count == 0) {
		} else {
			throw new IllegalArgumentException("position: " + position + " count: " + count);
		}
		return 0;
	}

	public final int transferTo(final WritableByteChannel target, int length)
			throws IOException {
		if (length > 0 && length <= remaining()) {

			int total = 0;

			final int limit = this.buffer.limit();
			this.buffer.limit(this.buffer.position() + length);
			try {
				while (length > 0) {
					final int n = target.write(this.buffer);

					if (n > 0) {
						total += n;
						length -= n;
					} else {
						break;
					}
				}
			} finally {
				this.buffer.limit(limit);
			}

			return total;

		} else if (length == 0) {
		} else {
			throw new IllegalArgumentException("length: " + length);
		}
		return 0;
	}

	public final int transferTo(final WritableByteChannel target) throws IOException {
		return transferTo(target, remaining());
	}

	/*
	 * InputStream methods
	 */

	public final InputStream inputStream() {
		return this;
	}

	public final int available() throws IOException {
		return this.buffer.remaining();
	}

	public final void close() throws IOException {
		this.buffer.position(this.buffer.limit());
	}

	public final void mark(final int readlimit) {
		this.buffer.mark();
	}

	public final boolean markSupported() {
		return true;
	}

	public final void reset() throws IOException {
		try {
			this.buffer.reset();
		} catch (InvalidMarkException ex) {
			final IOException ioe = new IOException(ex.getMessage());
			ioe.initCause(ex);
			throw ioe;
		}
	}

	public final int read() throws IOException {
		try {
			return this.buffer.get();
		} catch (BufferUnderflowException ex) {
			return -1;
		}
	}

	public final int read(final byte[] dst) throws IOException {
		return read(dst, 0, dst.length);
	}

	public final int read(final byte[] dst, final int offset, int length) throws IOException {
		if (length > 0 && offset >= 0 && (offset + length) <= dst.length) {
			final int remaining = this.buffer.remaining();
			if (remaining > 0) {
				if (length > remaining) {
					length = remaining;
				}

				this.buffer.get(dst, offset, length);

			} else {
				return -1;
			}
		} else if (length == 0) {
			return 0;
		} else {
			throw new IndexOutOfBoundsException("offset: " + offset + " length: " + length);
		}
		return length;
	}

	public final long skip(final long n) throws IOException {
		int count = (int)n;
		if (count > 0) {
			final int remaining = this.buffer.remaining();
			if (count > remaining) {
				count = remaining;
			}
			this.buffer.position(this.buffer.position() + count);
		}
		return count;
	}

	/*
	 * Channel, ReadableByteChannel, ScatteringByteChannel methods
	 */

	public final ScatteringByteChannel readableByteChannel() {
		return this;
	}

	public final boolean isOpen() {
		return true;
	}

	public final int read(final ByteBuffer dst) throws IOException {
		int length = dst.remaining();

		if (length > 0) {
			final int remaining = this.buffer.remaining();
			if (remaining > 0) {

				if (length > remaining) {
					length = remaining;
				}

				final int limit = this.buffer.limit();
				this.buffer.limit(this.buffer.position() + length);
				try {
					dst.put(this.buffer);
				} catch (IllegalArgumentException ex) {
					final IOException ioe = new IOException(ex.getMessage());
					ioe.initCause(ex);
					throw ioe;
				} catch (ReadOnlyBufferException ex) {
					final IOException ioe = new IOException(ex.getMessage());
					ioe.initCause(ex);
					throw ioe;
				} finally {
					this.buffer.limit(limit);
				}

			} else {
				return -1;
			}
		}

		return length;
	}

	public final long read(final ByteBuffer[] dsts, final int offset, final int length) throws IOException {
		long total = 0;

		if (length > 0 && offset >= 0 && (offset + length) <= dsts.length) {
			for (int i = offset; i < length; ++i) {
				int n = read(dsts[i]);

				if (n >= 0) {
					total += n;
				} else if (i > offset) {
					return total;
				} else {
					return -1;
				}
			}
		} else if (length == 0) {
			return 0;
		} else {
			throw new IndexOutOfBoundsException("offset: " + offset + " length: " + length);
		}

		return total;
	}

	public final long read(final ByteBuffer[] dsts) throws IOException {
		return read(dsts, 0, dsts.length);
	}

	/*
	 * ByteBuffer methods
	 */

	public final int position() {
		return this.buffer.position();
	}

	public final int limit() {
		return this.buffer.limit();
	}

	public final boolean hasRemaining() {
		return this.buffer.hasRemaining();
	}

	public final int remaining() {
		return this.buffer.remaining();
	}

	public final OctetInputOp skipBytes(final int count) throws OctetOpOutOfBoundsException {
		if (count >= 0 && count <= this.buffer.remaining()) {
			try {
				this.buffer.position(this.buffer.position() + count);
			} catch (IllegalArgumentException ex) {
				throw new OctetOpOutOfBoundsException(ex);
			}
		} else if (count < 0) {
			throw new IndexOutOfBoundsException("count: " + count);
		} else {
			throw new OctetOpOutOfBoundsException("count: " + count);
		}

		return this;
	}

	public final OctetInputOp skipInts(final int count) throws OctetOpOutOfBoundsException {
		return skipBytes(count * SIZE_OF_INT);
	}

	public final OctetInputOp get(final byte[] dst)
			throws OctetOpOutOfBoundsException {
		try {
			this.buffer.get(dst);
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
		return this;
	}

	public final OctetInputOp get(final byte[] dst, final int offset, final int length)
			throws OctetOpOutOfBoundsException {
		try {
			this.buffer.get(dst, offset, length);
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
		return this;
	}

	public final OctetInputOp get(final int fromIndex, final byte[] dst)
			throws OctetOpOutOfBoundsException {
		return get(fromIndex, dst, 0, dst.length);
	}

	public final OctetInputOp get(final int fromIndex,
			final byte[] dst, final int offset, final int length)
			throws OctetOpOutOfBoundsException {

		final int limit = this.buffer.limit();

		if (fromIndex >= 0 && fromIndex < limit) {
			final int position = this.buffer.position();

			this.buffer.position(fromIndex);
			try {
				this.buffer.get(dst, offset, length);
			} catch (BufferUnderflowException ex) {
				throw new OctetOpOutOfBoundsException(ex);
			} finally {
				this.buffer.position(position);
			}

		} else {
			throw new IndexOutOfBoundsException("fromIndex: " + fromIndex);
		}

		return this;
	}


	public final byte get() throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.get();
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final byte get(final int index) throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.get(index);
		} catch (IndexOutOfBoundsException ex) {
			throw ex;
		}
	}

	public final char getChar() throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getChar();
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final char getChar(final int index) throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getChar(index);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final short getShort() throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getShort();
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final short getShort(final int index) throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getShort(index);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final int getInt() throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getInt();
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final int getInt(final int index) throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getInt(index);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final long getLong() throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getLong();
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final long getLong(final int index) throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getLong(index);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final float getFloat() throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getFloat();
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final float getFloat(final int index) throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getFloat(index);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final double getDouble() throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getDouble();
		} catch (BufferUnderflowException ex) {
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

	public final double getDouble(final int index) throws OctetOpOutOfBoundsException {
		try {
			return this.buffer.getDouble(index);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
	}

}
