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
package com.farmafene.samples.mtom;

import java.io.IOException;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farmafene.samples.sample_service.schema.ObjectFactory;
import com.farmafene.samples.sample_service.schema.SampleRequest;
import com.farmafene.samples.sample_service.schema.SampleResponse;

public class SampleServiceClass {
	private static final Logger logger = LoggerFactory
			.getLogger(SampleServiceClass.class);
	protected SampleService port;
	protected String file;

	public void Ejecucion() throws IOException {

		// System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
		// "true");
		// System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump",
		// "true");
		// System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump",
		// "true");
		// System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump",
		// "true");
		final ObjectFactory f = new ObjectFactory();
		final SampleRequest request = f.createSampleRequest();
		request.setParam(UUID.randomUUID().toString());
		request.setParamFile(f.createMtomPart());
		request.getParamFile().setContentType("text/plain");
		request.getParamFile().setFile(
				new DataHandler(new FileDataSource(this.file)));
		logger.info("invocando .........");
		final SampleResponse response = this.port.echo(request);
		logger.info("......... termino!");
		Assert.assertEquals(response.getParam(), request.getParam());
	}

}
