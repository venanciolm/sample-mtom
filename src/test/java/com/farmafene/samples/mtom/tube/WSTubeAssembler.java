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
package com.farmafene.samples.mtom.tube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubelineAssembler;

public class WSTubeAssembler implements TubelineAssembler {

	private static final Logger LOGGER = LoggerFactory.getLogger(WSTubeAssembler.class);

	/**
	 * (non-Javadoc)
	 *
	 * @see com.sun.xml.ws.api.pipe.TubelineAssembler#createClient(com.sun.xml.ws
	 *      .api.pipe.ClientTubeAssemblerContext)
	 */
	@Override
	public Tube createClient(final ClientTubeAssemblerContext context) {
		LOGGER.info("Creamos el log '{}'", context);
		Tube head = context.createTransportTube();
		// this is where you plug in your
		// custom logging tube
		head = new LoggingTube(head);
		head = context.createSecurityTube(head);
		head = context.createWsaTube(head);
		head = context.createClientMUTube(head);
		return context.createHandlerTube(head);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see com.sun.xml.ws.api.pipe.TubelineAssembler#createServer(com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext)
	 */
	@Override
	public Tube createServer(final ServerTubeAssemblerContext context) {
		LOGGER.info("No debería hacer nada '{}'", context);
		// we did not need custom tube here at server
		Tube head = context.getTerminalTube();
		head = context.createHandlerTube(head);
		head = context.createMonitoringTube(head);
		head = context.createServerMUTube(head);
		head = context.createWsaTube(head);
		head = context.createSecurityTube(head);
		return head;
	}

}
