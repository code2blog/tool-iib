package gen;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbParserException;
import com.ibm.broker.plugin.MbService;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;

public class SyslogDemo_Request_Response_JavaCompute extends MbJavaComputeNode {
	public static final int SYSLOG_INFO = 0;
	public static final int SYSLOG_WARN = 1;
	public static final int SYSLOG_ERROR = 2;
	static final String resource = LogHandlingMessages.class.getName();

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			MbElement outXmlnsc = outMessage.getRootElement().createElementAsLastChild(MbXMLNSC.PARSER_NAME);
			MbElement outSyslogResponse = outXmlnsc.createElementAsLastChild(MbXMLNSC.FOLDER, "syslogDemoResponse", null);
			outSyslogResponse.setNamespace("http://WindowsCommands_V1_IS");
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			MbElement inXmlnsc = inMessage.getRootElement().getFirstElementByPath("XMLNSC");
			MbElement inSyslogDemo = inXmlnsc.getFirstElementByPath("syslogDemo");
			String objectClassName = valueOf("className", inSyslogDemo);
			String method = valueOf("method", inSyslogDemo);
			int level = Integer.valueOf(valueOf("level", inSyslogDemo));
			String key = valueOf("key", inSyslogDemo);
			String insert = valueOf("insert", inSyslogDemo);
			String levelStr = "none";
			switch (level) {
			case SYSLOG_INFO:
				MbService.logInformation(objectClassName, method, resource, key, insert, null);
				levelStr= "info";
				break;
			case SYSLOG_WARN:
				MbService.logWarning(objectClassName, method, resource, key, insert, null);
				levelStr= "warning";
				break;
			case SYSLOG_ERROR:
				MbService.logError(objectClassName, method, resource, key, insert, null);
				levelStr= "error";
				break;
			default:
				break;
			}
			outSyslogResponse.createElementAsLastChild(MbXMLNSC.FIELD, "output", String.format("syslog event created with level=[%s]", levelStr)); 

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

	private String valueOf(String fieldName, MbElement rootElement) throws MbException {
		if(rootElement==null){
			throw new IllegalArgumentException(String.format("rootElemnet is null. Cannot find [%s] within null", fieldName));
		}
		MbElement element = rootElement.getFirstElementByPath(fieldName);
		if (element == null) {
			throw new IllegalArgumentException(String.format("unable to find field [%s]", fieldName));
		}
		return element.getValueAsString();
	}
}
