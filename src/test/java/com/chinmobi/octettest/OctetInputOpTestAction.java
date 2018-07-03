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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.chinmobi.octet.BufferAllocator;
import com.chinmobi.octet.BufferOctet;
import com.chinmobi.octet.ExpandableOctetBuffer;
import com.chinmobi.octet.MutableArrayOctet;
import com.chinmobi.octet.OctetUtils;
import com.chinmobi.octet.io.OctetInputOp;
import com.chinmobi.octet.io.OctetOutputOp;
import com.chinmobi.testapp.BaseTestAction;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class OctetInputOpTestAction extends BaseTestAction implements BufferAllocator {

	private static final byte[] TEST_ARRAY_0 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private boolean isAllocateDirect;


	public OctetInputOpTestAction() {
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

	public final void testGetMethods1() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		setUpTestGet(arrayOctet.outputOp());

		final OctetInputOp inputOp = arrayOctet.inputOp();

		doTestGetMethods(inputOp);
		inputOp.restart();
		doTestGetMethods(inputOp);
	}

	public final void testGetMethods2() {
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		setUpTestGet(bufferOctet.outputOp());

		final OctetInputOp inputOp = bufferOctet.inputOp();

		doTestGetMethods(inputOp);
		inputOp.restart();
		doTestGetMethods(inputOp);
	}

	public final void testGetMethods3() {
		final ByteBuffer buf = ByteBuffer.allocateDirect(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		setUpTestGet(bufferOctet.outputOp());

		final OctetInputOp inputOp = bufferOctet.inputOp();

		doTestGetMethods(inputOp);
		inputOp.restart();
		doTestGetMethods(inputOp);
	}

	public final void testGetMethods4() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestGet(octetBuf.outputOp());

		final OctetInputOp inputOp = octetBuf.inputOp();

		doTestGetMethods(inputOp);
		inputOp.restart();
		doTestGetMethods(inputOp);
	}

	public final void testGetMethods5() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestGet(octetBuf.outputOp());

		final OctetInputOp inputOp = octetBuf.inputOp();

		doTestGetMethods(inputOp);
		inputOp.restart();
		doTestGetMethods(inputOp);
	}

	private static final void setUpTestGet(final OctetOutputOp outputOp) {
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
	}

	private static final void doTestGetMethods(final OctetInputOp inputOp) {
		assertEquals(0, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(36, inputOp.remaining());

		inputOp.skipBytes(3);
		assertEquals(3, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(33, inputOp.remaining());


		final byte[] array = new byte[8];
		inputOp.get(3, array, 0, 4);
		assertEquals(3, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(33, inputOp.remaining());

		final String expectedString =
				"\r\n" +
				"31 32 33 34                                       1234" + "\r\n";
		assertEquals(expectedString, OctetUtils.hexFormat(array, 0, 4));


		inputOp.get(array, 0, 4);
		assertEquals(7, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(29, inputOp.remaining());
		assertEquals(expectedString, OctetUtils.hexFormat(array, 0, 4));


		assertEquals((byte)'a', inputOp.get(7));
		assertEquals(7, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(29, inputOp.remaining());

		assertEquals((byte)'a', inputOp.get());
		assertEquals(8, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(28, inputOp.remaining());


		assertEquals((char)'b', inputOp.getChar(8));
		assertEquals(8, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(28, inputOp.remaining());

		assertEquals((char)'b', inputOp.getChar());
		assertEquals(10, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(26, inputOp.remaining());


		assertEquals((short)1, inputOp.getShort(10));
		assertEquals(10, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(26, inputOp.remaining());

		assertEquals((short)1, inputOp.getShort());
		assertEquals(12, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(24, inputOp.remaining());


		assertEquals((int)2, inputOp.getInt(12));
		assertEquals(12, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(24, inputOp.remaining());

		assertEquals((int)2, inputOp.getInt());
		assertEquals(16, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(20, inputOp.remaining());


		assertEquals((long)3, inputOp.getLong(16));
		assertEquals(16, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(20, inputOp.remaining());

		assertEquals((long)3, inputOp.getLong());
		assertEquals(24, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(12, inputOp.remaining());


		assertEquals((float)1.1, inputOp.getFloat(24), 0.001);
		assertEquals(24, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(12, inputOp.remaining());

		assertEquals((float)1.1, inputOp.getFloat(), 0.001);
		assertEquals(28, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(8, inputOp.remaining());


		assertEquals((double)2.2, inputOp.getDouble(28), 0.001);
		assertEquals(28, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(8, inputOp.remaining());

		assertEquals((double)2.2, inputOp.getDouble(), 0.001);
		assertEquals(36, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(0, inputOp.remaining());
	}


	public final void testReadableChannelMethods1() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		setUpTestGet(arrayOctet.outputOp());

		final OctetInputOp inputOp = arrayOctet.inputOp();

		try {
			doTestReadableChannelMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testReadableChannelMethods2() {
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		setUpTestGet(bufferOctet.outputOp());

		final OctetInputOp inputOp = bufferOctet.inputOp();

		try {
			doTestReadableChannelMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testReadableChannelMethods3() {
		final ByteBuffer buf = ByteBuffer.allocateDirect(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		setUpTestGet(bufferOctet.outputOp());

		final OctetInputOp inputOp = bufferOctet.inputOp();

		try {
			doTestReadableChannelMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testReadableChannelMethods4() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestGet(octetBuf.outputOp());

		final OctetInputOp inputOp = octetBuf.inputOp();

		try {
			doTestReadableChannelMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testReadableChannelMethods5() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestGet(octetBuf.outputOp());

		final OctetInputOp inputOp = octetBuf.inputOp();

		try {
			doTestReadableChannelMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	private static final void doTestReadableChannelMethods(final OctetInputOp inputOp)
			throws IOException {
		assertEquals(0, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(36, inputOp.remaining());

		inputOp.skipBytes(3);

		final ByteBuffer buf = ByteBuffer.allocate(3);
		final ByteBuffer bufD = ByteBuffer.allocateDirect(5);

		final ByteBuffer[] dsts = new ByteBuffer[2];
		dsts[0] = buf;
		dsts[1] = bufD;

		long count = inputOp.read(dsts);

		assertEquals((long)8, count);

		assertEquals(11, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(25, inputOp.remaining());


		count = inputOp.read(dsts);

		assertEquals((long)0, count);

		assertEquals(11, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(25, inputOp.remaining());


		buf.flip();
		final String expectedString1 =
				"\r\n" +
				"31 32 33                                          123" + "\r\n";
		assertEquals(expectedString1, OctetUtils.hexFormat(buf));

		bufD.flip();
		final String expectedString2 =
				"\r\n" +
				"34 61 00 62 00                                    4a.b." + "\r\n";
		assertEquals(expectedString2, OctetUtils.hexFormat(bufD));
	}


	public final void testStreamMethods1() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		setUpTestGet(arrayOctet.outputOp());

		final OctetInputOp inputOp = arrayOctet.inputOp();

		try {
			inputOp.mark(0);
			doTestStreamMethods(inputOp);
			inputOp.reset();
			doTestStreamMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testStreamMethods2() {
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		setUpTestGet(bufferOctet.outputOp());

		final OctetInputOp inputOp = bufferOctet.inputOp();

		try {
			inputOp.mark(0);
			doTestStreamMethods(inputOp);
			inputOp.reset();
			doTestStreamMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testStreamMethods3() {
		final ByteBuffer buf = ByteBuffer.allocateDirect(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		setUpTestGet(bufferOctet.outputOp());

		final OctetInputOp inputOp = bufferOctet.inputOp();

		try {
			inputOp.mark(0);
			doTestStreamMethods(inputOp);
			inputOp.reset();
			doTestStreamMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testStreamMethods4() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestGet(octetBuf.outputOp());

		final OctetInputOp inputOp = octetBuf.inputOp();

		try {
			inputOp.mark(0);
			doTestStreamMethods(inputOp);
			inputOp.reset();
			doTestStreamMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testStreamMethods5() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestGet(octetBuf.outputOp());

		final OctetInputOp inputOp = octetBuf.inputOp();

		try {
			inputOp.mark(0);
			doTestStreamMethods(inputOp);
			inputOp.reset();
			doTestStreamMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	private static final void doTestStreamMethods(final OctetInputOp inputOp)
			throws IOException {
		assertEquals(0, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(36, inputOp.available());

		long count = inputOp.skip(3);
		assertEquals((long)3, count);

		assertEquals(3, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(33, inputOp.available());

		final byte[] array = new byte[8];

		count = inputOp.read(array, 0, 3);
		assertEquals((long)3, count);

		assertEquals(6, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(30, inputOp.available());

		final String expectedString1 =
				"\r\n" +
				"31 32 33                                          123" + "\r\n";
		assertEquals(expectedString1, OctetUtils.hexFormat(array, 0, 3));

		final int b = inputOp.read();
		assertEquals((byte)'4', (byte)(b&0xFF));

		assertEquals(7, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(29, inputOp.available());
	}


	public final void testTransferMethods1() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);

		setUpTestGet(arrayOctet.outputOp());

		final OctetInputOp inputOp = arrayOctet.inputOp();

		try {
			doTestTransferMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testTransferMethods2() {
		final ByteBuffer buf = ByteBuffer.allocate(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		setUpTestGet(bufferOctet.outputOp());

		final OctetInputOp inputOp = bufferOctet.inputOp();

		try {
			doTestTransferMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testTransferMethods3() {
		final ByteBuffer buf = ByteBuffer.allocateDirect(64);

		buf.flip();
		final BufferOctet bufferOctet = new BufferOctet(buf);

		setUpTestGet(bufferOctet.outputOp());

		final OctetInputOp inputOp = bufferOctet.inputOp();

		try {
			doTestTransferMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testTransferMethods4() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestGet(octetBuf.outputOp());

		final OctetInputOp inputOp = octetBuf.inputOp();

		try {
			doTestTransferMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	public final void testTransferMethods5() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);

		setUpTestGet(octetBuf.outputOp());

		final OctetInputOp inputOp = octetBuf.inputOp();

		try {
			doTestTransferMethods(inputOp);
		} catch (IOException ex) {
			fail(ex);
		}
	}

	private final void doTestTransferMethods(final OctetInputOp inputOp)
			throws IOException {
		assertEquals(0, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(36, inputOp.remaining());

		OctetOutputOp outputOp;

		// -------------------------------------------------
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(64);
		outputOp = arrayOctet.outputOp();

		doTestTransferMethods(inputOp, (WritableByteChannel)outputOp);

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());

		final String expectedString =
				"\r\n" +
				"31 32 33 34 00 00 00 31  32                       1234...12" + "\r\n";
		assertEquals(expectedString, outputOp.octet().toString());


		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(inputOp, (OutputStream)outputOp);

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

		// -------------------------------------------------
		final ByteBuffer bufD = ByteBuffer.allocateDirect(64);
		bufD.flip();
		final BufferOctet bufferOctet = new BufferOctet(bufD);
		outputOp = bufferOctet.outputOp();

		inputOp.restart();

		doTestTransferMethods(inputOp, outputOp.writableByteChannel());
		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(inputOp, outputOp.outputStream());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

		// -------------------------------------------------
		final ByteBuffer buf = ByteBuffer.allocate(64);
		buf.flip();
		bufferOctet.wrap(buf);
		outputOp = bufferOctet.outputOp();

		inputOp.restart();

		doTestTransferMethods(inputOp, outputOp.writableByteChannel());
		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(inputOp, outputOp.outputStream());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

		// -------------------------------------------------
		this.isAllocateDirect = true;
		final ExpandableOctetBuffer octetBufD = new ExpandableOctetBuffer(this, 1);
		outputOp = octetBufD.outputOp();

		inputOp.restart();

		doTestTransferMethods(inputOp, outputOp.writableByteChannel());
		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(inputOp, outputOp.outputStream());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

		// -------------------------------------------------
		this.isAllocateDirect = false;
		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer(this, 1);
		outputOp = octetBuf.outputOp();

		inputOp.restart();

		doTestTransferMethods(inputOp, outputOp.writableByteChannel());
		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

		inputOp.restart();
		outputOp.clear().restart();

		doTestTransferMethods(inputOp, outputOp.outputStream());

		outputOp.update();
		assertEquals(0, outputOp.octet().begin());
		assertEquals(9, outputOp.octet().length());
		assertEquals(expectedString, outputOp.octet().toString());

	}

	private static final void doTestTransferMethods(final OctetInputOp inputOp,
			final WritableByteChannel target) throws IOException {
		assertEquals(0, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(36, inputOp.remaining());

		int count = inputOp.transferTo(3, 4, target);
		assertEquals((int)4, count);

		assertEquals(0, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(36, inputOp.remaining());

		count = inputOp.transferTo(target, 5);
		assertEquals((int)5, count);

		assertEquals(5, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(31, inputOp.remaining());
	}

	private static final void doTestTransferMethods(final OctetInputOp inputOp,
			final OutputStream target) throws IOException {
		assertEquals(0, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(36, inputOp.remaining());

		int count = inputOp.transferTo(3, 4, target);
		assertEquals((int)4, count);

		assertEquals(0, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(36, inputOp.remaining());

		count = inputOp.transferTo(target, 5);
		assertEquals((int)5, count);

		assertEquals(5, inputOp.position());
		assertEquals(36, inputOp.limit());
		assertEquals(31, inputOp.remaining());
	}

}
