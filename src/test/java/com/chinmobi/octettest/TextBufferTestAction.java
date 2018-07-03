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

import com.chinmobi.octet.BufferAllocator;
import com.chinmobi.testapp.BaseTestAction;
import com.chinmobi.text.ExpandableTextBuffer;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class TextBufferTestAction extends BaseTestAction implements BufferAllocator {

	private boolean isAllocateDirect;


	public TextBufferTestAction() {
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

	public final void testAppend() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestAppend();
		}
	}

	private final void doTestAppend() {
		ExpandableTextBuffer octetTxt = new ExpandableTextBuffer((BufferAllocator)this, 1);

		final String str = "0123456789";
		octetTxt.append(str);
		assertEquals(10, octetTxt.length());
		assertEquals(2, octetTxt.remaining());
		assertEquals(12, octetTxt.capacity());

		assertEquals(str, octetTxt.toString());


		octetTxt.append((char)'a');
		assertEquals(11, octetTxt.length());
		assertEquals(1, octetTxt.remaining());
		assertEquals(12, octetTxt.capacity());

		assertEquals("0123456789a", octetTxt.toString());


		octetTxt.append(str, 1, 3);
		assertEquals(13, octetTxt.length());
		assertEquals(7, octetTxt.remaining());
		assertEquals(20, octetTxt.capacity());

		assertEquals("0123456789a12", octetTxt.toString());
	}

	public final void testReplace() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestReplace();
		}
	}

	private final void doTestReplace() {
		ExpandableTextBuffer octetTxt = new ExpandableTextBuffer((BufferAllocator)this, 1);

		final String str = "0123456789";
		octetTxt.append(str, 0, 5);
		assertEquals(5, octetTxt.length());
		assertEquals(3, octetTxt.remaining());
		assertEquals(8, octetTxt.capacity());

		assertEquals("01234", octetTxt.toString());


		final String csq = "abcdefghij";

		octetTxt.replace(5, 8, csq, 1, 3);
		assertEquals(7, octetTxt.length());
		assertEquals(1, octetTxt.remaining());
		assertEquals(8, octetTxt.capacity());

		assertEquals("01234bc", octetTxt.toString());


		octetTxt.clear().append(str, 0, 5);
		octetTxt.replace(2, 4, csq, 1, 2);
		assertEquals(4, octetTxt.length());
		assertEquals(4, octetTxt.remaining());
		assertEquals(8, octetTxt.capacity());

		assertEquals("01b4", octetTxt.toString());


		octetTxt.clear().append(str, 0, 5);
		octetTxt.replace(2, 4, csq, 1, 4);
		assertEquals(6, octetTxt.length());
		assertEquals(2, octetTxt.remaining());
		assertEquals(8, octetTxt.capacity());

		assertEquals("01bcd4", octetTxt.toString());


		octetTxt.clear().append(str, 0, 5);
		octetTxt.replace(2, 3, csq, 1, 6);
		assertEquals(9, octetTxt.length());
		assertEquals(3, octetTxt.remaining());
		assertEquals(12, octetTxt.capacity());

		assertEquals("01bcdef34", octetTxt.toString());


		octetTxt = new ExpandableTextBuffer((BufferAllocator)this, 1);
		octetTxt.clear().append(str, 0, 5);
		octetTxt.replace(3, 6, csq, 1, 7);
		assertEquals(9, octetTxt.length());
		assertEquals(3, octetTxt.remaining());
		assertEquals(12, octetTxt.capacity());

		assertEquals("012bcdefg", octetTxt.toString());
	}

	public final void testConvert() {
		for (int i = 1; i >= 0; --i) {
			this.isAllocateDirect = (i > 0);
			doTestConvert();
		}
	}

	private final void doTestConvert() {
		ExpandableTextBuffer octetTxt = new ExpandableTextBuffer((BufferAllocator)this, 1);

		final String str = "0123456789";
		assertTrue(octetTxt.isSameCharset("UTF-8"));
		octetTxt.append(str);
		assertEquals(10, octetTxt.length());

		assertEquals(str, octetTxt.toString());


		octetTxt.convert("UTF-16BE");
		assertTrue(octetTxt.isSameCharset("UTF-16BE"));
		assertEquals(20, octetTxt.length());

		assertEquals(str, octetTxt.toString());


		octetTxt.convert("GBK");
		assertTrue(octetTxt.isSameCharset("GBK"));
		assertEquals(10, octetTxt.length());

		assertEquals(str, octetTxt.toString());
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
		final ExpandableTextBuffer octetTxtS = new ExpandableTextBuffer((BufferAllocator)this, 1);

		octetTxtS.setCharset("UTF-16BE");

		octetTxtS.append("0123456789");
		octetTxtS.insert(4, "abcde");

		final String expectedString =
				"\r\n" +
				"00 30 00 31 00 61 00 62  00 63 00 64 00 65 00 32  .0.1.a.b.c.d.e.2" + "\r\n" +
				"00 33 00 34 00 35 00 36  00 37 00 38 00 39        .3.4.5.6.7.8.9" + "\r\n";
		assertEquals(expectedString, octetTxtS.toString(true));

		final String fileName = "../tmp/textbuftest0.txt";
		octetTxtS.store(fileName, false, true);

		final ExpandableTextBuffer octetTxtD = new ExpandableTextBuffer((BufferAllocator)this, 1);
		octetTxtD.load(fileName);

		assertTrue(octetTxtD.hasCharset());
		assertTrue(octetTxtD.isSameCharset("UTF-16BE"));

		assertEquals(expectedString, octetTxtD.toString(true));

		// -------------------------------------------------
		octetTxtS.convert("UTF-16LE");

		assertTrue(octetTxtS.isSameCharset("UTF-16LE"));

		final String expectedString1 =
				"\r\n" +
				"30 00 31 00 61 00 62 00  63 00 64 00 65 00 32 00  0.1.a.b.c.d.e.2." + "\r\n" +
				"33 00 34 00 35 00 36 00  37 00 38 00 39 00        3.4.5.6.7.8.9." + "\r\n";
		assertEquals(expectedString1, octetTxtS.toString(true));

		octetTxtS.store(fileName, false, true);

		octetTxtD.load(fileName);

		assertTrue(octetTxtD.hasCharset());
		assertTrue(octetTxtD.isSameCharset("UTF-16BE"));

		final String expectedString2 =
				"\r\n" +
				"00 30 00 31 00 61 00 62  00 63 00 64 00 65 00 32  .0.1.a.b.c.d.e.2" + "\r\n" +
				"00 33 00 34 00 35 00 36  00 37 00 38 00 39 00 30  .3.4.5.6.7.8.9.0" + "\r\n" +
				"00 31 00 61 00 62 00 63  00 64 00 65 00 32 00 33  .1.a.b.c.d.e.2.3" + "\r\n" +
				"00 34 00 35 00 36 00 37  00 38 00 39              .4.5.6.7.8.9" + "\r\n";
		assertEquals(expectedString2, octetTxtD.toString(true));

		// -------------------------------------------------
		octetTxtS.convert("UTF-8");

		assertTrue(octetTxtS.isSameCharset("UTF-8"));

		final String expectedString3 =
				"\r\n" +
				//"" + "\r\n" +
				"30 31 61 62 63 64 65 32  33 34 35 36 37 38 39     01abcde23456789" + "\r\n";
		assertEquals(expectedString3, octetTxtS.toString(true));

		octetTxtS.store(fileName, false, true);

		octetTxtD.load(fileName);

		assertTrue(octetTxtD.hasCharset());
		assertTrue(octetTxtD.isSameCharset("UTF-16BE"));

		final String expectedString4 =
				"\r\n" +
				"00 30 00 31 00 61 00 62  00 63 00 64 00 65 00 32  .0.1.a.b.c.d.e.2" + "\r\n" +
				"00 33 00 34 00 35 00 36  00 37 00 38 00 39 00 30  .3.4.5.6.7.8.9.0" + "\r\n" +
				"00 31 00 61 00 62 00 63  00 64 00 65 00 32 00 33  .1.a.b.c.d.e.2.3" + "\r\n" +
				"00 34 00 35 00 36 00 37  00 38 00 39 00 30 00 31  .4.5.6.7.8.9.0.1" + "\r\n" +
				"00 61 00 62 00 63 00 64  00 65 00 32 00 33 00 34  .a.b.c.d.e.2.3.4" + "\r\n" +
				"00 35 00 36 00 37 00 38  00 39                    .5.6.7.8.9" + "\r\n";
		assertEquals(expectedString4, octetTxtD.toString(true));

		// -------------------------------------------------
		octetTxtD.convert("UTF-8");

		assertTrue(octetTxtD.isSameCharset("UTF-8"));

		final String expectedString5 =
				"\r\n" +
				"30 31 61 62 63 64 65 32  33 34 35 36 37 38 39 30  01abcde234567890" + "\r\n" +
				"31 61 62 63 64 65 32 33  34 35 36 37 38 39 30 31  1abcde2345678901" + "\r\n" +
				"61 62 63 64 65 32 33 34  35 36 37 38 39           abcde23456789" + "\r\n";
		assertEquals(expectedString5, octetTxtD.toString(true));

	}

}
