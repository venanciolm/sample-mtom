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

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.pipe.TubelineAssembler;
import com.sun.xml.ws.api.pipe.TubelineAssemblerFactory;

//
//
//
//
// in /META-INF/services/com.sun.xml.ws.api.pipe.TubelineAssemblerFactory
// debe estar esta clase: com.farmafene.samples.mtom.tube.TubeFactory
//
public class TubeFactory extends TubelineAssemblerFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(TubeFactory.class);

	/**
	 * {@inheritDoc}
	 * @see com.sun.xml.ws.api.pipe.TubelineAssemblerFactory#doCreate(com.sun.xml.ws.api.BindingID)
	 */
	@Override
	public TubelineAssembler doCreate(final BindingID bindingId) {
		LOGGER.info("Obtenida la factoría {}", this);
		return new WSTubeAssembler();
	}

}
