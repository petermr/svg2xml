package org.xmlcml.svg2xml.table;

import java.io.FileOutputStream;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTable;

/** 
 * test for TableBodyChunk
 * @author pm286
 *
 */
public class TableBodyTest {

	private final static Logger LOG = Logger.getLogger(TableBodyTest.class);
	
	@Test
	public void dummy() {
		LOG.debug("TableBodyChunkTest NYI");
	}

	@Test
	public void testTDBlockValue() {
		GenericChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		String value = genericChunk.getValue();
		Assert.assertEquals("value", "IN6127445.72.92IN56(WT)23065.13.24IN1604729.53.28IN6213654.33.42IN705254.53.86IN575347.04.25IN6911945.04.38IN6320941.24.55IN646348.44.60IN6815354.15.14IN6618982.25.87IN6721257.66.71IN653383.86.95IN714968.87.67", value);
	}
	
	@Test
	public void testCreateRows() {
		GenericChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		List<TableRow> tableRowList = tableBody.createUnstructuredRows();
		Assert.assertEquals("rows", 14, tableRowList.size());
		String[] values = {
			"IN6127445.72.92",
			"IN56(WT)23065.13.24",
			"IN1604729.53.28",
			"IN6213654.33.42",
			"IN705254.53.86",
			"IN575347.04.25",
			"IN6911945.04.38",
			"IN6320941.24.55",
			"IN646348.44.60",
			"IN6815354.15.14",
			"IN6618982.25.87",
			"IN6721257.66.71",
			"IN653383.86.95",
			"IN714968.87.67",
		};
		for (int i = 0; i < tableRowList.size(); i++) {
			TableRow row = tableRowList.get(i);
			Assert.assertEquals("val"+1, values[i], row.getValue());
		}
	}
	
	@Test
	public void testCreateStructuredRows() {
		GenericChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		List<TableRow> rowList = tableBody.createStructuredRows();
		Assert.assertEquals("rows", 14, rowList.size());
		String[] rows = {
			"{{IN61}{274}{45.7}{2.92}}",
			"{{IN56(WT)}{230}{65.1}{3.24}}",
			"{{IN160}{47}{29.5}{3.28}}",
			"{{IN62}{136}{54.3}{3.42}}",
			"{{IN70}{52}{54.5}{3.86}}",
			"{{IN57}{53}{47.0}{4.25}}",
			"{{IN69}{119}{45.0}{4.38}}",
			"{{IN63}{209}{41.2}{4.55}}",
			"{{IN64}{63}{48.4}{4.60}}",
			"{{IN68}{153}{54.1}{5.14}}",
			"{{IN66}{189}{82.2}{5.87}}",
			"{{IN67}{212}{57.6}{6.71}}",
			"{{IN65}{33}{83.8}{6.95}}",
			"{{IN71}{49}{68.8}{7.67}}",
		};
		for (int i = 0; i < rowList.size(); i++) {
			TableRow row = rowList.get(i);
			Assert.assertEquals("row"+i, rows[i], row.toString());
		}
	}
	
	@Test
	public void testCreateHtml() {
		GenericChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		HtmlElement rowBody = tableBody.getHtml();
		Assert.assertEquals("body",
		"<table xmlns=\"http://www.w3.org/1999/xhtml\">" +
		"<tr><td>IN61</td><td>274</td><td>45.7</td><td>2.92</td></tr>" +
		"<tr><td>IN56(WT)</td><td>230</td><td>65.1</td><td>3.24</td></tr>" +
		"<tr><td>IN160</td><td>47</td><td>29.5</td><td>3.28</td></tr>" +
		"<tr><td>IN62</td><td>136</td><td>54.3</td><td>3.42</td></tr>" +
		"<tr><td>IN70</td><td>52</td><td>54.5</td><td>3.86</td></tr>" +
		"<tr><td>IN57</td><td>53</td><td>47.0</td><td>4.25</td></tr>" +
		"<tr><td>IN69</td><td>119</td><td>45.0</td><td>4.38</td></tr>" +
		"<tr><td>IN63</td><td>209</td><td>41.2</td><td>4.55</td></tr>" +
		"<tr><td>IN64</td><td>63</td><td>48.4</td><td>4.60</td></tr>" +
		"<tr><td>IN68</td><td>153</td><td>54.1</td><td>5.14</td></tr>" +
		"<tr><td>IN66</td><td>189</td><td>82.2</td><td>5.87</td></tr>" +
		"<tr><td>IN67</td><td>212</td><td>57.6</td><td>6.71</td></tr>" +
		"<tr><td>IN65</td><td>33</td><td>83.8</td><td>6.95</td></tr>" +
		"<tr><td>IN71</td><td>49</td><td>68.8</td><td>7.67</td></tr>" +
		"</table>",
		rowBody.toXML());

		HtmlTable table = (HtmlTable) tableBody.getHtml();
		table.setBorder(1);
		try {
			FileOutputStream fos = new FileOutputStream("target/table.html");
			CMLUtil.debug(table, fos, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
}
