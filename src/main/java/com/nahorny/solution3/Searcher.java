package com.nahorny.solution3;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class Searcher extends Thread {
    private Exchanger<Object> exchanger;
    private BlockingQueue<String> resultQueue;
    private int depth;
    private String mask;
    private String rootDir;


    public Searcher(Exchanger<Object> exchanger, BlockingQueue<String> resultQueue, int depth, String mask, String rootDir) {
        this.exchanger = exchanger;
        this.depth = depth;
        this.mask = mask;
        this.rootDir = rootDir;
        this.resultQueue = resultQueue;
        this.setName("Searcher");
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Searcher");
        System.out.println(String.format("-----Searcher was called with %s starts with '%s' root, %d depth, '%s' mask",
                Thread.currentThread().getName(), rootDir, depth, mask));

        change(rootDir);
        File root = (File) change(null);

        Queue<FileAndDepthStorage> queue = new ArrayDeque<>();
        FileAndDepthStorage ss = new FileAndDepthStorage(root, 0);
        queue.add(ss);
        while (!queue.isEmpty()) {
            FileAndDepthStorage current = queue.poll();
            File[] listOfFilesAndDirectory = current.file.listFiles();

            if (listOfFilesAndDirectory != null) {
                for (File file : listOfFilesAndDirectory) {
                    if (file.isDirectory() && current.depth + 1 <= depth) {
                        queue.add(new FileAndDepthStorage(file, current.depth + 1));
                    }
                    if (depth == current.depth && file.getName().contains(mask)) {
                        System.out.println(file);
                        resultQueue.add(file.toString());
                    }
                }
            }
        }
        resultQueue.add("@END_OF_SEARCH");
        System.out.println("-----end of search.");
    }

    private Object change(Object object) {
        Object res = null;
        try {
            res = exchanger.exchange(object);
        } catch (InterruptedException ignored) {
        }
        return res;
    }

    private static class FileAndDepthStorage {
        private File file;
        private int depth;

        public FileAndDepthStorage(File file, int depth) {
            this.file = file;
            this.depth = depth;
        }
    }

}
