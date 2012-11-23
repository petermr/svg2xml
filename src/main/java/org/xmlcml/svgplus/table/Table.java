package org.xmlcml.svgplus.table;


import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.svgplus.tools.Chunk;

public class Table extends Chunk {

	public final static String TABLE = "TABLE";
	private Chunk caption;
	
	public Table() {
		super();
	}
	
	public static Table createFromAndReplace(SVGG g) {
		Table table = new Table();
//		table.copyAttributesAndChildrenFromSVGElement(g);
		return table;
	}
}
