package com.balsick.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.Timer;

import com.balsick.controllers.EventHandler;
import com.balsick.tools.communication.ClientServerDBResult;
import com.balsick.tools.communication.ClientServerResult;
import com.balsick.tools.communication.JSonParser;

public class CCActivityClient {
	
	CCActivityClientGUI gui;
	Socket socket;
	BufferedReader in;
	private Consumer<Object> eventHandler;
	private Timer timer;
	final static private int serverPort = 5432;
	final static private String serverAddress = "212.47.246.70";//"2001:b07:aaa:59b5:d454:621c:4571:5a90";
	
	private static CCActivityClient client = null;
	
	private CCActivityClient() {
		eventHandler = new EventHandler();
		gui = new CCActivityClientGUI();
		gui.addListener(eventHandler);
		timer = new Timer(500, e-> {
				e.setSource(CCActivityClient.this);
				eventHandler.accept(e);
			}
		);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void start() {
		JFrame frame = new JFrame("CC Activity Client");
		frame.setContentPane(gui.getPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 700);
		frame.setVisible(true);
	}
	
	public static CCActivityClient getClient() {
		if (client == null)
			client = new CCActivityClient();
		return client;
	}
	
	public CCActivityClientGUI getGUI() {
		return gui;
	}
	
	public void sendToServerDBRequest(List<String> lines) {
		sendToServerDBRequest(lines, null);
	}
	
	public void sendToServerDBRequest(List<String> lines, Object caller) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//			out.println("requesting_data_start");
			for (String s : lines)
				out.println(s);
			receiveDBResult(caller);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveDBResult(Object caller) {
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			Object readObject = objectInputStream.readObject();
			ClientServerResult deserializedResult = null;
			if (readObject instanceof ClientServerDBResult) {
				deserializedResult = (ClientServerDBResult)readObject;
			}
			else if (readObject instanceof String) {
				Object obj = JSonParser.revertJSon(((String)readObject).substring(1, ((String)readObject).length()-1));
				deserializedResult = (ClientServerResult)obj;
			}
			decodeServerResponse(deserializedResult, caller);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void decodeServerResponse(ClientServerResult result, Object caller) {
		gui.update(result, caller);
	}
	
	public void disconnect() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("byebye");
			socket.close();
			System.out.println("Disconnected");
			gui.setConnected(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		try {
			Socket socket = new Socket(serverAddress, serverPort);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.socket = socket;
			while (in.readLine().equals("OK") == false);
			System.out.println("Connected");
			gui.setConnected(true);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("Ciao");
			while (in.readLine().equals("OK") == false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (timer != null)
			timer.start();
	}

	public Socket getSocket() {
		return socket;
	}
	
	public static void main(String[] args) {
		try {
			/*
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			String fromServer;
			String fromUser;
 
			
			while ((fromServer = in.readLine()) != null) {
				System.out.println("Server: " + fromServer);
				if (fromServer.equalsIgnoreCase("Bye"))
					break;
				fromUser = stdIn.readLine();
				if (fromUser != null) {
					System.out.println("Client: " + fromUser);
					out.println(fromUser);
				}
			}*/
			CCActivityClient client = CCActivityClient.getClient();
			client.start();
			Socket clientSocket = new Socket("localhost", 5432);
//			clientSocket.setKeepAlive(true);
			client.setSocket(clientSocket);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
}
