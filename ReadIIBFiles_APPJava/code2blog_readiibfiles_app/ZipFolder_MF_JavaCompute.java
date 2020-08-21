package code2blog_readiibfiles_app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.zip.ZipOutputStream;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class ZipFolder_MF_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			MbElement outBlob = outMessage.getRootElement().createElementAsLastChild(MbBLOB.PARSER_NAME);
			MbElement blob = outBlob.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", "");

			MbElement pathElement = inMessage.getRootElement().getFirstElementByPath("HTTPInputHeader/X-Query-String");
			Utility.nullCheck(pathElement, "null value for zipFolder query string is not allowed");
			String path = pathElement.getValueAsString().split("=")[1];
			path = path.replace("/", "\\");
			if (path.length() < 30) {
				throw new RuntimeException("for safety reasons, i will not zip this folder");
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			File inputFolder = new File(path);
			Utility.addDirToZipArchive(zos, inputFolder , null);
			zos.flush();
			baos.flush();
			zos.close();
			baos.close();

			blob.setValue(baos.toByteArray());

			MbElement properties = outMessage.getRootElement().createElementAsFirstChild("Properties");
			properties.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "ContentType", "application/octet");
			
			MbElement httpResponseHeader = properties.createElementAfter("HTTPResponseHeader");
			httpResponseHeader.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Content-Disposition", String.format("filename=\"%s.zip\"", inputFolder.getName()));

			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
