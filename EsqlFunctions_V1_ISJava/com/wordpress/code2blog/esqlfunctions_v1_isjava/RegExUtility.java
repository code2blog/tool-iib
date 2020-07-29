package com.wordpress.code2blog.esqlfunctions_v1_isjava;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExUtility {

	public static String findEmail(String source, String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		matcher.find();
		return matcher.group(1);
	}
}
