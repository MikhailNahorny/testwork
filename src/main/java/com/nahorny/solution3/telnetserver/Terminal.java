package com.nahorny.solution3.telnetserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;

public interface Terminal {
    String readLine() throws IOException;

    void write(String s) throws IOException;

    void writeLine(String s) throws IOException;

    void flush() throws IOException;

    void close() throws IOException;

    InputStream getInputStream();

    Charset getEncoding();

    void setPrompt(String prompt);

    boolean isEcho();

    void setEcho(boolean enable);

    boolean isLogMode();

    void setLogMode(boolean logMode);

    Set<String> getSessionKeys();

    Object getSession(String key);

    void setSession(String key, Object value);
}
