This is a test task. It consists of 3 parts:
1. Tree traversal to a given depth without using recursion.

Write a console application that takes three parameters:
- path to the initial directory (rootPath)
- search depth - non-negative integer (depth)
- mask - string (mask)

The application must find all elements of the file system tree that are at depth from the rootPath tree root and contain the string mask in their name. Requirements:
- The application must be implemented WITHOUT using recursion.

2. Modify the application from task 1 as follows:
- one thread searches
- another thread to print the results to the console as they appear.

3. Modify the application from tasks 1, 2 into a simple multi-user telnet server:
The application takes two parameters:
- serverPort - the port it will "listen" to
- path to the initial directory (rootPath)
Search criteria (depth and mask) are set via the telnet client console (use standard programs for this: telnet, putty, ...)

Requirements:
- all accesses to the file system must be made from one thread
Those. there is a thread on the server from which, and only from, the file system is accessed.
- "telnet server" must be multi-user + interactive
if 4 clients come to the server at the same time and each sets a "search query", then the results to the clients should come in parallel, not sequentially. the user does not have to wait for the results to be returned to all previous users.

Tasks must be performed in Java (not Scala). Maven/Gradle to choose from. Competently, use the standard library, and not invent the wheel (for example, concurrency).

Java 8/Maven used.

note: while(true) / InterruptedException ignored / System.out.println - are not best practices, but acceptable for test work, I believe. also, unit tests would be nice, I'll try to implement it before you read this)