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

import com.chinmobi.octet.BufferSettableOctet;
import com.chinmobi.octet.data.BufferData;
import com.chinmobi.octet.io.OctetInputOp;
import com.chinmobi.octet.io.OctetOpSupplier;
import com.chinmobi.octet.io.OctetOutputOp;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public class BufferText extends AbstractMutableTextOctet implements BufferSettableOctet {

	private transient OctetOpSupplier opSupplier;


	public BufferText() {
		super(new BufferData());
	}

	public BufferText(final ByteBuffer buffer) {
		this(buffer, buffer.position(), buffer.remaining());
	}

	public BufferText(final ByteBuffer buffer, final int offset, final int length) {
		super(new BufferData(buffer));

		this.begin = offset;
		this.length = length;
	}

	public BufferText(final BufferText source) {
		super((AbstractMutableTextOctet)source);
	}


	public BufferText wrap(final ByteBuffer buffer) {
		return wrap(buffer, buffer.position(), buffer.remaining());
	}

	public BufferText wrap(final ByteBuffer buffer, final int offset, final int length) {
		data().wrap(buffer);

		this.begin = offset;
		this.length = length;

		return this;
	}


	public BufferText set(final BufferText source) {
		super.set((AbstractMutableTextOctet)source);
		return this;
	}

	public BufferText swap(final BufferText another) {
		super.swap((AbstractMutableTextOctet)another);
		return this;
	}


	protected final BufferData data() {
		return (BufferData)this.data;
	}

	@Override
	public final OctetInputOp inputOp() {
		final OctetOpSupplier supplier = this.opSupplier;
		if (supplier != null) {
			return supplier.inputOp();
		} else {
			return super.inputOp();
		}
	}

	@Override
	public final OctetOutputOp outputOp() {
		final OctetOpSupplier supplier = this.opSupplier;
		if (supplier != null) {
			return supplier.outputOp();
		} else {
			return super.outputOp();
		}
	}

	/*
	 * BufferSettableOctet methods
	 */

	public final void setOpSupplier(final OctetOpSupplier supplier) {
		this.opSupplier = supplier;
	}

	public final void setBuffer(final ByteBuffer buffer) {
		data().wrap(buffer);
	}

	public final void setBegin(final int begin) {
		this.begin = begin;
	}

	public final void setLength(final int length) {
		this.length = length;
	}

	public final void swap(final BufferSettableOctet settableOctet) {
		if (settableOctet instanceof BufferText) {
			swap((BufferText)settableOctet);
			return;
		}

		final ByteBuffer bufTmp = data().buffer();
		data().wrap(settableOctet.buffer());
		settableOctet.setBuffer(bufTmp);

		int tmp = this.begin;
		this.begin = settableOctet.begin();
		settableOctet.setBegin(tmp);

		tmp = this.length;
		this.length = settableOctet.length();
		settableOctet.setLength(tmp);
	}

}
