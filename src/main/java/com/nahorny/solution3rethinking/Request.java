package com.nahorny.solution3rethinking;

/*
the object of this class responsibilities:
- store query data
- creating a queue of results and providing it to the client code
- store a start-stop point for traversing the tree in a circle, starting from any point
- checking the file for matching the request and adding it to result queue
- self-check for completeness
 */

import java.util.concurrent.LinkedBlockingQueue;

public class Request {
    private final int requiredDepth;
    private final String requiredMask;
    private final LinkedBlockingQueue<String> resultQueue = new LinkedBlockingQueue<>();
    private FileAndDepthStorage startStopPoint;

    public Request(int requiredDepth, String requiredMask) {
        this.requiredDepth = requiredDepth;
        this.requiredMask = requiredMask;
        System.out.println(String.format("-----Request object [depth = %d, mask = '%s'] created.",
                this.requiredDepth, this.requiredMask));
    }

    public boolean checkCurrentFile(FileAndDepthStorage current) {
        if (current.equals(startStopPoint)) {
            resultQueue.add("@END_OF_SEARCH");
            System.out.println(String.format("-----Request [depth = %d, mask = '%s'] is completed",
                    this.requiredDepth, this.requiredMask));
            return true; //is this complete?
        }
        if (startStopPoint == null) {
            startStopPoint = current;
        }
        if (requiredDepth == current.getDepth() && current.getFile().getName().contains(requiredMask)) {
            System.out.println(String.format("File '%s' was added to result queue of request [depth = %d, mask = '%s']",
                    current.getFile(), requiredDepth, requiredMask));
            resultQueue.add(current.getFile().toString());
        }
        return false;
    }

    public int getRequiredDepth() {
        return requiredDepth;
    }

    public LinkedBlockingQueue<String> getResultQueue() {
        return resultQueue;
    }

}
