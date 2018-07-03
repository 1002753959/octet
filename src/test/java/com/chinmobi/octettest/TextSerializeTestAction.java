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

import com.chinmobi.octet.BufferAllocator;
import com.chinmobi.octet.ExpandableOctetBuffer;
import com.chinmobi.testapp.BaseTestAction;
import com.chinmobi.text.ArrayText;
import com.chinmobi.text.BufferText;
import com.chinmobi.text.ExpandableTextBuffer;
import com.chinmobi.text.MutableArrayText;
import com.chinmobi.text.TextOctet;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public final class TextSerializeTestAction extends BaseTestAction implements BufferAllocator {

	private static final byte[] TEST_ARRAY_0 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	private boolean isAllocateDirect;


	public TextSerializeTestAction() {
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

	public final void testArrayText() {
		final ArrayText arrayText = new ArrayText(TEST_ARRAY_0);

		arrayText.setCharset("UTF-8");
		arrayText.suboctet(1, 5);

		assertText(arrayText);
		assertNull(arrayText.buffer());
		assertNotNull(arrayText.array());
		assertEquals(TEST_ARRAY_0.length, arrayText.array().length);

		TextOctet text = null;
		try {
			text = doSerialize(arrayText);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(text);
		assertTrue((text instanceof ArrayText));

		assertText(text);
		assertNull(text.buffer());
		assertNotNull(text.array());
		assertEquals(TEST_ARRAY_0.length, text.array().length);
	}

	public final void testMutableArrayText() {
		final MutableArrayText arrayText = new MutableArrayText(TEST_ARRAY_0.length);
		arrayText.outputOp().put(TEST_ARRAY_0).update();

		arrayText.setCharset("UTF-8");
		arrayText.suboctet(1, 5);

		assertText(arrayText);
		assertNull(arrayText.buffer());
		assertNotNull(arrayText.array());
		assertEquals(TEST_ARRAY_0.length, arrayText.array().length);

		TextOctet text = null;
		try {
			text = doSerialize(arrayText);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(text);
		assertTrue((text instanceof MutableArrayText));

		assertText(text);
		assertNull(text.buffer());
		assertNotNull(text.array());
		assertEquals(TEST_ARRAY_0.length, text.array().length);
	}

	public final void testBufferText() {
		final BufferText bufferText = new BufferText(ByteBuffer.allocate(TEST_ARRAY_0.length));
		bufferText.clear().outputOp().put(TEST_ARRAY_0).update();

		bufferText.setCharset("UTF-8");
		bufferText.suboctet(1, 5);

		assertText(bufferText);
		assertNotNull(bufferText.buffer());
		assertNotNull(bufferText.array());

		TextOctet text = null;
		try {
			text = doSerialize(bufferText);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(text);
		assertTrue((text instanceof BufferText));

		assertText(text);
		assertNotNull(text.buffer());
		assertNotNull(text.array());
		assertEquals(bufferText.buffer().capacity(), text.buffer().capacity());
		assertEquals(bufferText.buffer().position(), text.buffer().position());
		assertEquals(bufferText.buffer().limit(), text.buffer().limit());
	}

	public final void testBufferTextD() {
		final BufferText bufferText = new BufferText(ByteBuffer.allocateDirect(TEST_ARRAY_0.length));
		bufferText.clear().outputOp().put(TEST_ARRAY_0).update();

		bufferText.setCharset("UTF-8");
		bufferText.suboctet(1, 5);

		assertText(bufferText);
		assertNotNull(bufferText.buffer());
		assertNull(bufferText.array());

		TextOctet text = null;
		try {
			text = doSerialize(bufferText);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(text);
		assertTrue((text instanceof BufferText));

		assertText(text);
		assertNotNull(text.buffer());
		assertNull(text.array());
		assertEquals(bufferText.buffer().capacity(), text.buffer().capacity());
		assertEquals(bufferText.buffer().position(), text.buffer().position());
		assertEquals(bufferText.buffer().limit(), text.buffer().limit());
	}

	public final void testTextBuffer() {
		this.isAllocateDirect = false;

		final ExpandableTextBuffer textBuf = new ExpandableTextBuffer((BufferAllocator)this, 1);
		textBuf.outputOp().put(TEST_ARRAY_0).update();

		textBuf.setCharset("UTF-8");

		final TextOctet bufferText = textBuf.toInputText();
		bufferText.suboctet(1, 5);

		assertText(bufferText);
		assertNotNull(bufferText.buffer());
		assertNotNull(bufferText.array());

		ExpandableTextBuffer result = null;
		try {
			result = doSerialize(textBuf);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(result);
		assertTrue((result instanceof ExpandableTextBuffer));

		final TextOctet text = result.toInputText();
		assertText(text);
		assertNotNull(text.buffer());
		assertNotNull(text.array());
		assertEquals(bufferText.buffer().capacity(), text.buffer().capacity());
		assertEquals(bufferText.buffer().position(), text.buffer().position());
		assertEquals(bufferText.buffer().limit(), text.buffer().limit());
	}

	public final void testTextBufferD() {
		this.isAllocateDirect = true;

		final ExpandableTextBuffer textBuf = new ExpandableTextBuffer((BufferAllocator)this, 1);
		textBuf.outputOp().put(TEST_ARRAY_0).update();

		textBuf.setCharset("UTF-8");

		final TextOctet bufferText = textBuf.toInputText();
		bufferText.suboctet(1, 5);

		assertText(bufferText);
		assertNotNull(bufferText.buffer());
		assertNull(bufferText.array());

		ExpandableTextBuffer result = null;
		try {
			result = doSerialize(textBuf);
		} catch (IOException ex) {
			fail(ex);
		} catch (ClassNotFoundException ex) {
			fail(ex);
		}

		assertNotNull(result);
		assertTrue((result instanceof ExpandableTextBuffer));

		final TextOctet text = result.toInputText();
		assertText(text);
		assertNotNull(text.buffer());
		assertNull(text.array());
		assertEquals(bufferText.buffer().capacity(), text.buffer().capacity());
		assertEquals(bufferText.buffer().position(), text.buffer().position());
		assertEquals(bufferText.buffer().limit(), text.buffer().limit());
	}


	private static final void assertText(final TextOctet text) {
		assertEquals(1, text.begin());
		assertEquals(5, text.end());

		assertTrue(text.hasCharset());
		assertTrue(text.isSameCharset("UTF-8"));

		final String expectedString = "1234";

		assertEquals(expectedString, text.toString());
	}

	private final TextOctet doSerialize(final TextOctet text) throws IOException, ClassNotFoundException {
		final ExpandableOctetBuffer dump = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		final ObjectOutputStream objOut = new ObjectOutputStream(dump.outputOp());

		objOut.writeObject(text);
		objOut.flush();
		objOut.close();

		final ObjectInputStream objIn = new ObjectInputStream(dump.inputOp());
		final TextOctet result = (TextOctet)objIn.readObject();
		objIn.close();

		return result;
	}

	private final ExpandableTextBuffer doSerialize(final ExpandableTextBuffer textBuf) throws IOException, ClassNotFoundException {
		final ExpandableOctetBuffer dump = new ExpandableOctetBuffer((BufferAllocator)this, 1);

		final ObjectOutputStream objOut = new ObjectOutputStream(dump.outputOp());

		objOut.writeObject(textBuf);
		objOut.flush();
		objOut.close();

		final ObjectInputStream objIn = new ObjectInputStream(dump.inputOp());
		final ExpandableTextBuffer result = (ExpandableTextBuffer)objIn.readObject();
		objIn.close();

		return result;
	}

}
