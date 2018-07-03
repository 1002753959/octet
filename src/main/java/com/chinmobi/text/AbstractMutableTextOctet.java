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

import com.chinmobi.octet.AbstractMutableOctet;
import com.chinmobi.octet.MutableOctet;
import com.chinmobi.octet.data.OctetData;
import com.chinmobi.octet.io.OctetOutputOp;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public abstract class AbstractMutableTextOctet extends AbstractTextOctet
	implements MutableOctet {

	/**
	 *
	 */
	private static final long serialVersionUID = -8691496274955559439L;


	protected AbstractMutableTextOctet(final OctetData data) {
		super(data);
	}

	protected AbstractMutableTextOctet(final AbstractMutableTextOctet source) {
		super((AbstractTextOctet)source);
	}


	protected void set(final AbstractMutableTextOctet source) {
		super.set((AbstractTextOctet)source);
	}

	protected void swap(final AbstractMutableTextOctet another) {
		super.swap((AbstractTextOctet)another);
	}

	/*
	 * mutable methods
	 */

	public MutableOctet clear() {
		final ByteBuffer buf = buffer();
		if (buf != null) {
			buf.clear();
		}

		this.begin = 0;
		this.length = 0;

		return this;
	}

	public final MutableOctet delete(final int start, final int end) {
		AbstractMutableOctet.delete(this, start, end);
		return this;
	}

	public final MutableOctet replaceAll(final byte oldByte, final byte newByte) {
		AbstractMutableOctet.replaceAll(this, oldByte, newByte);
		return this;
	}

	public final MutableOctet setByteAt(final int index, final byte b) {
		if (index >= begin() && index < end()) {
			this.data.setByteAt(index, b);
		}
		return this;
	}

	public OctetOutputOp outputOp() {
		return new OutputOp(this, this);
	}


	static final class OutputOp extends OctetOutputOp {

		private final AbstractMutableTextOctet mutableOctet;


		OutputOp(final AbstractMutableTextOctet mutableOctet, final MutableOctet octet) {
			super(octet, false);
			this.mutableOctet = mutableOctet;
			init(octet);
		}


		@Override
		protected final void init(final MutableOctet octet) {
			final ByteBuffer buf = octet.buffer();
			if (buf != null) {
				buf.clear().position(octet.end());
			}
			super.init(octet);
		}

		@Override
		protected final ByteBuffer expandLength(final ByteBuffer buffer, final int requiredLength) {
			return buffer;
		}

		@Override
		protected final void updateBounds(final int newBegin, final int newEnd) {
			this.mutableOctet.begin = newBegin;
			this.mutableOctet.length = newEnd - newBegin;
		}

	}

}
