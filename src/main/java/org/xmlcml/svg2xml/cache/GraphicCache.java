package org.xmlcml.svg2xml.cache;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.cache.ComponentCache;

/** container and processor for complex graphics objects.
 * SVGCache is roughly the objects supported by SVG spec
 * GraphicCache is for objects which are refined or combined
 * 
 * in general SVGCache objects do not know about their environment or their
 * neighbours.
 * 
 * @author pm286
 *
 */
@Deprecated // use ComponentCache
public class GraphicCache extends ComponentCache {
	private static final Logger LOG = Logger.getLogger(GraphicCache.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public GraphicCache() {
		super();
	}
}
