package com.nahorny.solution3;

/*
separate thread only for interaction with file system
 */

import java.io.File;
import java.util.concurrent.Exchanger;

public class FileSystemHandler extends Thread {
    private final Exchanger<Object> exchanger;

    private static volatile FileSystemHandler instance;

    private FileSystemHandler(Exchanger<Object> exchanger) {
        if (instance != null) throw new RuntimeException("FileSystemHandler class should be singleton.");
        this.exchanger = exchanger;
        this.setDaemon(true);
        this.setName("FileSystemHandler");
        System.out.println("-----FileSystemHandler started");
    }

    public static FileSystemHandler getInstance(Exchanger<Object> exchanger) {
        if (instance == null) {
            synchronized (FileSystemHandler.class) {
                if (instance == null) instance = new FileSystemHandler(exchanger);
            }
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String rootPath;
                rootPath = (String) exchanger.exchange(null);
                File rootFile = new File(rootPath);
                exchanger.exchange(rootFile);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
