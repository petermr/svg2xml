package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.svgplus.core.AbstractActionElement;


public class PageAnalyzerElement extends AbstractActionElement implements Iterable<AbstractActionElement> {

	public final static String TAG ="pageAnalyzer";
	
	private List<AbstractActionElement> pageActionCommandElements;
	private static final List<String> ATTNAMES = new ArrayList<String>();

	public static final String MAX_MBYTE = "maxMbyte";
	
	static {
		ATTNAMES.add(TIMEOUT);
		ATTNAMES.add(MAX_MBYTE);
	}

	/** constructor
	 */
	public PageAnalyzerElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public PageAnalyzerElement(AbstractActionElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageAnalyzerElement(this);
    }

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public List<AbstractActionElement> getPageActionCommandElements() {
		if (pageActionCommandElements == null) {
			Nodes nodes = this.query("*");
			pageActionCommandElements = new ArrayList<AbstractActionElement>();
			for (int i = 0; i < nodes.size(); i++) {
				pageActionCommandElements.add((AbstractActionElement) nodes.get(i));
			}
		}
		return pageActionCommandElements;
	}
	
	public Iterator<AbstractActionElement> iterator() {
		getPageActionCommandElements();
		return pageActionCommandElements.iterator();
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}

}
