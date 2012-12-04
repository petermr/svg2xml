package org.xmlcml.svgplus.action;

import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.util.ToXML;

public class AssertActionX extends AbstractActionX {

	final static Logger LOG = Logger.getLogger(AssertActionX.class);

	
	public AssertActionX(AbstractActionX actionElement) {
		super(actionElement);
	}
	public final static String TAG ="assert";
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(AbstractActionX.COUNT);
		ATTNAMES.add(PageActionX.FAIL);
		ATTNAMES.add(AbstractActionX.FILENAME);
		ATTNAMES.add(AbstractActionX.NAME);
		ATTNAMES.add(AbstractActionX.MESSAGE);
		ATTNAMES.add(PageActionX.VALUE);
		ATTNAMES.add(AbstractActionX.XPATH);
	}

	/** constructor
	 */
	public AssertActionX() {
		super(TAG);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new AssertActionX(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
			return Arrays.asList(new String[]{
			});
	}
	
	@Override
	public void run() {
		String message = getMessage() == null ? "" : getMessage();
		String refFilename = getFilename();
		String xpath = getXPath();
		String name = getName();
		String refValue = getValueString();
		if (refFilename != null) {
			compareXML(message, refFilename, xpath, name);
		} else if (xpath != null) {
			testXPath(message, xpath, refValue);
		} else if (name != null && refValue != null) {
			testNameValue(name, refValue);
		}
	}


	private void compareXML(String message, String filename, String xpath, String name) {
		if (name != null) {
			Object obj = semanticDocumentActionX.getVariable(name);
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
	
	private void testXPath(String message, String xpath, String refValue) {
		Nodes nodes = getSVGPage().query(xpath, CMLConstants.SVG_XPATH);
		int nnode = nodes.size();
		String expectedCountS = getCount();
		int	expectedCount = getCountWithDefault();
		if (expectedCount != 1) {
			compareCounts(message, xpath, nodes, nnode, expectedCount);
		} else {
			compareValues(message, xpath, refValue, nodes, nnode, expectedCountS);
		}
	}

	private void compareValues(String message, String xpath, String refValue,
			Nodes nodes, int nnode, String expectedCountS) {
		if (nnode != 1) {
			fail(message+" expected 1 node from "+xpath+", found: "+nodes.size()+" on "+toXML());
		} else{
			if (refValue == null) {
				if (expectedCountS == null) {
					warn("no value or count given: "+toXML());
				}
			} else {
				String nodeValue = nodes.get(0).getValue();
				if (!refValue.equals(nodeValue)) {
					fail(message+" expected "+refValue+" from "+xpath+", got: "+nodeValue+" on "+toXML());
				}
			}
		}
	}

	private void compareCounts(String message, String xpath, Nodes nodes,
			int nnode, int expectedCount) {
		if (nnode != expectedCount) {
			for (int i = 0; i < nnode; i++) {
				LOG.trace(nodes.get(i).toXML());
			}
			fail(message+" expected "+expectedCount+" nodes from "+xpath+", found: "+nnode+" on "+toXML());
		}
	}
	
	private void testNameValue(String name, String refValue) {
		LOG.trace(semanticDocumentActionX.getDebugString());
		Object testValue = semanticDocumentActionX.getVariable(name);
		if (testValue == null) {
			throw new RuntimeException("Cannot find name: "+name);
		}
		String testString = testValue.toString();
		if (!testString.equals(refValue)) {
			throw new RuntimeException("Assert for: ("+name+") expected: "+refValue+"; found: ("+testString+")");
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
