/* Compilation: javac client.java
   Execution  : java client 5000
*/

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CLIENT
{
	private static Socket socket;
    //private static Socket httpSocket;
 
    public static void main(String args[])
    {
        try
        {
            //Connecting with the server
            //String host = "localhost"; //Both in the same machine [IP address 127.0.0.1]
            String host = "129.120.151.94"; //IP address of server
            int portNum = Integer.parseInt(args[0]); //Get port from the user via cmd line
            InetAddress address = InetAddress.getByName(host);
            //Check to see if it is CSE02 - CSE06 machines and not CSE01 acting as server
            socket = new Socket(address, portNum);
            
            /* 
                It took me forever to realize that comparing ==
                is not the same as .equals() and was instead 
                comparing 2 objects versus what was in the string
                for the IP address
            */
            //System.out.println(socket.getLocalAddress());
            //System.out.println(socket.getInetAddress());
            //String server = socket.getInetAddress().toString();
            //String client = socket.getLocalAddress().toString();
            //server = server.substring(1,15);
            //client = client.substring(1,15);
            if(socket.getLocalAddress().equals(socket.getInetAddress()))
            {    
                System.out.println("Error: Try using CSE02-CSE06");
                socket.close();
            }
            else
                System.out.println("Check to see if not CSE01. Confirmed");
            
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            int length;
            
            //Get input URL from user
            //Scanner scanner = new Scanner(System.in);
            //System.out.print("URL: "); 
            //String url = scanner.next();
            
            //int httpPortNum = 80; //Standard port for HTTP
            //String fileName = "/index.html"; //The file to read from the server
            //httpSocket = new Socket(url, httpPortNum); //Create a new socket to connect to the server
            
            //Get I/O streams we can use to talk to the server
            //InputStream sin = httpSocket.getInputStream();
            //BufferedReader fromServer = new BufferedReader(new InputStreamReader(sin));
            //OutputStream sout = httpSocket.getOutputStream();
            //PrintWriter toServer = new PrintWriter(new OutputStreamWriter(sout));
            
            //Request the file from the server, using the HTTP protocol
            //toServer.print("GET " + fileName + " HTTP/1.0\r\n\r\n");
            //toServer.flush();
            
            //Now read the server's response, assume it is a text file, and print it out
            //for(String l = null; (l = fromServer.readLine()) != null; )
            //    System.out.println(l);
            
            //Close this down when finished
            //toServer.close();
            //fromServer.close();
            //httpSocket.close();
			
            //Sending the message to the server
            //System.out.print("Enter client's message: ");
            //String sendMessage = br.readLine();
            //byte[] smessage = new byte[256];
            //smessage = sendMessage.getBytes();
            //out.writeInt(smessage.length);
            //out.write(smessage);
            //out.flush();			
            //System.out.println("Message sent to the server: " + sendMessage);
            
            //Send the url request to the server
            System.out.print("URL: ");
            String sndMsgReq = br.readLine();
            byte[] sMsg = new byte[256];
            sMsg = sndMsgReq.getBytes();
            out.writeInt(sMsg.length);
            out.write(sMsg);
            out.flush();
            System.out.println("URL request sent to the server: " + sndMsgReq);
            
            //Receiving the message from the server
            length = in.readInt();
            byte[] rmessage = new byte[256];
            in.readFully(rmessage, 0, length);
            String recvMessage = new String(rmessage);
            System.out.println("Message received from the server: " + recvMessage);

            //Closing the connection
            socket.close();
        }
        catch (Exception e)
        {
            System.out.println("Client socket error!");
        }
    }
}