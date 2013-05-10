package org.xmlcml.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlElement;

/** 
 * test for RowChunk
 * @author pm286
 *
 */
public class TableRowTest {

	private final static Logger LOG = Logger.getLogger(TableRowTest.class);
	
	@Test
	public void dummy() {
		LOG.debug("TableRowChunkTest NYI");
	}

	@Test
	public void testTHChunkValue() {
		GenericChunk cellChunk = new GenericChunk();
		Element element = CMLUtil.parseQuietlyToDocument(TableFixtures.HROWFILE).getRootElement();
		SVGElement svgElement = SVGElement.readAndCreateSVG(element);
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(svgElement, TableFixtures.TEXT_OR_PATH_XPATH);
		Assert.assertEquals("elements", 23, elementList.size());
		cellChunk.setElementList(elementList);
		String value = cellChunk.getValue();
		Assert.assertEquals("value", "aStrainnMLT(min)SD(min)", value);
	}

	@Test
	public void testRowChunk() {
		GenericChunk cellChunk = new GenericChunk();
		Element element = CMLUtil.parseQuietlyToDocument(TableFixtures.TDBLOCKFILE).getRootElement();
		SVGElement svgElement = SVGElement.readAndCreateSVG(element);
//		svgElement.debug("XXX");
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(svgElement, TableFixtures.TEXT_OR_PATH_XPATH);
		cellChunk.setElementList(elementList);
	}
	
	
	@Test
	public void testCreateStructuredRows() {
		GenericChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		List<TableRow> rowList = tableBody.createStructuredRows();
		String[] rowHtml = {
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN61</td><td>274</td><td>45.7</td><td>2.92</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN56(WT)</td><td>230</td><td>65.1</td><td>3.24</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN160</td><td>47</td><td>29.5</td><td>3.28</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN62</td><td>136</td><td>54.3</td><td>3.42</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN70</td><td>52</td><td>54.5</td><td>3.86</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN57</td><td>53</td><td>47.0</td><td>4.25</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN69</td><td>119</td><td>45.0</td><td>4.38</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN63</td><td>209</td><td>41.2</td><td>4.55</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN64</td><td>63</td><td>48.4</td><td>4.60</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN68</td><td>153</td><td>54.1</td><td>5.14</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN66</td><td>189</td><td>82.2</td><td>5.87</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN67</td><td>212</td><td>57.6</td><td>6.71</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN65</td><td>33</td><td>83.8</td><td>6.95</td></tr>",
				"<tr xmlns=\"http://www.w3.org/1999/xhtml\"><td>IN71</td><td>49</td><td>68.8</td><td>7.67</td></tr>",
			};
		for (int i = 0; i < rowList.size(); i++) {
			TableRow row = rowList.get(i);
			HtmlElement tr = row.getHtml();
			Assert.assertEquals("row"+i, rowHtml[i], row.getHtml().toXML());
		}
	}
}
