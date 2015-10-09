package com.balsick.controllers;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import com.balsick.client.CCActivityClient;

public class EventHandler implements Listener {
	
	public static List<String> lines;
	
	public void handleEvent(Object e) {
		if (e instanceof MouseEvent)
			this.handleEvent((MouseEvent) e);
		else if (e instanceof ActionEvent)
			this.handleEvent((ActionEvent)e);
	}

	public void handleEvent(MouseEvent e) {
		if (e.getSource() instanceof Component) {
			switch (((Component)e.getSource()).getAccessibleContext().getAccessibleName()) {
			case "close_connection":
				if (e.getClickCount() == 1)
					CCActivityClient.getClient().disconnect();
				break;
			case "open_connection":
				if (e.getClickCount() == 1)
					CCActivityClient.getClient().connect();
				break;
			}
		}
	}

	public void handleEvent(ActionEvent e) {
		if (e.getSource() instanceof CCActivityClient) {
			Socket socket = CCActivityClient.getClient().getSocket();
			(CCActivityClient.getClient().getGUI()).setConnected(!socket.isClosed());
		} else if (e.getActionCommand() != null) {
			String command = e.getActionCommand();
			if (command.equals("textfield"))
				{
				List<String> lines = CCActivityClient.getClient().getGUI().getSelect();
				CCActivityClient.getClient().sendToServerDBRequest(lines.toArray(new String[lines.size()]));
//				String text = ((JTextField) e.getSource()).getText();
//				if (text.equals("END"))
//					{
//					lines.add("request_data_end");
//					CCActivityClient.getClient().sendToServerDBRequest(lines.toArray(new String[lines.size()]));
//					lines = new ArrayList<>();
//					}
//				else
//					{
//					if (lines == null)
//						lines = new ArrayList<>();
//					lines.add(text);
//					}
//				((JTextField) e.getSource()).setText("");
				}
		} else
			System.err.println("Source = " + e.getSource());
	}

}
