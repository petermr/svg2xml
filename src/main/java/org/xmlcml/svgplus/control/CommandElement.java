package org.xmlcml.svgplus.control;

import java.io.File;


import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.svgplus.control.document.DocumentActionElement;
import org.xmlcml.svgplus.control.document.DocumentActionListElement;
import org.xmlcml.svgplus.control.document.DocumentBreakElement;
import org.xmlcml.svgplus.control.document.DocumentDebuggerElement;
import org.xmlcml.svgplus.control.document.DocumentIteratorElement;
import org.xmlcml.svgplus.control.document.DocumentReaderElement;
import org.xmlcml.svgplus.control.document.DocumentWriterElement;
import org.xmlcml.svgplus.control.document.PageIteratorElement;
import org.xmlcml.svgplus.control.document.PageSelectorElement;
import org.xmlcml.svgplus.control.page.ChunkAnalyzerElement;
import org.xmlcml.svgplus.control.page.BoxDrawerElement;
import org.xmlcml.svgplus.control.page.BoxProcessorElement;
import org.xmlcml.svgplus.control.page.NodeDeleterElement;
import org.xmlcml.svgplus.control.page.ElementStylerElement;
import org.xmlcml.svgplus.control.page.PageActionElement;
import org.xmlcml.svgplus.control.page.PageAnalyzerElement;
import org.xmlcml.svgplus.control.page.PageAssertElement;
import org.xmlcml.svgplus.control.page.PageNormalizerElement;
import org.xmlcml.svgplus.control.page.PageVariableElement;
import org.xmlcml.svgplus.control.page.PageWriterElement;
import org.xmlcml.svgplus.control.page.PathNormalizerElement;
import org.xmlcml.svgplus.control.page.TextChunkerElement;
import org.xmlcml.svgplus.control.page.VariableExtractorElement;
import org.xmlcml.svgplus.control.page.WhitespaceChunkerElement;
import org.xmlcml.svgplus.figure.FigureAnalyzerElement;
import org.xmlcml.svgplus.paths.PathElement;

public abstract class CommandElement extends Element {

	private static final String NAME = "name";
	private final static Logger LOG = Logger.getLogger(CommandElement.class);
	protected SemanticDocumentElement semanticDocumentElement;

	/** constructor.
	 * 
	 * @param name
	 */
	public CommandElement(String name) {
		super(name);
		init();
	}

	public CommandElement(CommandElement commandElement) {
		super(commandElement);
	}
	
	private void init() {
//		checkAttributes();
	}

	/** check attributes */
	protected abstract List<String> getAttributeNames();
	protected abstract List<String> getRequiredAttributeNames();

	public void checkAttributes() {
		List<String> allowedNames = getAttributeNames();
		if (allowedNames == null) {
			throw new RuntimeException("Must give some allowed attributes: "+this.getClass());
		}
		List<String> attNames = new ArrayList<String>();
		for (int i = 0; i < this.getAttributeCount(); i++) {
			String attName = this.getAttribute(i).getLocalName();
			if (!allowedNames.contains(attName)) {
				throw new RuntimeException("Unknown attribute : "+attName+" on "+this.getClass());
			}
			attNames.add(attName);
		}
		List<String> requiredNames = getRequiredAttributeNames();
		if (requiredNames != null) {
			for (String requiredName : requiredNames) {
				if (!attNames.contains(requiredName)) {
					throw new RuntimeException("Missing attribute : "+requiredName+" on "+this.getClass()+" // "+this.toXML());
				}
			}
		}
	}

	/** copy constructor from non-subclassed elements
	 */
	public static CommandElement createCommand(Element element) {
		CommandElement newElement = null;
		String tag = element.getLocalName();
		LOG.trace("TAG "+tag);
		if (tag == null || tag.equals("")) {
			throw new RuntimeException("no tag");
			
		} else if (tag.equals(DocumentIteratorElement.TAG)) {
			newElement = new DocumentIteratorElement();
			
		} else if (tag.equals(DocumentActionElement.TAG)) {
			newElement = new DocumentActionElement();
		} else if (tag.equals(DocumentActionListElement.TAG)) {
			newElement = new DocumentActionListElement();
		} else if (tag.equals(PageActionElement.TAG)) {
			newElement = new PageActionElement();
		} else if (tag.equals(PageAnalyzerElement.TAG)) {
			newElement = new PageAnalyzerElement();
		} else if (tag.equals(PathElement.TAG)) {
			newElement = new PathElement();
		} else if (tag.equals(SemanticDocumentElement.TAG)) {
			newElement = new SemanticDocumentElement();
			
		} else if (tag.equals(DocumentBreakElement.TAG)) {
			newElement = new DocumentBreakElement();
		} else if (tag.equals(DocumentDebuggerElement.TAG)) {
			newElement = new DocumentDebuggerElement();
		} else if (tag.equals(PageIteratorElement.TAG)) {
			newElement = new PageIteratorElement();
		} else if (tag.equals(PageSelectorElement.TAG)) {
			newElement = new PageSelectorElement();
		} else if (tag.equals(DocumentReaderElement.TAG)) {
			newElement = new DocumentReaderElement();
		} else if (tag.equals(DocumentWriterElement.TAG)) {
			newElement = new DocumentWriterElement();
			
		} else if (tag.equals(ChunkAnalyzerElement.TAG)) {
			newElement = new ChunkAnalyzerElement();
		} else if (tag.equals(BoxDrawerElement.TAG)) {
			newElement = new BoxDrawerElement();
		} else if (tag.equals(BoxProcessorElement.TAG)) {
			newElement = new BoxProcessorElement();
		} else if (tag.equals(NodeDeleterElement.TAG)) {
			newElement = new NodeDeleterElement();
		} else if (tag.equals(ElementStylerElement.TAG)) {
			newElement = new ElementStylerElement();
		} else if (tag.equals(FigureAnalyzerElement.TAG)) {
			newElement = new FigureAnalyzerElement();
		} else if (tag.equals(PageActionElement.TAG)) {
			throw new RuntimeException("PageActionElement is deprecated");
		} else if (tag.equals(PageAnalyzerElement.TAG)) {
			newElement = new PageAnalyzerElement();
		} else if (tag.equals(PageNormalizerElement.TAG)) {
			newElement = new PageNormalizerElement();
		} else if (tag.equals(PageWriterElement.TAG)) {
			newElement = new PageWriterElement();
		} else if (tag.equals(PathNormalizerElement.TAG)) {
			newElement = new PathNormalizerElement();
		} else if (tag.equals(TextChunkerElement.TAG)) {
			newElement = new TextChunkerElement();
		} else if (tag.equals(VariableExtractorElement.TAG)) {
			newElement = new VariableExtractorElement();
		} else if (tag.equals(WhitespaceChunkerElement.TAG)) {
			newElement = new WhitespaceChunkerElement();
			
		} else if (tag.equals(PageAssertElement.TAG)) {
			newElement = new PageAssertElement();
		} else if (tag.equals(PageVariableElement.TAG)) {
			newElement = new PageVariableElement();
			
//		} else if (tag.equals(SemanticDocumentElement.TAG)) {
//			newElement = new SemanticDocumentElement();
//			
		} else {
			throw new RuntimeException("unsupported command element: "+tag);
		}
		if (newElement != null) {
			CMLUtil.copyAttributes(element, newElement);
	        createSubclassedChildren(element, newElement);
	        ((CommandElement)newElement).checkAttributes();
		}
        return newElement;
		
	}
	
	protected static void createSubclassedChildren(Element oldElement, CommandElement newElement) {
		if (oldElement != null) {
			for (int i = 0; i < oldElement.getChildCount(); i++) {
				Node node = oldElement.getChild(i);
				Node newNode = null;
				if (node instanceof Text) {
					newNode = new Text(node.getValue());
				} else if (node instanceof Comment) {
					newNode = new Comment(node.getValue());
				} else if (node instanceof ProcessingInstruction) {
					newNode = new ProcessingInstruction((ProcessingInstruction) node);
				} else if (node instanceof Element) {
					newNode = createCommand((Element) node);
				} else {
					throw new RuntimeException("Cannot create new node: "+node.getClass());
				}
				newElement.appendChild(newNode);
			}
		}
	}

	public String getName() {
		return this.getAttributeValue(NAME);
	}

	public static CommandElement createCommand(File file) {
		CommandElement commandElement = null;
		try {
			Element elem = new Builder().build(file).getRootElement();
			elem = replaceIncludesRecursively(file, elem);
			commandElement = CommandElement.createCommand(elem);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read commandfile "+file, e);
		}
//		CMLUtil.debug(commandElement, "INCLUDE");
		return commandElement;
	}

	private static Element replaceIncludesRecursively(File file, Element elem) {
		Nodes includes = elem.query(".//"+IncludeElement.TAG);
		for (int i = 0; i < includes.size(); i++) {
			Element includeCommandElement = (Element) includes.get(i);
			String includeFilename = includeCommandElement.getAttributeValue(AbstractActionElement.FILENAME);
			if (includeFilename == null) {
				throw new RuntimeException("must give filename");
			}
			try {
				File includeFile = new File(file.getParentFile(), includeFilename).getCanonicalFile();
				Element includeContentElement = new Builder().build(includeFile).getRootElement();
				includeContentElement = replaceIncludesRecursively(includeFile, includeContentElement);
				includeCommandElement.getParent().replaceChild(includeCommandElement, includeContentElement.copy());
			} catch (Exception e) {
				throw new RuntimeException("Cannot create / parse includeFile "+file, e);
			}
		}
		return elem;
	}

	public SemanticDocumentElement getSemanticDocument() {
		if (semanticDocumentElement == null) {
			Element element = (Element) this.query("/*").get(0);
			if (!(element instanceof SemanticDocumentElement)) {
				throw new RuntimeException("root element must be <semanticDocument>, found: <"+element.getLocalName()+">");
			}
			semanticDocumentElement = (SemanticDocumentElement) element;
		}
		return semanticDocumentElement;
	}
	
	public String getString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getLocalName()+"\n");
		for (int i = 0; i < this.getAttributeCount(); i++) {
			Attribute attribute = this.getAttribute(i);
			sb.append(" "+attribute.getLocalName()+"='"+attribute.getValue()+"'");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	public void debug(String msg) {
		CMLUtil.debug(this, msg);
	}
}
