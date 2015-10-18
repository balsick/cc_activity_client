package com.balsick.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.balsick.tools.communication.ClientServerDBResult;
import com.balsick.tools.communication.ClientServerDBResultRow;

public class BondedValuesChooser extends JPanel implements ServerModelDependant {

	private static final long serialVersionUID = -1536168010599505437L;
	List<ActionListener> listeners = null;
	JComboBox<String> combo;
	
	{
		setLayout(new BorderLayout());
		setOpaque(false);
	}
	
	public BondedValuesChooser(final String command) {
		combo = new JComboBox<>();
		add(combo, BorderLayout.CENTER);
		ClickableLabel scanForTables = new ClickableLabel("scan");
		scanForTables.setAction( ()-> {
				ActionEvent e = new ActionEvent(BondedValuesChooser.this, -1, command);
				if (listeners != null)
					for (ActionListener l : listeners)
						l.actionPerformed(e);
			}
		);
		add(scanForTables, BorderLayout.EAST);
	}
	
	public void addActionListener(ActionListener l) {
		if (listeners == null)
			listeners = new ArrayList<>();
		listeners.add(l);
	}
	
	@Override
	public void deliverResult(Object obj) {
		if (obj instanceof ClientServerDBResult == false)
			return;
		ClientServerDBResult result = (ClientServerDBResult) obj;
		for (ClientServerDBResultRow row : result.getRows().values()) {
			combo.addItem(row.get("TABLE_NAME").toString());
		}
		repaint();
	}

	public String getChoice() {
		if (combo.getSelectedItem() == null)
			return "";
		return (String) combo.getSelectedItem();
	}

}
