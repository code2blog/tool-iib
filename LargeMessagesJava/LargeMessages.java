/*
 * Sample program for use with IBM Integration Bus
 * © Copyright International Business Machines Corporation 2005, 2015
 * Licensed Materials - Property of IBM
*/

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;
import com.ibm.broker.plugin.MbTimestamp;

public class LargeMessages extends
		MbJavaComputeNode {
	
	// constant variables
	final String COMPLETION_ROOT = "SlicingReport";
	
	final String ROOT_LEVEL = "SaleEnvelope";
	final String HEADER = "Header";
	final String REPEATING_ELEMENT_COUNT = "SaleListCount";
	final String REPEATING_ELEMENT = "SaleList";
	
	// global variables
	private int intNumberOfSaleListsDeclared = 0;
	private int intNumberOfSaleListsFound = 0;
	private MbMessage inMessage = null;

	// int j = 0;
	
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();

		// Add user code below

		ProcessLargeMessageToProduceIndividualMessages(inAssembly, out, inMessage);
		ProduceProcessingCompleteNotification(inAssembly, alt);
			
		// End of user code
			
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		//out.propagate(outAssembly);
	}
	
    /**
    ============================================================================================
      > Declare variables
      > Find first instance of the element to process
      > For each instance found
        1> Release memory used to store information about the previous instance (if appropriate)
        2> Call a procedure to produce a single message the current instance
        3> Look for a following instance         
    ============================================================================================
    
	final String ROOT_LEVEL = "SaleEnvelope";
	final String HEADER = "Header";
	final String REPEATING_ELEMENT_COUNT = "SaleListCount";
	final String REPEATING_ELEMENT = "SaleList";
     */
	public void ProcessLargeMessageToProduceIndividualMessages(MbMessageAssembly inAssembly, MbOutputTerminal out, MbMessage inMessage) throws MbException {
		//create a reference of the input message
		this.inMessage = inMessage;
		// copy the input message
		MbMessage copyMessage = new MbMessage();
        MbElement xmlnsc = copyMessage.getRootElement();
        xmlnsc.addAsLastChild(inMessage.getRootElement().getFirstElementByPath("XMLNSC").copy());
        xmlnsc = xmlnsc.getFirstElementByPath("XMLNSC");
        // get the number of SaleList elements in the message
        intNumberOfSaleListsDeclared = Integer.parseInt((String)xmlnsc.getFirstElementByPath(ROOT_LEVEL + "/" + HEADER + "/" + REPEATING_ELEMENT_COUNT).getValue());
        
        // Locate the first instance of the repeating element to process
        // and declare a cursor on that element, so we can iterate over it
        MbElement refEnvironmentSaleList = xmlnsc.getFirstElementByPath(ROOT_LEVEL + "/" + REPEATING_ELEMENT);

        // now we have the start of the list, iterate over each element
        // find the repeating elements and call ProduceIndividualSaleListMessage on them.
        while (refEnvironmentSaleList != null){
        	intNumberOfSaleListsFound++;

        	// Note this line!
        	// This frees up any memory held by the parser from the last time around the loop.
        	// If you don't explicitly delete the parsed input element, the memory associated with parsing it
        	// is not freed until the whole message has finished being processed.
        	// For a large document this will be a significant overhead, and since we do not need random
        	// access to this document we can safely free this up now.
        	if (intNumberOfSaleListsFound > 1) refEnvironmentSaleList.getPreviousSibling().delete();

        	// Call a procedure to transform the current element, and send it down the flow
        	ProduceIndividualSaleListMessage(inAssembly, out, refEnvironmentSaleList);
        	
        	// find the next REPEATING_ELEMENT at this level of the message tree, if there is one
        	do {
        		refEnvironmentSaleList = refEnvironmentSaleList.getNextSibling();
        	}
        	while(refEnvironmentSaleList != null && !refEnvironmentSaleList.getName().equals(REPEATING_ELEMENT));
        }
	}
	
	/**
	 * Create and send a complete message containing a SaleList element and its Number
	 * by extracting part of an input message
	 * 
	 * @param inAssembly - pointer to the complete tree that came into this node
	 * @param out        - reference to the output terminal which the message slice should be sent to
	 * @param inSaleList - the current position in the input message, which is to be transformed and sent out
	 * @throws MbException
	 */
	public void ProduceIndividualSaleListMessage(MbMessageAssembly inAssembly, MbOutputTerminal out, MbElement inSaleList) throws MbException {

		// creates a new output message to hold the transformed data
		MbMessage outMessage = new MbMessage();
		
		// prepare the message headers so they have valid content for the output nodes we want to use
		copyMessageHeaders(outMessage);
		
		// take the elements we want out of the input message, and put them in the right place in the new output
		MbElement outElement = outMessage.getRootElement().createElementAsLastChild("XMLNSC");
		MbElement outRoot = outElement.createElementAsLastChild(MbXMLNSC.FOLDER, ROOT_LEVEL, null);
		outRoot.createElementAsLastChild(MbXMLNSC.FOLDER, "Number", "" + intNumberOfSaleListsFound);
		outRoot.createElementAsLastChild(MbXMLNSC.FOLDER, REPEATING_ELEMENT, null).copyElementTree(inSaleList);
		
		// create a wrapper object so this can be sent to a terminal
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);
		
		// pass data down the flow using LocalEnvironment
		// this field controls the output file name for this slice of the input data
		// this value replaces the * in the output file name
		MbMessage localEnv = outAssembly.getLocalEnvironment();
		MbElement filenameWildCard = localEnv.getRootElement().getFirstElementByPath("/Wildcard/WildcardMatch");
		filenameWildCard.setValue(intNumberOfSaleListsFound);

		//TODO error handling...

		// propagate the transformed element down the flow
		// returns once all downstream nodes have done their work
        out.propagate(outAssembly);
	}
	
	/**
	 * Creates a record of all the work done, and sends it out
	 *  
	 * @param inAssembly  - the complete input tree
	 * @param alt         - the output terminal to which the final notification will be propagated
	 * @throws MbException
	 */
	public void ProduceProcessingCompleteNotification(MbMessageAssembly inAssembly, MbOutputTerminal alt) throws MbException {

		// create a new output message, and initialise it with basic headers
		MbMessage outMessage = new MbMessage();
		copyMessageHeaders(outMessage);
		
		// fill in data to say what has done, using class variables
		MbElement outElement = outMessage.getRootElement().createElementAsLastChild("XMLNSC");
		MbElement outRoot = outElement.createElementAsLastChild(MbXMLNSC.FOLDER, COMPLETION_ROOT, null);
		MbElement counts = outRoot.createElementAsLastChild(MbXMLNSC.FOLDER, "Counts", null);
		counts.createElementAsLastChild(MbXMLNSC.FOLDER, "Declared", "" + intNumberOfSaleListsDeclared);
		counts.createElementAsLastChild(MbXMLNSC.FOLDER, "Actual", "" + intNumberOfSaleListsFound);
		outRoot.createElementAsLastChild(MbXMLNSC.FOLDER, "Completed", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").format(new java.util.Date()));
		
		// create a wrapper and then send the message down the "alt" terminal
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly, outMessage);		
        alt.propagate(outAssembly);
	}

	public void copyMessageHeaders(MbMessage outMessage) throws MbException
	{
		MbElement outRoot = outMessage.getRootElement();
	    MbElement header = inMessage.getRootElement().getFirstChild();

	    while(header != null && header.getNextSibling() != null)
	    {
	        outRoot.addAsLastChild(header.copy());
	        header = header.getNextSibling();
	    }
	}

}
