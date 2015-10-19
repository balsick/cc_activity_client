package com.balsick.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.balsick.datastructures.ComponentColors;
import com.balsick.tools.communication.ColumnStructure;
import com.balsick.tools.utils.EBColor;

public class EBTable {
	
	List<ColumnStructure> columns;
	HashMap<Integer, EBTableRow> rows;
	JPanel panel;
	int selectedLine = -1;
	
	public EBTable(List<ColumnStructure> columns, HashMap<Integer, EBTableRow> rows) {
		this.columns = columns;
		this.rows = rows;
	}
	
	public JPanel resetPanel() {
		panel = null;
		return getPanel();
	}
	
	public JPanel getPanel() {
		if (panel != null)
			return panel;
		if (columns == null || rows == null)
			return null;
		JPanel panel = new JPanel(new BorderLayout());
		JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setOpaque(true);
		contentPane.setBackground(Color.white);
		JScrollPane scrollPane = new JScrollPane(contentPane);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		
		for (ColumnStructure cs : columns) {
			gbc.gridy = 0;
			JLabel colheader = new JLabel(cs.getName(), JLabel.CENTER);
			colheader.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.black), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
//			header.add(colheader);
			contentPane.add(colheader,gbc);
			gbc.gridy++;
			for (final Integer index : rows.keySet()) {
				EBTableRow row = rows.get(index);
				ClickableLabel label = new ClickableLabel(row.get(cs.getName()).toString()) {
					
					private static final long serialVersionUID = 201559132327642841L;
					Color stdbck = index % 2 == 0 ? EBColor.ebtable1 : EBColor.ebtable2;
					{
						setBackground(stdbck);
						setForeground(Color.black);
						setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.black), BorderFactory.createEmptyBorder(0, 5, 0, 5)));
						setOpaque(true);
						setAction(() -> EBTable.this.setSelectedLine(index)
						);
					}
					@Override
					public void updateColors() {
						ComponentColors cc = getColors();
						if (getForeground().equals(cc.foreground) && getBackground().equals(cc.background))
							return;
						for (Component c : rows.get(index).getComponents()) {
							c.setForeground(cc.foreground);
							c.setBackground(cc.background);
							c.repaint();
						}
						for (Integer i : rows.keySet()) {
							if (index != i) {
								((ClickableLabel)rows.get(i).getComponents().get(0)).updateColors();
							}
						}
					}
					@Override
					public ComponentColors getColors() {
						Color fg = Color.black, bg = stdbck;
						if (selectedLine == index)
							bg = EBColor.ebtableselected;
						return new ComponentColors(fg, bg);
					}
				};
				row.addComponent(label);
				contentPane.add(label, gbc);
				gbc.gridy++;
			}
			JLabel filler = new JLabel();
			gbc.weighty = 10000;
			contentPane.add(filler, gbc);
			gbc.weighty = 0;
			gbc.gridx++;
		}
		
		
		this.panel = panel;
		return panel;
	}
	
	public void setSelectedLine(int index) {
		selectedLine = index;
		getPanel().repaint();
	}

}
