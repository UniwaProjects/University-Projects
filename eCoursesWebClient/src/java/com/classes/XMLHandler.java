package com.classes;

import java.io.StringReader;
import java.io.StringWriter;
import javax.security.auth.login.FailedLoginException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLHandler {

    private static CommunicationHandler com = new CommunicationHandler();
    private static XMLInputFactory xif = XMLInputFactory.newInstance();

    public static Object getUnmarshall(String path, JAXBContext jaxbContext) {
        Object o = new Object();
        try {
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StringReader stringReader = new StringReader(com.getXml(path));
            XMLStreamReader xsr = xif.createXMLStreamReader(stringReader);
            o = jaxbUnmarshaller.unmarshal(xsr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static void marshallSend(String path, Object o, JAXBContext jaxbContext) {
        try {
            StringWriter sw = new StringWriter();
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(o, sw);
            com.sendXml(path, sw.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Token authenticate(Credentials c) throws FailedLoginException {
        Token t = new Token();
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(Credentials.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(c, sw);
            StringReader stringReader = new StringReader(com.authenticate(sw.toString()));

            jaxbContext = JAXBContext.newInstance(Token.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            XMLStreamReader xsr = xif.createXMLStreamReader(stringReader);

            t = (Token) jaxbUnmarshaller.unmarshal(xsr);

        } catch (JAXBException | XMLStreamException e) {
            e.printStackTrace();
        }
        return t;
    }
}
