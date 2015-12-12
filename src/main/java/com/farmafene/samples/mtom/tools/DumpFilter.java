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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class DumpFilter extends SpringBeanAutowiringSupport implements Filter,
		InitializingBean {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DumpFilter.class);

	@Value("${filter.dumpRequest:false}")
	private boolean dumpRequest;
	@Value("${filter.dumpResponse:false}")
	private boolean dumpResponse;

	/**
	 * {@inheritDoc}
	 *
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("DumpRequest: {}", this.dumpRequest);
		LOGGER.info("DumpResponse: {}", this.dumpResponse);
	}

	private static class ByteArrayServletStream extends ServletOutputStream {

		ByteArrayOutputStream baos;

		ByteArrayServletStream(final ByteArrayOutputStream baos) {
			this.baos = baos;
		}

		@Override
		public void write(final int param) throws IOException {
			this.baos.write(param);
		}
	}

	private static class ByteArrayPrintWriter {

		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		private final PrintWriter pw = new PrintWriter(this.baos);

		private final ServletOutputStream sos = new ByteArrayServletStream(
				this.baos);

		public PrintWriter getWriter() {
			return this.pw;
		}

		public ServletOutputStream getStream() {
			return this.sos;
		}

		byte[] toByteArray() {
			return this.baos.toByteArray();
		}
	}

	private class BufferedServletInputStream extends ServletInputStream {

		ByteArrayInputStream bais;

		public BufferedServletInputStream(final ByteArrayInputStream bais) {
			this.bais = bais;
		}

		@Override
		public int available() {
			return this.bais.available();
		}

		@Override
		public int read() {
			return this.bais.read();
		}

		@Override
		public int read(final byte[] buf, final int off, final int len) {
			return this.bais.read(buf, off, len);
		}

	}

	private class BufferedRequestWrapper extends HttpServletRequestWrapper {

		ByteArrayInputStream bais;

		ByteArrayOutputStream baos;

		BufferedServletInputStream bsis;

		byte[] buffer;

		public BufferedRequestWrapper(final HttpServletRequest req)
				throws IOException {
			super(req);
			final InputStream is = req.getInputStream();
			this.baos = new ByteArrayOutputStream();
			final byte buf[] = new byte[1024];
			int letti;
			while ((letti = is.read(buf)) > 0) {
				this.baos.write(buf, 0, letti);
			}
			this.buffer = this.baos.toByteArray();
		}

		@Override
		public ServletInputStream getInputStream() {
			try {
				this.bais = new ByteArrayInputStream(this.buffer);
				this.bsis = new BufferedServletInputStream(this.bais);
			} catch (final Exception ex) {
				ex.printStackTrace();
			}

			return this.bsis;
		}

		public byte[] getBuffer() {
			return this.buffer;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(final ServletRequest servletRequest,
			final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {

		final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		final BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(
				httpRequest);

		if (this.dumpRequest) {
			LOGGER.warn("REQUEST ---> {}",
					new String(bufferedRequest.getBuffer()));
		}

		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		final ByteArrayPrintWriter pw = new ByteArrayPrintWriter();
		final HttpServletResponse wrappedResp = new HttpServletResponseWrapper(
				response) {
			@Override
			public PrintWriter getWriter() {
				return pw.getWriter();
			}

			@Override
			public ServletOutputStream getOutputStream() {
				return pw.getStream();
			}

		};

		filterChain.doFilter(bufferedRequest, wrappedResp);

		final byte[] bytes = pw.toByteArray();
		response.getOutputStream().write(bytes);
		if (this.dumpResponse) {
			LOGGER.warn("RESPONSE ---> {}", new String(bytes));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}
}