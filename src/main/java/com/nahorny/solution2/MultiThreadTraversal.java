package com.nahorny.solution2;

/*
program search for nodes of a file system whose name contains a
specific string (mask), on a specific depth from the root directory.
a program runs in threads: search and output of the result.
the program will ignore nodes dipper than the required depth.
the program runs iteratively.
 */

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Exchanger;

class MultiThreadTraversal {
    private static final int MIN_PATH_LENGTH = 4;
    private static final int MIN_MASK_LENGTH = 2;
    private static final int MIN_DEPTH = 0;
    private static final Exchanger<String> EXCHANGER = new Exchanger<>();

    public static void main(String[] args) {
        new ResultIndicator(EXCHANGER).start();
        new Searcher(EXCHANGER).start();
    }

    private static String change(String message) {
        String res = null;
        try {
            res = EXCHANGER.exchange(message);
        } catch (InterruptedException ignored) {
        }
        return res;
    }

    static class Searcher extends Thread {
        private Exchanger<String> ex;

        public Searcher(Exchanger<String> ex) {
            this.ex = ex;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("Searcher");

            String rootDir = change(null);
            String mask = change(null);
            int requiredDepth = Integer.parseInt(change(null));

            File root = new File(rootDir);
            Queue<FileAndDepthStorage> queue = new ArrayDeque<>();
            FileAndDepthStorage fileStorage = new FileAndDepthStorage(root, 0);
            queue.add(fileStorage);
            while (!queue.isEmpty()) {
                FileAndDepthStorage current = queue.poll();
                File[] listOfFilesAndDirectory = current.file.listFiles();
                if (listOfFilesAndDirectory != null) {
                    for (File file : listOfFilesAndDirectory) {
                        if (file.isDirectory() && current.depth + 1 <= requiredDepth) {
                            queue.add(new FileAndDepthStorage(file, current.depth + 1));
                        }
                        if (requiredDepth == current.depth && file.getName().contains(mask)) {
                            change(file.toString());
                        }
                    }
                }
            }
            change("@END_OF_SEARCH");
        }
    }


    static class ResultIndicator extends Thread {
        private Exchanger<String> ex;

        public ResultIndicator(Exchanger<String> ex) {
            this.ex = ex;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("ResultIndicator");

            Scanner scanner = new Scanner(System.in);
            int depth;
            String mask;
            String rootPath;

            System.out.println("Application search node in the file system, that contains a specific string (mask) " +
                    "in name and is located on a specific depth from the root directory.");

            do {
                System.out.print("Please, enter path to root dir: ");
                rootPath = scanner.nextLine();
            } while (rootPath.length() < MIN_PATH_LENGTH);

            do {
                System.out.print("Please, enter mask (more, than 1 character): ");
                mask = scanner.nextLine();
            } while (mask.length() < MIN_MASK_LENGTH);

            do {
                System.out.print("Please, enter depth (non-negative number): ");
                depth = scanner.nextInt();
            } while (depth < MIN_DEPTH);

            change(rootPath);
            change(mask);
            change(String.valueOf(depth));

            System.out.println("Search started. Results are:");
            do {
                String res = change(null);
                if (res != null) {
                    if (res.equals("@END_OF_SEARCH")) break;
                    System.out.println(res);
                }
            }
            while (true);
        }
    }

    public static class FileAndDepthStorage {
        private File file;
        private int depth;

        public FileAndDepthStorage(File file, int depth) {
            this.file = file;
            this.depth = depth;
        }
    }

}
