package com.nahorny.solution3rethinking;

/*
program search for nodes of a file system whose name contains a
specific string (mask), on a specific depth from the root directory.
a program runs in threads: search, interaction with file system handling, and main (telnet server).
the program will ignore nodes dipper than the required depth.
the program runs iteratively.
only one thread handle file system.
 */

import com.nahorny.solution3rethinking.telnetserver.ShellServer;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Exchanger;
import java.util.concurrent.LinkedBlockingQueue;

class TelnetServerTraversal {

    private static final int MIN_PATH_LENGTH = 5;
    private static final int MIN_MASK_LENGTH = 1;
    private static final int MIN_DEPTH = 0;
    private static final Exchanger<Object> EXCHANGER = new Exchanger<>();

    public static void main(String[] args) {
        //-----------------start, get port and root
        Scanner scanner = new Scanner(System.in);
        String[] rootPath = new String[1];
        int port = -1;

        System.out.println("Application search node in the file system, that contains a specific string (mask) in name "
                + "and is located on a specific depth from the root directory."
                + "\nYou required to set port for telnet server and root directory in this terminal."
                + "\nThan open telnet terminal with localhost ip and port you set, where you will be required "
                + "to start search with command find <depth> <mask>");

        do {
            System.out.print(String.format("Please, enter path to root dir (more, than %d character): ", MIN_PATH_LENGTH));
            rootPath[0] = scanner.nextLine();
        }
        while (rootPath[0].length() < MIN_PATH_LENGTH);

        boolean isPortValid = false;
        do {
            System.out.print("Please, enter port (number between 0 and 32_767, be sure you chose not reserved port): ");
            long portLong = scanner.nextInt();
            if ((portLong > 0) && (portLong < 32768)) {
                port = (int) portLong;
                isPortValid = true;
            }
        }
        while (!isPortValid);

        Searcher.generateInstanceAndGet(rootPath[0]).start();

        //-----------------server init
        ShellServer srv = new ShellServer();
        srv.registerCommand("find", (name, arguments, terminal) -> {
            //getting arguments and validation
            System.out.println("-----find command was called");
            String[] argsAsArray = arguments.split(" ");
            int depth = Integer.parseInt(argsAsArray[0]) + 1;
            if (depth < MIN_DEPTH) {
                terminal.writeLine("Depth should not be less than zero.");
                terminal.flush();
                System.out.println(String.format("-----Invalid request. Depth = %d", depth));
                return;
            }
            String mask = argsAsArray[1];
            if (mask.length() < MIN_MASK_LENGTH) {
                terminal.writeLine(String.format("Mask length should not be less than %d.", MIN_MASK_LENGTH));
                terminal.flush();
                System.out.println(String.format("-----Invalid request. mask = '%s'", mask));
                return;
            }

            //creating request-object and adding it to searcher
            Request request = new Request(depth, mask);
            Searcher.getInstance().addRequest(request);
            terminal.writeLine("Search start. Results are:");
            terminal.flush();
            LinkedBlockingQueue<String> resultQ = request.getResultQueue();

            //waiting and printing results until not the end of search
            do {
                String res = resultQ.take();
                if (res.equals("@END_OF_SEARCH")) {
                    terminal.writeLine("Search end.");
                    terminal.flush();
                    //infinity loop exit if end of search
                    break;
                }
                terminal.writeLine(res);
                terminal.flush();
            }
            while (true);
        });


        try {
            srv.start(port);
            System.out.println("-----Server started on port: " + port);
        } catch (IOException e) {
            System.err.println("Server failed: " + e.getMessage());
        }

    }

}
