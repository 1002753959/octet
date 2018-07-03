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
package com.chinmobi.text;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import com.chinmobi.octet.Octet;

/**
 * @author <a href="mailto:yuzhaoping1970@gmail.com">Zhaoping Yu</a>
 *
 */
public interface TextOctet extends Octet {

	public int compareToIgnoreCase(TextOctet another);

	public boolean equalsIgnoreCase(TextOctet another);
	public boolean equalsIgnoreCase(byte[] bytes, int offset, int length);
	public boolean equalsIgnoreCase(byte[] bytes);

	public boolean startsWith(boolean ignoreCase, TextOctet prefix);
	public boolean startsWith(boolean ignoreCase, byte[] bytes, int offset, int length);
	public boolean startsWith(boolean ignoreCase, byte[] bytes);

	public boolean startsWith(boolean ignoreCase, int fromIndex, TextOctet prefix);
	public boolean startsWith(boolean ignoreCase, int fromIndex, byte[] bytes, int offset, int length);
	public boolean startsWith(boolean ignoreCase, int fromIndex, byte[] bytes);

	public boolean endsWith(boolean ignoreCase, TextOctet suffix);
	public boolean endsWith(boolean ignoreCase, byte[] bytes, int offset, int length);
	public boolean endsWith(boolean ignoreCase, byte[] bytes);

	public int indexOf(boolean ignoreCase, TextOctet octet);
	public int indexOf(boolean ignoreCase, byte[] bytes, int offset, int length);
	public int indexOf(boolean ignoreCase, byte[] bytes);

	public int indexOf(boolean ignoreCase, int fromIndex, TextOctet octet);
	public int indexOf(boolean ignoreCase, int fromIndex, byte[] bytes, int offset, int length);
	public int indexOf(boolean ignoreCase, int fromIndex, byte[] bytes);

	public int lastIndexOf(boolean ignoreCase, TextOctet octet);
	public int lastIndexOf(boolean ignoreCase, byte[] bytes, int offset, int length);
	public int lastIndexOf(boolean ignoreCase, byte[] bytes);

	public int lastIndexOf(boolean ignoreCase, int fromIndex, TextOctet octet);
	public int lastIndexOf(boolean ignoreCase, int fromIndex, byte[] bytes, int offset, int length);
	public int lastIndexOf(boolean ignoreCase, int fromIndex, byte[] bytes);


	public TextOctet subtext(int beginIndex);
	public TextOctet subtext(int beginIndex, int endIndex);

	public TextOctet trim();

	public boolean hasCharset();

	public boolean isSameCharset(String charsetName);

	public Charset getCharset();

	public void appendTo(Appendable appendable) throws IOException;

	public Reader getReader();

	public String toString();

}
