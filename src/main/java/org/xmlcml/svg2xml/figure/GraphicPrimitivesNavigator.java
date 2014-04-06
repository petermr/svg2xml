package org.xmlcml.svg2xml.figure;

import java.util.HashMap;
import java.util.Map;

public class GraphicPrimitivesNavigator {

	public static final String COUNT = "count";
	public static final String DIMENSION = "dimension";
	public static final String HORIZONTAL = "horizontal";
	public static final String LENGTH = "length";
	public static final String POINTS = "points";
	public static final String RADIUS = "radius";
	public static final String VECTOR = "vector";
	public static final String VERTICAL = "vertical";
	
	private String type;
	private Map<String, Object> objectMap;

	public GraphicPrimitivesNavigator(String type, Object[] objects) {
		this.type = type;
		createMap(objects);
	}

	private void createMap(Object[] objects) {
		this.objectMap = new HashMap<String, Object>();
		if (objects.length %2 != 0) {
			throw new RuntimeException("Need String, object ...pairs");
		}
		for (int i = 0; i < objects.length; i+= 2) {
			if (!(objects[i] instanceof String)) {
				throw new RuntimeException("expected string, found: "+objects[i]);
			}
			objectMap.put((String)objects[i], objects[i+1]);
		}
	}
	
	public Object getObject(String key) {
		return objectMap.get(key);
	}
}
