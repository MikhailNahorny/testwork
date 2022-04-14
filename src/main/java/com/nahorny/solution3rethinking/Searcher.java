package com.nahorny.solution3rethinking;

/*
according to requirement 'there is a thread on the server from which, and only from, the file system is accessed'
class implement singleton  pattern.
this thread (class) responsibilities:
- filesystem tree traversal
- storing and processing list of requests
 */

import com.nahorny.solution3.FileSystemHandler;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Searcher extends Thread {
    private List<Request> requests = new ArrayList<>();
    private List<Request> concurrentRequests = new ArrayList<>();
    private final String rootPath;
    //max required depth of opened (currently not completed) requests
    private int maxCurrentDepth = -1;


    private static volatile Searcher instance;

    private Searcher(String rootPath) {
        if (instance != null) throw new RuntimeException("Searcher thread should be singleton.");
        this.setName("Searcher");
        this.rootPath = rootPath;
        System.out.println("-----Searcher started");
    }

    public static Searcher generateInstanceAndGet(String rootPath) {
        if (instance == null) {
            synchronized (FileSystemHandler.class) {
                if (instance == null) instance = new Searcher(rootPath);
            }
        }
        return instance;
    }

    public static Searcher getInstance() {
        if (instance == null)
            throw new RuntimeException("Call Searcher generateInstanceAndGet(String rootPath) before!");
        return instance;
    }

    public void addRequest(Request request) {
        this.concurrentRequests.add(request);
        System.out.println("-----Request object was added to Searcher's requests list.");
    }

    @Override
    public void run() {

        File root = new File(rootPath);

        if (!root.exists()) {
            System.err.println("Could not find file system root");
            System.exit(-1);
        }

        Queue<FileAndDepthStorage> mainFileQueue = new ArrayDeque<>();
        FileAndDepthStorage fileAndDepthRootNode = new FileAndDepthStorage(root, 0);

        //infinity loop for search logic
        while (true) {

            //stand-by via guard clause
            if (requests.isEmpty() && concurrentRequests.isEmpty()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    continue;
                } catch (InterruptedException ignored) {
                }
            }

            //if file tree end was reached, start new traversal
            mainFileQueue.add(fileAndDepthRootNode);

            //while end of file tree is not reached
            while (!mainFileQueue.isEmpty()) {

                //to avoid ConcurrentModificationException if addRequest() called while 'request : requests' loop
                if (!concurrentRequests.isEmpty()) {
                    requests.addAll(concurrentRequests);
                    concurrentRequests.clear();
                }

                //calculating maxCurrentDepth value
                Optional<Integer> newMaxDepth = requests.stream()
                        .map(Request::getRequiredDepth)
                        .max(Comparator.naturalOrder());
                maxCurrentDepth = newMaxDepth.orElse(-1);

                // maxCurrentDepth == -1 mean request queue is empty. to be driven to stand-by mode
                if (maxCurrentDepth < 0) {
                    break;
                }

                FileAndDepthStorage current = mainFileQueue.poll();
                File[] listOfFilesAndDirectory = current.getFile().listFiles();
                if (listOfFilesAndDirectory != null) {
                    for (File file : listOfFilesAndDirectory) {

                        //traversal depth limitation according to max required depth of currently opened requests
                        if (file.isDirectory() && current.getDepth() + 1 <= maxCurrentDepth) {
                            mainFileQueue.add(new FileAndDepthStorage(file, current.getDepth() + 1));
                        }

                        if (!concurrentRequests.isEmpty()) {
                            requests.addAll(concurrentRequests);
                            concurrentRequests.clear();
                        }

                        //check current file for matching for any requests and collect completed requests
                        ArrayList<Request> completedRequests = new ArrayList<>();
                        for (Request request : requests) {
                            boolean isRequestCompleted = request.
                                    checkCurrentFile(new FileAndDepthStorage(file, current.getDepth() + 1));
                            if (isRequestCompleted) {
                                completedRequests.add(request);
                            }
                        }

                        //remove completed requests from open requests list
                        requests.removeAll(completedRequests);

                    }
                }
            }

            //can be not empty, if last request run not from root of the tree
            //will affect next request if not clean
            mainFileQueue.clear();

        }

    }

}
