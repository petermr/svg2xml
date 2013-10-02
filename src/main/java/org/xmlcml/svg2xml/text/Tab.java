package org.xmlcml.svg2xml.text;

import com.google.common.collect.Multiset.Entry;

/** a tab position.
 * 
 * Not yet stable.
 * 
 * @author pm286
 *
 */
public class Tab {
	public String position;
	public Entry<Integer> entry;
	public Tab(String position, Entry<Integer> entry) {
		this.position = position;
		this.entry = entry;
	}
	
	public String toString() {
		return String.valueOf(position.charAt(0))+"_"+entry.getElement()+"("+entry.getCount()+")";
	}
}
