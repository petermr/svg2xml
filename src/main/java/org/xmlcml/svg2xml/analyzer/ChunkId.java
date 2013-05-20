package org.xmlcml.svg2xml.analyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlcml.graphics.svg.SVGG;

public class ChunkId implements Comparable<ChunkId> {

	private static final String G = "g";

	public static Pattern ID_PATTERN = Pattern.compile("g\\.(\\d+)\\.(\\d+)");
	
	private int pageNumber;
	private int chunkNumber;

	private String id;
	
	public ChunkId(String id) {
		this.id = id;
		try {
			processId();
		} catch (Exception e) {
			throw new RuntimeException("cannot parse identifier: "+id, e);
		}
	}

	public ChunkId(int pageNumber, int ichunk) {
		this.pageNumber = pageNumber;
		this.chunkNumber = ichunk;
	}

	private void processId() {
		Matcher matcher = ID_PATTERN.matcher(id);
		if (matcher.matches() && matcher.groupCount() == 2) {
			pageNumber = new Integer(matcher.group(1));
			chunkNumber = new Integer(matcher.group(2));
		} else {
			
		}
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getChunkNumber() {
		return chunkNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof ChunkId) {
			ChunkId id = (ChunkId) o;
			return (this.toString().equals(id.toString()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 17 * pageNumber + 31 * chunkNumber;
	}
	
	public int compareTo(ChunkId chunk2) {
		int compare = 0;
		if (pageNumber < chunk2.pageNumber) {
			compare = -1;
		} else if (pageNumber > chunk2.pageNumber) {
			compare = 1;
		}
		if (compare == 0) {
			if (chunkNumber < chunk2.chunkNumber) {
				compare = -1;
			} else if (chunkNumber > chunk2.chunkNumber) {
				compare = 1;
			}
		}
		return compare;
	}


	public String toString() {
		return createId();
	}

	private String createId() {
		return G+"."+pageNumber+"."+chunkNumber;
	}

	public static ChunkId createChunkId(SVGG gChunk) {
		return null;
	}
}
