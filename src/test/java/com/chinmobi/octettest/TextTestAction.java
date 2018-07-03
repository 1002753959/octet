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
import java.io.PrintWriter;
import java.nio.ByteBuffer;

import com.chinmobi.testapp.BaseTestAction;
import com.chinmobi.text.ArrayText;
import com.chinmobi.text.BufferText;
import com.chinmobi.text.TextOctet;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class TextTestAction extends BaseTestAction {

	static final byte[] TEST_ARRAY = OctetTestAction.TEST_ARRAY;


	public TextTestAction() {

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

	public final void testCompareToIgnoreCase() {
		TextOctet text;

		int expected = 0;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);

		final ArrayText subArrayText = new ArrayText(arrayText);


		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);

		bufferTextD.setByteAt(20, (byte)'A');

		final BufferText subBufferTextD = new BufferText(bufferTextD);


		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);

		final BufferText subBufferText = new BufferText(bufferText);

		bufferText.setByteAt(22, (byte)'C');


		// -------------------------------------------------
		text = arrayText;

		assertEquals(expected, text.compareToIgnoreCase(subArrayText));
		assertEquals(expected, text.compareToIgnoreCase(subBufferTextD));
		assertEquals(expected, text.compareToIgnoreCase(subBufferText));

		text = bufferTextD;

		assertEquals(expected, text.compareToIgnoreCase(subArrayText));
		assertEquals(expected, text.compareToIgnoreCase(subBufferTextD));
		assertEquals(expected, text.compareToIgnoreCase(subBufferText));

		text = bufferText;

		assertEquals(expected, text.compareToIgnoreCase(subArrayText));
		assertEquals(expected, text.compareToIgnoreCase(subBufferTextD));
		assertEquals(expected, text.compareToIgnoreCase(subBufferText));

		// -------------------------------------------------
		final int fromIndex = 0;
		final int endIndex = 26;

		subArrayText.suboctet(fromIndex, endIndex);
		subBufferTextD.suboctet(fromIndex, endIndex);
		subBufferText.suboctet(fromIndex, endIndex);

		expected = 4;

		text = arrayText;

		assertEquals(expected, text.compareToIgnoreCase(subArrayText));
		assertEquals(expected, text.compareToIgnoreCase(subBufferTextD));
		assertEquals(expected, text.compareToIgnoreCase(subBufferText));

		text = bufferTextD;

		assertEquals(expected, text.compareToIgnoreCase(subArrayText));
		assertEquals(expected, text.compareToIgnoreCase(subBufferTextD));
		assertEquals(expected, text.compareToIgnoreCase(subBufferText));

		text = bufferText;

		assertEquals(expected, text.compareToIgnoreCase(subArrayText));
		assertEquals(expected, text.compareToIgnoreCase(subBufferTextD));
		assertEquals(expected, text.compareToIgnoreCase(subBufferText));

		// -------------------------------------------------
	}

	public final void testEqualsIgnoreCase() {
		TextOctet text;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);

		final ArrayText subArrayText = new ArrayText(arrayText);


		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);

		bufferTextD.setByteAt(20, (byte)'A');

		final BufferText subBufferTextD = new BufferText(bufferTextD);


		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);

		final BufferText subBufferText = new BufferText(bufferText);

		bufferText.setByteAt(22, (byte)'C');


		// -------------------------------------------------
		text = arrayText;

		assertTrue(text.equalsIgnoreCase(TEST_ARRAY));
		assertTrue(text.equalsIgnoreCase(subArrayText));
		assertTrue(text.equalsIgnoreCase(subBufferTextD));
		assertTrue(text.equalsIgnoreCase(subBufferText));

		text = bufferTextD;

		assertTrue(text.equalsIgnoreCase(TEST_ARRAY));
		assertTrue(text.equalsIgnoreCase(subArrayText));
		assertTrue(text.equalsIgnoreCase(subBufferTextD));
		assertTrue(text.equalsIgnoreCase(subBufferText));

		text = bufferText;

		assertTrue(text.equalsIgnoreCase(TEST_ARRAY));
		assertTrue(text.equalsIgnoreCase(subArrayText));
		assertTrue(text.equalsIgnoreCase(subBufferTextD));
		assertTrue(text.equalsIgnoreCase(subBufferText));

		// -------------------------------------------------
	}

	public final void testStartsWith() {
		TextOctet text;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);

		final ArrayText subArrayText = new ArrayText(arrayText);


		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);

		bufferTextD.setByteAt(20, (byte)'A');

		final BufferText subBufferTextD = new BufferText(bufferTextD);


		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);

		final BufferText subBufferText = new BufferText(bufferText);

		bufferText.setByteAt(22, (byte)'C');


		// -------------------------------------------------
		final int fromIndex = 19;
		final int length = 5;

		subArrayText.suboctet(fromIndex, fromIndex + length);
		subBufferTextD.suboctet(fromIndex, fromIndex + length);
		subBufferText.suboctet(fromIndex, fromIndex + length);

		text = arrayText;

		assertTrue(text.startsWith(true, fromIndex, TEST_ARRAY, fromIndex, length));
		assertTrue(text.startsWith(true, fromIndex, subArrayText));
		assertTrue(text.startsWith(true, fromIndex, subBufferTextD));
		assertTrue(text.startsWith(true, fromIndex, subBufferText));

		assertTrue(text.startsWith(false, fromIndex, TEST_ARRAY, fromIndex, length));
		assertTrue(text.startsWith(false, fromIndex, subArrayText));
		assertFalse(text.startsWith(false, fromIndex, subBufferTextD));
		assertFalse(text.startsWith(false, fromIndex, subBufferText));

		text = bufferTextD;

		assertTrue(text.startsWith(true, fromIndex, TEST_ARRAY, fromIndex, length));
		assertTrue(text.startsWith(true, fromIndex, subArrayText));
		assertTrue(text.startsWith(true, fromIndex, subBufferTextD));
		assertTrue(text.startsWith(true, fromIndex, subBufferText));

		assertFalse(text.startsWith(false, fromIndex, TEST_ARRAY, fromIndex, length));
		assertFalse(text.startsWith(false, fromIndex, subArrayText));
		assertTrue(text.startsWith(false, fromIndex, subBufferTextD));
		assertFalse(text.startsWith(false, fromIndex, subBufferText));

		text = bufferText;

		assertTrue(text.startsWith(true, fromIndex, TEST_ARRAY, fromIndex, length));
		assertTrue(text.startsWith(true, fromIndex, subArrayText));
		assertTrue(text.startsWith(true, fromIndex, subBufferTextD));
		assertTrue(text.startsWith(true, fromIndex, subBufferText));

		assertFalse(text.startsWith(false, fromIndex, TEST_ARRAY, fromIndex, length));
		assertFalse(text.startsWith(false, fromIndex, subArrayText));
		assertFalse(text.startsWith(false, fromIndex, subBufferTextD));
		assertTrue(text.startsWith(false, fromIndex, subBufferText));

		// -------------------------------------------------
	}

	public final void testEndsWith() {
		TextOctet text;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);

		final ArrayText subArrayText = new ArrayText(arrayText);


		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);

		bufferTextD.setByteAt(20, (byte)'A');

		final BufferText subBufferTextD = new BufferText(bufferTextD);


		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);

		final BufferText subBufferText = new BufferText(bufferText);

		bufferText.setByteAt(22, (byte)'C');


		// -------------------------------------------------
		final int fromIndex = 19;

		subArrayText.suboctet(fromIndex);
		subBufferTextD.suboctet(fromIndex);
		subBufferText.suboctet(fromIndex);

		text = arrayText;

		assertTrue(text.endsWith(true, TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertTrue(text.endsWith(true, subArrayText));
		assertTrue(text.endsWith(true, subBufferTextD));
		assertTrue(text.endsWith(true, subBufferText));

		assertTrue(text.endsWith(false, TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertTrue(text.endsWith(false, subArrayText));
		assertFalse(text.endsWith(false, subBufferTextD));
		assertFalse(text.endsWith(false, subBufferText));

		text = bufferTextD;

		assertTrue(text.endsWith(true, TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertTrue(text.endsWith(true, subArrayText));
		assertTrue(text.endsWith(true, subBufferTextD));
		assertTrue(text.endsWith(true, subBufferText));

		assertFalse(text.endsWith(false, TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertFalse(text.endsWith(false, subArrayText));
		assertTrue(text.endsWith(false, subBufferTextD));
		assertFalse(text.endsWith(false, subBufferText));

		text = bufferText;

		assertTrue(text.endsWith(true, TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertTrue(text.endsWith(true, subArrayText));
		assertTrue(text.endsWith(true, subBufferTextD));
		assertTrue(text.endsWith(true, subBufferText));

		assertFalse(text.endsWith(false, TEST_ARRAY, fromIndex, TEST_ARRAY.length - fromIndex));
		assertFalse(text.endsWith(false, subArrayText));
		assertFalse(text.endsWith(false, subBufferTextD));
		assertTrue(text.endsWith(false, subBufferText));

		// -------------------------------------------------
	}

	public final void testIndexOf() {
		TextOctet text;

		final int expected = 11;
		final int expected2 = 21;
		final int expected_1 = -1;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);
		text = arrayText;

		assertEquals(expected, text.indexOf(true, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.indexOf(false, TEST_ARRAY, 11, 4));

		final ArrayText subArrayText = new ArrayText(arrayText);
		subArrayText.suboctet(21, 25);

		assertEquals(expected, text.indexOf(true, subArrayText));
		assertEquals(expected, text.indexOf(false, subArrayText));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);
		text = bufferTextD;

		bufferTextD.setByteAt(21, (byte)'B');

		final BufferText subBufferTextD = new BufferText(bufferTextD);
		subBufferTextD.suboctet(21, 25);

		assertEquals(expected, text.indexOf(true, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.indexOf(true, subArrayText));
		assertEquals(expected, text.indexOf(true, subBufferTextD));

		assertEquals(expected, text.indexOf(false, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.indexOf(false, subArrayText));
		assertEquals(expected2, text.indexOf(false, subBufferTextD));

		text = arrayText;

		assertEquals(expected, text.indexOf(true, subBufferTextD));
		assertEquals(expected_1, text.indexOf(false, subBufferTextD));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);
		text = bufferText;

		bufferText.setByteAt(23, (byte)'D');

		final BufferText subBufferText = new BufferText(bufferText);
		subBufferText.suboctet(21, 25);

		assertEquals(expected, text.indexOf(true, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.indexOf(true, subArrayText));
		assertEquals(expected, text.indexOf(true, subBufferTextD));
		assertEquals(expected, text.indexOf(true, subBufferText));

		assertEquals(expected, text.indexOf(false, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.indexOf(false, subArrayText));
		assertEquals(expected_1, text.indexOf(false, subBufferTextD));
		assertEquals(expected2, text.indexOf(false, subBufferText));

		text = bufferTextD;

		assertEquals(expected, text.indexOf(true, subBufferText));
		assertEquals(expected_1, text.indexOf(false, subBufferText));


		text = arrayText;

		assertEquals(expected, text.indexOf(true, subBufferText));
		assertEquals(expected_1, text.indexOf(false, subBufferText));

		// -------------------------------------------------
	}

	public final void testIndexOfFromIndex() {
		TextOctet text;

		final int fromIndex = 12;

		final int expected = 21;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);
		text = arrayText;

		assertEquals(expected, text.indexOf(true, fromIndex, TEST_ARRAY, 11, 4));

		final ArrayText subArrayText = new ArrayText(arrayText);
		subArrayText.suboctet(11, 15);

		assertEquals(expected, text.indexOf(true, fromIndex, subArrayText));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);
		text = bufferTextD;

		bufferTextD.setByteAt(21, (byte)'B');

		final BufferText subBufferTextD = new BufferText(bufferTextD);
		subBufferTextD.suboctet(11, 15);

		assertEquals(expected, text.indexOf(true, fromIndex, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.indexOf(true, fromIndex, subArrayText));
		assertEquals(expected, text.indexOf(true, fromIndex, subBufferTextD));

		text = arrayText;

		assertEquals(expected, text.indexOf(true, fromIndex, subBufferTextD));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);
		text = bufferText;

		bufferText.setByteAt(23, (byte)'D');

		final BufferText subBufferText = new BufferText(bufferText);
		subBufferText.suboctet(11, 15);

		assertEquals(expected, text.indexOf(true, fromIndex, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.indexOf(true, fromIndex, subArrayText));
		assertEquals(expected, text.indexOf(true, fromIndex, subBufferTextD));
		assertEquals(expected, text.indexOf(true, fromIndex, subBufferText));

		text = bufferTextD;

		assertEquals(expected, text.indexOf(true, fromIndex, subBufferText));

		text = arrayText;

		assertEquals(expected, text.indexOf(true, fromIndex, subBufferText));

		// -------------------------------------------------
	}

	public final void testLastIndexOf() {
		TextOctet text;

		final int expected = 21;
		final int expected1 = 11;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);
		text = arrayText;

		assertEquals(expected, text.lastIndexOf(true, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.lastIndexOf(false, TEST_ARRAY, 11, 4));

		final ArrayText subArrayText = new ArrayText(arrayText);
		subArrayText.suboctet(11, 15);

		assertEquals(expected, text.lastIndexOf(true, subArrayText));
		assertEquals(expected, text.lastIndexOf(false, subArrayText));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);
		text = bufferTextD;

		bufferTextD.setByteAt(21, (byte)'B');

		final BufferText subBufferTextD = new BufferText(bufferTextD);
		subBufferTextD.suboctet(11, 15);

		assertEquals(expected, text.lastIndexOf(true, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.lastIndexOf(true, subArrayText));
		assertEquals(expected, text.lastIndexOf(true, subBufferTextD));

		assertEquals(expected1, text.lastIndexOf(false, TEST_ARRAY, 11, 4));
		assertEquals(expected1, text.lastIndexOf(false, subArrayText));
		assertEquals(expected1, text.lastIndexOf(false, subBufferTextD));

		text = arrayText;

		assertEquals(expected, text.lastIndexOf(true, subBufferTextD));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);
		text = bufferText;

		bufferText.setByteAt(23, (byte)'D');

		final BufferText subBufferText = new BufferText(bufferText);
		subBufferText.suboctet(11, 15);

		assertEquals(expected, text.lastIndexOf(true, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.lastIndexOf(true, subArrayText));
		assertEquals(expected, text.lastIndexOf(true, subBufferTextD));
		assertEquals(expected, text.lastIndexOf(true, subBufferText));

		assertEquals(expected1, text.lastIndexOf(false, TEST_ARRAY, 11, 4));
		assertEquals(expected1, text.lastIndexOf(false, subArrayText));
		assertEquals(expected1, text.lastIndexOf(false, subBufferTextD));
		assertEquals(expected1, text.lastIndexOf(false, subBufferText));

		text = bufferTextD;

		assertEquals(expected, text.lastIndexOf(true, subBufferText));
		assertEquals(expected1, text.lastIndexOf(false, subBufferText));

		text = arrayText;

		assertEquals(expected, text.lastIndexOf(true, subBufferText));

		// -------------------------------------------------
	}

	public final void testLastIndexOfFromIndex() {
		TextOctet text;

		final int fromIndex = 19;

		final int expected = 11;
		final int expected_1 = -1;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);
		text = arrayText;

		assertEquals(expected, text.lastIndexOf(true, fromIndex, TEST_ARRAY, 21, 4));
		assertEquals(expected, text.lastIndexOf(false, fromIndex, TEST_ARRAY, 21, 4));

		final ArrayText subArrayText = new ArrayText(arrayText);
		subArrayText.suboctet(21, 25);

		assertEquals(expected, text.lastIndexOf(true, fromIndex, subArrayText));
		assertEquals(expected, text.lastIndexOf(false, fromIndex, subArrayText));

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);
		text = bufferTextD;

		bufferTextD.setByteAt(21, (byte)'B');

		final BufferText subBufferTextD = new BufferText(bufferTextD);
		subBufferTextD.suboctet(21, 25);

		assertEquals(expected, text.lastIndexOf(true, fromIndex, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.lastIndexOf(true, fromIndex, subArrayText));
		assertEquals(expected, text.lastIndexOf(true, fromIndex, subBufferTextD));

		assertEquals(expected, text.lastIndexOf(false, fromIndex, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.lastIndexOf(false, fromIndex, subArrayText));
		assertEquals(expected_1, text.lastIndexOf(false, fromIndex, subBufferTextD));

		text = arrayText;

		assertEquals(expected, text.lastIndexOf(true, fromIndex, subBufferTextD));
		assertEquals(expected_1, text.lastIndexOf(false, fromIndex, subBufferTextD));

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);
		text = bufferText;

		bufferText.setByteAt(23, (byte)'D');

		final BufferText subBufferText = new BufferText(bufferText);
		subBufferText.suboctet(21, 25);

		assertEquals(expected, text.lastIndexOf(true, fromIndex, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.lastIndexOf(true, fromIndex, subArrayText));
		assertEquals(expected, text.lastIndexOf(true, fromIndex, subBufferTextD));
		assertEquals(expected, text.lastIndexOf(true, fromIndex, subBufferText));

		assertEquals(expected, text.lastIndexOf(false, fromIndex, TEST_ARRAY, 11, 4));
		assertEquals(expected, text.lastIndexOf(false, fromIndex, subArrayText));
		assertEquals(expected_1, text.lastIndexOf(false, fromIndex, subBufferTextD));
		assertEquals(expected_1, text.lastIndexOf(false, fromIndex, subBufferText));

		text = bufferTextD;

		assertEquals(expected, text.lastIndexOf(true, fromIndex, subBufferText));
		assertEquals(expected_1, text.lastIndexOf(false, fromIndex, subBufferText));

		text = arrayText;

		assertEquals(expected, text.lastIndexOf(true, fromIndex, subBufferText));
		assertEquals(expected_1, text.lastIndexOf(false, fromIndex, subBufferText));

		// -------------------------------------------------
	}

	static final byte[] TRIM_TEST_ARRAY = { ' ', '\t', '\r', '\n', 'a', ' ', '\t', '\r', '\n', 'b', ' ', '\t', '\r', '\n'};

	public final void testTrim() {
		TextOctet text;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TRIM_TEST_ARRAY);

		final ArrayText subArrayText = new ArrayText(arrayText);


		text = subArrayText;
		assertBeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);


		subArrayText.set(arrayText);
		subArrayText.suboctet(0, 10);

		text = subArrayText;
		assertSub0_10BeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);


		subArrayText.set(arrayText);
		subArrayText.suboctet(4);

		text = subArrayText;
		assertSub4_BeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TRIM_TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);

		final BufferText subBufferTextD = new BufferText(bufferTextD);


		text = subBufferTextD;
		assertBeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);


		subBufferTextD.set(bufferTextD);
		subBufferTextD.suboctet(0, 10);

		text = subBufferTextD;
		assertSub0_10BeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);


		subBufferTextD.set(bufferTextD);
		subBufferTextD.suboctet(4);

		text = subBufferTextD;
		assertSub4_BeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TRIM_TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);
		text = bufferText;

		final BufferText subBufferText = new BufferText(bufferText);


		text = subBufferText;
		assertBeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);


		subBufferText.set(bufferText);
		subBufferText.suboctet(0, 10);

		text = subBufferText;
		assertSub0_10BeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);


		subBufferText.set(bufferText);
		subBufferText.suboctet(4);

		text = subBufferText;
		assertSub4_BeforeTrim(text);

		text = text.trim();
		assertAfterTrimed(text);

		// -------------------------------------------------

	}

	private static final void assertBeforeTrim(final TextOctet text) {
		assertEquals(0, text.begin());
		assertEquals(14, text.length());
		assertEquals((byte)' ', text.byteAt(text.begin()));
		assertEquals((byte)'\n', text.byteAt(text.end() - 1));
	}

	private static final void assertSub0_10BeforeTrim(final TextOctet text) {
		assertEquals(0, text.begin());
		assertEquals(10, text.length());
		assertEquals((byte)' ', text.byteAt(text.begin()));
		assertEquals((byte)'b', text.byteAt(text.end() - 1));
	}

	private static final void assertSub4_BeforeTrim(final TextOctet text) {
		assertEquals(4, text.begin());
		assertEquals(10, text.length());
		assertEquals((byte)'a', text.byteAt(text.begin()));
		assertEquals((byte)'\n', text.byteAt(text.end() - 1));
	}

	private static final void assertAfterTrimed(final TextOctet text) {
		assertEquals(4, text.begin());
		assertEquals(6, text.length());
		assertEquals((byte)'a', text.byteAt(text.begin()));
		assertEquals((byte)'b', text.byteAt(text.end() - 1));
	}


	public final void testCharsetMethods() {
		TextOctet text;

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);

		text = arrayText;
		assertFalse(text.hasCharset());
		arrayText.setCharset("UTF-8");
		assertTrue(text.hasCharset());
		assertTrue(arrayText.isSameCharset("utf-8"));
		assertFalse(arrayText.isSameCharset("UTF-16BE"));
		assertFalse(arrayText.isSameCharset("UTF-16LE"));
		assertFalse(arrayText.isSameCharset("gbk"));

	}


	private static final String APPEND_RESULT_STR = "0123456789abcdefghijabcdefghij";

	public final void testAppendTo() {
		TextOctet text;

		final StringBuilder builder = new StringBuilder();

		// -------------------------------------------------
		final ArrayText arrayText = new ArrayText(TEST_ARRAY);

		arrayText.setCharset("UTF-8");


		text = arrayText;
		try {
			text.appendTo(builder);
		} catch (IOException ex) {
			fail(ex);
		}

		assertEquals(APPEND_RESULT_STR, builder.toString());
		assertAfterAppended(text);

		// -------------------------------------------------
		final ByteBuffer directBuf = ByteBuffer.allocateDirect(64);

		directBuf.put(TEST_ARRAY);
		directBuf.flip();

		final BufferText bufferTextD = new BufferText(directBuf);


		text = bufferTextD;

		builder.delete(0, builder.length());
		try {
			text.appendTo(builder);
		} catch (IOException ex) {
			fail(ex);
		}

		assertEquals(APPEND_RESULT_STR, builder.toString());
		assertAfterAppended(text);

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.put(TEST_ARRAY);
		buf.flip();

		final BufferText bufferText = new BufferText(buf);

		text = bufferText;

		builder.delete(0, builder.length());
		try {
			text.appendTo(builder);
		} catch (IOException ex) {
			fail(ex);
		}

		assertEquals(APPEND_RESULT_STR, builder.toString());
		assertAfterAppended(text);

		// -------------------------------------------------
	}

	private static final void assertAfterAppended(final TextOctet text) {
		assertEquals(0, text.begin());
		assertEquals(30, text.length());
		assertEquals(APPEND_RESULT_STR, text.toString());
	}

}
