package org.xmlcml.svgplus.command;

import java.io.FileInputStream;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.page.PageAction;
import org.xmlcml.svgplus.util.ToXML;

public class AssertAction extends PageAction {

	final static Logger LOG = Logger.getLogger(AssertAction.class);

	public AssertAction(AbstractActionElement documentActionCommand) {
		super(documentActionCommand);
	}
	
	@Override
	public void run() {
		String message = getMessage() == null ? "" : getMessage();
		String filename = getFilename();
		String xpath = getXPath();
		String name = getName();
		if (filename != null) {
			compareXML(message, filename, xpath, name);
			return;
		}
		Nodes nodes = getSVGPage().query(xpath, CMLConstants.SVG_XPATH);
		int nnode = nodes.size();
		String expectedCountS = getCount();
		int	expectedCount = getCountWithDefault();
		if (expectedCount != 1) {
			if (nnode != expectedCount) {
				for (int i = 0; i < nnode; i++) {
					LOG.trace(nodes.get(i).toXML());
				}
				fail(message+" expected "+expectedCount+" nodes from "+xpath+", found: "+nnode+" on "+getActionElement().toXML());
			}
		} else {
			if (nnode != 1) {
				fail(message+" expected 1 node from "+xpath+", found: "+nodes.size()+" on "+getActionElement().toXML());
			}
			String value = getValue();
			if (value == null) {
				if (expectedCountS == null) {
					warn("no value or count given: "+getActionElement().toXML());
				}
			} else {
				String nodeValue = nodes.get(0).getValue();
				if (!value.equals(nodeValue)) {
					fail(message+" expected "+value+" from "+xpath+", got: "+nodeValue+" on "+getActionElement().toXML());
				}
			}
		}

	}

	private void compareXML(String message, String filename, String xpath, String name) {
		if (name != null) {
			Object obj = semanticDocumentAction.getVariable(name);
			if (obj == null) {
				throw new RuntimeException("Cannot find object with name: "+name);
			} else if (obj instanceof ToXML) {
				assertTestAgainstXMLFile(message, filename, ((ToXML) obj).toXML());
			} else if (obj instanceof Element) {
				assertTestAgainstXMLFile(message, filename, (Element) obj);
			} else if (obj instanceof String){
				assertTestAgainstStringFile(message, filename, (String) obj);
			} else {
				throw new RuntimeException("Cannot compare objects of type: "+obj.getClass().getName());
			}
		}
	}
	
	private void compareXML(String message, String filename, String xpath) {
		try {
			
			Element testElem = getSVGPage();
			if (xpath != null) {
				Nodes nodes = getSVGPage().query(xpath, CMLConstants.SVG_XPATH);
				if (nodes.size() != 1) {
					throw new RuntimeException("Cannot compare more than one node");
				}
				Element testElem0 = (Element) nodes.get(0);
				testElem = new SVGSVG();
				testElem0.detach();
				testElem.appendChild(testElem0);
			}
			assertTestAgainstXMLFile(message, filename, testElem);
		} catch (Exception e) {
			throw new RuntimeException("Cannot carry out comparison", e);
		}
	}

	private void assertTestAgainstXMLFile(String message, String filename, Element testElem) {
		Element element = null;
		try {
			element = new Builder().build(new FileInputStream(filename)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse / find reference: "+filename, e);
		}

//		SVGElement refElement = SVGElement.readAndCreateSVG(element);
		String messageOut = CMLUtil.equalsCanonically(element, testElem, true);
		if (messageOut != null) {
			fail("FAIL "+message+": "+messageOut);
		}
	}

	/** likely to be flaky because of whitespace
	 * 
	 * @param message
	 * @param filename
	 * @param testString
	 */
	private void assertTestAgainstStringFile(String message, String filename, String testString) {
		try {
			String s = IOUtils.toString(new FileInputStream(filename));
			if (!s.equals(testString)) {
				fail("FAIL "+message+": "+s+" != "+testString);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot parse / find reference: "+filename, e);
		}
	}

	private Integer getCountWithDefault() {
		Integer count = 1;
		String countS = getCount();
		LOG.trace("***"+countS);
		if (countS != null) {
			try {
				count = new Integer(countS);
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("bad count: "+countS);
			}
		}
		return count;
	}

}
