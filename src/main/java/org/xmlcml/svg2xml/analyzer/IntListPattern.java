package org.xmlcml.svg2xml.analyzer;

import java.util.List;
import java.util.regex.Pattern;

public class IntListPattern {

	private List<Integer> intList;
	private Pattern pattern;

	public IntListPattern(Pattern p, List<Integer> intList) {
		this.pattern = p;
		this.intList = intList;
	}
	
	public String toString() {
		return pattern.toString()+" = "+intList;
	}
}
