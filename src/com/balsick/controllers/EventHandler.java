package com.balsick.controllers;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.balsick.client.CCActivityClient;

public class EventHandler implements Consumer<Object> {
	
	public static List<String> lines;

	@Override
	public void accept(Object e) {
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
		} 
		else if (e.getActionCommand() != null) {
			String command = e.getActionCommand();
			if (command.equals("textfield") || command.equals("textfieldjson")){
				List<String> lines = CCActivityClient.getClient().getGUI().getSelect();
				lines.add("json_request=yes");
				lines.add("request_data_end");
				CCActivityClient.getClient().sendToServerDBRequest(lines);
			}
			if (command.equals("scanfortables")) {
				List<String> lines = new ArrayList<>();
				lines.add("request_data_start");
				lines.add("columns={table_name}");
				lines.add("tables={information_schema.tables}");
				lines.add("criteria={table_type='BASE TABLE'}");
				lines.add("json_request=yes");
				lines.add("request_data_end");
				CCActivityClient.getClient().sendToServerDBRequest(lines, e.getSource());
			}
		}
		else
			System.err.println("Source = " + e.getSource());
	}

}
