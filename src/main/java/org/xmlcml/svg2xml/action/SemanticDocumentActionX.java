package org.xmlcml.svg2xml.action;


import java.io.File;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.svg2xml.text.SimpleFont;

public class SemanticDocumentActionX extends DocumentActionX {

	private final static Logger LOG = Logger.getLogger(SemanticDocumentActionX.class);

	public static final String S_INFILE = SVGPlusConstantsX.S_DOT+INFILE;
	public static final String S_OUTDIR = SVGPlusConstantsX.S_DOT+OUTDIR;
	public static final String S_OUTFILE = SVGPlusConstantsX.S_DOT+OUTFILE;
	public static final String SEMDOC = "semdoc";
	public static final String S_SEMDOC = SVGPlusConstantsX.S_DOT+SEMDOC;
	
	private String semanticDocumentFilename;
	private VariableStore variableStore;
	private SimpleFont simpleFont;
	private SVGPlusConverterX svgPlusConverterX;
	private PageEditorX pageEditor;
	
	public final static String TAG ="semanticDocument";

	private static final String COMMAND_DIRECTORY = "src/main/resources/org/xmlcml/graphics/styles";
	private static final String DEFAULT_COMMAND_FILENAME = COMMAND_DIRECTORY+"/"+"basic.xml";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(AbstractActionX.DEBUG);
	}

	public static String getDefaultCommandFilename() { 
		return DEFAULT_COMMAND_FILENAME;
	}
	
	private AbstractActionX documentIteratorElement;
//	private AbstractActionX semanticDocumentActionX;

	/** constructor
	 */
	public SemanticDocumentActionX() {
		super(TAG);
		semanticDocumentActionX = this;
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SemanticDocumentActionX(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public AbstractActionX getDocumentIteratorElement() {
		if (documentIteratorElement == null) {
			Nodes nodes = this.query(DocumentIteratorActionX.TAG);
			documentIteratorElement =  (nodes.size() == 1) ? (DocumentIteratorActionX) nodes.get(0) : null;
		}
		return documentIteratorElement;
	}
	public static SemanticDocumentActionX createSemanticDocumentActionWithSVGPageFile(File svgPageFile) {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgPageFile);
		return createSemanticDocumentActionWithSVGPage(svgPage);
	}

	public static SemanticDocumentActionX createSemanticDocumentActionWithSVGPage(SVGSVG svgPage) {
		SemanticDocumentActionX semanticDocumentAction = new SemanticDocumentActionX();
		semanticDocumentAction.getPageEditor().setSVGPage(svgPage);
		return semanticDocumentAction;
	}

	public static SemanticDocumentActionX createSemanticDocument(File file) {
		SemanticDocumentActionX semanticDocumentElement = null;
		try {
			Element elem = new Builder().build(file).getRootElement();
			elem = replaceIncludesRecursively(file, elem);
			semanticDocumentElement = SemanticDocumentActionX.createSemanticDocument(elem);
			semanticDocumentElement.setFilename(file.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("Cannot read commandfile "+file, e);
		}
		return semanticDocumentElement;
	}

	private static Element replaceIncludesRecursively(File file, Element elem) {
		Nodes includes = elem.query(".//"+IncludeActionX.TAG);
		for (int i = 0; i < includes.size(); i++) {
			Element includeElement = (Element) includes.get(i);
			String includeFilename = includeElement.getAttributeValue(AbstractActionX.FILENAME);
			if (includeFilename == null) {
				throw new RuntimeException("must give filename");
			}
			try {
				File includeFile = new File(file.getParentFile(), includeFilename).getCanonicalFile();
				Element includeContentElement = new Builder().build(includeFile).getRootElement();
				includeContentElement = replaceIncludesRecursively(includeFile, includeContentElement);
				includeElement.getParent().replaceChild(includeElement, includeContentElement.copy());
			} catch (Exception e) {
				throw new RuntimeException("Cannot create / parse includeFile "+file, e);
			}
		}
		return elem;
	}

	
	public static SemanticDocumentActionX createSemanticDocument(Element element) {
		SemanticDocumentActionX semanticDocumentActionX = null;
		AbstractActionX actionElement = AbstractActionX.createActionX(element);
		if (actionElement != null && actionElement instanceof SemanticDocumentActionX) {
			semanticDocumentActionX = (SemanticDocumentActionX) actionElement;
			semanticDocumentActionX.setAllDescendants(semanticDocumentActionX);
		}
		return semanticDocumentActionX;
	}

	private void setAllDescendants(SemanticDocumentActionX semanticDocumentActionX) {
		Nodes elements = this.query("//*");
		for (int i = 0; i <elements.size(); i++) {
			Element element = (Element) elements.get(i);
			if (element instanceof AbstractActionX) {
				AbstractActionX actionElement = (AbstractActionX) element; 
				actionElement.setSemanticDocumentActionX(semanticDocumentActionX);
			}
		}
	}

	private void setFilename(String filename) {
		this.addAttribute(new Attribute(FILENAME, filename));
	}
	

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}


	public SemanticDocumentActionX(AbstractActionX documentActionElement) {
		super(documentActionElement);
	}
	
	@Override
	public void run() {
		if (getDebug() != null && getDebug()) {
			debugSemanticDocument();
		}
		runChildActionList();
	}

	private void debugSemanticDocument() {
		LOG.debug("DEBUG: \n"+toString());
		
	}

	public void setInfile(File infile) {
		ensureVariableStore();
		this.setVariable(S_INFILE, infile);
	}
	
	public void setOutdir(File outfile) {
		ensureVariableStore();
		this.setVariable(S_OUTDIR, outfile);
	}

	public void setOutfile(File outfile) {
		ensureVariableStore();
		this.setVariable(S_OUTFILE, outfile);
	}

	public File getInfile() {
		ensureVariableStore();
		return (File) this.getVariable(S_INFILE);
	}

	public File getOutfile() {
		ensureVariableStore();
		return (File) this.getVariable(S_OUTFILE);
	}

	private void ensureVariableStore() {
		if (this.variableStore == null) {
			this.variableStore = new VariableStore();
		}
	}

	public void setDocumentFilename(String semanticDocumentFilename) {
		this.semanticDocumentFilename = semanticDocumentFilename;
		this.setVariable(S_SEMDOC, this.semanticDocumentFilename);
	}
	
	public VariableStore getVariableStore() {
		ensureVariableStore();
		return variableStore;
	}
	
	public Object getVariable(String name) {
		ensureVariableStore();
		for (String key : variableStore.keySet()) {
			LOG.trace(key+": "+"("+variableStore.getVariable(key)+")");
		}
		return variableStore.getVariable(name);
		
	}

	public void setVariable(String name, Object value) {
		ensureVariableStore();
		if (value == null) {
			variableStore.deleteKey(name);
		} else {
			variableStore.setVariable(name, value);
		}
	}
	
	public String getDebugString() {
		ensureVariableStore();
		return variableStore.debugString("VARIABLES");
	}

	public List<String> getVariableNames() {
		ensureVariableStore();
		return variableStore.getVariableNames();
	}

	public SimpleFont getSimpleFont() {
		return this.simpleFont;
	}

	public void setSVGPlusConverter(SVGPlusConverterX svgPlusConverterX) {
		this.svgPlusConverterX = svgPlusConverterX;
	}

	public SVGPlusConverterX getSVGPlusConverter() {
		return svgPlusConverterX;
	}
	
	public void ensurePageEditor(AbstractActionX semanticDocumentAction) {
		if (pageEditor == null) {
			pageEditor = new PageEditorX(this);
		}
	}

	public PageEditorX getPageEditor() {
		ensurePageEditor(this);
		return pageEditor;
	}

	public SVGPlusConverterX ensureSVGPlusConverter() {
		if (svgPlusConverterX == null) {
			svgPlusConverterX = new SVGPlusConverterX();
		}
		return svgPlusConverterX;
	}

}

