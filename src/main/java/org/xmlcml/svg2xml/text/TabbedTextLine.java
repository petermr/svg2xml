package org.xmlcml.svg2xml.text;

import java.util.List;

/** a lists of LineChunks, normally within a TextLine.
 * <p>
 * Normally the list will be Phrase, Blank, Phrase, Blank... Phrase but 
 * this may evolve.
 * </p>
 * 
 * @author pm286
 *
 */
public class TabbedTextLine {

	private List<LineChunk> lineChunkList;
}
