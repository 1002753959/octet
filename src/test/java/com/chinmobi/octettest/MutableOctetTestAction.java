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
package com.chinmobi.octettest;

import java.nio.ByteBuffer;

import com.chinmobi.octet.BufferOctet;
import com.chinmobi.testapp.BaseTestAction;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class MutableOctetTestAction extends BaseTestAction {

	private static final byte[] TEST_ARRAY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private boolean isAllocateDirect;


	public MutableOctetTestAction() {
		super();
	}


	/*
	 * Test methods
	 */

	public final void testDelete() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestDelete();
		}
	}

	private final void doTestDelete() {

		final ByteBuffer directBuf =
				(this.isAllocateDirect) ? ByteBuffer.allocateDirect(64) : ByteBuffer.allocate(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);

		assertEquals(0, bufferOctetD.begin());
		assertEquals(10, bufferOctetD.length());

		directBuf.position(7);

		bufferOctetD.delete(1, 5);
		assertEquals(0, bufferOctetD.begin());
		assertEquals(6, bufferOctetD.length());
		assertEquals(3, directBuf.position());

		final String expectedString =
				"\r\n" +
				"30 35 36 37 38 39                                 056789" + "\r\n";

		assertEquals(expectedString, bufferOctetD.toString());


		bufferOctetD.delete(1, 6);
		assertEquals(0, bufferOctetD.begin());
		assertEquals(1, bufferOctetD.length());
		assertEquals(1, directBuf.position());

		final String expectedString1 =
				"\r\n" +
				"30                                                0" + "\r\n";

		assertEquals(expectedString1, bufferOctetD.toString());
	}

	public final void testReplaceAll() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestReplaceAll();
		}
	}

	private final void doTestReplaceAll() {

		final ByteBuffer directBuf =
				(this.isAllocateDirect) ? ByteBuffer.allocateDirect(64) : ByteBuffer.allocate(64);

		directBuf.put(TEST_ARRAY, 0, 5);
		directBuf.put(TEST_ARRAY, 0, 5);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);
		assertEquals(0, bufferOctetD.begin());
		assertEquals(10, bufferOctetD.length());

		directBuf.position(7);

		bufferOctetD.replaceAll((byte)'1', (byte)'b');
		assertEquals(0, bufferOctetD.begin());
		assertEquals(10, bufferOctetD.length());
		assertEquals(7, directBuf.position());

		final String expectedString =
				"\r\n" +
				"30 62 32 33 34 30 62 32  33 34                    0b2340b234" + "\r\n";

		assertEquals(expectedString, bufferOctetD.toString());
	}

	public final void testSetByteAt() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestSetByteAt();
		}
	}

	private final void doTestSetByteAt() {

		final ByteBuffer directBuf =
				(this.isAllocateDirect) ? ByteBuffer.allocateDirect(64) : ByteBuffer.allocate(64);

		directBuf.put(TEST_ARRAY, 0, 5);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);
		assertEquals(0, bufferOctetD.begin());
		assertEquals(5, bufferOctetD.length());

		directBuf.position(3);

		bufferOctetD.setByteAt(1, (byte)'b');
		assertEquals(0, bufferOctetD.begin());
		assertEquals(5, bufferOctetD.length());
		assertEquals(3, directBuf.position());

		final String expectedString =
				"\r\n" +
				"30 62 32 33 34                                    0b234" + "\r\n";

		assertEquals(expectedString, bufferOctetD.toString());
	}

}
