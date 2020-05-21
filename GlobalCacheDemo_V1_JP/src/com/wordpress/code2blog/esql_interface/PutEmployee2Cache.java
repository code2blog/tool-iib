package com.wordpress.code2blog.esql_interface;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import com.ibm.broker.plugin.MbElement;

public class PutEmployee2Cache {
	public static void loadDbRow(MbElement dbRow){
		try{
			Node domNode = dbRow.getDOMNode();
			String xmlString = domToString(domNode);
			xmlString.length();// stop debug here
		}catch(Exception e){
			// let this error be handled by message flow
			throw new RuntimeException(e);
		}
	}
	
	public static String domToString(Node domNode) throws Exception {
		DOMSource domSource = new DOMSource(domNode);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(domSource, result);
		String xmlString = writer.toString();
		return xmlString;
	}
}

