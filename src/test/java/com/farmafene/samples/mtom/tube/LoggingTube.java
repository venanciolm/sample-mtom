package com.farmafene.samples.mtom.tube;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;

public class LoggingTube extends AbstractFilterTubeImpl {

	final XMLOutputFactory staxOut = XMLOutputFactory.newInstance();
	private static final Logger log = LoggerFactory.getLogger(LoggingTube.class);

	/**
	 * @param that
	 * @param cloner
	 */
	public LoggingTube(final AbstractFilterTubeImpl that, final TubeCloner cloner) {
		super(that, cloner);
	}

	/**
	 * @param next
	 */
	public LoggingTube(final Tube next) {
		super(next);
	}

/**
	 * {@inheritDoc
	 * @see com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl#copy(com.sun.xml.ws.api.pipe.TubeCloner)
	 */
	@Override
	public AbstractTubeImpl copy(final TubeCloner cloner) {
		return new LoggingTube(this, cloner);
	}

	@Override
	public NextAction processRequest(final Packet request) {

		final Message message = request.getMessage();
		logMessage(message);

		return super.processRequest(request);
	}

	@Override
	public NextAction processResponse(final Packet packet) {

		final Message message = packet.getMessage();
		logMessage(message);

		return super.processResponse(packet);
	}

	private void logMessage(final Message message) {
		if (message == null) {
			log.info("Logger processing request message: null");
			return;
		}

		// extract message URI an log it with different logger so that user
		// sees that messages from this uri gets to this implementation
		// but it may be filtered out
		// and not shown due to log4 logging level settings
		final String messageUri = extractMessageUri(message);
		log.info("Logger processing response for URI: " + messageUri);

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			XMLStreamWriter writer = this.staxOut.createXMLStreamWriter(outputStream);
			writer = createIndenter(writer);
			message.copy().writeTo(writer);
			writer.close();
			logByteOutputStream(messageUri, outputStream);
		} catch (final XMLStreamException e) {
			log.error("Error during logging message in ExampleLoggingTube", e);
		}
	}

	private void logByteOutputStream(final String messageUri, final ByteArrayOutputStream outputStream) {
		log.warn("Escribiendo el mensaje {},'{}'", outputStream.size(), messageUri);
		if (outputStream.size() > 0) {
			final String outputLog = new String(outputStream.toByteArray());
			final StringBuffer buffer = new StringBuffer();
			buffer.append(messageUri);
			buffer.append(" : ");
			buffer.append(outputLog);

			logMessageToNamspaceAwareLogger(messageUri, buffer.toString());
		}
	}

	// log your message here to any logger you like, you can create
	// logger based on messageUri here or any other message/packet attribute
	// for that matter, then in log4j file you can choose which messages you
	// want to appear in log files
	protected void logMessageToNamspaceAwareLogger(final String messageUri, final String message) {
		final Logger log = LoggerFactory.getLogger(messageUri);
		log.debug(message);
	}

	private String extractMessageUri(final Message message) {
		String messageUri = message.getPayloadNamespaceURI();
		if (messageUri == null || messageUri.length() == 0) {
			messageUri = "default";
		}
		return messageUri;
	}

	/**
	 * @param writer
	 * @return
	 */
	private XMLStreamWriter createIndenter(XMLStreamWriter writer) {
		try {
			log.warn("SEARCHING: stax-utils.jar?");
			@SuppressWarnings("rawtypes")
			final Class clazz = getClass().getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
			@SuppressWarnings({ "rawtypes", "unchecked" })
			final Constructor c = clazz.getConstructor(XMLStreamWriter.class);
			writer = (XMLStreamWriter) c.newInstance(writer);
		} catch (final Exception e) {
			log.warn("WARNING: put stax-utils.jar to the classpath to indent the dump output");
		}
		return writer;
	}
}
