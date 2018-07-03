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
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import com.chinmobi.octet.BufferAllocator;
import com.chinmobi.octet.BufferOctet;
import com.chinmobi.octet.ExpandableOctetBuffer;
import com.chinmobi.octet.MutableArrayOctet;
import com.chinmobi.octet.io.OctetInputOp;
import com.chinmobi.octet.io.OctetOutputOp;
import com.chinmobi.testapp.BaseTestAction;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class OctetOutputOpTestAction extends BaseTestAction implements BufferAllocator {

	private static final byte[] TEST_ARRAY_0 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private boolean isAllocateDirect;


	public OctetOutputOpTestAction() {
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

	public final void testPutMethods1() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		final OctetOutputOp outputOp = arrayOctet.outputOp();

		doTestPutMethods(outputOp);
		outputOp.clear().restart();
		doTestPutMethods(outputOp);
	}

	public final void testPutMethods2() {
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		final OctetOutputOp outputOp = bufferOctet.outputOp();

		doTestPutMethods(outputOp);
		outputOp.clear().restart();
		doTestPutMethods(outputOp);
	}

	public final void testPutMethods3() {
		final ByteBuffer buf = ByteBuffer.allocateDirect(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		final OctetOutputOp outputOp = bufferOctet.outputOp();

		doTestPutMethods(outputOp);
		outputOp.clear().restart();
		doTestPutMethods(outputOp);
	}

	public final void testPutMethods4() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		final OctetOutputOp outputOp = octetBuf.outputOp();

		doTestPutMethods(outputOp);
		outputOp.clear().restart();
		doTestPutMethods(outputOp);
	}

	public final void testPutMethods5() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		final OctetOutputOp outputOp = octetBuf.outputOp();

		doTestPutMethods(outputOp);
		outputOp.clear().restart();
		doTestPutMethods(outputOp);
	}

	private static final void doTestPutMethods(final OctetOutputOp outputOp) {
		assertEquals(0, outputOp.position());

		outputOp.skipBytes(3);
		assertEquals(3, outputOp.position());

		outputOp.fillZero(0, 3);
		assertEquals(3, outputOp.position());


		outputOp.put(TEST_ARRAY_0, 1, 4);
		assertEquals(7, outputOp.position());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(7, outputOp.octet().length());

		final String expectedString =
				"\r\n" +
				"00 00 00 31 32 33 34                              ...1234" + "\r\n";
		assertEquals(expectedString, outputOp.octet().toString());


		outputOp.put(1, TEST_ARRAY_0, 8, 2);
		assertEquals(7, outputOp.position());

		assertEquals(0, outputOp.octet().begin());
		assertEquals(7, outputOp.octet().length());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(7, outputOp.octet().length());

		final String expectedString1 =
				"\r\n" +
				"00 38 39 31 32 33 34                              .891234" + "\r\n";
		assertEquals(expectedString1, outputOp.octet().toString());


		final ByteBuffer buf = ByteBuffer.allocate(16);
		buf.put(TEST_ARRAY_0, 5, 3);
		buf.flip();

		outputOp.put(buf);
		assertEquals(10, outputOp.position());
		assertEquals(0, buf.remaining());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(10, outputOp.octet().length());

		final String expectedString2 =
				"\r\n" +
				"00 38 39 31 32 33 34 35  36 37                    .891234567" + "\r\n";
		assertEquals(expectedString2, outputOp.octet().toString());


		buf.rewind();
		outputOp.put(0, buf);
		assertEquals(10, outputOp.position());
		assertEquals(0, buf.remaining());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(10, outputOp.octet().length());

		final String expectedString3 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 37                    5671234567" + "\r\n";
		assertEquals(expectedString3, outputOp.octet().toString());


		buf.clear();
		buf.put(TEST_ARRAY_0, 0, 5);
		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);
		assertEquals(0, bufferOctet.begin());
		assertEquals(5, bufferOctet.length());
		assertEquals(5, buf.remaining());

		outputOp.put(bufferOctet);
		assertEquals(15, outputOp.position());

		assertEquals(0, bufferOctet.begin());
		assertEquals(5, bufferOctet.length());
		assertEquals(5, buf.remaining());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(15, outputOp.octet().length());

		final String expectedString4 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 37 30 31 32 33 34     567123456701234" + "\r\n";
		assertEquals(expectedString4, outputOp.octet().toString());


		final ByteBuffer bufD = ByteBuffer.allocateDirect(16);
		bufD.put(TEST_ARRAY_0, 5, 3);
		bufD.flip();
		bufferOctet.wrap(bufD);
		assertEquals(0, bufferOctet.begin());
		assertEquals(3, bufferOctet.length());
		assertEquals(3, bufD.remaining());

		outputOp.put(bufferOctet);
		assertEquals(18, outputOp.position());

		assertEquals(0, bufferOctet.begin());
		assertEquals(3, bufferOctet.length());
		assertEquals(3, bufD.remaining());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(18, outputOp.octet().length());

		final String expectedString5 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 37 30 31 32 33 34 35  5671234567012345" + "\r\n" +
				"36 37                                             67" + "\r\n";
		assertEquals(expectedString5, outputOp.octet().toString());


		outputOp.put(10, bufferOctet);
		assertEquals(18, outputOp.position());

		assertEquals(0, bufferOctet.begin());
		assertEquals(3, bufferOctet.length());
		assertEquals(3, bufD.remaining());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(18, outputOp.octet().length());

		final String expectedString6 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 37 35 36 37 33 34 35  5671234567567345" + "\r\n" +
				"36 37                                             67" + "\r\n";
		assertEquals(expectedString6, outputOp.octet().toString());


		outputOp.put((byte)'a');
		assertEquals(19, outputOp.position());

		outputOp.put(9, (byte)'b');
		assertEquals(19, outputOp.position());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(19, outputOp.octet().length());

		final String expectedString7 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 62 35 36 37 33 34 35  567123456b567345" + "\r\n" +
				"36 37 61                                          67a" + "\r\n";
		assertEquals(expectedString7, outputOp.octet().toString());


		outputOp.putChar((char)'c');
		assertEquals(21, outputOp.position());

		outputOp.putChar(10, (char)'d');
		assertEquals(21, outputOp.position());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(21, outputOp.octet().length());

		final String expectedString8 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 62 00 64 37 33 34 35  567123456b.d7345" + "\r\n" +
				"36 37 61 00 63                                    67a.c" + "\r\n";
		assertEquals(expectedString8, outputOp.octet().toString());


		outputOp.putShort((short)5);
		assertEquals(23, outputOp.position());

		outputOp.putShort(12, (short)6);
		assertEquals(23, outputOp.position());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(23, outputOp.octet().length());

		final String expectedString9 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 62 00 64 00 06 34 35  567123456b.d..45" + "\r\n" +
				"36 37 61 00 63 00 05                              67a.c.." + "\r\n";
		assertEquals(expectedString9, outputOp.octet().toString());


		outputOp.putInt((int)7);
		assertEquals(27, outputOp.position());

		outputOp.putInt(14, (int)8);
		assertEquals(27, outputOp.position());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(27, outputOp.octet().length());

		final String expectedString10 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 62 00 64 00 06 00 00  567123456b.d...." + "\r\n" +
				"00 08 61 00 63 00 05 00  00 00 07                 ..a.c......" + "\r\n";
		assertEquals(expectedString10, outputOp.octet().toString());


		outputOp.putLong((long)9);
		assertEquals(35, outputOp.position());

		outputOp.putLong(18, (long)10);
		assertEquals(35, outputOp.position());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(35, outputOp.octet().length());

		final String expectedString11 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 62 00 64 00 06 00 00  567123456b.d...." + "\r\n" +
				"00 08 00 00 00 00 00 00  00 0A 07 00 00 00 00 00  ................" + "\r\n" +
				"00 00 09                                          ..." + "\r\n";
		assertEquals(expectedString11, outputOp.octet().toString());


		outputOp.putFloat((float)1.1);
		assertEquals(39, outputOp.position());

		outputOp.putFloat(18, (float)2.3);
		assertEquals(39, outputOp.position());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(39, outputOp.octet().length());

		final String expectedString12 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 62 00 64 00 06 00 00  567123456b.d...." + "\r\n" +
				"00 08 40 13 33 33 00 00  00 0A 07 00 00 00 00 00  ..@.33.........." + "\r\n" +
				"00 00 09 3F 8C CC CD                              ...?..." + "\r\n";
		assertEquals(expectedString12, outputOp.octet().toString());


		outputOp.putDouble((double)1.2);
		assertEquals(47, outputOp.position());

		outputOp.putDouble(18, (double)3.4);
		assertEquals(47, outputOp.position());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(47, outputOp.octet().length());

		final String expectedString13 =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 62 00 64 00 06 00 00  567123456b.d...." + "\r\n" +
				"00 08 40 0B 33 33 33 33  33 33 07 00 00 00 00 00  ..@.333333......" + "\r\n" +
				"00 00 09 3F 8C CC CD 3F  F3 33 33 33 33 33 33     ...?...?.333333" + "\r\n";
		assertEquals(expectedString13, outputOp.octet().toString());
	}


	public final void testWritableChannelMethods1() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		try {
			doTestWritableChannelMethods(arrayOctet.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testWritableChannelMethods2() {
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		try {
			doTestWritableChannelMethods(bufferOctet.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testWritableChannelMethods3() {
		final ByteBuffer buf = ByteBuffer.allocateDirect(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		try {
			doTestWritableChannelMethods(bufferOctet.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testWritableChannelMethods4() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		try {
			doTestWritableChannelMethods(octetBuf.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testWritableChannelMethods5() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		try {
			doTestWritableChannelMethods(octetBuf.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	private static final void doTestWritableChannelMethods(final OctetOutputOp outputOp)
			throws IOException {
		assertEquals(0, outputOp.position());

		final ByteBuffer buf = ByteBuffer.allocate(16);
		buf.put(TEST_ARRAY_0, 5, 3);
		buf.flip();

		final ByteBuffer bufD = ByteBuffer.allocateDirect(16);
		bufD.put(TEST_ARRAY_0, 1, 9);
		bufD.flip();

		final ByteBuffer[] srcs = new ByteBuffer[2];
		srcs[0] = buf;
		srcs[1] = bufD;

		long count = outputOp.write(srcs);

		assertEquals((long)12, count);

		assertEquals(12, outputOp.position());

		assertEquals(0, buf.remaining());
		assertEquals(0, bufD.remaining());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(12, outputOp.octet().length());

		final String expectedString =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 37 38 39              567123456789" + "\r\n";
		assertEquals(expectedString, outputOp.octet().toString());
	}


	public final void testStreamMethods1() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		try {
			doTestStreamMethods(arrayOctet.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testStreamMethods2() {
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		try {
			doTestStreamMethods(bufferOctet.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testStreamMethods3() {
		final ByteBuffer buf = ByteBuffer.allocateDirect(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		try {
			doTestStreamMethods(bufferOctet.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testStreamMethods4() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		try {
			doTestStreamMethods(octetBuf.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testStreamMethods5() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		try {
			doTestStreamMethods(octetBuf.outputOp());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	private static final void doTestStreamMethods(final OctetOutputOp outputOp)
			throws IOException {
		assertEquals(0, outputOp.position());

		outputOp.write(TEST_ARRAY_0, 5, 3);
		outputOp.write(TEST_ARRAY_0, 1, 9);
		outputOp.write((int)'a');

		assertEquals(13, outputOp.position());

		outputOp.flush();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(13, outputOp.octet().length());

		final String expectedString =
				"\r\n" +
				"35 36 37 31 32 33 34 35  36 37 38 39 61           567123456789a" + "\r\n";
		assertEquals(expectedString, outputOp.octet().toString());
	}


	public final void testTransferMethods1() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		final OctetOutputOp outputOp = arrayOctet.outputOp();

		try {
			doTestTransferMethods(outputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testTransferMethods2() {
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		final OctetOutputOp outputOp = bufferOctet.outputOp();

		try {
			doTestTransferMethods(outputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testTransferMethods3() {
		final ByteBuffer buf = ByteBuffer.allocateDirect(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		final OctetOutputOp outputOp = bufferOctet.outputOp();

		try {
			doTestTransferMethods(outputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testTransferMethods4() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		final OctetOutputOp outputOp = octetBuf.outputOp();

		try {
			doTestTransferMethods(outputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testTransferMethods5() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		final OctetOutputOp outputOp = octetBuf.outputOp();

		try {
			doTestTransferMethods(outputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	private static final void setUpTestTransfer(final OctetOutputOp outputOp) {
		assertEquals(0, outputOp.position());

		outputOp.skipBytes(3);
		outputOp.put(TEST_ARRAY_0, 1, 4);
		outputOp.put((byte)'a');
		outputOp.putChar((char)'b');
		outputOp.putShort((short)1);
		outputOp.putInt((int)2);
		outputOp.putLong((long)3);
		outputOp.putFloat((float)1.1);
		outputOp.putDouble((double)2.2);

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(36, outputOp.octet().length());

		final String expectedString =
				"\r\n" +
				"00 00 00 31 32 33 34 61  00 62 00 01 00 00 00 02  ...1234a.b......" + "\r\n" +
				"00 00 00 00 00 00 00 03  3F 8C CC CD 40 01 99 99  ........?...@..." + "\r\n" +
				"99 99 99 9A                                       ...." + "\r\n";
		assertEquals(expectedString, outputOp.octet().toString());
	}

	private final void doTestTransferMethods(final OctetOutputOp outputOp)
			throws IOException {
		assertEquals(0, outputOp.position());

		OctetInputOp inputOp;

		// -------------------------------------------------
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		setUpTestTransfer(arrayOctet.outputOp());
		inputOp = arrayOctet.inputOp();

		doTestTransferMethods(outputOp, inputOp.readableByteChannel());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.inputStream());

		// -------------------------------------------------
		final ByteBuffer bufD = ByteBuffer.allocateDirect(64);

		bufD.flip();
		final BufferOctet bufferOctet = new BufferOctet(bufD);

		setUpTestTransfer(bufferOctet.outputOp());
		inputOp = bufferOctet.inputOp();

		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.readableByteChannel());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.inputStream());

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		bufferOctet.wrap(buf);

		setUpTestTransfer(bufferOctet.outputOp());
		inputOp = bufferOctet.inputOp();

		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.readableByteChannel());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.inputStream());

		// -------------------------------------------------
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBufD = new ExpandableOctetBuffer(this, 1);

		setUpTestTransfer(octetBufD.outputOp());
		inputOp = octetBufD.inputOp();

		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.readableByteChannel());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.inputStream());

		// -------------------------------------------------
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestTransfer(octetBuf.outputOp());
		inputOp = octetBuf.inputOp();

		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.readableByteChannel());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(outputOp, inputOp.inputStream());

		// -------------------------------------------------
	}

	private static final void doTestTransferMethods(final OctetOutputOp outputOp,
			final ReadableByteChannel src) throws IOException {
		assertEquals(0, outputOp.position());

		int count = outputOp.transferFrom(src, 3);
		assertEquals((int)3, count);

		assertEquals(3, outputOp.position());

		count = outputOp.transferFrom(0, 4, src);
		assertEquals((int)4, count);

		assertEquals(3, outputOp.position());

		outputOp.skipBytes(1);
		assertEquals(4, outputOp.position());

		count = outputOp.transferFrom(src, 5);
		assertEquals((int)5, count);

		assertEquals(9, outputOp.position());

		outputOp.update();
		final String expectedString =
				"\r\n" +
				"31 32 33 34 61 00 62 00  01                       1234a.b.." + "\r\n";
		assertEquals(expectedString, outputOp.octet().toString());

		count = outputOp.transferFrom(src);
		assertEquals((int)24, count);

		assertEquals(33, outputOp.position());

		count = outputOp.transferFrom(src);
		assertEquals((int)-1, count);

		assertEquals(33, outputOp.position());
	}

	private static final void doTestTransferMethods(final OctetOutputOp outputOp,
			final InputStream src) throws IOException {
		assertEquals(0, outputOp.position());

		int count = outputOp.transferFrom(src, 3);
		assertEquals((int)3, count);

		assertEquals(3, outputOp.position());

		count = outputOp.transferFrom(0, 4, src);
		assertEquals((int)4, count);

		assertEquals(3, outputOp.position());

		outputOp.skipBytes(1);
		assertEquals(4, outputOp.position());

		count = outputOp.transferFrom(src, 5);
		assertEquals((int)5, count);

		assertEquals(9, outputOp.position());

		outputOp.update();
		final String expectedString =
				"\r\n" +
				"31 32 33 34 61 00 62 00  01                       1234a.b.." + "\r\n";
		assertEquals(expectedString, outputOp.octet().toString());

		count = outputOp.transferFrom(src);
		assertEquals((int)24, count);

		assertEquals(33, outputOp.position());

		count = outputOp.transferFrom(src);
		assertEquals((int)-1, count);

		assertEquals(33, outputOp.position());
	}

}
