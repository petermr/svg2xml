package org.xmlcml.svg2xml.table;


import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svg2xml.tools.Chunk;

/** not actually used
 * 
 * @author pm286
 *
 */
public class TableOld extends Chunk {

	public final static String TABLE = "TABLE";
	private Chunk caption;
	
	public TableOld() {
		super();
	}
	
	private static TableOld createFromAndReplace(SVGG g) {
		TableOld table = new TableOld();
//		table.copyAttributesAndChildrenFromSVGElement(g);
		return table;
	}
}
