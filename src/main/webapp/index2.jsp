<!DOCTYPE html>
<%@page import="java.io.PrintStream"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.net.URL"%>
<%@page import="com.farmafene.samples.mtom.tools.PipedDataSource"%>
<%@page import="com.farmafene.samples.sample_service.schema.MtomPart"%>
<%@page
	import="com.farmafene.samples.sample_service.schema.SampleResponse"%>
<%@page
	import="com.farmafene.samples.sample_service.schema.SampleRequest"%>
<%@page import="com.farmafene.samples.mtom.SampleService"%>
<%@page import="javax.xml.ws.soap.SOAPBinding"%>
<%@page import="java.util.List"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="javax.xml.ws.Service"%>
<%@page import="javax.xml.ws.ServiceMode"%>
<%@page import="java.io.InputStream"%>
<%@page import="javax.activation.FileDataSource"%>
<%@page import="javax.activation.DataHandler"%>
<%@page import="javax.xml.ws.WebServiceFeature"%>
<%@page import="javax.xml.ws.soap.MTOMFeature"%>
<%@page
	import="org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<title>Pagina</title>
</head>
<body>
	<h1>Prueba de funcionamiento</h1>
	URL&nbsp;<%=request.getRequestURI()%>
	</br> URL&nbsp;<%=request.getScheme()%>
	</br> URL&nbsp;<%=request.getServerName()%>
	</br> URL&nbsp;<%=request.getServerPort()%>
	</br> URL&nbsp;<%=request.getContextPath()%>
	</br>
</body>
<%
	String ns = "http://mtom.samples.farmafene.com/";
	String sName = "SampleService";
	String pName = "SampleServicePort";
	String wsdl = request.getScheme() + "://" + request.getServerName()
			+ ":" + request.getServerPort() + request.getContextPath()
			+ "/SampleService?WSDL";
	String file = "schema/sampleSchema.xsd";
%>URL:&nbsp;<%=wsdl%></br>
<%
	Service service = Service.create(new URL(wsdl),
			new QName(ns, sName));
	SampleService port = service.getPort(new QName(ns, pName),
			SampleService.class,
			new WebServiceFeature[] { new MTOMFeature(128) });

	final SampleRequest req = new SampleRequest();
	req.setParam("Un nombre");
	req.setParamFile(new MtomPart());
	PipedDataSource ds = new PipedDataSource(Thread.currentThread()
			.getContextClassLoader().getResourceAsStream(file),
			"text/plain");
	req.getParamFile().setContentType(ds.getContentType());
	DataHandler h = new DataHandler(ds);
	req.getParamFile().setFile(h);
	// Sending request
%>Sending request
</br>
<%
	SampleResponse res = null;
	try {
		res = port.echo(req);
%>done....
<br />
<%
	
%>Server responded:<%=res.getErrorCode()%><br /> Server responded:
<%=res.getErrorDescription()%><br />
<%
	final InputStream is = res.getParamFileResponse().getFile()
				.getInputStream();
		final byte[] buffer = new byte[512];
		int read = -1;
		int count = 0;
		while ((read = is.read(buffer)) > 0) {
%>Leidos&nbsp;
<%=read%><br />
<%
	count += read;
		}
%>Server responded:<%=count%><br /> Server responded:<%=res.getParam()%><br />
Server responded:<%=res.getParamFileResponse().getContentType()%><br />
<%=port%><br />
<%
	} catch (Exception e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		ps.flush();
		baos.flush();
		out.print(baos);
		ps.close();
		baos.close();
	}
%>
</html>