package com.balsick.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.balsick.components.BondedValuesChooser;
import com.balsick.components.ClickableLabel;
import com.balsick.components.EBTable;
import com.balsick.components.EBTableRow;
import com.balsick.components.ServerModelDependant;
import com.balsick.tools.communication.ClientServerDBResult;
import com.balsick.tools.communication.ClientServerMessageResult;
import com.balsick.tools.communication.ClientServerResult;
import com.balsick.tools.utils.EBColor;

public class CCActivityClientGUI implements MouseListener, ActionListener {

	private static JPanel panel;
	private List<Consumer<Object>> listeners;
	private HashMap<String, Component> componentsMap;
	
	private Date sentAt;
	
	public JPanel getPanel() {
		if (panel == null)
			createPanel();
		return panel;
	}
	
	public void setConnected(boolean connected) {
		Component component = getComponent("connection_status");
		if (component == null)
			{
//			System.err.println("Impossibile trovare componente connLed");
//			System.exit(1);
			return;
			}
		component.setBackground(connected ? Color.green : Color.red);
		
		component = getComponent("close_connection");
		component.setVisible(connected);
		
		component = getComponent("open_connection");
		component.setVisible(!connected);
		getPanel().repaint();
	}
	
	private Component getComponent(String name) {
		if (componentsMap == null)
			return null;
		return componentsMap.get(name);
	}
	
	public void update(ClientServerResult result, Object caller) {
		if (caller == null) {
			update(result);
			return;
		}
		// this is a system call
		if (caller instanceof ServerModelDependant)
			((ServerModelDependant)caller).deliverResult(result);
	}
	
	public void update(ClientServerResult result) {
		Date received = new Date();
		Long diff = received.getTime() - sentAt.getTime();
		JLabel led = (JLabel)getComponent("connection_status");
		led.setText("!!LED!!\t\t"+diff+"ms");
		JPanel panel = (JPanel) getComponent("table_destination");
		panel.removeAll();
		if (result instanceof ClientServerDBResult) {
			HashMap<Integer, EBTableRow> rows = new HashMap<>();
			for (Object index : ((ClientServerDBResult)result).getRows().keySet()) {
				Integer i = Integer.parseInt(index+"");
				rows.put(i, new EBTableRow(((ClientServerDBResult)result).getRows().get(index).getValues()));
			}
			EBTable table = new EBTable(((ClientServerDBResult)result).getColumns(), rows);
			panel.add(table.resetPanel(), BorderLayout.CENTER);
		}
		else if (result instanceof ClientServerMessageResult) {
			JTextPane message = new JTextPane();
			message.setText(((ClientServerMessageResult)result).getMessage());
			panel.add(message, BorderLayout.CENTER);
		}
		panel.repaint();
		panel.validate();
	}
	
	private void createPanel() {
		panel = new JPanel(new GridBagLayout());
		panel.setOpaque(true);
		panel.setBackground(Color.white);
		componentsMap = new HashMap<>();
		GridBagConstraints gbc = new GridBagConstraints();
		Font labelsFont = new Font("Verdana", Font.BOLD, 18);
		gbc.gridx = 0;
//		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
//		gbc.insets = new Insets(10,2,10,2);
		gbc.ipady = 10;
		ClickableLabel openConnection = new ClickableLabel("Connetti", Color.white, EBColor.main, null);
		openConnection.getAccessibleContext().setAccessibleName("open_connection");
		openConnection.addMouseListener(this);
		openConnection.setFont(labelsFont);
		openConnection.setVisible(false);
		componentsMap.put("open_connection", openConnection);
		panel.add(openConnection, gbc);
		ClickableLabel closeConnection = new ClickableLabel("Disconnetti", Color.white, EBColor.main, null);
		closeConnection.getAccessibleContext().setAccessibleName("close_connection");
		closeConnection.addMouseListener(this);
		closeConnection.setFont(labelsFont);
		componentsMap.put("close_connection", closeConnection);
		panel.add(closeConnection, gbc);
		gbc.gridx ++;
		JLabel connectionLed = new JLabel("IO SONO UN LED!");
		connectionLed.getAccessibleContext().setAccessibleName("connection_status");
		connectionLed.setOpaque(true);
		connectionLed.setHorizontalAlignment(JLabel.CENTER);
		connectionLed.setFont(labelsFont);
		connectionLed.setForeground(Color.white);
		connectionLed.setBackground(Color.green);
		componentsMap.put("connection_status", connectionLed);
		panel.add(connectionLed, gbc);
//		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		JPanel inputArea = createInputArea();
		panel.add(inputArea, gbc);
		/*JTextField textfield = new JTextField();
		textfield.setFont(new Font("Arial", Font.PLAIN, 15));
		textfield.setActionCommand("textfield");
		textfield.addActionListener(this);
		componentsMap.put("textfield_connection", textfield);
		panel.add(textfield, gbc);*/
		
//		gbc.gridy++;
		gbc.weighty = 1;
		JPanel tableDestination = new JPanel(new BorderLayout());
		tableDestination.setOpaque(true);
		tableDestination.setBackground(Color.white);
		tableDestination.getAccessibleContext().setAccessibleName("table_destination");
		componentsMap.put("table_destination", tableDestination);
		panel.add(tableDestination, gbc);
	}
	
	private JPanel createInputArea() {
		JPanel area = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 5, 2, 5);
		area.setOpaque(true);
		area.setBackground(EBColor.main);
		Font font = new Font("Arial", Font.PLAIN, 20);
		
		JLabel tablelabel = new JLabel("Tabella");
		tablelabel.setFont(font);
		tablelabel.setOpaque(false);
		tablelabel.setForeground(Color.white);
		area.add(tablelabel, gbc);
		gbc.gridx++;
		BondedValuesChooser table = new BondedValuesChooser("scanfortables");
		gbc.weightx = 6;
		table.addActionListener(this);
		componentsMap.put("table_textfield", table);
		area.add(table, gbc);
		gbc.gridx = 0;
		gbc.weightx = 0;
//		JTextField table = new JTextField("card_activity");
//		table.setEditable(false);
//		gbc.weightx = 6;
//		table.setFont(font);
//		table.setActionCommand("textfield");
//		table.addActionListener(this);
//		componentsMap.put("table_textfield", table);
//		area.add(table, gbc);
//		gbc.gridx++;
//		ClickableLabel scanForTables = new ClickableLabel("scan");
//		scanForTables.setAction(new EBAction() {
//			@Override
//			public void action() {
//				ActionEvent e = new ActionEvent(this, -1, "scanfortables");
//				handleEvent(e);
//			}
//		});
//		area.add(scanForTables, gbc);
//		gbc.gridx = 0;
//		gbc.weightx = 0;
		
		JLabel columnslabel = new JLabel("Colonne");
		columnslabel.setFont(font);
		columnslabel.setOpaque(false);
		columnslabel.setForeground(Color.white);
		area.add(columnslabel, gbc);
		gbc.gridx++;
		JTextField columns = new JTextField();
		columns.setFont(font);
		columns.setActionCommand("textfield");
		columns.addActionListener(this);
		componentsMap.put("columns_textfield", columns);
		area.add(columns, gbc);
		gbc.gridx = 0;
		
		JLabel criterialabel = new JLabel("Criteria");
		criterialabel.setFont(font);
		criterialabel.setOpaque(false);
		criterialabel.setForeground(Color.white);
		area.add(criterialabel, gbc);
		gbc.gridx++;
		JTextField criteria = new JTextField();
		criteria.setFont(font);
		criteria.setActionCommand("textfield");
		criteria.addActionListener(this);
		componentsMap.put("criteria_textfield", criteria);
		area.add(criteria, gbc);
		gbc.gridx = 0;
		
		JLabel groupbylabel = new JLabel("Group by");
		groupbylabel.setFont(font);
		groupbylabel.setOpaque(false);
		groupbylabel.setForeground(Color.white);
		area.add(groupbylabel, gbc);
		gbc.gridx++;
		JTextField groupby = new JTextField();
		groupby.setFont(font);
		groupby.setActionCommand("textfield");
		groupby.addActionListener(this);
		componentsMap.put("groupby_textfield", groupby);
		area.add(groupby, gbc);
		gbc.gridx = 0;
		
		JLabel orderbylabel = new JLabel("Order by");
		orderbylabel.setFont(font);
		orderbylabel.setOpaque(false);
		orderbylabel.setForeground(Color.white);
		area.add(orderbylabel, gbc);
		gbc.gridx++;
		JTextField orderby = new JTextField();
		orderby.setFont(font);
		orderby.setActionCommand("textfield");
		orderby.addActionListener(this);
		componentsMap.put("orderby_textfield", orderby);
		area.add(orderby, gbc);
		gbc.gridx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		ClickableLabel submit = new ClickableLabel(
				"SELECT!", 
				EBColor.main, Color.white,
				()-> {
				ActionEvent e = new ActionEvent(this, -1, "textfield");
				handleEvent(e);
			}
		);
		submit.setFont(font.deriveFont(Font.BOLD));
		gbc.fill = GridBagConstraints.VERTICAL;
		area.add(submit, gbc);
		return area;
	}
	
	public void addListener(Consumer<Object> listener) {
		if (listeners == null)
			listeners = new ArrayList<>();
		listeners.add(listener);
	}

	private void handleEvent(Object e) {
		if (listeners != null)
			for (Consumer<Object> listener : listeners) {
				sentAt = new Date();
				listener.accept(e);
			}
	}
	
	public List<String> getSelect() {
		List<String> lines = new ArrayList<>();
		lines.add("request_data_start");
		BondedValuesChooser tables = (BondedValuesChooser) getComponent("table_textfield");
		if (tables.getChoice().length() > 0)
			lines.add("tables="+tables.getChoice());
		JTextField c = /*(JTextField)getComponent("table_textfield");
		if (c.getText().length()>0)
			lines.add("tables="+c.getText());
		c =*/ (JTextField)getComponent("criteria_textfield");
		if (c.getText().length()>0)
			lines.add("criteria={"+c.getText()+"}");
		c = (JTextField)getComponent("columns_textfield");
		if (c.getText().length()>0)
			lines.add("columns={"+c.getText()+"}");
		c = (JTextField)getComponent("groupby_textfield");
		if (c.getText().length()>0)
			lines.add("groupby={"+c.getText()+"}");
		c = (JTextField)getComponent("orderby_textfield");
		if (c.getText().length()>0)
			lines.add("orderby={"+c.getText()+"}");
		return lines;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		handleEvent(e);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		handleEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
//		handleEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
//		handleEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
//		handleEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
//		handleEvent(e);
	}
}
