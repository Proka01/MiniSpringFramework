package server;

import di_ioc_engine.DI_Engine;
import tmp.ProkicClass;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int TCP_PORT = 8080;

    public static void main(String[] args) throws IOException {

        DI_Engine di_engine = DI_Engine.getInstance();
        di_engine.initializeAllBeans();
        di_engine.printAllBeans();

        ProkicClass prokicClass = new ProkicClass();
        prokicClass = (ProkicClass) di_engine.findInstanceByType(di_engine.beanInstancesRegistry, prokicClass.getClass());

        prokicClass.f();

//        try {
//            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
//            System.out.println("Server is running at http://localhost:"+TCP_PORT);
//            while(true){
//                Socket socket = serverSocket.accept();
//                new Thread(new ServerThread(socket)).start();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
