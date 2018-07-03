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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.chinmobi.octet.io.OctetInputOp;
import com.chinmobi.octet.io.OctetOpSupplier;
import com.chinmobi.octet.io.OctetOutputOp;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public class ExpandableOctetBuffer
	implements BufferAllocator, OctetAppendable, OctetOpSupplier, Externalizable {

	private final static int OUTPUT_MODE = 0;
	private final static int INPUT_MODE = 1;


	protected final BufferSettableOctet bufferOctet;

	protected transient BufferAllocator allocator;

	private int mode;


	protected ExpandableOctetBuffer(final BufferSettableOctet bufferOctet) {
		super();
		this.bufferOctet = bufferOctet;
		this.bufferOctet.setOpSupplier(this);
	}

	public ExpandableOctetBuffer() {
		this(new BufferOctet());
	}

	public ExpandableOctetBuffer(final int size) {
		this((BufferAllocator)null, size);
	}

	public ExpandableOctetBuffer(final BufferAllocator allocator, final int size) {
		super();

		this.bufferOctet = new BufferOctet(allocate(allocator, null, size), 0, 0);
		this.bufferOctet.setOpSupplier(this);

		this.allocator = allocator;

		clear();
	}

	protected ExpandableOctetBuffer(final BufferAllocator allocator, final BufferSettableOctet bufferOctet) {
		super();

		this.bufferOctet = bufferOctet;
		this.bufferOctet.setOpSupplier(this);

		this.allocator = allocator;

		clear();
	}


	protected void swap(final ExpandableOctetBuffer another) {
		this.bufferOctet.swap((BufferSettableOctet)another.bufferOctet);

		final int tmp = this.mode;
		this.mode = another.mode;
		another.mode = tmp;
	}


	public void setAllocator(final BufferAllocator allocator) {
		this.allocator = allocator;
	}

	public ExpandableOctetBuffer load(final String fileName) throws IOException {
		final FileInputStream inputStream = new FileInputStream(fileName);

		try {
			load(inputStream.getChannel());
		} finally {
			inputStream.close();
		}

		return this;
	}

	public ExpandableOctetBuffer load(final FileChannel fileChannel) throws IOException {
		long size = fileChannel.size();
		size -= fileChannel.position();

		if (size > 0) {
			if (size <= Integer.MAX_VALUE) {
				outputOp().transferFrom(fileChannel, (int)size);
			} else {
				throw new BufferOverflowException();
			}
		}

		return this;
	}

	public void store(final String fileName, final boolean append) throws IOException {
		final FileOutputStream outputStream = new FileOutputStream(fileName, append);

		try {
			final FileChannel fileChannel = outputStream.getChannel();
			if (!append) {
				fileChannel.position(0);
			}

			store(fileChannel);

			fileChannel.truncate(fileChannel.position());
		} finally {
			outputStream.close();
		}
	}

	public void store(final FileChannel fileChannel) throws IOException {
		inputOp().transferTo(fileChannel);
	}


	public final Object lock() {
		return this.bufferOctet.lock();
	}

	public ExpandableOctetBuffer clear() {
		this.bufferOctet.buffer().clear();

		this.bufferOctet.setBegin(0);
		this.bufferOctet.setLength(0);

		this.mode = OUTPUT_MODE;
		return this;
	}


	public final ByteBuffer buffer() {
		return this.bufferOctet.buffer();
	}

	public final int position() {
		return this.bufferOctet.buffer().position();
	}

	public final int remaining() {
		return this.bufferOctet.buffer().remaining();
	}

	public final int length() {
		if (this.mode == OUTPUT_MODE) {
			return this.bufferOctet.buffer().position();
		} else {
			return this.bufferOctet.buffer().remaining();
		}
	}

	public final int capacity() {
		return this.bufferOctet.buffer().capacity();
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public final void toString(boolean asHexFormat, Appendable appendable) {
		final int oldmode = this.mode;
		if (oldmode == OUTPUT_MODE) {
			setInputMode();
		}

		this.bufferOctet.toString(asHexFormat, appendable);

		if (oldmode == OUTPUT_MODE) {
			setOutputMode();
		}
	}

	public final String toString(boolean asHexFormat) {
		final StringBuilder builder = new StringBuilder();
		toString(asHexFormat, builder);
		return builder.toString();
	}


	public final ExpandableOctetBuffer toOutput() {
		setOutputMode();
		return this;
	}

	public final ExpandableOctetBuffer toOutput(final int start, final int end) {
		setOutputMode();
		ensureCapacity(end);

		this.bufferOctet.buffer().position(0).limit(end).position(start);

		return this;
	}

	public final Octet toInput() {
		setInputMode();
		return this.bufferOctet;
	}

	/*
	 * OctetOp methods
	 */

	public final OctetOutputOp outputOp() {
		return new OutputOp(this, this.bufferOctet);
	}

	public final OctetInputOp inputOp() {
		return new InputOp(this, this.bufferOctet);
	}

	/*
	 * Expand methods
	 */

	public final ExpandableOctetBuffer ensureCapacity(final int requiredCapacity) {
		final ByteBuffer buffer = this.bufferOctet.buffer();

		if (buffer == null) {
			this.allocate(null, requiredCapacity);
			clear();
		} else if (buffer.capacity() < requiredCapacity) {
			final int position = buffer.position();

			final ByteBuffer newBuffer = allocate(buffer, requiredCapacity);

			if (this.mode == OUTPUT_MODE) {
				buffer.flip();
			} else {
				buffer.position(0);
			}

			newBuffer.put(buffer);

			if (this.mode != OUTPUT_MODE) {
				newBuffer.flip();
			}

			newBuffer.position(position);
		}

		return this;
	}

	public final ExpandableOctetBuffer ensureLength(final int requiredLength) {
		ensureOutputLength(requiredLength);
		return this;
	}

	public final ExpandableOctetBuffer reserveLength(final int offset, int oldLength, final int newLength) {
		setOutputMode();

		ByteBuffer buffer = this.bufferOctet.buffer();
		final int position = buffer.position();

		if (offset >= position) {
			buffer = ensureLength(buffer, offset + newLength - position);

			buffer.position(offset + newLength);
		} else {
			if (oldLength > (position - offset)) {
				oldLength = (position - offset);
			}

			BufferUtils.expand(buffer, offset, oldLength, newLength, this);
		}

		return this;
	}

	public final ExpandableOctetBuffer fill(final int start, final int end, final byte b) {
		BufferUtils.fill(this.bufferOctet.buffer(), start, end - start, b);
		return this;
	}

	public final ExpandableOctetBuffer fillZero(final int start, final int end) {
		BufferUtils.fill(this.bufferOctet.buffer(), start, end - start, (byte)0x00);
		return this;
	}

	public final ExpandableOctetBuffer delete(final int start, final int end) {
		if (start >= 0 && end > start) {
			setOutputMode();

			final ByteBuffer buffer = this.bufferOctet.buffer();
			final int position = buffer.position();

			if (start >= position) {
				return this;
			}

			final int length = (end <= position) ? (end - start) : (position - start);
			BufferUtils.expand(buffer, start, length, 0, this);
		}
		return this;
	}

	public final ExpandableOctetBuffer replaceAll(final Octet mathed, final Octet replacement) {
		final int length = mathed.length();
		if (length > 0) {
			int index = toInput().begin();

			index = toInput().indexOf(index, mathed);
			while (index >= 0) {
				replace(index, index + length, replacement);

				index += replacement.length();

				index = toInput().indexOf(index, mathed);
			}

			setOutputMode();
		}
		return this;
	}

	public final ExpandableOctetBuffer replaceAll(final byte oldByte, final byte newByte) {
		setOutputMode();

		final ByteBuffer buffer = this.bufferOctet.buffer();
		final int position = buffer.position();

		if (position > 0) {
			BufferUtils.replaceAll(buffer, 0, position, oldByte, newByte);
		}

		return this;
	}

	public final ExpandableOctetBuffer setByteAt(final int index, final byte b) {
		this.bufferOctet.buffer().put(index, b);
		return this;
	}

	/*
	 * Append methods
	 */

	public final ExpandableOctetBuffer append(final Octet src) {
		return append(src, src.begin(), src.end());
	}

	public final ExpandableOctetBuffer append(final Octet src, final int start, final int end) {
		if (src.buffer() != null) {
			return append(src.buffer(), start, end);
		} else
		if (src.array() != null) {
			return append(src.array(), start + src.arrayOffset(), (end - start));
		}
		return this;
	}

	public final ExpandableOctetBuffer append(final ByteBuffer src, final int start, final int end) {
		final ByteBuffer buffer = ensureOutputLength((end - start));

		appendBuffer(buffer, src, start, end);

		return this;
	}

	public final ExpandableOctetBuffer append(final byte[] src) {
		return append(src, 0, src.length);
	}

	public final ExpandableOctetBuffer append(final byte[] src, final int offset, final int length) {
		final ByteBuffer buffer = ensureOutputLength(length);

		buffer.put(src, offset, length);

		return this;
	}

	public final ExpandableOctetBuffer append(final byte b) {
		final ByteBuffer buffer = ensureOutputLength(1);

		buffer.put(b);

		return this;
	}

	private static final void appendBuffer(final ByteBuffer dst,
			final ByteBuffer src, final int start, final int end) {
		final int oldPos = src.position();
		final int oldLimit = src.limit();

		src.position(0).limit(end).position(start);
		try {
			dst.put(src);
		} finally {
			src.position(0).limit(oldLimit).position(oldPos);
		}
	}

	/*
	 * Replace methods
	 */

	public final ExpandableOctetBuffer replace(final int beginIndex, final int endIndex,
			final Octet src) {
		return replace(beginIndex, endIndex, src, src.begin(), src.end());
	}

	public final ExpandableOctetBuffer replace(final int beginIndex, final int endIndex,
			final Octet src, final int start, final int end) {
		if (src.buffer() != null) {
			return replace(beginIndex, endIndex, src.buffer(), start, end);
		} else
		if (src.array() != null) {
			return replace(beginIndex, endIndex, src.array(), start + src.arrayOffset(), (end - start));
		}
		return this;
	}

	public final ExpandableOctetBuffer replace(final int beginIndex, final int endIndex,
			final ByteBuffer src, final int start, final int end) {
		setOutputMode();

		final int length = (end - start);

		ByteBuffer buffer = this.bufferOctet.buffer();
		int position = buffer.position();

		if (beginIndex >= position) {
			buffer = ensureLength(buffer, beginIndex + length - position);

			buffer.position(beginIndex);
			appendBuffer(buffer, src, start, end);

			return this;
		}

		final int replaces = (endIndex <= position) ? (endIndex - beginIndex) : (position - beginIndex);
		buffer = BufferUtils.expand(buffer, beginIndex, replaces, length, this);

		position = buffer.position();

		buffer.position(beginIndex);
		appendBuffer(buffer, src, start, end);

		buffer.position(position);

		return this;
	}

	public final ExpandableOctetBuffer replace(final int beginIndex, final int endIndex, final byte[] src) {
		return replace(beginIndex, endIndex, src, 0, src.length);
	}

	public final ExpandableOctetBuffer replace(final int beginIndex, final int endIndex,
			final byte[] src, final int offset, final int length) {
		setOutputMode();

		ByteBuffer buffer = this.bufferOctet.buffer();
		int position = buffer.position();

		if (beginIndex >= position) {
			buffer = ensureLength(buffer, beginIndex + length - position);

			buffer.position(beginIndex);
			buffer.put(src, offset, length);

			return this;
		}

		final int replaces = (endIndex <= position) ? (endIndex - beginIndex) : (position - beginIndex);
		buffer = BufferUtils.expand(buffer, beginIndex, replaces, length, this);

		position = buffer.position();

		buffer.position(beginIndex);
		buffer.put(src, offset, length);

		buffer.position(position);

		return this;
	}

	public final ExpandableOctetBuffer replace(final int beginIndex, final int endIndex, final byte b) {
		setOutputMode();

		ByteBuffer buffer = this.bufferOctet.buffer();
		int position = buffer.position();

		if (beginIndex >= position) {
			buffer = ensureLength(buffer, beginIndex + 1 - position);

			buffer.put(beginIndex, b);

			return this;
		}

		final int replaces = (endIndex <= position) ? (endIndex - beginIndex) : (position - beginIndex);
		buffer = BufferUtils.expand(buffer, beginIndex, replaces, 1, this);

		buffer.put(beginIndex, b);

		return this;
	}

	/*
	 * Insert methods
	 */

	public final ExpandableOctetBuffer insert(final int offset, final Octet src) {
		return insert(offset, src, src.begin(), src.end());
	}

	public final ExpandableOctetBuffer insert(final int offset, final Octet src, final int start, final int end) {
		if (src.buffer() != null) {
			return insert(offset, src.buffer(), start, end);
		} else
		if (src.array() != null) {
			return insert(offset, src.array(), start + src.arrayOffset(), (end - start));
		}
		return this;
	}

	public final ExpandableOctetBuffer insert(final int offset, final ByteBuffer src, final int start, final int end) {
		return replace(offset, offset, src, start, end);
	}

	public final ExpandableOctetBuffer insert(final int offset, final byte[] src) {
		return insert(offset, src, 0, src.length);
	}

	public final ExpandableOctetBuffer insert(final int offset, final byte[] src, final int srcOffset, final int length) {
		return replace(offset, offset, src, srcOffset, length);
	}

	public final ExpandableOctetBuffer insert(final int offset, final byte b) {
		return replace(offset, offset, b);
	}

	/*
	 * Allocate methods
	 */

	protected final ByteBuffer ensureOutputLength(final int requiredLength) {
		setOutputMode();
		return ensureLength(this.bufferOctet.buffer(), requiredLength);
	}

	protected final ByteBuffer ensureLength(ByteBuffer buffer, final int requiredLength) {
		final int requiredCapacity = buffer.position() + requiredLength;
		if (requiredCapacity > buffer.capacity()) {
			buffer = expandLength(buffer, requiredLength);
		}
		return buffer;
	}

	protected final ByteBuffer expandLength(final ByteBuffer buffer, final int length) {
		final int requiredCapacity = buffer.position() + length;
		final ByteBuffer newBuffer = allocate(buffer, requiredCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	public final ByteBuffer allocate(final ByteBuffer oldBuffer, final int size) {
		final ByteBuffer buffer = allocate(this.allocator, oldBuffer, size);
		this.bufferOctet.setBuffer(buffer);
		return buffer;
	}

	public static final ByteBuffer allocate(final BufferAllocator allocator,
			final ByteBuffer oldBuffer, int size) {
		size = newCapacity(oldBuffer, size);

		ByteBuffer newBuffer;
		if (allocator != null) {
			newBuffer = allocator.allocate(oldBuffer, size);
		} else {
			if (oldBuffer != null && oldBuffer.isDirect()) {
				newBuffer = ByteBuffer.allocateDirect(size);
			} else {
				newBuffer = ByteBuffer.allocate(size);
			}
		}
		return newBuffer;
	}

	private static final int newCapacity(final ByteBuffer buffer, final int requiredCapacity) {
		final int currentCapacity = (buffer != null) ? buffer.capacity() : 0;
		int newCapacity = currentCapacity;

		newCapacity += (newCapacity >> 1);

		if (requiredCapacity > newCapacity) {
			newCapacity = requiredCapacity;
		}

		int padding = newCapacity & 0x03;
		if (padding > 0) padding = (4 - padding);
		newCapacity += padding;

		return newCapacity;
	}

	/*
	 * Mode methods
	 */

	protected final void setOutputMode() {
		if (this.mode != OUTPUT_MODE) {
			this.mode = OUTPUT_MODE;

			final ByteBuffer buffer = this.bufferOctet.buffer();

			if (buffer.hasRemaining()) {
				buffer.compact();
			} else {
				buffer.clear();
			}
		}
	}

	protected final void setInputMode() {
		if (this.mode == OUTPUT_MODE) {
			this.mode = INPUT_MODE;

			final ByteBuffer buffer = this.bufferOctet.buffer();

			buffer.flip();

			this.bufferOctet.setBegin(0);
			this.bufferOctet.setLength(buffer.limit());
		}
	}

	/*
	 * Externalizable methods
	 */

	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(1);
		out.writeInt(this.mode);

		out.writeObject(this.bufferOctet);
	}


	public void readExternal(final ObjectInput in) throws IOException,
			ClassNotFoundException {
		/*int tmp = */in.readInt();
		this.mode = in.readInt();

		final BufferSettableOctet settableOctet = (BufferSettableOctet)in.readObject();
		this.bufferOctet.swap(settableOctet);
	}


	static final class OutputOp extends OctetOutputOp {

		private final ExpandableOctetBuffer expandableBuffer;


		OutputOp(final ExpandableOctetBuffer expandableBuffer, final MutableOctet octet) {
			super(octet, false);
			this.expandableBuffer = expandableBuffer;
			init(octet);
		}


		@Override
		protected final void init(final MutableOctet octet) {
			this.expandableBuffer.setOutputMode();
			super.init(octet);
		}

		@Override
		protected final ByteBuffer expandLength(final ByteBuffer buffer, final int requiredLength) {
			return this.expandableBuffer.expandLength(buffer, requiredLength);
		}

		@Override
		protected final void updateBounds(final int newBegin, final int newEnd) {
			final BufferSettableOctet settableOctet = this.expandableBuffer.bufferOctet;
			settableOctet.setBegin(newBegin);
			settableOctet.setLength(newEnd - newBegin);
		}

	}

	static final class InputOp extends OctetInputOp {

		private final ExpandableOctetBuffer expandableBuffer;


		InputOp(final ExpandableOctetBuffer expandableBuffer, final Octet octet) {
			super(octet, false);
			this.expandableBuffer = expandableBuffer;
			init(octet);
		}


		@Override
		protected void init(final Octet octet) {
			this.expandableBuffer.setInputMode();
			super.init(octet);
		}

	}

}
