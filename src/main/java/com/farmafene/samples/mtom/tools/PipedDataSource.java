/*
 * Copyright (c) 2009-2015 farmafene.com
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.farmafene.samples.mtom.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipedDataSource implements DataSource {

	private static final Logger logger = LoggerFactory.getLogger(PipedDataSource.class);
	private InputStream input = null;
	private String contentType = "*/*";
	private String name = null;

	public PipedDataSource(final InputStream is) throws IOException {
		this(is, "application/octet-stream");
	}

	public PipedDataSource(final InputStream is, final String contentType) throws IOException {
		this(is, contentType, "");
	}

	public PipedDataSource(final InputStream is, final String contentType, final String name) throws IOException {
		this.input = is;
		this.contentType = contentType;
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.activation.DataSource#getContentType()
	 */
	@Override
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.activation.DataSource#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return this.input;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.activation.DataSource#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		final UnsupportedOperationException e = new UnsupportedOperationException("OutputStream is not supported");
		logger.error("getOutputStram()", e);
		throw e;
	}

}
