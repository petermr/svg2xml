package org.xmlcml.svgplus.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.svgplus.core.SVGPlusConstants;

/** manages Page and Document lookup
 *  may not be necessary
 * 
 * @author pm286
 *
 */
public class VariableStore {

	private static final Logger LOG = Logger.getLogger(VariableStore.class);
	private Map<String, Object> variableMap;

	public VariableStore() {
		
	}
	
	//check name of form <p>.name where p is a 1-letter prefix
	private static boolean checkName(String name) {
		boolean ok = false;
		if (name != null) {
			String[] names = name.split(CMLConstants.S_BACKSLASH+CMLConstants.S_PERIOD);
			if (names.length == 2){
				if (names[0].length() == 1) {
					for (String prefix : SVGPlusConstants.PREFIXES) {
						if (prefix.equals(names[0])) {
							ok = true;
							break;
						}
					}
				}
			}
		}
		return ok;
	}
	
	/** returns keys in sorted order
	 * 
	 */
	public List<String> getVariableNames() {
		ensureVariableMap();
		List<String> keyList = new ArrayList<String>();
		Set<String> keySet = variableMap.keySet();
		if (keySet != null && keySet.size() > 0) {
			if (!keyList.addAll(keySet)) {
				throw new RuntimeException("Cannot add keys");
			}
			Collections.sort(keyList);
		}
		return keyList;
	}

	private void ensureVariableMap() {
		if (variableMap == null) {
			variableMap = new HashMap<String, Object>();
		}
	}

	public Object getVariable(String name) {
		ensureVariableMap();
		Object obj = null;
		if (checkName(name)) {
			obj = variableMap.get(name);
		}
		return obj;
	}

	public void setVariable(String name, Object value) {
		ensureVariableMap();
		if (checkName(name)) {
			variableMap.put(name, value);
		}
	}

	public int size() {
		ensureVariableMap();
		return variableMap.size();
	}

	public String debugString(String title) {
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		if (variableMap != null) {
			sb.append("values: "+this.size()+"\n");
			for (String key : variableMap.keySet()) {
				sb.append("  "+key+" = "+variableMap.get(key)+"\n");
			}
		}
		return sb.toString();
	}

	public Set<String> keySet() {
		ensureVariableMap();
		return variableMap.keySet();
	}

	public void deleteKey(String name) {
		ensureVariableMap();
		Object obj = variableMap.get(name);
		if (obj != null) {
			variableMap.remove(obj);
		}
	}
	
}
