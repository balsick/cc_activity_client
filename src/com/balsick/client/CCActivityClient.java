package com.balsick.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.Timer;

import com.balsick.controllers.EventHandler;
import com.balsick.controllers.Listener;
import com.balsick.tools.communication.ClientServerDBResult;

public class CCActivityClient {
	
	CCActivityClientGUI gui;
	Socket socket;
	BufferedReader in;
	private Listener eventHandler;
	private Timer timer;
	final static private int serverPort = 5432;
	final static private String serverAddress = "212.47.246.70";//"2001:b07:aaa:59b5:d454:621c:4571:5a90";
	
	private static CCActivityClient client = null;
	
	private CCActivityClient() {
		eventHandler = new EventHandler();
		gui = new CCActivityClientGUI();
		gui.addListener(eventHandler);
		timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				e.setSource(CCActivityClient.this);
				eventHandler.handleEvent(e);
			}
		});
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
	
	public void sendToServerDBRequest(String... lines) {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//			out.println("requesting_data_start");
			for (String s : lines)
				out.println(s);
			receiveDBResult();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void receiveDBResult() {
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			ClientServerDBResult deserializedResult = (ClientServerDBResult)objectInputStream.readObject();
			
			decodeServerResponse(deserializedResult);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	protected void decodeServerResponse(ClientServerDBResult result) {
		gui.update(result);
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
			Socket clientSocket = new Socket(serverAddress, 5432);
//			clientSocket.setKeepAlive(true);
			client.setSocket(clientSocket);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
}
