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

import java.io.IOException;
import java.nio.ByteBuffer;

import com.chinmobi.octet.ArrayOctet;
import com.chinmobi.octet.BufferAllocator;
import com.chinmobi.octet.ExpandableOctetBuffer;
import com.chinmobi.testapp.BaseTestAction;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class OctetBufferTestAction extends BaseTestAction implements BufferAllocator {

	private static final byte[] TEST_ARRAY_0 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private static final byte[] TEST_ARRAY_a = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j' };
	//private static final byte[] TEST_ARRAY_A = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J' };


	private boolean isAllocateDirect;


	public OctetBufferTestAction() {
		super();
		this.isAllocateDirect = false;
	}


	public final ByteBuffer allocate(final ByteBuffer oldBuffer, final int size) {
		if (this.isAllocateDirect) {
			return ByteBuffer.allocateDirect(size);
		} else {
			return ByteBuffer.allocate(size);
		}
	}


	/*
	 * Test methods
	 */

	public final void testConstruct() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestConstruct();
		}
	}

	private final void doTestConstruct() {
		ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);
		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 4);
		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 5);
		assertEquals(0, octetBuf.length());
		assertEquals(8, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 8);
		assertEquals(0, octetBuf.length());
		assertEquals(8, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 9);
		assertEquals(0, octetBuf.length());
		assertEquals(12, octetBuf.remaining());
		assertEquals(12, octetBuf.capacity());
	}

	public final void testEnsureCapacity() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestEnsureCapacity();
		}
	}

	private final void doTestEnsureCapacity() {
		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf.ensureCapacity(2);
		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf.ensureCapacity(4);
		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf.ensureCapacity(5);
		assertEquals(0, octetBuf.length());
		assertEquals(8, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf.ensureCapacity(9);
		assertEquals(0, octetBuf.length());
		assertEquals(12, octetBuf.remaining());
		assertEquals(12, octetBuf.capacity());

		octetBuf.ensureCapacity(13);
		assertEquals(0, octetBuf.length());
		assertEquals(20, octetBuf.remaining());
		assertEquals(20, octetBuf.capacity());
	}

	public final void testEnsureLength() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestEnsureLength();
		}
	}

	private final void doTestEnsureLength() {
		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf.append(TEST_ARRAY_0, 0, 3);
		assertEquals(3, octetBuf.length());
		assertEquals(1, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		final String expectedString =
				"\r\n" +
				"30 31 32                                          012" + "\r\n";

		assertEquals(expectedString, octetBuf.toString());

		octetBuf.ensureLength(2);
		assertEquals(3, octetBuf.length());
		assertTrue(octetBuf.remaining() >= 2);
		assertEquals(8, octetBuf.capacity());
		assertEquals(expectedString, octetBuf.toString());
	}

	public final void testEnsureCapacity2() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestEnsureCapacity2();
		}
	}

	private final void doTestEnsureCapacity2() {
		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf.append(TEST_ARRAY_0, 0, 3);
		assertEquals(3, octetBuf.length());
		assertEquals(1, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		final String expectedString =
				"\r\n" +
				"30 31 32                                          012" + "\r\n";

		assertEquals(expectedString, octetBuf.toString());
		assertEquals(3, octetBuf.length());
		assertEquals(1, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf.toInput();
		octetBuf.ensureCapacity(8);
		assertEquals(3, octetBuf.length());
		assertEquals(3, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());
		assertEquals(expectedString, octetBuf.toString());

		octetBuf.toOutput();
		assertEquals(3, octetBuf.length());
		assertEquals(5, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());
		assertEquals(expectedString, octetBuf.toString());

		octetBuf.append(TEST_ARRAY_0, 0, 3);
		assertEquals(6, octetBuf.length());
		assertEquals(2, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf.ensureCapacity(9);
		assertEquals(6, octetBuf.length());
		assertEquals(6, octetBuf.remaining());
		assertEquals(12, octetBuf.capacity());

		final String expectedString2 =
				"\r\n" +
				"30 31 32 30 31 32                                 012012" + "\r\n";

		assertEquals(expectedString2, octetBuf.toString());
	}

	public final void testReserveLength() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestReserveLength();
		}
	}

	private final void doTestReserveLength() {
		ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf.reserveLength(0, 0, 5);
		assertEquals(5, octetBuf.length());
		assertEquals(3, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf.reserveLength(7, 0, 3);
		assertEquals(10, octetBuf.length());
		assertEquals(2, octetBuf.remaining());
		assertEquals(12, octetBuf.capacity());

		// -------------------------------------------------
		octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		octetBuf.append(TEST_ARRAY_0, 0, 5);
		assertEquals(5, octetBuf.length());
		assertEquals(3, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString =
				"\r\n" +
				"30 31 32 33 34                                    01234" + "\r\n";

		assertEquals(expectedString, octetBuf.toString());

		octetBuf.reserveLength(1, 2, 1);
		assertEquals(4, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString1 =
				"\r\n" +
				"30 31 33 34                                       0134" + "\r\n";

		assertEquals(expectedString1, octetBuf.toString());

		octetBuf.clear().append(TEST_ARRAY_0, 0, 5);
		octetBuf.reserveLength(1, 2, 0);
		assertEquals(3, octetBuf.length());
		assertEquals(5, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString2 =
				"\r\n" +
				"30 33 34                                          034" + "\r\n";

		assertEquals(expectedString2, octetBuf.toString());


		octetBuf.clear().append(TEST_ARRAY_0, 0, 5);
		octetBuf.reserveLength(3, 3, 1);
		assertEquals(4, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString3 =
				"\r\n" +
				"30 31 32 33                                       0123" + "\r\n";

		assertEquals(expectedString3, octetBuf.toString());

		// -------------------------------------------------
		octetBuf.clear().append(TEST_ARRAY_0, 0, 5);
		octetBuf.reserveLength(1, 2, 4);
		assertEquals(7, octetBuf.length());
		assertEquals(1, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf.fillZero(1, 5);
		final String expectedString4 =
				"\r\n" +
				"30 00 00 00 00 33 34                              0....34" + "\r\n";

		assertEquals(expectedString4, octetBuf.toString());


		octetBuf.clear().append(TEST_ARRAY_0, 0, 5);
		octetBuf.reserveLength(1, 0, 2);
		assertEquals(7, octetBuf.length());
		assertEquals(1, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf.fillZero(1, 3);
		final String expectedString40 =
				"\r\n" +
				"30 00 00 31 32 33 34                              0..1234" + "\r\n";

		assertEquals(expectedString40, octetBuf.toString());


		octetBuf.clear().append(TEST_ARRAY_0, 0, 5);
		octetBuf.reserveLength(3, 3, 5);
		assertEquals(8, octetBuf.length());
		assertEquals(0, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf.fillZero(3, 7);
		final String expectedString5 =
				"\r\n" +
				"30 31 32 00 00 00 00 00                           012....." + "\r\n";

		assertEquals(expectedString5, octetBuf.toString());


		octetBuf.clear().append(TEST_ARRAY_0, 0, 5);
		octetBuf.reserveLength(2, 0, 5);
		assertEquals(10, octetBuf.length());
		assertEquals(2, octetBuf.remaining());
		assertEquals(12, octetBuf.capacity());

		octetBuf.fillZero(2, 7);
		final String expectedString6 =
				"\r\n" +
				"30 31 00 00 00 00 00 32  33 34                    01.....234" + "\r\n";

		assertEquals(expectedString6, octetBuf.toString());


		octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		octetBuf.append(TEST_ARRAY_0, 0, 5);
		octetBuf.reserveLength(2, 1, 6);
		assertEquals(10, octetBuf.length());
		assertEquals(2, octetBuf.remaining());
		assertEquals(12, octetBuf.capacity());

		octetBuf.fillZero(2, 7);
		final String expectedString7 =
				"\r\n" +
				"30 31 00 00 00 00 00 00  33 34                    01......34" + "\r\n";

		assertEquals(expectedString7, octetBuf.toString());
	}

	public final void testDelete() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestDelete();
		}
	}

	private final void doTestDelete() {
		ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());

		octetBuf.delete(1, 3);
		assertEquals(0, octetBuf.length());
		assertEquals(4, octetBuf.remaining());
		assertEquals(4, octetBuf.capacity());


		octetBuf.append(TEST_ARRAY_0, 0, 5);
		assertEquals(5, octetBuf.length());
		assertEquals(3, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		octetBuf.delete(1, 3);
		assertEquals(3, octetBuf.length());
		assertEquals(5, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString2 =
				"\r\n" +
				"30 33 34                                          034" + "\r\n";

		assertEquals(expectedString2, octetBuf.toString());


		octetBuf.clear().append(TEST_ARRAY_0, 0, 5);
		octetBuf.delete(2, 6);
		assertEquals(2, octetBuf.length());
		assertEquals(6, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString3 =
				"\r\n" +
				"30 31                                             01" + "\r\n";

		assertEquals(expectedString3, octetBuf.toString());


		octetBuf.delete(3, 5);
		assertEquals(2, octetBuf.length());
		assertEquals(6, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());
		assertEquals(expectedString3, octetBuf.toString());
	}

	public final void testAppend() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestAppend();
		}
	}

	private final void doTestAppend() {
		ExpandableOctetBuffer octetBufD = new ExpandableOctetBuffer((BufferAllocator)this, 1);
		octetBufD.append(TEST_ARRAY_0, 0, 5);
		assertEquals(5, octetBufD.length());
		assertEquals(3, octetBufD.remaining());
		assertEquals(8, octetBufD.capacity());

		if (this.isAllocateDirect) {
			this.isAllocateDirect = false;
		} else {
			this.isAllocateDirect = true;
		}

		ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);
		octetBuf.append(TEST_ARRAY_0, 0, 5);
		assertEquals(5, octetBuf.length());
		assertEquals(3, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString =
				"\r\n" +
				"30 31 32 33 34                                    01234" + "\r\n";

		assertEquals(expectedString, octetBuf.toString());


		octetBuf.append(octetBufD.toInput(), 1, 3);
		assertEquals(7, octetBuf.length());
		assertEquals(1, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString2 =
				"\r\n" +
				"30 31 32 33 34 31 32                              0123412" + "\r\n";

		assertEquals(expectedString2, octetBuf.toString());
		assertEquals(expectedString, octetBufD.toString());


		octetBuf.append(octetBufD.toInput(), 2, 4);
		assertEquals(9, octetBuf.length());
		assertEquals(3, octetBuf.remaining());
		assertEquals(12, octetBuf.capacity());

		final String expectedString3 =
				"\r\n" +
				"30 31 32 33 34 31 32 32  33                       012341223" + "\r\n";

		assertEquals(expectedString3, octetBuf.toString());
		assertEquals(expectedString, octetBufD.toString());
	}

	public final void testReplace() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestReplace();
		}
	}

	private final void doTestReplace() {
		ExpandableOctetBuffer octetBufB = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		ExpandableOctetBuffer octetBufD = new ExpandableOctetBuffer((BufferAllocator)this, 1);
		octetBufD.append(TEST_ARRAY_0, 0, 5);
		assertEquals(5, octetBufD.length());
		assertEquals(3, octetBufD.remaining());
		assertEquals(8, octetBufD.capacity());

		final String expectedString =
				"\r\n" +
				"30 31 32 33 34                                    01234" + "\r\n";

		assertEquals(expectedString, octetBufD.toString());


		if (this.isAllocateDirect) {
			this.isAllocateDirect = false;
		} else {
			this.isAllocateDirect = true;
		}

		ExpandableOctetBuffer octetBufE = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);
		octetBuf.append(TEST_ARRAY_a, 0, 8);
		assertEquals(8, octetBuf.length());
		assertEquals(0, octetBuf.remaining());
		assertEquals(8, octetBuf.capacity());

		final String expectedString1 =
				"\r\n" +
				"61 62 63 64 65 66 67 68                           abcdefgh" + "\r\n";

		assertEquals(expectedString1, octetBuf.toString());


		octetBufD.replace(6, 8, octetBuf.toInput(), 1, 3);
		assertEquals(8, octetBufD.length());
		assertEquals(0, octetBufD.remaining());
		assertEquals(8, octetBufD.capacity());

		assertEquals(expectedString1, octetBuf.toString());


		octetBufD.clear().append(TEST_ARRAY_0, 0, 5);
		octetBufD.replace(2, 4, octetBuf.toInput(), 1, 4);
		assertEquals(6, octetBufD.length());
		assertEquals(2, octetBufD.remaining());
		assertEquals(8, octetBufD.capacity());

		final String expectedString2 =
				"\r\n" +
				"30 31 62 63 64 34                                 01bcd4" + "\r\n";
		assertEquals(expectedString2, octetBufD.toString());
		assertEquals(expectedString1, octetBuf.toString());


		octetBufD.clear().append(TEST_ARRAY_0, 0, 5);
		octetBufD.replace(2, 4, octetBuf.toInput(), 1, 2);
		assertEquals(4, octetBufD.length());
		assertEquals(4, octetBufD.remaining());
		assertEquals(8, octetBufD.capacity());

		final String expectedString3 =
				"\r\n" +
				"30 31 62 34                                       01b4" + "\r\n";
		assertEquals(expectedString3, octetBufD.toString());
		assertEquals(expectedString1, octetBuf.toString());


		octetBufD.clear().append(TEST_ARRAY_0, 0, 5);
		octetBufD.replace(2, 3, octetBuf.toInput(), 1, 6);
		assertEquals(9, octetBufD.length());
		assertEquals(3, octetBufD.remaining());
		assertEquals(12, octetBufD.capacity());

		final String expectedString4 =
				"\r\n" +
				"30 31 62 63 64 65 66 33  34                       01bcdef34" + "\r\n";
		assertEquals(expectedString4, octetBufD.toString());
		assertEquals(expectedString1, octetBuf.toString());


		octetBufD = octetBufB;
		octetBufD.clear().append(TEST_ARRAY_0, 0, 5);
		octetBufD.replace(3, 6, octetBuf.toInput(), 1, 7);
		assertEquals(9, octetBufD.length());
		assertEquals(3, octetBufD.remaining());
		assertEquals(12, octetBufD.capacity());

		final String expectedString5 =
				"\r\n" +
				"30 31 32 62 63 64 65 66  67                       012bcdefg" + "\r\n";
		assertEquals(expectedString5, octetBufD.toString());
		assertEquals(expectedString1, octetBuf.toString());


		octetBufD = octetBufE;
		octetBufD.clear().append(TEST_ARRAY_0, 0, 5);
		octetBufD.replace(3, 6, (byte)'a');
		assertEquals(4, octetBufD.length());
		assertEquals(4, octetBufD.remaining());
		assertEquals(8, octetBufD.capacity());

		final String expectedString6 =
				"\r\n" +
				"30 31 32 61                                       012a" + "\r\n";
		assertEquals(expectedString6, octetBufD.toString());


		octetBufD.clear().append(TEST_ARRAY_0, 0, 5);
		octetBufD.replace(1, 3, (byte)'a');
		assertEquals(4, octetBufD.length());
		assertEquals(4, octetBufD.remaining());
		assertEquals(8, octetBufD.capacity());

		final String expectedString7 =
				"\r\n" +
				"30 61 33 34                                       0a34" + "\r\n";
		assertEquals(expectedString7, octetBufD.toString());


		octetBufD.clear().append(TEST_ARRAY_0, 0, 5);
		octetBufD.replace(1, 1, (byte)'a');
		assertEquals(6, octetBufD.length());
		assertEquals(2, octetBufD.remaining());
		assertEquals(8, octetBufD.capacity());

		final String expectedString8 =
				"\r\n" +
				"30 61 31 32 33 34                                 0a1234" + "\r\n";
		assertEquals(expectedString8, octetBufD.toString());

	}

	public final void testReplaceAll() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestReplaceAll();
		}
	}

	private final void doTestReplaceAll() {
		ExpandableOctetBuffer octetBufD = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		octetBufD.append(TEST_ARRAY_0).append(TEST_ARRAY_0);
		assertEquals(20, octetBufD.length());
		assertEquals(0, octetBufD.remaining());
		assertEquals(20, octetBufD.capacity());

		final String expectedString =
				"\r\n" +
				"30 31 32 33 34 35 36 37  38 39 30 31 32 33 34 35  0123456789012345" + "\r\n" +
				"36 37 38 39                                       6789" + "\r\n";
		assertEquals(expectedString, octetBufD.toString());

		final ArrayOctet mathed = new ArrayOctet(TEST_ARRAY_0, 1, 2);
		final ArrayOctet replacement = new ArrayOctet(TEST_ARRAY_a, 0, 5);

		octetBufD.replaceAll(mathed, replacement);
		assertEquals(26, octetBufD.length());
		assertEquals(6, octetBufD.remaining());
		assertEquals(32, octetBufD.capacity());

		final String expectedString1 =
				"\r\n" +
				"30 61 62 63 64 65 33 34  35 36 37 38 39 30 61 62  0abcde34567890ab" + "\r\n" +
				"63 64 65 33 34 35 36 37  38 39                    cde3456789" + "\r\n";
		assertEquals(expectedString1, octetBufD.toString());
	}


	public final void testLoadAndStore() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);

			try {
				doTestLoadAndStore();
			} catch (IOException ex) {
				fail(ex);
			}
		}
	}

	private final void doTestLoadAndStore() throws IOException {
		final ExpandableOctetBuffer octetBufS = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		octetBufS.append(TEST_ARRAY_0).append(TEST_ARRAY_0);
		assertEquals(20, octetBufS.length());
		assertEquals(0, octetBufS.remaining());
		assertEquals(20, octetBufS.capacity());

		final String expectedString =
				"\r\n" +
				"30 31 32 33 34 35 36 37  38 39 30 31 32 33 34 35  0123456789012345" + "\r\n" +
				"36 37 38 39                                       6789" + "\r\n";
		assertEquals(expectedString, octetBufS.toString());

		final String fileName = "../tmp/octetbuftest0.txt";

		octetBufS.store(fileName, false);

		final ExpandableOctetBuffer octetBufD = new ExpandableOctetBuffer((BufferAllocator)this, 1);
		octetBufD.load(fileName);

		assertEquals(20, octetBufD.length());
		assertEquals(0, octetBufD.remaining());
		assertEquals(20, octetBufD.capacity());
		assertEquals(expectedString, octetBufD.toString());
	}

}
