package com.wordpress.code2blog.esql_interface;

import com.ibm.broker.plugin.MbGlobalMap;

public class GetEmployeeFromCache {
	public static String retrieve(String key) {
		try {
			int primaryKey = Integer.valueOf(key);
			// Establish connection with Map called "DemoMap"
			MbGlobalMap globalMap = MbGlobalMap.getGlobalMap("DemoMap");
			String xmlString = (String) globalMap.get(primaryKey);
			return xmlString;
		} catch (Exception e) {
			// let this error be handled by message flow
			throw new RuntimeException(e);
		}
	}
}
