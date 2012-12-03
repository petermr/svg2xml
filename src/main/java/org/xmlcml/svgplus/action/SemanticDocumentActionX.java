package org.xmlcml.svgplus.action;


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
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.command.DocumentIteratorElement;
import org.xmlcml.svgplus.command.IncludeElement;
import org.xmlcml.svgplus.command.PageActionElement;
import org.xmlcml.svgplus.command.PageEditor;
import org.xmlcml.svgplus.command.VariableStore;
import org.xmlcml.svgplus.core.SVGPlusConstants;
import org.xmlcml.svgplus.core.SVGPlusConverter;
import org.xmlcml.svgplus.core.SemanticDocumentAction;
import org.xmlcml.svgplus.core.SemanticDocumentElement;
import org.xmlcml.svgplus.text.SimpleFont;

public class SemanticDocumentActionX extends DocumentActionX {

	private final static Logger LOG = Logger.getLogger(SemanticDocumentActionX.class);

	public static final String INFILE = "infile";
	public static final String S_INFILE = SVGPlusConstants.S_DOT+INFILE;
	public static final String OUTFILE = "outfile";
	public static final String S_OUTFILE = SVGPlusConstants.S_DOT+OUTFILE;
	public static final String SEMDOC = "semdoc";
	public static final String S_SEMDOC = SVGPlusConstants.S_DOT+SEMDOC;
	
	private String semanticDocumentFilename;
	private VariableStore variableStore;
	private SimpleFont simpleFont;
	private SVGPlusConverter svgPlusConverter;
	private PageEditorX pageEditor;
	
	public final static String TAG ="semanticDocument";

	private static final String COMMAND_DIRECTORY = "src/main/resources/org/xmlcml/graphics/styles";
	private static final String DEFAULT_COMMAND_FILENAME = COMMAND_DIRECTORY+"/"+"basic.xml";
	
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.DEBUG);
	}

	public static String getDefaultCommandFilename() { 
		return DEFAULT_COMMAND_FILENAME;
	}
	
	private DocumentIteratorElement documentIteratorElement;
	private AbstractActionX semanticDocumentActionX;

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

	public DocumentIteratorElement getDocumentIteratorElement() {
		if (documentIteratorElement == null) {
			Nodes nodes = this.query(DocumentIteratorElement.TAG);
			documentIteratorElement =  (nodes.size() == 1) ? (DocumentIteratorElement) nodes.get(0) : null;
		}
		return documentIteratorElement;
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
		Nodes includes = elem.query(".//"+IncludeElement.TAG);
		for (int i = 0; i < includes.size(); i++) {
			Element includeElement = (Element) includes.get(i);
			String includeFilename = includeElement.getAttributeValue(AbstractActionElement.FILENAME);
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
		SemanticDocumentActionX semanticDocumentElement = null;
		AbstractActionX actionElement = AbstractActionX.createActionX(element);
		if (actionElement != null && actionElement instanceof SemanticDocumentActionX) {
			semanticDocumentElement = (SemanticDocumentActionX) actionElement;
			SemanticDocumentAction semanticDocumentAction = (SemanticDocumentAction) actionElement.getAction();
			semanticDocumentElement.setAllDescendants(semanticDocumentAction);
		}
		return semanticDocumentElement;
	}

	private void setAllDescendants(SemanticDocumentAction semanticDocumentAction) {
		Nodes elements = this.query("//*");
		for (int i = 0; i <elements.size(); i++) {
			Element element = (Element) elements.get(i);
			if (element instanceof AbstractActionElement) {
				AbstractActionElement actionElement = (AbstractActionElement) element; 
				AbstractAction action = actionElement.getAction();
				if (action != null) {
					action.setSemanticDocumentAction(semanticDocumentAction);
				}
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

	public SemanticDocumentAction getSemanticDocumentAction() {
		return (SemanticDocumentAction) this.getAction();
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

	public void setSVGPlusConverter(SVGPlusConverter svgPlusConverter) {
		this.svgPlusConverter = svgPlusConverter;
	}

	public SVGPlusConverter getSVGPlusConverter() {
		return svgPlusConverter;
	}
	
	public void ensurePageEditor(SemanticDocumentActionX semanticDocumentAction) {
		if (pageEditor == null) {
			pageEditor = new PageEditorX(this);
		}
	}

	public PageEditorX getPageEditor() {
		ensurePageEditor(this);
		return pageEditor;
	}

}

