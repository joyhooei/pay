package com.vrg.payserver.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XmlUtils {

	@SuppressWarnings("unchecked")
	public static <T> T parseObject(String xmlString, Class<T> returnClass) {
		try {
			JAXBContext context = JAXBContext.newInstance(returnClass);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			T returnObject = (T) unmarshaller.unmarshal(new StringReader(xmlString));

			return returnObject;
		} catch (Throwable t) {
		  Log.supplementExceptionMessage(t);
		}
		return null;
	}

	public static String toXmlString(Object object) {
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			StringWriter writer = new StringWriter();
			marshaller.marshal(object, writer);
			String xmlString = writer.toString();
			return xmlString;
		} catch (Throwable t) {
		  Log.supplementExceptionMessage(t);
		}
		return null;
	}
}