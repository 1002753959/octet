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

import java.io.PrintWriter;
import java.nio.ByteBuffer;

import com.chinmobi.octet.ArrayOctet;
import com.chinmobi.octet.BufferOctet;
import com.chinmobi.octet.Octet;
import com.chinmobi.testapp.BaseTestAction;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class OctetTestAction extends BaseTestAction {

	static final byte[] TEST_ARRAY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j' };


	public OctetTestAction() {
		super();
	}


	public final void init() {
	}

	public final void perform(final String command, final PrintWriter out) {
	}

	public final void destroy() {

	}

	/*
	 * Test methods
	 */

	public final void testCompareTo() {
		Octet octet;

		int expected = 0;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);

		final ArrayOctet subArrayOctet = new ArrayOctet(arrayOctet);


		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);

		final BufferOctet subBufferOctetD = new BufferOctet(bufferOctetD);


		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);

		final BufferOctet subBufferOctet = new BufferOctet(bufferOctet);


		// -------------------------------------------------
		octet = arrayOctet;

		assertEquals(expected, octet.compareTo(subArrayOctet));
		assertEquals(expected, octet.compareTo(subBufferOctetD));
		assertEquals(expected, octet.compareTo(subBufferOctet));

		octet = bufferOctetD;

		assertEquals(expected, octet.compareTo(subArrayOctet));
		assertEquals(expected, octet.compareTo(subBufferOctetD));
		assertEquals(expected, octet.compareTo(subBufferOctet));

		octet = bufferOctet;

		assertEquals(expected, octet.compareTo(subArrayOctet));
		assertEquals(expected, octet.compareTo(subBufferOctetD));
		assertEquals(expected, octet.compareTo(subBufferOctet));

		// -------------------------------------------------
		final int fromIndex = 19;
		final int length = 5;

		subArrayOctet.suboctet(fromIndex, fromIndex + length);
		subBufferOctetD.suboctet(fromIndex, fromIndex + length);
		subBufferOctet.suboctet(fromIndex, fromIndex + length);

		expected = -58;

		octet = arrayOctet;

		assertEquals(expected, octet.compareTo(subArrayOctet));
		assertEquals(expected, octet.compareTo(subBufferOctetD));
		assertEquals(expected, octet.compareTo(subBufferOctet));

		octet = bufferOctetD;

		assertEquals(expected, octet.compareTo(subArrayOctet));
		assertEquals(expected, octet.compareTo(subBufferOctetD));
		assertEquals(expected, octet.compareTo(subBufferOctet));

		octet = bufferOctet;

		assertEquals(expected, octet.compareTo(subArrayOctet));
		assertEquals(expected, octet.compareTo(subBufferOctetD));
		assertEquals(expected, octet.compareTo(subBufferOctet));

		// -------------------------------------------------
	}

	public final void testEquals() {
		Octet octet;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);

		final ArrayOctet subArrayOctet = new ArrayOctet(arrayOctet);


		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);

		final BufferOctet subBufferOctetD = new BufferOctet(bufferOctetD);


		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);

		final BufferOctet subBufferOctet = new BufferOctet(bufferOctet);


		// -------------------------------------------------
		octet = arrayOctet;

		assertTrue(octet.equals(TEST_ARRAY));
		assertTrue(octet.equals(subArrayOctet));
		assertTrue(octet.equals(subBufferOctetD));
		assertTrue(octet.equals(subBufferOctet));

		octet = bufferOctetD;

		assertTrue(octet.equals(TEST_ARRAY));
		assertTrue(octet.equals(subArrayOctet));
		assertTrue(octet.equals(subBufferOctetD));
		assertTrue(octet.equals(subBufferOctet));

		octet = bufferOctet;

		assertTrue(octet.equals(TEST_ARRAY));
		assertTrue(octet.equals(subArrayOctet));
		assertTrue(octet.equals(subBufferOctetD));
		assertTrue(octet.equals(subBufferOctet));

		// -------------------------------------------------
	}

	public final void testStartsWith() {
		Octet octet;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);

		final ArrayOctet subArrayOctet = new ArrayOctet(arrayOctet);


		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);

		final BufferOctet subBufferOctetD = new BufferOctet(bufferOctetD);


		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);

		final BufferOctet subBufferOctet = new BufferOctet(bufferOctet);


		// -------------------------------------------------
		final int fromIndex = 19;
		final int length = 5;

		subArrayOctet.suboctet(fromIndex, fromIndex + length);
		subBufferOctetD.suboctet(fromIndex, fromIndex + length);
		subBufferOctet.suboctet(fromIndex, fromIndex + length);

		octet = arrayOctet;

		assertTrue(octet.startsWith(fromIndex, TEST_ARRAY, fromIndex, length));
		assertTrue(octet.startsWith(fromIndex, subArrayOctet));
		assertTrue(octet.startsWith(fromIndex, subBufferOctetD));
		assertTrue(octet.startsWith(fromIndex, subBufferOctet));

		octet = bufferOctetD;

		assertTrue(octet.startsWith(fromIndex, TEST_ARRAY, fromIndex, length));
		assertTrue(octet.startsWith(fromIndex, subArrayOctet));
		assertTrue(octet.startsWith(fromIndex, subBufferOctetD));
		assertTrue(octet.startsWith(fromIndex, subBufferOctet));

		octet = bufferOctet;

		assertTrue(octet.startsWith(fromIndex, TEST_ARRAY, fromIndex, length));
		assertTrue(octet.startsWith(fromIndex, subArrayOctet));
		assertTrue(octet.startsWith(fromIndex, subBufferOctetD));
		assertTrue(octet.startsWith(fromIndex, subBufferOctet));

		// -------------------------------------------------
	}

	public final void testEndsWith() {
		Octet octet;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);

		final ArrayOctet subArrayOctet = new ArrayOctet(arrayOctet);


		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);

		final BufferOctet subBufferOctetD = new BufferOctet(bufferOctetD);


		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);

		final BufferOctet subBufferOctet = new BufferOctet(bufferOctet);


		// -------------------------------------------------
		final int fromIndex = 19;

		subArrayOctet.suboctet(fromIndex);
		subBufferOctetD.suboctet(fromIndex);
		subBufferOctet.suboctet(fromIndex);

		octet = arrayOctet;

		assertTrue(octet.endsWith(TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertTrue(octet.endsWith(subArrayOctet));
		assertTrue(octet.endsWith(subBufferOctetD));
		assertTrue(octet.endsWith(subBufferOctet));

		octet = bufferOctetD;

		assertTrue(octet.endsWith(TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertTrue(octet.endsWith(subArrayOctet));
		assertTrue(octet.endsWith(subBufferOctetD));
		assertTrue(octet.endsWith(subBufferOctet));

		octet = bufferOctet;

		assertTrue(octet.endsWith(TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertTrue(octet.endsWith(subArrayOctet));
		assertTrue(octet.endsWith(subBufferOctetD));
		assertTrue(octet.endsWith(subBufferOctet));

		// -------------------------------------------------
	}

	public final void testIndexOfByte() {
		Octet octet;

		final int expected1 = 11;
		final int expected2 = 21;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);
		octet = arrayOctet;


		assertEquals(expected1, octet.indexOf((byte)'b'));
		assertEquals(expected2, octet.lastIndexOf((byte)'b'));
		assertEquals(expected1, octet.lastIndexOf(19, (byte)'b'));
		assertEquals(expected2, octet.indexOf(12, (byte)'b'));
		assertEquals(expected1, octet.indexOneOf(TEST_ARRAY, 11, 3));
		assertEquals(expected2, octet.indexOneOf(15, TEST_ARRAY, 11, 3));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);
		octet = bufferOctetD;


		assertEquals(expected1, octet.indexOf((byte)'b'));
		assertEquals(expected2, octet.lastIndexOf((byte)'b'));
		assertEquals(expected1, octet.lastIndexOf(19, (byte)'b'));
		assertEquals(expected2, octet.indexOf(12, (byte)'b'));
		assertEquals(expected1, octet.indexOneOf(TEST_ARRAY, 11, 3));
		assertEquals(expected2, octet.indexOneOf(15, TEST_ARRAY, 11, 3));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);
		octet = bufferOctet;


		assertEquals(expected1, octet.indexOf((byte)'b'));
		assertEquals(expected2, octet.lastIndexOf((byte)'b'));
		assertEquals(expected1, octet.lastIndexOf(19, (byte)'b'));
		assertEquals(expected2, octet.indexOf(12, (byte)'b'));
		assertEquals(expected1, octet.indexOneOf(TEST_ARRAY, 11, 3));
		assertEquals(expected2, octet.indexOneOf(15, TEST_ARRAY, 11, 3));

		// -------------------------------------------------
	}

	public final void testLastIndexOfFromIndex() {
		Octet octet;

		final int fromIndex = 19;

		final int expected = 11;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);
		octet = arrayOctet;

		assertEquals(expected, octet.lastIndexOf(fromIndex, TEST_ARRAY, 11, 3));

		final ArrayOctet subArrayOctet = new ArrayOctet(arrayOctet);
		subArrayOctet.suboctet(11, 14);

		assertEquals(expected, octet.lastIndexOf(fromIndex, subArrayOctet));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);
		octet = bufferOctetD;

		final BufferOctet subBufferOctetD = new BufferOctet(bufferOctetD);
		subBufferOctetD.suboctet(11, 14);

		assertEquals(expected, octet.lastIndexOf(fromIndex, TEST_ARRAY, 11, 3));
		assertEquals(expected, octet.lastIndexOf(fromIndex, subArrayOctet));
		assertEquals(expected, octet.lastIndexOf(fromIndex, subBufferOctetD));

		octet = arrayOctet;

		assertEquals(expected, octet.lastIndexOf(fromIndex, subBufferOctetD));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);
		octet = bufferOctet;

		final BufferOctet subBufferOctet = new BufferOctet(bufferOctet);
		subBufferOctet.suboctet(11, 14);

		assertEquals(expected, octet.lastIndexOf(fromIndex, TEST_ARRAY, 11, 3));
		assertEquals(expected, octet.lastIndexOf(fromIndex, subArrayOctet));
		assertEquals(expected, octet.lastIndexOf(fromIndex, subBufferOctetD));
		assertEquals(expected, octet.lastIndexOf(fromIndex, subBufferOctet));

		octet = bufferOctetD;

		assertEquals(expected, octet.lastIndexOf(fromIndex, subBufferOctet));

		octet = arrayOctet;

		assertEquals(expected, octet.lastIndexOf(fromIndex, subBufferOctet));

		// -------------------------------------------------
	}

	public final void testLastIndexOf() {
		Octet octet;

		final int expected = 21;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);
		octet = arrayOctet;

		assertEquals(expected, octet.lastIndexOf(TEST_ARRAY, 11, 3));

		final ArrayOctet subArrayOctet = new ArrayOctet(arrayOctet);
		subArrayOctet.suboctet(11, 14);

		assertEquals(expected, octet.lastIndexOf(subArrayOctet));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);
		octet = bufferOctetD;

		final BufferOctet subBufferOctetD = new BufferOctet(bufferOctetD);
		subBufferOctetD.suboctet(11, 14);

		assertEquals(expected, octet.lastIndexOf(TEST_ARRAY, 11, 3));
		assertEquals(expected, octet.lastIndexOf(subArrayOctet));
		assertEquals(expected, octet.lastIndexOf(subBufferOctetD));

		octet = arrayOctet;

		assertEquals(expected, octet.lastIndexOf(subBufferOctetD));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);
		octet = bufferOctet;

		final BufferOctet subBufferOctet = new BufferOctet(bufferOctet);
		subBufferOctet.suboctet(11, 14);

		assertEquals(expected, octet.lastIndexOf(TEST_ARRAY, 11, 3));
		assertEquals(expected, octet.lastIndexOf(subArrayOctet));
		assertEquals(expected, octet.lastIndexOf(subBufferOctetD));
		assertEquals(expected, octet.lastIndexOf(subBufferOctet));

		octet = bufferOctetD;

		assertEquals(expected, octet.lastIndexOf(subBufferOctet));

		octet = arrayOctet;

		assertEquals(expected, octet.lastIndexOf(subBufferOctet));

		// -------------------------------------------------
	}

	public final void testIndexOf() {
		Octet octet;

		final int expected = 11;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);
		octet = arrayOctet;

		assertEquals(expected, octet.indexOf(TEST_ARRAY, 11, 3));

		final ArrayOctet subArrayOctet = new ArrayOctet(arrayOctet);
		subArrayOctet.suboctet(11, 14);

		assertEquals(expected, octet.indexOf(subArrayOctet));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);
		octet = bufferOctetD;

		final BufferOctet subBufferOctetD = new BufferOctet(bufferOctetD);
		subBufferOctetD.suboctet(11, 14);

		assertEquals(expected, octet.indexOf(TEST_ARRAY, 11, 3));
		assertEquals(expected, octet.indexOf(subArrayOctet));
		assertEquals(expected, octet.indexOf(subBufferOctetD));

		octet = arrayOctet;

		assertEquals(expected, octet.indexOf(subBufferOctetD));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);
		octet = bufferOctet;

		final BufferOctet subBufferOctet = new BufferOctet(bufferOctet);
		subBufferOctet.suboctet(11, 14);

		assertEquals(expected, octet.indexOf(TEST_ARRAY, 11, 3));
		assertEquals(expected, octet.indexOf(subArrayOctet));
		assertEquals(expected, octet.indexOf(subBufferOctetD));
		assertEquals(expected, octet.indexOf(subBufferOctet));

		octet = bufferOctetD;

		assertEquals(expected, octet.indexOf(subBufferOctet));

		octet = arrayOctet;

		assertEquals(expected, octet.indexOf(subBufferOctet));

		// -------------------------------------------------
	}

	public final void testIndexOfFromIndex() {
		Octet octet;

		final int fromIndex = 12;

		final int expected = 21;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);
		octet = arrayOctet;

		assertEquals(expected, octet.indexOf(fromIndex, TEST_ARRAY, 11, 3));

		final ArrayOctet subArrayOctet = new ArrayOctet(arrayOctet);
		subArrayOctet.suboctet(11, 14);

		assertEquals(expected, octet.indexOf(fromIndex, subArrayOctet));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);
		octet = bufferOctetD;

		final BufferOctet subBufferOctetD = new BufferOctet(bufferOctetD);
		subBufferOctetD.suboctet(11, 14);

		assertEquals(expected, octet.indexOf(fromIndex, TEST_ARRAY, 11, 3));
		assertEquals(expected, octet.indexOf(fromIndex, subArrayOctet));
		assertEquals(expected, octet.indexOf(fromIndex, subBufferOctetD));

		octet = arrayOctet;

		assertEquals(expected, octet.indexOf(fromIndex, subBufferOctetD));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);
		octet = bufferOctet;

		final BufferOctet subBufferOctet = new BufferOctet(bufferOctet);
		subBufferOctet.suboctet(11, 14);

		assertEquals(expected, octet.indexOf(fromIndex, TEST_ARRAY, 11, 3));
		assertEquals(expected, octet.indexOf(fromIndex, subArrayOctet));
		assertEquals(expected, octet.indexOf(fromIndex, subBufferOctetD));
		assertEquals(expected, octet.indexOf(fromIndex, subBufferOctet));

		octet = bufferOctetD;

		assertEquals(expected, octet.indexOf(fromIndex, subBufferOctet));

		octet = arrayOctet;

		assertEquals(expected, octet.indexOf(fromIndex, subBufferOctet));

		// -------------------------------------------------
	}

	public final void testBaseMethods() {
		Octet octet;

		// -------------------------------------------------
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY);
		octet = arrayOctet;

		assertOctet(octet);

		octet = arrayOctet.wrap(arrayOctet.getBytes());
		assertOctet(octet);

		octet = octet.suboctet(3);
		assertSubOctet_3_(octet);

		octet = octet.suboctet(5, 10);
		assertSubOctet_5_10(octet);

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferOctet bufferOctetD = new BufferOctet(directBuf);

		octet = arrayOctet.wrap(bufferOctetD.getBytes());
		assertOctet(octet);

		octet = bufferOctetD;
		assertOctet(octet);

		octet = octet.suboctet(3);
		assertSubOctet_3_(octet);

		octet = octet.suboctet(5, 10);
		assertSubOctet_5_10(octet);

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferOctet bufferOctet = new BufferOctet(buf);

		octet = arrayOctet.wrap(bufferOctet.getBytes());
		assertOctet(octet);

		octet = bufferOctet;
		assertOctet(octet);

		octet = octet.suboctet(3);
		assertSubOctet_3_(octet);

		octet = octet.suboctet(5, 10);
		assertSubOctet_5_10(octet);

		// -------------------------------------------------
	}

	private final void assertOctet(final Octet octet) {
		final String expectedFormattedHex =
				"\r\n" +
				"30 31 32 33 34 35 36 37  38 39 61 62 63 64 65 66  0123456789abcdef" + "\r\n" +
				"67 68 69 6A 61 62 63 64  65 66 67 68 69 6A        ghijabcdefghij"   + "\r\n";

		final byte expectedByte = (byte)'9';
		final int expectedHashCode = -812289905;

		final int index = 9;

		assertEquals(0, octet.begin());
		assertEquals(30, octet.length());
		assertEquals(30, octet.end());
		assertEquals(expectedByte, octet.byteAt(index));
		assertEquals(expectedFormattedHex, octet.toString());
		assertEquals(expectedHashCode, octet.hashCode());
	}

	private final void assertSubOctet_3_(final Octet octet) {
		final String expectedFormattedHex =
				"\r\n" +
				"33 34 35 36 37 38 39 61  62 63 64 65 66 67 68 69  3456789abcdefghi" + "\r\n" +
				"6A 61 62 63 64 65 66 67  68 69 6A                 jabcdefghij"      + "\r\n";

		final byte expectedByte = (byte)'9';
		final int expectedHashCode = -1105783424;

		final int index = 9;

		assertEquals(3, octet.begin());
		assertEquals(27, octet.length());
		assertEquals(30, octet.end());
		assertEquals(expectedByte, octet.byteAt(index));
		assertEquals(expectedFormattedHex, octet.toString());
		assertEquals(expectedHashCode, octet.hashCode());
	}

	private final void assertSubOctet_5_10(final Octet octet) {
		final String expectedFormattedHex =
				"\r\n" +
				"35 36 37 38 39                                    56789"            + "\r\n";

		final byte expectedByte = (byte)'9';
		final int expectedHashCode = 50609975;

		final int index = 9;

		assertEquals(5, octet.begin());
		assertEquals(5, octet.length());
		assertEquals(10, octet.end());
		assertEquals(expectedByte, octet.byteAt(index));
		assertEquals(expectedFormattedHex, octet.toString());
		assertEquals(expectedHashCode, octet.hashCode());
	}

}
