package methodtest.newServer;

import java.net.*;
import java.io.*;

/**
 * server
 */
class ClientHandler implements Runnable {
    Socket clientSocket = null;
    int clientNo;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    File folder = new File("C:/Users/Ice/Desktop/threaded/Server/");
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    BufferedOutputStream out = null;
    String cm = "";
    String namefile = "";

    ClientHandler(Socket socket, int count) {
        clientSocket = socket;
        clientNo = count;
    }

    public void run() {

        try {
            boolean check = false;
            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());
            while (!clientSocket.isClosed()) {

                cm = dis.readUTF();
                System.out.println("[ " + "Client" + clientNo + " ]" + " Select File name: " + cm);

                File[] listOfFiles = folder.listFiles();
                String[] filename = new String[10000];
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        filename[i] = listOfFiles[i].getName();
                    }
                }

                for (int i = 0; i < listOfFiles.length; ++i) {
                    if (cm.equals(filename[i])) {
                        namefile = filename[i];
                        System.out.println("[ SERVER ] Send File: " + namefile);
                        check = true;
                        break;
                    }
                }
                dos.writeBoolean(check);
                dos.flush();
                if (check == true) {
                    File file = new File("C:/Users/Ice/Desktop/threaded/Server/" + namefile);
                    byte buffer[] = new byte[(int) file.length()];
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    out = new BufferedOutputStream(clientSocket.getOutputStream());
                    int len = -1;
                    while ((len = bis.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    fis.close();
                    bis.close();
                    out.close();
                    System.out.println("[ SERVER ] Send File:  " + namefile + " Successful!!");
                    System.out.println("[ SERVER ] Client: " + clientNo + " Disconnected!");
                }
            }
            dis.close();
            dos.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("[ SERVER ] Client: " + clientNo + " Disconnected!");
        }
    }

}

public class Server {
    static ServerSocket serverSocket = null;

    public static void main(String[] args) throws IOException {
        try {
            serverSocket = new ServerSocket(5000);

            int Numclient = 1;
            System.out.println("[ Wait for Client join the Server ]");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("============================================");
                System.out.println("|           New Client Connected           |");
                System.out.println("|               Client No: " + Numclient + "               |");
                System.out.println("============================================");
                ClientHandler clientSock = new ClientHandler(socket, Numclient);
                new Thread(clientSock).start();
                Numclient++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
