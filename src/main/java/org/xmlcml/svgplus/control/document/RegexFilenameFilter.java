package org.xmlcml.svgplus.control.document;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFilenameFilter implements FilenameFilter {

	private String regex;
	public RegexFilenameFilter(String regex) {
		this.regex = regex;
	}
	
	public boolean accept(File file, String filename) {
		boolean matches = false;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(filename);
		matches = matcher.matches();
		return matches;
	}

}
