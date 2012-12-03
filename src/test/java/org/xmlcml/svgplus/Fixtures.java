package org.xmlcml.svgplus;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.core.SemanticDocumentElement;

public class Fixtures {

	public static final String SVGPLUS_DIR = "src/test/resources/org/xmlcml/svgplus/";
	public static final String COMMAND_DIR= SVGPLUS_DIR+"command/";
	public static final String CORE_DIR = SVGPLUS_DIR+"core/";
	public static final String AJC_PAGE6_PDF = CORE_DIR+"ajc-page6.pdf";
	public final static File NOOP_FILE = new File(CORE_DIR+"noopTst.xml");
	public final static File BASIC_FILE = new File(CORE_DIR+"basicTst.xml");
	public static final File INCLUDE_TEST_FILE = new File(CORE_DIR+"includeTst.xml");
	public static final File INFILE_TEST = new File(CORE_DIR+"infileTst.xml");
	public static final File ASSERT_TST = new File(COMMAND_DIR+"assertTst.xml");
	public static final File NO_ASSERT_TST = new File(COMMAND_DIR+"noAssertTst.xml");
	public static final File VARIABLE_TST = new File(COMMAND_DIR+"variableTst.xml");
	public static final File WHITESPACE_CHUNKER_COMMAND = new File(Fixtures.COMMAND_DIR+"whitespaceChunkerTst.xml");
	public static final File WHITESPACE_0_TST = new File(Fixtures.COMMAND_DIR+"pageTst0.xml");
	public static final File PAGE0_SVG = new File(Fixtures.COMMAND_DIR+"test-page0.svg");
	public static final File HARTER3_SVG = new File(Fixtures.COMMAND_DIR+"harter3.svg");
	public static final File HARTER3SMALL_SVG = new File(Fixtures.COMMAND_DIR+"harter3small.svg");
	public static final File AJC6_SVG = new File(Fixtures.COMMAND_DIR+"ajc6.svg");
	public static final File POLICIES_SVG = new File(Fixtures.COMMAND_DIR+"policies.svg");
	
	public static SemanticDocumentAction getSemanticDocumentAction(File commandFile) {
		SemanticDocumentAction semanticDocumentAction = null;
		try {
			Element element = new Builder().build(commandFile).getRootElement();
			SemanticDocumentElement semanticDocumentElement = SemanticDocumentElement.createSemanticDocument(element);
			semanticDocumentAction = semanticDocumentElement.getSemanticDocumentAction();
		} catch (Exception e) {
			throw new RuntimeException("Cannot create semanticDocumentAction ", e);
		}
		return semanticDocumentAction;
	}
	
	public static SemanticDocumentAction createSemanticDocumentActionWithSVGPage(File svgPageFile) {
		SemanticDocumentAction semanticDocumentAction = null;
		try {
			Element element = new Builder().build(svgPageFile).getRootElement();
			SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(element);
			semanticDocumentAction = new SemanticDocumentAction();
			semanticDocumentAction.getPageEditor().setSVGPage(svgPage);
		} catch (Exception e) {
			throw new RuntimeException("cannot create page: ", e);
		}
		return semanticDocumentAction;
	}

}
