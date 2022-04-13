package com.nahorny.solution1;
/*
program search for nodes of a file system whose name contains a
specific string (mask), on a specific depth from the root directory.
a program runs in a single thread.
the program will ignore nodes dipper than the required depth.
the program runs iteratively.
 */

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

class SimpleTraversal {
    private static final int MIN_PATH_LENGTH = 4;
    private static final int MIN_MASK_LENGTH = 2;
    private static final int MIN_DEPTH = 0;
    public static void main(String[] args) {

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

        System.out.println("Search started. Results are:");
        goThroughFilesUntilDepth(rootPath, depth, mask);
    }

    public static void goThroughFilesUntilDepth(final String rootDir, final int requiredDepth, final String mask) {
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
                        System.out.println(file);
                    }
                }
            }
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
