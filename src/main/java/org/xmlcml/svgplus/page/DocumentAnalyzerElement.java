package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.svgplus.core.AbstractActionElement;


public class DocumentAnalyzerElement extends AbstractActionElement implements Iterable<AbstractActionElement> {

	public final static String TAG ="documentAnalyzer";
	
	private List<AbstractActionElement> documentActionCommandElements;
	private static final List<String> ATTNAMES = new ArrayList<String>();

	public static final String MAX_MBYTE = "maxMbyte";
	
	static {
	}

	/** constructor
	 */
	public DocumentAnalyzerElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public DocumentAnalyzerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new DocumentAnalyzerElement(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public List<AbstractActionElement> getDocumentActionCommandElements() {
		if (documentActionCommandElements == null) {
			Nodes nodes = this.query("*");
			documentActionCommandElements = new ArrayList<AbstractActionElement>();
			for (int i = 0; i < nodes.size(); i++) {
				documentActionCommandElements.add((AbstractActionElement) nodes.get(i));
			}
		}
		return documentActionCommandElements;
	}
	
	public Iterator<AbstractActionElement> iterator() {
		getDocumentActionCommandElements();
		return documentActionCommandElements.iterator();
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}

}
