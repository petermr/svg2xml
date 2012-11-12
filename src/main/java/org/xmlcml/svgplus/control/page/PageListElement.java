package org.xmlcml.svgplus.control.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.xmlcml.svgplus.control.CommandElement;

public class PageListElement extends CommandElement implements Iterable<PageActionElement> {

	public final static String TAG ="pageList";
	private List<PageActionElement> actionElements;
	private static final List<String> ATTNAMES = new ArrayList<String>();
	
	static {
	//		ATTNAMES.add(PageActionElement.ACTION);
		}

	/** constructor
	 */
	public PageListElement() {
		super(TAG);
		init();
	}
	
	protected void init() {
	}
	
	/** constructor
	 */
	public PageListElement(CommandElement element) {
        super(element);
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new PageListElement(this);
    }

	

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public List<PageActionElement> getPageElements() {
		if (actionElements == null) {
			Nodes nodes = this.query(PageActionElement.TAG);
			actionElements = new ArrayList<PageActionElement>();
			for (int i = 0; i < nodes.size(); i++) {
				actionElements.add((PageActionElement) nodes.get(i));
			}
		}
		return actionElements;
	}
	
	public Iterator<PageActionElement> iterator() {
		getPageElements();
		return actionElements.iterator();
	}

	protected List<String> getAttributeNames() {
		return ATTNAMES;
	}

	protected List<String> getRequiredAttributeNames() {
		return Arrays.asList(new String[]{
		});
	}

}
