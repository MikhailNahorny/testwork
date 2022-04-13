package com.nahorny.solution3;

/*
separate thread only for interaction with file system
 */

import java.io.File;
import java.util.concurrent.Exchanger;

public class FileSystemHandler extends Thread {
    private Exchanger<Object> ex;

    private static volatile FileSystemHandler instance;

    private FileSystemHandler(Exchanger<Object> ex) {
        if (instance != null) throw new RuntimeException("FileSystemHandler class should be singleton.");
        this.ex = ex;
        this.setDaemon(true);
        this.setName("FileSystemHandler");
        System.out.println("-----FileSystemHandler started");
    }

    public static FileSystemHandler getInstance(Exchanger<Object> ex) {
        if (instance == null) {
            synchronized (FileSystemHandler.class) {
                if (instance == null) instance = new FileSystemHandler(ex);
            }
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String rootPath;
                rootPath = (String) ex.exchange(null);
                File rootFile = new File(rootPath);
                ex.exchange(rootFile);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
