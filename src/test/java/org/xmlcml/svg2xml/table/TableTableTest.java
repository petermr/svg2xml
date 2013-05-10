package org.xmlcml.svg2xml.table;

import java.io.File;
import java.util.List;

import junit.framework.Assert;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.RealRangeArray;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGText;

/** 
 * test for TableChunkList
 * @author pm286
 *
 */
public class TableTableTest {

	private final static Logger LOG = Logger.getLogger(TableTableTest.class);
	
	@Test
	public void testTable0() {
		Element element = CMLUtil.parseQuietlyToDocument(TableFixtures.TABLEFILE).getRootElement();
		SVGElement svgElement = SVGElement.readAndCreateSVG(element);
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		Assert.assertEquals("paths", 8, pathList.size());
		List<SVGText> textList = SVGText.extractTexts(svgElement);
		Assert.assertEquals("texts", 430, textList.size());
	}
	
	@Test
	public void testTableAndVerticalMask() {
		TableTable table = createTable(TableFixtures.TABLEFILE);
		Assert.assertEquals("unnormalized paths", 8, table.getPathList().size());
		RealRangeArray vMask = table.createCoarseVerticalMask();
		Assert.assertEquals("normalized paths", 4, table.getPathList().size());
		Assert.assertEquals("vMask", "Direction: null; size: 4\n"+
			"((86.485,110.36)(110.645,123.571)(123.854,293.195)(293.422,318.872))", vMask.toString());
	}

	@Test
	public void testCreateVerticalChunks() {
		String[] values = {
				"Table1Effectsofholinallelicsequencesonthestochasticityoflysistime",
				"StrainnMLT(min)SD(min)",
				"IN6127445.72.92IN56(WT)23065.13.24IN1604729.53.28IN6213654.33.42IN705254.53.86IN575347.04.25IN6911945.04.38IN6320941.24.55IN646348.44.60IN6815354.15.14IN6618982.25.87IN6721257.66.71IN653383.86.95IN714968.87.67",
				"aInsomecases,thesamplesizenisthepoolednumberofcellsobservedacrossseveraldays.DetailedinformationcanbefoundinTableS1ofadditionalfile1."
		};
		TableTable table = createTable(TableFixtures.TABLEFILE);
		List<GenericChunk> chunkList = table.createVerticalTextChunks();
		Assert.assertEquals("vertical chunks ", 4, chunkList.size());
		for (int i = 0; i < chunkList.size(); i++) {
			GenericChunk genericChunk = chunkList.get(i);
			Assert.assertEquals("row"+i, values[i], genericChunk.getValue());
		}
	}

	@Test
	public void testAnalyzeChunkHorizontalMasks() {
		String[] masks = {
			"Direction: HORIZONTAL; size: 1\n"+
			"((54.693,252.88496))",
			"Direction: HORIZONTAL; size: 4\n"+
			"((73.855,98.3147)(134.516,143.3777)(177.036,216.4081)(245.118,279.3331))",
			"Direction: HORIZONTAL; size: 4\n"+
			"((68.243,104.0201)(132.531,148.6292)(188.147,205.5682)(253.686,271.10720000000003))",
			"Direction: HORIZONTAL; size: 1\n"+
			"((54.693,281.02114))"
		};
		TableTable table = createTable(TableFixtures.TABLEFILE);
		List<GenericChunk> chunkList = table.analyzeVerticalTextChunks();
		Assert.assertEquals("vertical chunks ", 4, chunkList.size());
		for (int i = 0; i < chunkList.size(); i++) {
			GenericChunk genericChunk = chunkList.get(i);
			Assert.assertEquals("row"+i, masks[i], genericChunk.getHorizontalMask().toString());
		}
	}

// ======================utils==================================
	
	private TableTable createTable(File file) {
		Element element = CMLUtil.parseQuietlyToDocument(file).getRootElement();
		SVGElement svgElement = SVGElement.readAndCreateSVG(element);
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGText> textList = SVGText.extractTexts(svgElement);
		TableTable table = new TableTable(pathList, textList);
		return table;
	}
}
