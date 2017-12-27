# Chat-server
Overview:

The program implements a simple client-server chat system allowing one server to connect to multiple clients using a program that uses a connectionless protocol.
All clients are on the same network but not on the same computer.
Both server and clients have a user interface where previous messages can be seen and new messages(single line) can be typed and sent.
Messages from clients and server are broadcasted to  everyone connected to the server on the network, so everyone can see the messages exchanged.

Implementation:

- Socket() and ServerSocket() is used to establish connection between client and server.
- JAVA AWT is used for user interface implementation.
- Client activities are put into SwingWorker since chat can run for long and needs continuous updates from UI
- PrintWriter() is used for output stream 
- Server IP and port number is used to connect to the server
