package org.xmlcml.svg2xml.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TableTitle {

	private static final Logger LOG = Logger.getLogger(TableTitle.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String title;
	private String chunkName;

	public TableTitle(String title, String chunkName) {
		this.title = title.trim();
		this.chunkName = chunkName.trim();
	}

	public String getTitle() {
		return title;
	}
	
	public String getChunkName() {
		return chunkName;
	}
	
	public String toString() {
		return title+": "+chunkName;
	}
	
}
