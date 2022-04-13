package com.nahorny.solution3.telnetserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShellServer {
    private final Map<String, Command> commands = new HashMap<>();

    {
        registerCommand("exit", (name, argument, terminal) -> terminal.close());

        registerCommand("help", (name, argument, terminal) -> {
            terminal.writeLine(StringUtils.join(" ", commands.keySet()));
            terminal.flush();
        });

        registerCommand("logmode", (name, argument, terminal) -> {
            if (terminal.isLogMode()) {
                terminal.setLogMode(false);
                terminal.writeLine("LogMode disabled.");
            } else {
                terminal.setLogMode(true);
                terminal.writeLine("LogMode enabled.");
            }
            terminal.flush();
        });

        registerCommand("echo", (name, argument, terminal) -> {
            terminal.writeLine(argument);
            terminal.flush();
        });
    }

    private TelnetServer telnet = null;

    public void start(int port) throws IOException {
        if (telnet == null) {
            TelnetServer srv = new TelnetServer();
            srv.setOnCommandLineListener(new CommandProcessor());
            srv.start(port);
            telnet = srv;
        } else {
            throw new IllegalStateException();
        }
    }

    public void stop() throws InterruptedException {
        if (telnet != null) {
            telnet.stop();
            telnet = null;
        } else {
            throw new IllegalStateException();
        }
    }

    public void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(Locale.getDefault()), command);
    }

    private class CommandProcessor implements OnCommandLineListener {
        @Override
        public void onCommandLine(Terminal terminal, String commandLine) throws IOException {
            try {
                commandLine = commandLine.trim();
                String[] tokens = commandLine.split(" ");
                String name = tokens[0].toLowerCase(Locale.getDefault());
                Command command = commands.get(name);
                if (command != null) {
                    command.execute(name, commandLine.substring(name.length()).trim(), terminal);
                } else if (name.isEmpty()) {
                    // Do nothing
                } else {
                    terminal.writeLine("Command not found.");
                    terminal.flush();
                }
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                terminal.writeLine("Error: " + e.toString());
                terminal.flush();
            }
        }
    }

    public interface Command {
        void execute(String name, String argument, Terminal terminal) throws IOException, InterruptedException;
    }
}
