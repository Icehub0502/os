package methodtest.newServer;

import java.net.*;
import java.util.Scanner;
import java.io.*;

class downdloadFile implements Runnable {
    Socket socket = null;
    String namefile = null;
    InputStream is = null;
    int bufferSize;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    downdloadFile(Socket socket, String namefile) {
        this.socket = socket;
        this.namefile = namefile;
    }

    public void run() {
        try {
            is = socket.getInputStream();
            bufferSize = socket.getReceiveBufferSize();
            fos = new FileOutputStream("C:/Users/Ice/Desktop/threaded/Client/" + namefile);
            bos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[bufferSize];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            System.out.println("[ SERVER ] Download file " + namefile + " Successful!!");
            bos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Client {
    static Socket socket = null;
    static DataInputStream dis;
    static DataOutputStream dos;
    static String namefile = "";
    static File folder = new File("C:/Users/Ice/Desktop/threaded/Server/");

    public static void main(String[] args) throws Exception {
        try {
            socket = new Socket("localhost", 5000);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);

            boolean check;
            while (!socket.isClosed()) {
                System.out.println(" __________________________________________");
                System.out.println("|                                          |");
                System.out.println("|             File in Server               |");
                System.out.println("|__________________________________________|");
                System.out.println("|                                          |");
                File[] listOfFiles = folder.listFiles();
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        System.out.println("     File: " + listOfFiles[i].getName());
                    }
                }
                System.out.println("|                                          |");
                System.out.println("|__________________________________________|");

                System.out.print("[ CLEINT ] Select File: ");
                namefile = sc.nextLine();
                dos.writeUTF(namefile);
                dos.flush();
                check = dis.readBoolean();
                if (check == true) {
                    System.out.println("[ SERVER ] Client Select File name: " + namefile);
                    downdloadFile downdloadFile = new downdloadFile(socket, namefile);

                    Thread thread = new Thread(downdloadFile);
                    thread.start();
                    thread.join();

                } else {
                    System.out.println("============================================");
                    System.out.println("            [ SERVER ]: No File!");
                    System.out.println("        [ SERVER ]: Select File Again");
                    System.out.println("============================================");
                }
            }
            dis.close();
            dos.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("[ CLEINT ] Leave The Server!");
        }
    }
}
