package com.nahorny.solution3;

/*
program search for nodes of a file system whose name contains a
specific string (mask), on a specific depth from the root directory.
a program runs in threads: search, interaction with file system handling, and main (telnet server).
the program will ignore nodes dipper than the required depth.
the program runs iteratively.
only one thread handle file system.
 */

import com.nahorny.solution3.telnetserver.ShellServer;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.LinkedBlockingQueue;

class TelnetServerTraversal {

    public static void main(String[] args) {
        //-----------------start, get port and root

        final int minPathLength = 4;
        final int minMaskLength = 2;
        final int minDepth = 0;
        final Exchanger<Object> ex = new Exchanger<>();
        Scanner scanner = new Scanner(System.in);
        String[] rootPath = new String[1];
        int port;

        System.out.println("Application search node in the file system, that contains a specific string (mask) in name "
                + "and is located on a specific depth from the root directory."
                + "\nYou required to set port for telnet server and root directory in this terminal."
                + "\nThan open telnet terminal with localhost ip and port you set, where you will be required "
                + "to start search with command find <depth> <mask>");

        do {
            System.out.print("Please, enter path to root dir (more, than 4 character): ");
            rootPath[0] = scanner.nextLine();
        }
        while (rootPath[0].length() < minPathLength);

        do {
            System.out.print("Please, enter port (number between 0 and 32_767, be sure you chose not reserved port): ");
            port = scanner.nextInt();
        }
        while (!isPortValid(port));
        FileSystemHandler.getInstance(ex).start();

        //-----------------server init
        ShellServer srv = new ShellServer();
        srv.registerCommand("find", (name, arguments, terminal) -> {
            System.out.println("-----find command was called");
            String[] argsAsArray = arguments.split(" ");
            int depth = Integer.parseInt(argsAsArray[0]);
            if (depth < minDepth) {
                terminal.writeLine("Depth should not be less than zero.");
                terminal.flush();
                System.out.println(String.format("-----Invalid request. Depth = %d", depth));
                return;
            }
            String mask = argsAsArray[1];
            if (mask.length() < minMaskLength) {
                terminal.writeLine(String.format("Mask length should not be less than %d.", minMaskLength));
                terminal.flush();
                System.out.println(String.format("-----Invalid request. mask = '%s'", mask));
                return;
            }
            BlockingQueue<String> resq = new LinkedBlockingQueue<>();
            String root = rootPath[0];

            new Searcher(ex, resq, depth, mask, root).start();
            terminal.writeLine("Search start. Results are:");
            terminal.flush();

            do {
                String res = resq.take();
                if (res.equals("@END_OF_SEARCH")) {
                    terminal.writeLine("Search end.");
                    terminal.flush();
                    break;
                }
                terminal.writeLine(res);
                terminal.flush();
            }
            while (true);
        });


        try {
            srv.start(port);
            System.out.println("Server started on port: " + port);
        } catch (IOException e) {
            System.err.println("Server failed: " + e.getMessage());
        }

    }

    private static boolean isPortValid(int port) {
        return port > 0 && port < 32768;
    }
}

