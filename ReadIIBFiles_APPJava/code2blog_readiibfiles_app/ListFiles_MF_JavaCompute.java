package code2blog_readiibfiles_app;

import java.io.File;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;

public class ListFiles_MF_JavaCompute extends MbJavaComputeNode {

	@SuppressWarnings("deprecation")
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
			MbElement outXmlnsc = outMessage.getRootElement().createElementAsLastChild(MbXMLNSC.PARSER_NAME);
			MbElement response = outXmlnsc.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "response", "");

			MbElement pathElement = inMessage.getRootElement().getFirstElementByPath("HTTPInputHeader/X-Query-String");
			Utility.nullCheck(pathElement, "null value for listFolder query string is not allowed");
			String path = pathElement.getValueAsString().split("=")[1];
			path = path.replace("/", "\\");
			File file = new File(path);
			String[] list = file.list();
			if (list == null) {
				return;
			}
			for (String entry : list) {
				response.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "dir_entry", entry);
			}
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
