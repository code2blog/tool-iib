package com.wordpress.code2blog.retry_jp;

import java.util.Date;
import java.util.Random;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbDate;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbTime;
import com.ibm.broker.plugin.MbUserException;

public class PrepareTimeoutRequest extends MbJavaComputeNode {

	@Override
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			final Date date = new Date();
			date.setHours(22);// 22:00 is 10pm
			date.setDate(date.getDate() + 1);// retry date is set to day+1; that is tomorrow
			MbDate startDate = new MbDate(date.getYear() + 1900, date.getMonth(), date.getDate());
			MbTime startTime = new MbTime(date.getHours(), date.getMinutes(), date.getSeconds());
			
			MbElement timeoutRequest = inAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME, "TimeoutRequest", null);
			set(timeoutRequest, "Action", "SET");
			set(timeoutRequest, "Identifier", new Random().nextLong());
			set(timeoutRequest, "StartDate", startDate);
			set(timeoutRequest, "StartTime", startTime);
			set(timeoutRequest, "Count", new Long(1));
			set(timeoutRequest, "IgnoreMissed", new Boolean(false));
			set(timeoutRequest, "AllowOverwrite", new Boolean(false));
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
			// Example handling ensures all exceptions are re-thrown to be
			// handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(), null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

	private MbElement set(MbElement root, String key, Object value) throws MbException {
		MbElement child = null;
		child = root.getFirstElementByPath(key);
		// create child field if not already present
		if (child == null) {
			child = root.createElementAsLastChild(MbElement.TYPE_NAME, key, null);
		}
		// you can not set tree structure, you have to copy them
		if (value != null) {
			if (value instanceof MbElement) {
				child.copyElementTree((MbElement) value);
			} else {
				child.setValue(value);
			}
		}
		return child;
	}

}
