package com.nahorny.solution3.telnetserver;

import java.io.IOException;

public interface OnCommandLineListener {
    void onCommandLine(Terminal terminal, String commandLine) throws IOException;
}
