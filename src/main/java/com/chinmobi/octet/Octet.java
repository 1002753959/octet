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
package com.chinmobi.octet;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.chinmobi.octet.io.OctetInputOp;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public interface Octet extends Comparable<Octet>, Serializable {

	public byte byteAt(int index);

	public int begin();

	public int end();

	public boolean isEmpty();

	public int length();

	public boolean hasBuffer();
	public ByteBuffer buffer();

	public boolean hasArray();
	public byte[] array();
	public int arrayOffset();


	public boolean equals(Octet another);
	public boolean equals(byte[] bytes, int offset, int length);
	public boolean equals(byte[] bytes);

	public boolean startsWith(Octet prefix);
	public boolean startsWith(byte[] bytes, int offset, int length);
	public boolean startsWith(byte[] bytes);

	public boolean startsWith(int fromIndex, Octet prefix);
	public boolean startsWith(int fromIndex, byte[] bytes, int offset, int length);
	public boolean startsWith(int fromIndex, byte[] bytes);

	public boolean endsWith(Octet suffix);
	public boolean endsWith(byte[] bytes, int offset, int length);
	public boolean endsWith(byte[] bytes);

	public int indexOf(byte b);
	public int indexOf(int fromIndex, byte b);

	public int indexOneOf(byte[] bytes, int offset, int length);
	public int indexOneOf(byte[] bytes);

	public int indexOneOf(int fromIndex, byte[] bytes, int offset, int length);
	public int indexOneOf(int fromIndex, byte[] bytes);

	public int lastIndexOf(byte b);
	public int lastIndexOf(int fromIndex, byte b);

	public int indexOf(Octet octet);
	public int indexOf(byte[] bytes, int offset, int length);
	public int indexOf(byte[] bytes);

	public int indexOf(int fromIndex, Octet octet);
	public int indexOf(int fromIndex, byte[] bytes, int offset, int length);
	public int indexOf(int fromIndex, byte[] bytes);

	public int lastIndexOf(Octet octet);
	public int lastIndexOf(byte[] bytes, int offset, int length);
	public int lastIndexOf(byte[] bytes);

	public int lastIndexOf(int fromIndex, Octet octet);
	public int lastIndexOf(int fromIndex, byte[] bytes, int offset, int length);
	public int lastIndexOf(int fromIndex, byte[] bytes);


	public Octet suboctet(int beginIndex);
	public Octet suboctet(int beginIndex, int endIndex);

	public byte[] getBytes();

	public boolean equals(Object obj);

	public OctetInputOp inputOp();

	public Object lock();

	public void toString(boolean asHexFormat, Appendable appendable);
	public String toString(boolean asHexFormat);

}
