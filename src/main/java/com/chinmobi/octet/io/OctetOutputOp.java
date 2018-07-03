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
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;

import com.chinmobi.octet.BufferUtils;
import com.chinmobi.octet.MutableOctet;
import com.chinmobi.octet.Octet;
import com.chinmobi.octet.OctetAppendable;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public class OctetOutputOp extends OutputStream
	implements OctetOp, OctetAppendable, GatheringByteChannel {

	private static final int LENGTH_PER_TRANSFER = 64;

	private final MutableOctet octet;

	private ByteBuffer buffer;


	public OctetOutputOp(final MutableOctet octet) {
		this(octet, true);
	}

	protected OctetOutputOp(final MutableOctet octet, final boolean init) {
		this.octet = octet;
		if (init) {
			init(octet);
		}
	}


	protected void init(final MutableOctet octet) {
		final ByteBuffer buf = octet.buffer();

		if (buf != null) {
			this.buffer = buf;
		} else {
			final byte[] tmpArray = octet.array();
			if (tmpArray != null) {
				if (this.buffer == null || this.buffer.array() != tmpArray) {
					this.buffer = ByteBuffer.wrap(tmpArray);
				}
				this.buffer.clear().position(octet.end());
			} else {
				throw new IllegalArgumentException("Null octet buffer or array.");
			}
		}
	}

	protected ByteBuffer expandLength(final ByteBuffer buffer, final int requiredLength) {
		return buffer;
	}

	protected void updateBounds(final int newBegin, final int newEnd) {
	}


	private final boolean expendLength(final int requiredLength) {
		final ByteBuffer buf = expandLength(this.buffer, requiredLength);
		if (buf != this.buffer) {
			this.buffer = buf;
			return true;
		}
		return false;
	}

	private final boolean ensureLength(final int fromIndex, int requiredLength) {
		if (fromIndex >= 0) {
			if ((fromIndex + requiredLength) > this.buffer.capacity()) {
				requiredLength += (fromIndex - this.buffer.position());
				return expendLength(requiredLength);
			}
			return true;
		}
		return false;
	}

	public final boolean ensureLength(final int requiredLength) {
		return ensureLength(this.buffer.position(), requiredLength);
	}


	public final MutableOctet octet() {
		return this.octet;
	}

	public final OctetOutputOp restart() {
		init(this.octet);
		return this;
	}

	public final Object lock() {
		return this.octet.lock();
	}

	public final OctetOutputOp clear() {
		this.octet.clear();
		return this;
	}

	public final OctetOutputOp update() {
		updateBounds(0, this.buffer.position());
		return this;
	}


	/*
	 * Transfer methods
	 */

	public final int transferFrom(int position, int count, final InputStream src)
			throws IOException {
		if (position >= 0 && count > 0 && ensureLength(position, count)) {

			int total = 0;

			byte[] array = this.octet.array();
			if (array != null) {
				position += this.octet.arrayOffset();

				while (count > 0) {
					final int n = src.read(array, position, count);

					if (n > 0) {
						position += n;
						count -= n;

						total += n;
					} else if (n < 0) {
						if (total == 0) {
							total = -1;
						}
						break;
					} else {
						break;
					}
				}
			} else {
				final int oldPosition = this.buffer.position();

				this.buffer.position(position);
				try {
					array = new byte[(count > LENGTH_PER_TRANSFER) ? LENGTH_PER_TRANSFER : count];

					while (count > 0) {
						final int len = (count < array.length) ? count : array.length;

						final int n = src.read(array, 0, len);

						if (n > 0) {
							this.buffer.put(array, 0, n);

							count -= n;
							total += n;
						} else if (n < 0) {
							if (total == 0) {
								total = -1;
							}
							break;
						} else {
							break;
						}
					}
				} finally {
					this.buffer.position(oldPosition);
				}

			}

			return total;

		} else if (count == 0) {
		} else {
			throw new IllegalArgumentException("position: " + position + " count: " + count);
		}
		return 0;
	}

	public final int transferFrom(final InputStream src, int length)
			throws IOException {
		if (length > 0 && ensureLength(length)) {

			int total = 0;

			int position = this.buffer.position();

			byte[] array = this.octet.array();
			if (array != null) {
				position += this.octet.arrayOffset();

				while (length > 0) {
					final int n = src.read(array, position, length);

					if (n > 0) {
						position += n;
						length -= n;

						total += n;

						this.buffer.position(position - this.octet.arrayOffset());
					} else if (n < 0) {
						if (total == 0) {
							total = -1;
						}
						break;
					} else {
						break;
					}
				}
			} else {
				array = new byte[(length > LENGTH_PER_TRANSFER) ? LENGTH_PER_TRANSFER : length];

				while (length > 0) {
					final int len = (length < array.length) ? length : array.length;

					final int n = src.read(array, 0, len);

					if (n > 0) {
						this.buffer.put(array, 0, n);

						length -= n;
						total += n;
					} else if (n < 0) {
						if (total == 0) {
							total = -1;
						}
						break;
					} else {
						break;
					}
				}
			}

			return total;

		} else if (length == 0) {
		} else {
			throw new IllegalArgumentException("length: " + length);
		}
		return 0;
	}

	public final int transferFrom(final InputStream src) throws IOException {
		int total = 0;

		for (;;) {
			int length = this.buffer.remaining();
			if (length > 0) {
				final int n = transferFrom(src, length);

				if (n > 0) {
					total += n;
					length -= n;

					if (length <= 0 && ensureLength(LENGTH_PER_TRANSFER)) {
						continue;
					}
				} else if (n < 0) {
					if (total == 0) {
						total = -1;
					}
				} else {
				}
			} else
			if (ensureLength(LENGTH_PER_TRANSFER)) {
				continue;
			}

			break;
		}

		return total;
	}


	public final int transferFrom(final int position, int count, final ReadableByteChannel src)
			throws IOException {
		if (position >= 0 && count > 0 && ensureLength(position, count)) {

			int total = 0;

			final int oldPosition = this.buffer.position();
			final int oldLimit = this.buffer.limit();

			this.buffer.position(0).limit(position + count).position(position);
			try {
				while (count > 0) {
					final int n = src.read(this.buffer);

					if (n > 0) {
						total += n;
						count -= n;
					} else if (n < 0) {
						if (total == 0) {
							total = -1;
						}
						break;
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

	public final int transferFrom(final ReadableByteChannel src, int length)
			throws IOException {
		if (length > 0 && ensureLength(length)) {

			int total = 0;

			final int limit = this.buffer.limit();
			this.buffer.limit(this.buffer.position() + length);
			try {
				while (length > 0) {
					final int n = src.read(this.buffer);

					if (n > 0) {
						total += n;
						length -= n;
					} else if (n < 0) {
						if (total == 0) {
							total = -1;
						}
						break;
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

	public final int transferFrom(final ReadableByteChannel src)
			throws IOException {
		int total = 0;

		for (;;) {
			int length = this.buffer.remaining();

			if (length > 0) {
				final int n = transferFrom(src, length);

				if (n > 0) {
					total += n;
					length -= n;

					if (length <= 0 && ensureLength(LENGTH_PER_TRANSFER)) {
						continue;
					}
				} else if (n < 0) {
					if (total == 0) {
						total = -1;
					}
				} else {
				}
			} else
			if (ensureLength(LENGTH_PER_TRANSFER)) {
				continue;
			}

			break;
		}

		return total;
	}

	/*
	 * OutputStream methods
	 */

	public final OutputStream outputStream() {
		return this;
	}

	public final void flush() throws IOException {
		update();
	}

	public final void close() throws IOException {
		flush();
	}

	public final void write(final byte[] src) throws IOException {
		write(src, 0, src.length);
	}

	public final void write(final byte[] src, final int offset, final int length) throws IOException {
		if (length > 0 && offset >= 0 && (offset + length) <= src.length) {
			for (;;) {
				try {
					this.buffer.put(src, offset, length);
				} catch (BufferOverflowException ex) {
					if (expendLength(length)) continue;

					final IOException ioe = new IOException(ex.getMessage());
					ioe.initCause(ex);
					throw ioe;
				} catch (ReadOnlyBufferException ex) {
					final IOException ioe = new IOException(ex.getMessage());
					ioe.initCause(ex);
					throw ioe;
				}
				return;
			}
		} else if (length == 0) {
		} else {
			throw new IndexOutOfBoundsException("offset: " + offset + " length: " + length);
		}
	}

	public final void write(final int b) throws IOException {
		for (;;) {
			try {
				this.buffer.put((byte)(b & 0xFF));
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_BYTE)) continue;

				final IOException ioe = new IOException(ex.getMessage());
				ioe.initCause(ex);
				throw ioe;
			} catch (ReadOnlyBufferException ex) {
				final IOException ioe = new IOException(ex.getMessage());
				ioe.initCause(ex);
				throw ioe;
			}
			return;
		}
	}

	/*
	 * Channel, WritableByteChannel, GatheringByteChannel methods
	 */

	public final GatheringByteChannel writableByteChannel() {
		return this;
	}

	public final boolean isOpen() {
		return true;
	}

	public final int write(final ByteBuffer src) throws IOException {
		int length = src.remaining();

		if (length > 0) {
			if (length > this.buffer.remaining()) {
				if (!expendLength(length)) {
					throw new IOException("BufferOverflow");
				}
			}

			try {
				this.buffer.put(src);
			} catch (IllegalArgumentException ex) {
				final IOException ioe = new IOException(ex.getMessage());
				ioe.initCause(ex);
				throw ioe;
			} catch (ReadOnlyBufferException ex) {
				final IOException ioe = new IOException(ex.getMessage());
				ioe.initCause(ex);
				throw ioe;
			}
		}

		return length;
	}

	public final long write(final ByteBuffer[] srcs, final int offset, final int length) throws IOException {
		long total = 0;

		if (length > 0 && offset >= 0 && (offset + length) <= srcs.length) {
			for (int i = offset; i < length; ++i) {
				total += write(srcs[i]);
			}
		} else if (length == 0) {
			return 0;
		} else {
			throw new IndexOutOfBoundsException("offset: " + offset + " length: " + length);
		}

		return total;
	}

	public final long write(final ByteBuffer[] srcs) throws IOException {
		return write(srcs, 0, srcs.length);
	}

	/*
	 * OctetAppendable methods
	 */

	public final OctetOutputOp append(final Octet octet)
			throws OctetOpOutOfBoundsException {
		return append(octet, octet.begin(), octet.end());
	}

	public final OctetOutputOp append(final Octet octet, final int start, final int end)
			throws OctetOpOutOfBoundsException {
		if ((start >= octet.begin()) && (end > start) && (end <= octet.end())) {

		final int length = end - start;
		final byte[] array = octet.array();
		if (array != null) {
			for (;;) {
				try {
					this.buffer.put(array, start + octet.arrayOffset(), length);
				} catch (BufferOverflowException ex) {
					if (expendLength(length)) continue;
					throw new OctetOpOutOfBoundsException(ex);
				}
				return this;
			}
		} else {
			final ByteBuffer buf = octet.buffer();
			if (buf != null) {
				final int position = buf.position();
				final int limit = buf.limit();

				buf.position(0).limit(end).position(start);
				try {
					for (;;) {
						try {
							this.buffer.put(buf);
						} catch (BufferOverflowException ex) {
							if (expendLength(length)) continue;
							throw new OctetOpOutOfBoundsException(ex);
						}
						break;
					}
				} finally {
					buf.position(0).limit(limit).position(position);
				}
			}
		}

		} else if (end == start) {
		} else {
			throw new IndexOutOfBoundsException("start: " + start + " end: " + end);
		}
		return this;
	}

	public final OctetOutputOp append(final byte b)
			throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.put(b);
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_BYTE)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
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


	public final OctetOutputOp skipBytes(final int count) throws OctetOpOutOfBoundsException {
		final int position = this.buffer.position();

		if (count >= 0 && count <= this.buffer.remaining()) {
			this.buffer.position(position + count);
		} else if (count < 0) {
			throw new IndexOutOfBoundsException("count: " + count);
		} else {
			if (ensureLength(position, count)) {
				this.buffer.position(position + count);
			} else {
				throw new OctetOpOutOfBoundsException("count: " + count);
			}
		}
		return this;
	}

	public final OctetOutputOp skipInts(final int count) throws OctetOpOutOfBoundsException {
		return skipBytes(count * SIZE_OF_INT);
	}

	public final OctetOutputOp fill(final int start, final int end, final byte b) {
		if (start >= 0 && end > start && end <= this.buffer.capacity()) {
			BufferUtils.fill(this.buffer, start, end - start, b);
		} else if (end == start) {
		} else {
			throw new IndexOutOfBoundsException("start: " + start + " end: " + end);
		}
		return this;
	}

	public final OctetOutputOp fillZero(final int start, final int end) {
		return fill(start, end, (byte)0x00);
	}


	public final OctetOutputOp put(final byte[] src)
			throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.put(src);
			} catch (BufferOverflowException ex) {
				if (expendLength(src.length)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp put(final byte[] src, final int offset, final int length)
			throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.put(src, offset, length);
			} catch (BufferOverflowException ex) {
				if (expendLength(length)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp put(final int fromIndex, final byte[] dst)
			throws OctetOpOutOfBoundsException {
		return put(fromIndex, dst, 0, dst.length);
	}

	public final OctetOutputOp put(final int fromIndex,
			final byte[] dst, final int offset, final int length)
			throws OctetOpOutOfBoundsException {

		ensureLength(fromIndex, length);

		final int limit = this.buffer.limit();

		if (fromIndex >= 0 && fromIndex < limit) {
			final int position = this.buffer.position();

			this.buffer.position(fromIndex);
			try {
				this.buffer.put(dst, offset, length);
			} catch (BufferOverflowException ex) {
				throw new OctetOpOutOfBoundsException(ex);
			} finally {
				this.buffer.position(position);
			}

		} else {
			throw new IndexOutOfBoundsException("fromIndex: " + fromIndex);
		}

		return this;
	}

	public final OctetOutputOp put(final ByteBuffer src) throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.put(src);
			} catch (BufferOverflowException ex) {
				if (expendLength(src.remaining())) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp put(final int fromIndex, final ByteBuffer src) throws OctetOpOutOfBoundsException {

		ensureLength(fromIndex, src.remaining());

		final int limit = this.buffer.limit();

		if (fromIndex >= 0 && fromIndex < limit) {
			final int position = this.buffer.position();

			this.buffer.position(fromIndex);
			try {
				this.buffer.put(src);
			} catch (BufferOverflowException ex) {
				throw new OctetOpOutOfBoundsException(ex);
			} finally {
				this.buffer.position(position);
			}

		} else {
			throw new IndexOutOfBoundsException("fromIndex: " + fromIndex);
		}

		return this;
	}

	public final OctetOutputOp put(final Octet src) throws OctetOpOutOfBoundsException {
		return append(src, src.begin(), src.end());
	}

	public final OctetOutputOp put(final int fromIndex, final Octet src) throws OctetOpOutOfBoundsException {

		ensureLength(fromIndex, src.length());

		final int limit = this.buffer.limit();

		if (fromIndex >= 0 && fromIndex < limit) {
			final int position = this.buffer.position();

			this.buffer.position(fromIndex);
			try {
				put(src);
			} finally {
				this.buffer.position(position);
			}

		} else {
			throw new IndexOutOfBoundsException("fromIndex: " + fromIndex);
		}

		return this;
	}


	public final OctetOutputOp put(final byte b) throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.put(b);
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_BYTE)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp put(final int index, final byte b) throws OctetOpOutOfBoundsException {

		ensureLength(index, SIZE_OF_BYTE);
		try {
			this.buffer.put(index, b);
		} catch (IndexOutOfBoundsException ex) {
			throw ex;
		}
		return this;
	}

	public final OctetOutputOp putChar(final char value) throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.putChar(value);
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_CHAR)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp putChar(final int index, final char value) throws OctetOpOutOfBoundsException {

		ensureLength(index, SIZE_OF_CHAR);
		try {
			this.buffer.putChar(index, value);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
		return this;
	}

	public final OctetOutputOp putShort(final short value) throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.putShort(value);
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_SHORT)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp putShort(final int index, final short value) throws OctetOpOutOfBoundsException {

		ensureLength(index, SIZE_OF_SHORT);
		try {
			this.buffer.putShort(index, value);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
		return this;
	}

	public final OctetOutputOp putInt(final int value) throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.putInt(value);
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_INT)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp putInt(final int index, final int value) throws OctetOpOutOfBoundsException {

		ensureLength(index, SIZE_OF_INT);
		try {
			this.buffer.putInt(index, value);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
		return this;
	}

	public final OctetOutputOp putLong(final long value) throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.putLong(value);
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_LONG)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp putLong(final int index, final long value) throws OctetOpOutOfBoundsException {

		ensureLength(index, SIZE_OF_LONG);
		try {
			this.buffer.putLong(index, value);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
		return this;
	}

	public final OctetOutputOp putFloat(final float value) throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.putFloat(value);
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_FLOAT)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp putFloat(final int index, final float value) throws OctetOpOutOfBoundsException {

		ensureLength(index, SIZE_OF_FLOAT);
		try {
			this.buffer.putFloat(index, value);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
		return this;
	}

	public final OctetOutputOp putDouble(final double value) throws OctetOpOutOfBoundsException {
		for (;;) {
			try {
				this.buffer.putDouble(value);
			} catch (BufferOverflowException ex) {
				if (expendLength(SIZE_OF_DOUBLE)) continue;
				throw new OctetOpOutOfBoundsException(ex);
			}
			return this;
		}
	}

	public final OctetOutputOp putDouble(final int index, final double value) throws OctetOpOutOfBoundsException {

		ensureLength(index, SIZE_OF_DOUBLE);
		try {
			this.buffer.putDouble(index, value);
		} catch (IndexOutOfBoundsException ex) {
			if (index < 0 || index >= this.buffer.limit()) {
				throw ex;
			}
			throw new OctetOpOutOfBoundsException(ex);
		}
		return this;
	}

}
