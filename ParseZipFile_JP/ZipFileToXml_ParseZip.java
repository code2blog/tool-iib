import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;

public class ZipFileToXml_ParseZip extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly assembly) throws MbException {
		try {
			MbOutputTerminal out = getOutputTerminal("out");
			MbOutputTerminal alt = getOutputTerminal("alternate");

			MbMessage message = assembly.getMessage();

			MbElement root = message.getRootElement();
			MbElement blob = root.getLastChild();
			MbElement blobblob = blob.getLastChild();
			Object array = blobblob.getValue();
			if (!(array instanceof byte[])) {
				throw new IllegalArgumentException("byte array not found");
			}
			InputStream is = new ByteArrayInputStream((byte[]) array);
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry = null;
			Map<String, String> xmlEntries = new HashMap();
			Map<String, String> pdfEntries = new HashMap();
			while ((entry = zis.getNextEntry()) != null) {
				String name = entry.getName();
				if (name.toLowerCase().endsWith(".xml")) {
					String content = new String(readBytesDontCloseStream(zis));
					xmlEntries.put(name, content);
				}
				if (name.toLowerCase().endsWith(".pdf")) {
					byte[] content = (readBytesDontCloseStream(zis));
					String base64 = encode(content);
					pdfEntries.put(name, base64);
				}
			}
			//
			Set<String> keySet = xmlEntries.keySet();
			for (String key : keySet) {
				String xml = xmlEntries.get(key);
				Document doc = parse(xml);
				List<Node> invoiceList = xpathFind("//Invoice", doc);
				for (Node node : invoiceList) {
					List<Node> list = xpathFind("./imagename", node);
					Node imagename = list.get(0);
					String pdfName = imagename.getTextContent();
					pdfName = pdfName.trim();
					String pdfContent = pdfEntries.get(pdfName);
					//
					Element pdfNode = doc.createElement("imageContent");
					pdfNode.setTextContent(pdfContent);
					Node parent = imagename.getParentNode();
					parent.appendChild(pdfNode);
					//
					Node nodeNew = node.cloneNode(true);
					String outxml = node2String(nodeNew);
					propagate(outxml, assembly);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String encode(byte[] content) {
		String encoded = new sun.misc.BASE64Encoder().encode(content);
		encoded = encoded.replace("\r", "");
		encoded = encoded.replace("\n", "");
		return encoded;
	}

	private List<Node> xpathFind(String path, Node node) throws Exception {
		XPathFactory xFactory = XPathFactory.newInstance();
		XPath xpath = xFactory.newXPath();
		Object result = xpath.evaluate(path, node, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		List<Node> list = new ArrayList();
		for (int i = 0; i < nodes.getLength(); i++) {
			list.add(nodes.item(i));
		}
		return list;
	}

	private Document parse(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		return doc;
	}

	private void propagate(String xml, MbMessageAssembly inAssembly) throws MbException {
		MbMessage inMessage = inAssembly.getMessage();
		MbElement inputRoot = inMessage.getRootElement();
		// create new output message
		MbMessage outMessage = new MbMessage(inMessage);
		MbElement outputRoot = outMessage.getRootElement();
		MbElement outputBody = outputRoot.getLastChild();
		final MbMessage env = inAssembly.getLocalEnvironment();
		MbMessage localEnv = new MbMessage(env);
		//
		while (outputBody.getFirstChild() != null) {
			outputBody.getFirstChild().detach();
		}
		outputBody.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", xml.getBytes());
		//
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, localEnv, inAssembly.getExceptionList(), outMessage);
		MbOutputTerminal out = getOutputTerminal("out");
		out.propagate(outAssembly);
	}

	public static byte[] readBytesDontCloseStream(InputStream is) throws IOException {
		ByteArrayOutputStream fileData = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int numRead = 0;
		while ((numRead = is.read(buf)) != -1) {
			fileData.write(buf, 0, numRead);
		}
		return fileData.toByteArray();
	}

	static String node2String(Node node) throws TransformerFactoryConfigurationError, TransformerException {
		// you may prefer to use single instances of Transformer, and
		// StringWriter rather than create each time. That would be up to your
		// judgement and whether your app is single threaded etc
		StreamResult xmlOutput = new StreamResult(new StringWriter());
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(node), xmlOutput);
		return xmlOutput.getWriter().toString();
	}

	public static String serialize(Document doc) throws IOException {
		final OutputFormat format = new OutputFormat(doc);
		format.setLineWidth(-1);
		format.setIndenting(false);
		// format.setIndent(2); //indenting will cause issues while mqsirestorebroker
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final XMLSerializer serializer = new XMLSerializer(os, format);
		serializer.serialize(doc);
		os.close();
		return os.toString();
	}
}
