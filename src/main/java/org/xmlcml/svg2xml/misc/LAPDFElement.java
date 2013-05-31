package org.xmlcml.svg2xml.misc;

import java.io.FileOutputStream;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGUtil;

public class LAPDFElement extends Element {

	public final static Logger LOG = Logger.getLogger(LAPDFElement.class);
	public static final String X1 = "x1";
	public static final String X2 = "x2";
	public static final String Y1 = "y1";
	public static final String Y2 = "y2";
	public static final String TYPE = "type";
	public static final String STYLE = "style";
	public final static String TAG = "LAPDF";

	protected Double x1;
	protected Double x2;
	protected Double y1;
	protected Double y2;
	protected String type;
	private String style;
	private SVGElement thisSvg;
	
	public LAPDFElement() {
		super(TAG);
	}
	
	public LAPDFElement(String tag) {
		super(tag);
	}
	
	public static LAPDFElement createLAPDF(Document document) {
		LAPDFElement root = createLAPDF(document.getRootElement());
		return root;
	}

	public static LAPDFElement createLAPDF(Element element) {
		LAPDFElement newElement = null;
		String tag = element.getLocalName();
		if (tag == null || tag.equals("")) {
			throw new RuntimeException("no tag");
		} else if (LAPDFChunk.TAG.equals(tag)) {
			newElement = new LAPDFChunk();
		} else if (LAPDFDocument.TAG.equals(tag)) {
			newElement = new LAPDFDocument();
		} else if (LAPDFPage.TAG.equals(tag)) {
			newElement = new LAPDFPage();
		} else if (LAPDFWord.TAG.equals(tag)) {
			newElement = new LAPDFWord();
		} else {
			newElement = new LAPDFElement(tag);
			System.err.println("unsupported svg element: "+tag);
		}
		if (newElement != null) {
	        newElement.copyAttributesFrom(element);
	        createSubclassedChildren(element, newElement);
	        newElement.processAttributes();
		}
	    return newElement;
	
	}
	
	protected static void createSubclassedChildren(Element oldElement, Element newElement) {
		if (oldElement != null) {
			for (int i = 0; i < oldElement.getChildCount(); i++) {
				Node node = oldElement.getChild(i);
				Node newNode = null;
				if (node instanceof Text) {
					String value = node.getValue();
//					LOG.debug(value+"/"+(int)value.charAt(0));
					newNode = new Text(value);
				} else if (node instanceof Comment) {
					newNode = new Comment(node.getValue());
				} else if (node instanceof ProcessingInstruction) {
					newNode = new ProcessingInstruction((ProcessingInstruction) node);
				} else if (node instanceof Element) {
					newNode = createLAPDF((Element) node);
				} else {
					throw new RuntimeException("Cannot create new node: "+node.getClass());
				}
				newElement.appendChild(newNode);
			}
		}
	}
	
    /**
     * copies attributes. makes subclass if necessary.
     * 
     * @param element to copy from
     */
    public void copyAttributesFrom(Element element) {
    	if (element != null) {
	        for (int i = 0; i < element.getAttributeCount(); i++) {
	            Attribute att = element.getAttribute(i);
	            Attribute newAtt = (Attribute) att.copy();
	            this.addAttribute(newAtt);
	        }
    	}
    }

	protected void processAttributes() {
		for(int i = 0; i < this.getAttributeCount(); i++) {
			Attribute attribute = this.getAttribute(i);
			String name = attribute.getLocalName();
			String value = attribute.getValue();
			if (X1.equals(name)) {
				setX1(value);
			} else if (X2.equals(name)) {
				setX2(value);
			} else if (Y1.equals(name)) {
				setY1(value);
			} else if (Y2.equals(name)) {
				setY2(value);
			} else if (STYLE.equals(name)) {
				setStyle(value);
			} else if (TYPE.equals(name)) {
				setType(value);
			} else {
				processAttribute(name, value);
			}
		}
	}

	protected void setX1(String value) {
		x1 = new Double(value);
	}

	protected void setX2(String value) {
		x2 = new Double(value);
	}

	protected void setY1(String value) {
		y1 = new Double(value);
	}

	protected void setY2(String value) {
		y2 = new Double(value);
	}

	protected void setType(String value) {
		type = value;
	}

	protected void setStyle(String value) {
		this.style = value;
	}

	public Double getX1() {return x1;}
	public Double getX2() {return x2;}
	public Double getY1() {return y1;}
	public Double getY2() {return y2;}
	public String getStyle() {return style;}
	public String getType() {return type;}
	
	protected void processAttribute(String name, String value)  {
		LOG.debug("Unprocessed attribute: "+name+" in "+this.getClass());
	}

	public SVGElement createSVGTop() {
		SVGElement svg = createSVG();
		this.addBoxes();
		return svg;
	}

//	private static void tweakText(SVGElement svg) {
//		List<SVGElement> svgRects = SVGUtil.getQuerySVGElements(svg, "//svg:text/svg:rect");
//		for (SVGElement rect : svgRects) {
//			ParentNode parent = rect.getParent();
//			rect.detach();
//			parent.getParent().appendChild(rect);
//		}
//	}

	public SVGElement createSVG() {
		
		thisSvg = createSVGElement();
		for (int i = 0; i < this.getChildElements().size(); i++) {
			SVGElement child = ((LAPDFElement) this.getChildElements().get(i)).createSVG();
			thisSvg.appendChild(child);
		}
		return thisSvg;
	}
	
	public void addBoxes() {
		for (int i = 0; i < this.getChildElements().size(); i++) {
			LAPDFElement child = (LAPDFElement) this.getChildElements().get(i);
			SVGRect rect = createRect();
			if (rect != null) {
				ParentNode parent = thisSvg.getParent();
				if (parent != null) {
					parent.appendChild(rect);
				}
			}
			child.addBoxes();
		}
	}

	protected SVGRect createRect() {
		SVGRect rect = null;
		if (x1 != null && x2 != null && y1 != null && y2 != null) {
			Double yy1 = y1;
			Double yy2 = y2;
			Nodes words = this.query(".//Word");
			Double size = (words.size() == 0) ? null : ((LAPDFWord)words.get(0)).getFontSize();
			if (size != null) {
				yy1 -= size;
				yy2 -= size;
			}
			rect = new SVGRect(new Real2(x1, yy1), new Real2(x2, yy2));
			rect.setStrokeWidth(0.1);
		}
		return rect;
	}

	protected SVGElement createSVGElement() {
		SVGElement svg = new SVGG();
		svg.setTitle("Unknown: "+this.getLocalName());
		return svg;
	}

	/** ======================= */
	public static void main(String[] args) throws Exception {
		LAPDFElement lapdf = LAPDFElement.createLAPDF(new Builder().build("src/test/resources/svg/misc/174_spatial.xml"));
		SVGElement svgg = lapdf.createSVGTop();
		for (int i = 0; i < svgg.getChildElements().size(); i++) {
			SVGElement gg = (SVGElement) svgg.getChildElements().get(i);
			CMLUtil.debug(gg, new FileOutputStream("target/blocks"+i+".svg"), 1);
		}
	}
	
}
