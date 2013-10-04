package org.xmlcml.svg2xml.dead;



public class PageSelectorDead {
	
//	private static final Logger LOG = Logger.getLogger(PageSelector.class);
//
//	public static final String NOT_FIRST = "notFirst";
//	public static final String FIRST = "first";
//	public static final String LAST = "last";
//	public static final String NOT_LAST = "notLast";
//	public static final String NOT_FIRST_OR_LAST = "notFirstOrLast";
//
//	private Boolean[] selected;
//	private Integer size = null;  
//	/** create selector with all bits set true
//	 * 
//	 * @param size
//	 */
//	public PageSelector(int size) {
//		setSize(size);
//	}
//
//	private void setSize(int size) {
//		this.size = size;
//		selected = new Boolean[size];
//		setAllTrue();
//	}
//	
//	/** create from pageRange string
//	 * 
//	 * @param pageRange
//	 */
//	public PageSelector(String pageRange) {
//		decodeRangeAndSetSelected(pageRange);
//	}
//	
//	public void setAllTrue() {
//		for (int i = 0; i < size; i++) {
//			setTrue(i);
//		}
//	}
//	public void setAllFalse() {
//		for (int i = 0; i < size; i++) {
//			setFalse(i);
//		}
//	}
//	public void setTrue(int i) {
//		if (i >= 0 && i < size) {
//			selected[i] = true;
//		}
//	}
//	
//	public void setFalse(int i) {
//		if (i >= 0 && i < size) {
//			selected[i] = false;
//		}
//	}
//	
//	public void setLast(Boolean b) {
//		if (size > 0) {
//			selected[size-1] = b;
//		}
//	}
//	
//	public Boolean isSet(int i) {
//		if (i < 0 || i >= size) {
//			throw new RuntimeException ("index out of bounds: "+i);
//		}
//		return selected[i];
//	}
//	
//	public int getSize() {
//		return size;
//	}
//	
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < size; i++) {
//			sb.append((selected[i]) ? "T" : "F");
//		}
//		return sb.toString();
//	}
//	
//	public void decodeRangeAndSetSelected(String range) {
//		setAllFalse();
//		boolean ok = couldInterpretAsKeyword(range);
//		if (!ok) {
//			range = range.replace(XMLConstants.S_WHITEREGEX, "");
//			String[] ranges = range.split(XMLConstants.S_COMMA);
//			Integer last = -1;
//			for (String rangeComponent : ranges) {
//				last = interpretRangeComponent(range, last, rangeComponent);
//			}
//		}
//	}
//	
//	public boolean couldInterpretAsKeyword(String range) {
//		boolean couldInterpret = true;
//		if (PageSelector.FIRST.equals(range)) {
//			this.setFirst();
//		} else if (PageSelector.NOT_FIRST.equals(range)) {
//			this.setNotFirst();
//		} else if (PageSelector.LAST.equals(range)) {
//			this.setLast();
//		} else if (PageSelector.NOT_LAST.equals(range)) {
//			this.setNotLast();
//		} else if (PageSelector.NOT_FIRST_OR_LAST.equals(range)) {
//			this.setNotFirstOrLast();
//		} else {
//			couldInterpret = false;
//		}
//		return couldInterpret;
//	}
//
//	private Integer interpretRangeComponent(String range, Integer last, String r) {
//		if (getSize() == 0) {
//			return 0;
//		}
//		String[] parts = r.split(XMLConstants.S_MINUS);
//		try {
//			Integer i0 = new Integer(parts[0]);
//			Integer i1 = null;
//			if (parts.length == 1) {
//				i1 = (r.endsWith(XMLConstants.S_MINUS)) ? size-1 : null;
//				setTrue(i0);
//			} else if (parts.length == 2) {
//				if (parts[1].length() == 0) {
//					i1 = getSize()-1;
//				} else {
//					i1 = new Integer(parts[1]);
//				}
//			} else {
//				throw new RuntimeException("Bad use of '-': "+range);
//			}
//			if (i1 != null && i1 < i0) {
//				throw new RuntimeException("Bad range : "+range+" for size: "+size);
//			}
//			if (i0 <= last) {
//				throw new RuntimeException("Range must continually increase: "+range);
//			}
//			if (i1 != null) {
//				for (Integer i = i0; i <= i1; i++) {
//					setTrue(i);
//				}
//			}
//			PageSelector.LOG.trace(this);
//			last = (i1 == null) ? i0 : i1;
//		} catch (Exception e) {
//			throw new RuntimeException("Bad range: "+range, e);
//		}
//		return last;
//	}
//	
//	public void setNotFirstOrLast() {
//		setNotFirst();
//		setLast(false);
//	}
//	
//	public void setNotLast() {
//		setAllTrue();
//		setLast(false);
//	}
//	
//	public void setLast() {
//		setAllFalse();
//		setLast(true);
//	}
//	
//	public void setNotFirst() {
//		setAllTrue();
//		setFalse(0);
//	}
//	
//	public void setFirst() {
//		setAllFalse();
//		setTrue(0);
//	}
	
}
