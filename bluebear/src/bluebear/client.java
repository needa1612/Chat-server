package bluebear;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class client extends JFrame implements ActionListener
{
	private static int clientID;                     //each client is identified by an ID
	private static Socket socket;                    
	private static JTextField messageField;          //field where client writes messages
	private static JTextArea messageArea;            //client's message list
	private static PrintWriter writer;               
	private static boolean connected;                //to indicate if the client is connected
	private static String serverIP;

	//default constructor
	public client() 
	{
		try 
		{
			clientID = (int)(Math.random() * 100);  //randomly generate a client ID
			serverIP = "";
			connected = false;
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		//set up the UI
		JFrame f=new JFrame("Client chat");
		f.setSize(400,400);        

		JPanel p1=new JPanel();
		p1.setLayout(new BorderLayout());

		JPanel p2=new JPanel();
		p2.setLayout(new BorderLayout());        

		messageField=new JTextField();
		p1.add(messageField, BorderLayout.CENTER);

		JButton b1=new JButton("Send");
		b1.addActionListener(this);
		p1.add(b1, BorderLayout.EAST); 

		messageArea=new JTextArea();
		messageArea.setEditable(false);
		p2.add(messageArea, BorderLayout.CENTER);
		p2.add(p1, BorderLayout.SOUTH);

		f.setContentPane(p2);
		f.setVisible(true);   

		//request for connection setup
		messageArea.append("\nEnter server IP address (eg: 127.0.0.1)"); 

	}

	//client activities put into SwingWorker (since chat can run for long and needs continuous updates from UI)
	public class ClientWorker extends SwingWorker<Void,Void> 
	{
		public Void doInBackground() 
		{
			BufferedReader receiveRead;
			String receiveMessage;
			try 
			{
				receiveRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				while(true)
				{
					if(!(receiveMessage = receiveRead.readLine()).equalsIgnoreCase("server : bye")) //receive message from server
						messageArea.append("\n" + receiveMessage); // display message received on client message list 
					else
						socket.close();           //close client connection if server says 'bye'
				}
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
			return null;	
		}		
	}

	//define client actions when "Send" button is clicked
	public void actionPerformed(ActionEvent arg0) 
	{
		String receiveMessage, sendMessage = null;
		int portNo = 0;

		//first setup connection - specify machine and port number to connect to
		if(!connected)
		{
			if(serverIP.equals(""))     //setup server IP first - if not specified
			{
				serverIP = messageField.getText();
				messageField.setText("");
				messageArea.append("\nEnter port to connect to (eg : 3002)");  
			}
			else                       //connected to machine, now connect to port 
			{
				portNo = Integer.parseInt(messageField.getText());
				messageField.setText("");
				try 
				{
					//set up connection and output stream through this port
					socket = new Socket(serverIP,portNo);
					writer = new PrintWriter(socket.getOutputStream(), true);
				} catch (Exception e) 
				{
					e.printStackTrace();
				} 

				connected = true;
				messageArea.append("\nConnected! Client ready to chat!");

				//start client chat once all setup is done
				(new ClientWorker()).execute();
			}
		}

		//when connected - actionPerformed will send messages from this client to the server	
		else
		{
			sendMessage = "CLIENT" + clientID + " : " + messageField.getText();
			messageField.setText("");
			writer.println(sendMessage);       // sending to server
			writer.flush();                    // flush the data
		}
	}

	//main class simply invokes default constructor
	public static void main(String[] args)
	{
		new client();        
	}

}                        


