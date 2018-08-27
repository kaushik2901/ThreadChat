import java.util.*;
import java.io.*;
import java.net.*;

class InputThread extends Thread {
    Socket t;

    InputThread(Socket t) {
        this.t = t;
    }

    public void run() {
        while(!MainClass.inputAvailable) {
            try {
                Scanner s = new Scanner(System.in);
                String msg = s.nextLine();

                if(msg.equals("exit")) {
                    OutputStream ops = t.getOutputStream();
                    byte[] b = "Requested to exit chat..".getBytes();
                    ops.write(b);
                    t.close();
                    System.exit(0);
                }

                OutputStream ops = t.getOutputStream();
                byte[] b = msg.getBytes();
                ops.write(b);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}

public class MainClass {

    public static boolean inputAvailable = false;

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Error : invalid arguments");
            return;
        }

        switch(args[0]) {
            case "-S": {
                if(args.length != 2) {
                    System.out.println("Error : invalid arguments");
                    return;
                }

                try {
                    ServerSocket s = new ServerSocket(Integer.parseInt(args[1]));
                    System.out.println("Server is listening on port " + args[1] + "...");
                    Socket sc = s.accept();
                    System.out.println("Server is connected, now you can send messages...");
                    InputThread t = new InputThread(sc);
                    t.start();

                    InputStream ips = sc.getInputStream();
                    BufferedInputStream bfs = new BufferedInputStream(ips);
                    char msg;
                    while(true) {
                        if(bfs.available() != 0) {
                            inputAvailable = true;
                            while(bfs.available() > 0) {
                                msg = (char)bfs.read();
                                System.out.print(msg);
                            }
                            System.out.print("\n");
                            inputAvailable = false;
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

                break;
            }
            case "-C": {
                if(args.length != 3) {
                    System.out.println("Error : invalid arguments");
                    return;
                }

                try {
                    Socket sc = new Socket(args[1], Integer.parseInt(args[2]));
                    InputThread t = new InputThread(sc);
                    t.start();

                    InputStream ips = sc.getInputStream();
                    BufferedInputStream bfs = new BufferedInputStream(ips);
                    char msg;
                    while(true) {
                        if(bfs.available() != 0) {
                            inputAvailable = true;
                            while(bfs.available() > 0) {
                                msg = (char)bfs.read();
                                System.out.print(msg);
                            }
                            System.out.print("\n");
                            inputAvailable = false;
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    } 
}