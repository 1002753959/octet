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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import com.chinmobi.octet.ArrayOctet;
import com.chinmobi.octet.BufferAllocator;
import com.chinmobi.octet.BufferOctet;
import com.chinmobi.octet.ExpandableOctetBuffer;
import com.chinmobi.octet.MutableArrayOctet;
import com.chinmobi.octet.Octet;
import com.chinmobi.testapp.BaseTestAction;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class OctetSerializeTestAction extends BaseTestAction implements BufferAllocator {

	private static final byte[] TEST_ARRAY_0 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private boolean isAllocateDirect;


	public OctetSerializeTestAction() {
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

	public final void testArrayOctet() {
		final ArrayOctet arrayOctet = new ArrayOctet(TEST_ARRAY_0);

		arrayOctet.suboctet(1, 5);

		assertOctet(arrayOctet);
		assertNull(arrayOctet.buffer());
		assertNotNull(arrayOctet.array());
		assertEquals(TEST_ARRAY_0.length, arrayOctet.array().length);

		Octet octet = null;
		try {
			octet = doSerialize(arrayOctet);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(octet);
		assertTrue((octet instanceof ArrayOctet));

		assertOctet(octet);
		assertNull(octet.buffer());
		assertNotNull(octet.array());
		assertEquals(TEST_ARRAY_0.length, octet.array().length);
	}

	public final void testMutableArrayOctet() {
		final MutableArrayOctet arrayOctet = new MutableArrayOctet(TEST_ARRAY_0.length);
		arrayOctet.outputOp().put(TEST_ARRAY_0).update();

		arrayOctet.suboctet(1, 5);

		assertOctet(arrayOctet);
		assertNull(arrayOctet.buffer());
		assertNotNull(arrayOctet.array());
		assertEquals(TEST_ARRAY_0.length, arrayOctet.array().length);

		Octet octet = null;
		try {
			octet = doSerialize(arrayOctet);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(octet);
		assertTrue((octet instanceof MutableArrayOctet));

		assertOctet(octet);
		assertNull(octet.buffer());
		assertNotNull(octet.array());
		assertEquals(TEST_ARRAY_0.length, octet.array().length);
	}

	public final void testBufferOctet() {
		final BufferOctet bufferOctet = new BufferOctet(ByteBuffer.allocate(TEST_ARRAY_0.length));
		bufferOctet.clear().outputOp().put(TEST_ARRAY_0).update();

		bufferOctet.suboctet(1, 5);

		assertOctet(bufferOctet);
		assertNotNull(bufferOctet.buffer());
		assertNotNull(bufferOctet.array());

		Octet octet = null;
		try {
			octet = doSerialize(bufferOctet);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(octet);
		assertTrue((octet instanceof BufferOctet));

		assertOctet(octet);
		assertNotNull(octet.buffer());
		assertNotNull(octet.array());
		assertEquals(bufferOctet.buffer().capacity(), octet.buffer().capacity());
		assertEquals(bufferOctet.buffer().position(), octet.buffer().position());
		assertEquals(bufferOctet.buffer().limit(), octet.buffer().limit());
	}

	public final void testBufferOctetD() {
		final BufferOctet bufferOctet = new BufferOctet(ByteBuffer.allocateDirect(TEST_ARRAY_0.length));
		bufferOctet.clear().outputOp().put(TEST_ARRAY_0).update();

		bufferOctet.suboctet(1, 5);

		assertOctet(bufferOctet);
		assertNotNull(bufferOctet.buffer());
		assertNull(bufferOctet.array());

		Octet octet = null;
		try {
			octet = doSerialize(bufferOctet);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(octet);
		assertTrue((octet instanceof BufferOctet));

		assertOctet(octet);
		assertNotNull(octet.buffer());
		assertNull(octet.array());
		assertEquals(bufferOctet.buffer().capacity(), octet.buffer().capacity());
		assertEquals(bufferOctet.buffer().position(), octet.buffer().position());
		assertEquals(bufferOctet.buffer().limit(), octet.buffer().limit());
	}

	public final void testOctetBuffer() {
		this.isAllocateDirect = false;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);
		octetBuf.outputOp().put(TEST_ARRAY_0).update();

		final Octet bufferOctet = octetBuf.toInput();
		bufferOctet.suboctet(1, 5);

		assertOctet(bufferOctet);
		assertNotNull(bufferOctet.buffer());
		assertNotNull(bufferOctet.array());

		ExpandableOctetBuffer result = null;
		try {
			result = doSerialize(octetBuf);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(result);
		assertTrue((result instanceof ExpandableOctetBuffer));

		final Octet octet = result.toInput();
		assertOctet(octet);
		assertNotNull(octet.buffer());
		assertNotNull(octet.array());
		assertEquals(bufferOctet.buffer().capacity(), octet.buffer().capacity());
		assertEquals(bufferOctet.buffer().position(), octet.buffer().position());
		assertEquals(bufferOctet.buffer().limit(), octet.buffer().limit());
	}

	public final void testOctetBufferD() {
		this.isAllocateDirect = true;

		final ExpandableOctetBuffer octetBuf = new ExpandableOctetBuffer((BufferAllocator)this, 1);
		octetBuf.outputOp().put(TEST_ARRAY_0).update();

		final Octet bufferOctet = octetBuf.toInput();
		bufferOctet.suboctet(1, 5);

		assertOctet(bufferOctet);
		assertNotNull(bufferOctet.buffer());
		assertNull(bufferOctet.array());

		ExpandableOctetBuffer result = null;
		try {
			result = doSerialize(octetBuf);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(result);
		assertTrue((result instanceof ExpandableOctetBuffer));

		final Octet octet = result.toInput();
		assertOctet(octet);
		assertNotNull(octet.buffer());
		assertNull(octet.array());
		assertEquals(bufferOctet.buffer().capacity(), octet.buffer().capacity());
		assertEquals(bufferOctet.buffer().position(), octet.buffer().position());
		assertEquals(bufferOctet.buffer().limit(), octet.buffer().limit());
	}


	private static final void assertOctet(final Octet octet) {
		assertEquals(1, octet.begin());
		assertEquals(5, octet.end());

		final String expectedString =
				"\r\n" +
				"31 32 33 34                                       1234" + "\r\n";

		assertEquals(expectedString, octet.toString(true));
	}

	private final Octet doSerialize(final Octet octet) throws IOException, ClassNotFoundException {
		final ExpandableOctetBuffer dump = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		final ObjectOutputStream objOut = new ObjectOutputStream(dump.outputOp());

		objOut.writeObject(octet);
		objOut.flush();
		objOut.close();

		final ObjectInputStream objIn = new ObjectInputStream(dump.inputOp());
		final Octet result = (Octet)objIn.readObject();
		objIn.close();

		return result;
	}

	private final ExpandableOctetBuffer doSerialize(final ExpandableOctetBuffer octetBuf) throws IOException, ClassNotFoundException {
		final ExpandableOctetBuffer dump = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		final ObjectOutputStream objOut = new ObjectOutputStream(dump.outputOp());

		objOut.writeObject(octetBuf);
		objOut.flush();
		objOut.close();

		final ObjectInputStream objIn = new ObjectInputStream(dump.inputOp());
		final ExpandableOctetBuffer result = (ExpandableOctetBuffer)objIn.readObject();
		objIn.close();

		return result;
	}

}
