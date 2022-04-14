package com.nahorny.solution3rethinking.telnetserver;

import java.io.IOException;

public interface OnCommandLineListener {
    void onCommandLine(Terminal terminal, String commandLine) throws IOException;
}
