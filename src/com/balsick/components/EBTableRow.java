package com.balsick.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EBTableRow {
	HashMap<String, Object> values;
	List<Component> comps;
	
	public EBTableRow(HashMap<String, Object> values) {
		this.values = values;
	}

	public Object get(String s) {
		if (values == null)
			return null;
		return values.get(s);
	}
	
	public void addComponent(Component c) {
		if (comps == null)
			comps = new ArrayList<>();
		comps.add(c);
	}

	public List<Component> getComponents() {
		return comps;
	}

	public void updateComponents() {
		for (Component c : comps)
			{
			if (c instanceof ClickableLabel)
				((ClickableLabel)c).updateColors();
			}
	}
	
	
}
