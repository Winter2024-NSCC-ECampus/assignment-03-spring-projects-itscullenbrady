package com.example.a03spring;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.CommandLineRunner;
        import org.springframework.context.annotation.Lazy;
        import org.springframework.core.io.ClassPathResource;
        import org.springframework.shell.Shell;
        import org.springframework.stereotype.Component;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.io.PrintWriter;
        import java.net.InetAddress;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        @Component
        public class TelnetServer implements CommandLineRunner {

            private static final int PORT = 23;
            private List<PrintWriter> clientWriters = new ArrayList<>();
            private Map<String, String> userCredentials = new HashMap<>();

            private TelnetShellCommands shellCommands;
            private Shell shell;

            @Autowired
            public void setShellCommands(@Lazy TelnetShellCommands shellCommands) {
                this.shellCommands = shellCommands;
            }

            @Autowired
            public void setShell(Shell shell) {
                this.shell = shell;
            }

            @Override
            public void run(String... args) throws Exception {
                String defaultUsername = System.getenv("USERNAME");
                String defaultPassword = System.getenv("PASSWORD");

                if (defaultUsername != null && defaultPassword != null) {
                    userCredentials.put(defaultUsername, defaultPassword);
                }

                loadUserCredentials();

                // Clearing thee screen using ANSI escape codes
                System.out.print("\033[H\033[2J");
                System.out.flush();

                InetAddress inetAddress = InetAddress.getLocalHost();
                String hostname = inetAddress.getHostName();
                String ipAddress = inetAddress.getHostAddress();

                System.out.println("Telnet server started on port " + PORT);
                System.out.println("Username selected " + defaultUsername);
                System.out.println("Password selected " + defaultPassword);
                System.out.println("Hostname: " + hostname);
               // System.out.println("IP Address: " + ipAddress);

                new Thread(this::readConsoleInput).start();

                try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                    while (true) {
                        new ClientHandler(serverSocket.accept()).start();
                    }
                }
            }

            private void loadUserCredentials() throws Exception {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("Users.txt").getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 2) {
                            String username = parts[0];
                            String password = parts[1].replace(";", "");
                            userCredentials.put(username, password);
                        }
                    }
                }
            }

            private void readConsoleInput() {
                try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                    String consoleMessage;
                    while ((consoleMessage = consoleReader.readLine()) != null) {
                        broadcastMessage(consoleMessage, "Console");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private class ClientHandler extends Thread {
                private Socket socket;
                private PrintWriter out;
                private BufferedReader in;
                private String username;

                public ClientHandler(Socket socket) {
                    this.socket = socket;
                }

                @Override
                public void run() {
                    try {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = new PrintWriter(socket.getOutputStream(), true);

                        if (!authenticate()) {
                            out.println("Authentication failed. Connection closed.");
                            socket.close();
                            return;
                        }

                        synchronized (clientWriters) {
                            clientWriters.add(out);
                        }

                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println("Received: " + message);
                            broadcastMessage(message, username);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        synchronized (clientWriters) {
                            clientWriters.remove(out);
                        }
                    }
                }

                private boolean authenticate() throws Exception {
                    out.println("Enter username:");
                    username = in.readLine();
                    out.println("Enter password:");
                    String password = in.readLine();

                    String storedPassword = userCredentials.get(username);
                    return storedPassword != null && storedPassword.equals(password);
                }
            }

            private void broadcastMessage(String message, String username) {
                if (message.startsWith("/")) {
                    String command = message.substring(1);
                    System.out.println("Executing command: " + command); // Debug statement
                    String response = executeShellCommand(command);
                    System.out.println("Command response: " + response); // Debug statement
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(username + ": " + response);
                        }
                    }
                } else {
                    String fullMessage = username + ": " + message;
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(fullMessage);
                        }
                    }
                }
            }

            private String executeShellCommand(String command) {
                // Split the command and arguments
                String[] parts = command.split(" ");
                String commandName = parts[0];
                String[] args = new String[parts.length - 1];
                System.arraycopy(parts, 1, args, 0, args.length);

                // Execute the corresponding shell command
                switch (commandName) {
                    case "help":
                        return shellCommands.help();
                    case "startServer":
                        return shellCommands.startServer();
                    case "stopServer":
                        return shellCommands.stopServer();
                    case "serverStatus":
                        return shellCommands.serverStatus();
                    case "listUsers":
                        return shellCommands.listUsers();
                    case "addUser":
                        if (args.length == 2) {
                            return shellCommands.addUser(args[0], args[1]);
                        } else {
                            return "Invalid arguments for addUser. Usage: addUser <username> <password>";
                        }
                    case "removeUser":
                        if (args.length == 1) {
                            return shellCommands.removeUser(args[0]);
                        } else {
                            return "Invalid arguments for removeUser. Usage: removeUser <username>";
                        }
                    case "changePassword":
                        if (args.length == 2) {
                            return shellCommands.changePassword(args[0], args[1]);
                        } else {
                            return "Invalid arguments for changePassword. Usage: changePassword <username> <newPassword>";
                        }
                    default:
                        return "Unknown command: " + commandName;
                }
            }

            public Map<String, String> getUserCredentials() {
                return userCredentials;
            }
        }