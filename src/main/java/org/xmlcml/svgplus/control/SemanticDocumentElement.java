package org.xmlcml.svgplus.control;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.document.DocumentActionListElement;
import org.xmlcml.svgplus.control.document.DocumentIteratorElement;
import org.xmlcml.svgplus.control.page.PageActionElement;

/*
 * rootElement
 */
public class SemanticDocumentElement extends AbstractActionElement {

	private final static Logger LOG = Logger.getLogger(SemanticDocumentElement.class);
	public final static String TAG ="semanticDocument";

	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
		ATTNAMES.add(PageActionElement.DEBUG);
	}

	private static final String COMMAND_DIRECTORY = "src/main/resources/org/xmlcml/graphics/styles";
	private static final String DEFAULT_COMMAND_FILENAME = COMMAND_DIRECTORY+"/"+"basic.xml";
	
	public static String getDefaultCommandFilename() { 
		return DEFAULT_COMMAND_FILENAME;
	}
	
	private DocumentIteratorElement documentIteratorElement;
	private DocumentActionListElement documentActionListElement;
	private SemanticDocumentAction semanticDocumentAction;

	/** constructor
	 */
	public SemanticDocumentElement() {
		super(TAG);
		semanticDocumentAction = new SemanticDocumentAction(this);
	}
	
	public SemanticDocumentAction getSemanticDocumentAction() {
		return semanticDocumentAction;
	}

	/** constructor
	 */
	public SemanticDocumentElement(CommandElement element) {
        super(element);
        init();
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SemanticDocumentElement(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public DocumentActionListElement getDocumentActionList() {
		if (documentActionListElement == null) {
			Nodes nodes = this.query(".//"+DocumentActionListElement.TAG);
			documentActionListElement =  (nodes.size() == 1) ? (DocumentActionListElement) nodes.get(0) : null;
		}
		return documentActionListElement;
	}

	public DocumentIteratorElement getDocumentIteratorElement() {
		if (documentIteratorElement == null) {
			Nodes nodes = this.query(DocumentIteratorElement.TAG);
			documentIteratorElement =  (nodes.size() == 1) ? (DocumentIteratorElement) nodes.get(0) : null;
		}
		return documentIteratorElement;
	}

	public static SemanticDocumentElement createSemanticDocument(Element element) {
		CommandElement commandElement = CommandElement.createCommand(element);
		return (commandElement instanceof SemanticDocumentElement) ? (SemanticDocumentElement) commandElement : null;
	}

	public static SemanticDocumentElement createSemanticDocument(File file) {
		CommandElement commandElement = CommandElement.createCommand(file);
		if (!(commandElement instanceof SemanticDocumentElement)) {
			throw new RuntimeException("commandFile must have root Element: "+SemanticDocumentElement.TAG);
		}
		
		SemanticDocumentElement semanticDocumentElement = (SemanticDocumentElement) commandElement;
		semanticDocumentElement.setFilename(file.getAbsolutePath());
		return semanticDocumentElement;
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

}
