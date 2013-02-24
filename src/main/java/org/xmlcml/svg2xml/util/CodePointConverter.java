package org.xmlcml.svg2xml.util;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Util;

public class CodePointConverter {
	
	private final static Logger LOG = Logger.getLogger(CodePointConverter.class);
	
	private static String CODEPOINT = "codepoints.xml";
	private static String CODEPOINT_DIR = "org/xmlcml/graphics/codepoint";
	private static final String NEW = "new";
	private static final String OLD = "old";
	
	private String codePointResourceName = CODEPOINT_DIR+"/"+CODEPOINT;
	private Element codePointResource;
	private Map<Character, String> map;

	public CodePointConverter() {
	}

	public String getCodePointResource() {
		return codePointResourceName;
	}

	public void setCodePointResource(String codePointResource) {
		this.codePointResourceName = codePointResource;
	}

	public Map<Character, String> readResourceAndCreateMap() {
		try {
			codePointResource = new Builder().build(Util.getResourceUsingContextClassLoader(codePointResourceName, this.getClass())).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read resource: "+codePointResourceName, e);
		}
		createMap();
		return map;
	}

	private void createMap() {
		map = new HashMap<Character, String>();
		Nodes nodes = codePointResource.query("./codepoint");
		for (int i = 0; i < nodes.size(); i++) {
			Element codepoint = (Element) nodes.get(i);
			String oldS = codepoint.getAttributeValue(OLD);
			String newS = codepoint.getAttributeValue(NEW);
			if (newS != null && oldS != null) {
				Character oldChar = null;
				//old value must be integer character value?
				try {
					oldChar = new Character((char) Integer.parseInt(oldS));
				} catch (Exception e) {
					throw new RuntimeException("Bad character: "+oldS, e);
				}
				if (map.get(oldChar) != null) {
					throw new RuntimeException("Duplicate character: "+oldS);
				}
				String newString = null;
				Integer newCharValue = null; 
				try {
					// is it an integer?
					newCharValue = Integer.parseInt(newS);
					newString = ""+((char) (int) newCharValue);
				} catch (Exception e) {
					// no it's a string
					newString = newS;
					if (newS.length() == 1) {
						newCharValue = (int) newString.charAt(0);
					}
				}
				if (newString != null) {
					map.put(oldChar, newString);
					LOG.trace((int)oldChar+" "+oldChar+" .. "+((newCharValue != null) ? newCharValue : "") +" "+newString);
				}
			}
		}
	}
	
	public String convertCharacter(Character ch) {
		return map.get(ch);
	}
	
}
