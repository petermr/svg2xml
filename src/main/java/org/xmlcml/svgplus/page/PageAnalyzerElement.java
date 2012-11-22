package org.xmlcml.svgplus.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.svgplus.command.AbstractAction;
import org.xmlcml.svgplus.command.AbstractActionElement;
import org.xmlcml.svgplus.document.DocumentWriterAction;


public class PageAnalyzerElement extends AbstractActionElement implements Iterable<AbstractActionElement> {

	private static final Logger LOG = Logger.getLogger(PageAnalyzerElement.class);
	
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

	@Override
	protected AbstractAction createAction() {
		return new PageAnalyzerAction(this);
	}
}
