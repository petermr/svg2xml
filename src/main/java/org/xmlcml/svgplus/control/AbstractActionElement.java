package org.xmlcml.svgplus.control;

import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.control.document.DocumentIteratorElement;

public abstract class AbstractActionElement extends CommandElement {

	private final static Logger LOGGER = Logger.getLogger(AbstractActionElement.class);

	public static final String ACTION = "action";
	public static final String DELETE_NAMESPACES = "deleteNamespaces";
	public static final String FILENAME = "filename";
	public static final String FORMAT = "format";
	public static final String MARK = "mark";
	public static final String DEBUG = "debug";

	public static final String COUNT = "count";
	public static final String LOG = "log";
	public static final String MESSAGE = "message";
	public static final String NAME = "name";
	public static final String OUT_DIR = "outDir";
	public static final String REGEX = "regex";
	public static final String SKIP_IF_EXISTS = "skipIfExists";
	public static final String TITLE = "title";
	public static final String XPATH = "xpath";
	public static final String MAX = "max";

	public static final String TIMEOUT = "timeout";

	public AbstractActionElement(String name) {
		super(name);
	}
	
	public AbstractActionElement(CommandElement element) {
		super(element);
	}
	
	protected void init() {
	}

	public DocumentIteratorElement getAncestorDocumentIteratorElement() {
		return (DocumentIteratorElement) getAncestorElement(DocumentIteratorElement.TAG);
	}

	public SemanticDocumentElement getAncestorSemanticDocumentElement() {
		return (SemanticDocumentElement) getAncestorElement(SemanticDocumentElement.TAG);
	}

	public AbstractActionElement getAncestorElement(String tag) {
		LOGGER.trace(this.getLocalName());
		Nodes nodes = this.query("ancestor-or-self::"+tag+"[1]");
		if (nodes.size() != 1) {
			throw new RuntimeException("Must have ancestor:"+tag);
		}
		return (AbstractActionElement) nodes.get(0);
	}
	
}
