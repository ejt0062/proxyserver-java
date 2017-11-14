/* Compilation: javac proxyserver.java
   Execution  : java proxyserver 5000
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
//import org.apache.http.HttpResponse;
 
public class PSERVER
{
	private static Socket socket;
    private static Socket httpSocket;
    private static Socket fileSocket;
    
    public static void main(String[] args) throws IOException
    {
        try
        {
            //Connecting with the client
            //int port = 6000;
            
            int portNum = Integer.parseInt(args[0]); //Get port from the user via cmd line
            ServerSocket serverSocket = new ServerSocket(portNum);
            System.out.println("Server started and listening to the port " + portNum);

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			int length;
 			
            //Server is running always. This is done using this while(true) loop
            while(true)
            {
				//Connecting to the client
				socket = serverSocket.accept();

				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				//Reading the message from the client
				//length = in.readInt();
				//byte[] rmessage = new byte[256];
				//in.readFully(rmessage, 0, length);
				//String recvMessage = new String(rmessage);
                //System.out.println("Message received from the client: " + recvMessage);
                
                //Get the url request from the client
                length = in.readInt();
                byte[] rMsg = new byte[256];
                in.readFully(rMsg, 0, length);
                String rcvMsgReq = new String(rMsg);
                System.out.println("URL received from the client: " + rcvMsgReq);
                System.out.println();
                
                //Begin processing url request
                String url = rcvMsgReq;
                int httpPortNum = 80; //Standard port for HTTP
                //String fileName = "/index.html"; //The file to read from the server
                PrintWriter httpSock_Out = null;
                BufferedReader httpSock_In = null;
                try
                {
                    httpSocket = new Socket(url, httpPortNum); //Create a new socket to connect to the server
                    //httpSocket.connect(new InetSocketAddress(url, httpPortNum));
                    System.out.println("Connected");
                    //httpSock_Out = new PrintWriter(httpSocket.getOutputStream(), true); //Writer for socket
                    //httpSock_In = new BufferedReader(new InputStreamReader(httpSocket.getInputStream()));
                }
                catch(UnknownHostException e) //Host not found
                {
                    System.err.println("Dont know about host: " + url);
                    System.exit(1);
                }
                //Get I/O streams we can use to talk to the server
                InputStream sin = httpSocket.getInputStream();
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(sin));
                OutputStream sout = httpSocket.getOutputStream();
                PrintWriter toServer = new PrintWriter(new OutputStreamWriter(sout));
                
                //Request the file from the server, using the HTTP protocol
                toServer.print("HEAD / HTTP/1.0\r\n\r\n"); //Change to HEAD to do get simple HEADER info
                toServer.flush();
                //Send request to server via GET using HTTP protocol
                //String sendReq = "GET / HTTP/1.1\r\n\r\n";
                //httpSock_Out.println(sendReq);
                
                //Get response from the server
                //String serverResponse;
                //while((serverResponse = httpSock_In.readLine()) != null)
                //{
                    //System.out.println(serverResponse);
                //}
                
                //Now read the server's response, assume it is a text file, and print it out
                //int i = 0;
                List<String> httpHeaderLines = new ArrayList<String>();
                for(String l = null; (l = fromServer.readLine()) != null; )
                {
                    System.out.println(l);
                    httpHeaderLines.add(l);
                    //httpHeaderLine = l;
                    //i++;
                }
                System.out.println(httpHeaderLines.get(0)); //Status Line of the Header from website
                String statusLine = httpHeaderLines.get(0); //Grab the status line for parsing
                String statusCode = statusLine.substring(9, 12); //Status Code reply from HTTP response message
                
                //Get the server's response code, if 200 then cache the file to top 5
                //System.setProperty("http.keepAlive", "false");
                URL url1 = new URL("https://" + url);
                HttpURLConnection http = (HttpURLConnection)url1.openConnection();
                java.util.Date lastModified = new java.util.Date(http.getLastModified());
                System.out.println("Last-Modified: " + lastModified);
                //int statusCode = http.getResponseCode();
                System.out.println(statusCode);
                
                //Begin logic for caching websites into file
                if(statusCode.equals("200"))
                {
                    System.out.println("The test condtion was successful");
                    //Create a SocketChannel connected to the web server from URL
                    SocketChannel sc = SocketChannel.open(new InetSocketAddress(url, httpPortNum)); 
                    //A charset for encoding the HTTP request
                    Charset charset = Charset.forName("ISO-8859-1");
                    //Send an HTTP request to the server. Start with a string, wrap it to
                    //a CharBuffer, encode it to a ByteBuffer, then write it to the socket
                    sc.write(charset.encode(CharBuffer.wrap("GET / HTTP/1.0\r\n\r\n")));
                    //Create a FileChannel to save the server's response to
                    FileOutputStream fos_out = new FileOutputStream(url); //This line won't work if it is a variable
                    FileChannel file = writer.getChannel();
                    //Get a buffer for holding bytes while transferring from socket to file
                    ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
                    //Now loop until all bytes are read from the socket and written to the file
                    while(fileSocket.read(buffer) != -1 || buffer.position() > 0) //Are we done?
                    {
                        buffer.flip(); //Prepare to read bytes from buffer and write to file
                        file.write(buffer); //Write some or all bytes to the file
                        buffer.compact(); //Discard those that were written
                    }
                    sc.close(); //Close socket channel
                    fos_out.close(); //Close the file channel
                    writer.close(); //Close the file
                }
                else
                {
                    System.out.println("This website will not be cached");
                }
                
				//Sending the response back to the client
				System.out.print("Enter server's message: ");
				String sendMessage = br.readLine();
				byte[] smessage = new byte[256];
				smessage = sendMessage.getBytes();
				out.writeInt(smessage.length);
				out.write(smessage);
				out.flush();			
                System.out.println("Message sent to the client: " + sendMessage);

				//Closing the connection
                toServer.close();
                fromServer.close();
                socket.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("Server socket error!");
        }
    }
}