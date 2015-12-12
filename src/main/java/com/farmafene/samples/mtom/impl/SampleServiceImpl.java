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
package com.farmafene.samples.mtom.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.farmafene.samples.mtom.SampleService;
import com.farmafene.samples.mtom.tools.PipedDataSource;
import com.farmafene.samples.sample_service.schema.MtomPart;
import com.farmafene.samples.sample_service.schema.ObjectFactory;
import com.farmafene.samples.sample_service.schema.SampleRequest;
import com.farmafene.samples.sample_service.schema.SampleResponse;

@WebService(
//
endpointInterface = "com.farmafene.samples.mtom.SampleService",
//
targetNamespace = "http://mtom.samples.farmafene.com/",
//
portName = "SampleServicePort",
//
serviceName = "SampleService"
//
)
@MTOM
public class SampleServiceImpl implements SampleService {

	private static final Logger logger = LoggerFactory
			.getLogger(SampleServiceImpl.class);

	public SampleServiceImpl() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see com.farmafene.samples.mtom.SampleService#echo(com.farmafene.samples.sample_service.schema.SampleRequest)
	 */
	@Override
	public SampleResponse echo(final SampleRequest request) {
		final ObjectFactory f = new ObjectFactory();
		final SampleResponse response = f.createSampleResponse();
		try {
			response.setParam(request.getParam());
			final MtomPart m = new ObjectFactory().createMtomPart();
			m.setFile(new DataHandler(new PipedDataSource(request
					.getParamFile().getFile().getInputStream(), "*/*")));
			m.setContentType(request.getParamFile().getContentType());
			response.setParamFileResponse(m);
			response.setErrorCode(0);
			response.setErrorDescription("N/A");
			/*
			 * Debe ser null ... sin no va bien!
			 */
		} catch (final IOException e) {
			response.setErrorCode(1);
			response.setErrorDescription(e.getMessage());
		} catch (final Exception e) {
			response.setErrorCode(2);
			response.setErrorDescription(e.getMessage());
		}
		logger.info("Hemos terminado con el servicio!");
		return response;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.farmafene.samples.mtom.SampleService#getLorem()
	 */
	@Override
	public SampleResponse getLorem() {
		final ObjectFactory f = new ObjectFactory();
		final SampleResponse response = f.createSampleResponse();
		try {
			final InputStream is = getClass().getClassLoader()
					.getResourceAsStream("files/lorem.txt");
			final DataSource ds = new PipedDataSource(is, "text/plain");
			final DataHandler dh = new DataHandler(ds);
			final MtomPart m = f.createMtomPart();
			m.setFile(dh);
			response.setParamFileResponse(m);
			response.setErrorDescription("N/A");
			response.setParam("Lorem");
		} catch (final IOException e) {
			response.setErrorCode(1);
			response.setErrorDescription(e.getMessage());
		} catch (final Exception e) {
			response.setErrorCode(2);
			response.setErrorDescription(e.getMessage());
		}
		logger.info("Hemos terminado con el servicio!");
		return response;
	}
}
