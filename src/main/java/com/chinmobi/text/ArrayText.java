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

import com.chinmobi.octet.data.ArrayData;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public class ArrayText extends AbstractTextOctet {


	public ArrayText() {
		super(new ArrayData());
	}

	public ArrayText(final byte[] array) {
		this(array, 0, array.length);
	}

	public ArrayText(final byte[] array, final int offset, final int length) {
		super(new ArrayData(array));

		this.begin = offset;
		this.length = length;
	}

	public ArrayText(final ArrayText source) {
		super((AbstractTextOctet)source);
	}


	public ArrayText wrap(final byte[] array) {
		return wrap(array, 0, array.length);
	}

	public ArrayText wrap(final byte[] array, final int offset, final int length) {
		data().wrap(array);

		this.begin = offset;
		this.length = length;

		return this;
	}


	public ArrayText set(final ArrayText source) {
		super.set((AbstractTextOctet)source);
		return this;
	}

	public ArrayText swap(final ArrayText another) {
		super.swap((AbstractTextOctet)another);
		return this;
	}


	protected final ArrayData data() {
		return (ArrayData)this.data;
	}

}
