package org.teamtators.rotator.datastream;

import org.teamtators.rotator.control.ITimeProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class DataServer {
    private ServerSocket server;
    private List<OutputStream> clients;
    private MainThread mainThread;
    private DataThread dataThread;
    private Thread mainThreadThread;
    private Thread dataThreadThread;
    private ITimeProvider timeProvider;

    @Inject
    public DataServer() {
        try {
            server = new ServerSocket(4123);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clients = new ArrayList<>();
        mainThread = new MainThread();
        dataThread = new DataThread();
        mainThreadThread = new Thread(mainThread);
        dataThreadThread = new Thread(dataThread);
        mainThreadThread.start();
        dataThreadThread.start();
    }

    public void setData(String s) {
        dataThread.data = s;
    }

    public class MainThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = server.accept();
                    System.out.println("Connection");
                    clients.add(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class DataThread implements Runnable {
        String data = null;
        int period = 20;

        @Override
        public void run() {
            while (true) {
                double startTime = timeProvider.getTimestamp();
                if (data != null) {
                    Iterator<OutputStream> iterator = clients.iterator();
                    while (iterator.hasNext()) {
                        OutputStream client = iterator.next();
                        try {
                            client.write(data.getBytes());
                            client.write(0);
                        } catch (SocketException e) {
                            iterator.remove();
                            System.out.println("Disconnection");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        data = null;
                    }
                }
                double endTime = timeProvider.getTimestamp();
                try {
                    Thread.sleep(Math.max(0, (long) (period - (endTime - startTime))));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Inject
    public void setTimeProvider(ITimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }
}
