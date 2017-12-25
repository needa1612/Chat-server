package bluebear;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import javax.swing.*;

public class server extends JFrame implements ActionListener
{
	private static ServerSocket sersock;       
	private static HashSet<PrintWriter> writers;       //one output stream for each client connection
	private static int PORT;                           //server port
	private static JTextField messageField;            //field where server writes messages
	private static JTextArea messageArea;              //server's message list

	//defualt constructor
	public server() throws IOException
	{
		//set up connection
		sersock = new ServerSocket(3002);
		writers = new HashSet<PrintWriter>();

		//set up UI
		JFrame f=new JFrame("Server chat");
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

		messageArea.setText("Server ready to chat!");
	}

	//main simply invokes the server's constructor and accepts each client connection as they come
	public static void main(String[] args) throws Exception
	{	  
		try 
		{
			new server();
			while(true)
			{
				//accept each client connection - start a new thread for each
				new Handler(sersock.accept()).start();  
			}

		} catch (Exception e1) 
		{
			e1.printStackTrace();
		} 	  
		finally 
		{
			sersock.close();
		}
	}         

	//handle connection establishment with client and receipt of messages from them
	public static class Handler extends Thread 
	{
		private Socket socket;
		private PrintWriter pwrite;
		String receiveMessage = null;

		public Handler(Socket socket) throws Exception 
		{
			this.socket = socket; 			
		}

		public void run()
		{                    
			BufferedReader receiveRead = null;
			try 
			{
				//create a new connection(output stream) for each client and add it to the set of client output streams
				pwrite = new PrintWriter(socket.getOutputStream(), true);
				receiveRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writers.add(pwrite);
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
			}						

			while(true)
			{
				//receive messages from client and display on server's message list and also send to other clients
				try 
				{
					if((receiveMessage = receiveRead.readLine()) != null)  
					{
						messageArea.append("\n" + receiveMessage);  
						for(PrintWriter pwriter: writers)
						{
							pwriter.println(receiveMessage);  
							pwriter.flush();
						}
					}  
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				} 

			}
		}
	}

	//define server action when 'Send' button is clicked
	public void actionPerformed(ActionEvent arg0) 
	{
		String sendMessage = "SERVER : " + messageField.getText();
		messageArea.append("\n" + sendMessage);
		messageField.setText("");

		//broadcast server message to every client
		for(PrintWriter pwriter: writers)
		{
			pwriter.println(sendMessage);  
			pwriter.flush();
		}

	}

}

