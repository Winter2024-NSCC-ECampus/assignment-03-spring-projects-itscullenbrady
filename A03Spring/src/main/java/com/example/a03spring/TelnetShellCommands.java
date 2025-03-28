package com.example.a03spring;

import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Map;
import java.util.Set;

@ShellComponent
public class TelnetShellCommands {

    private boolean serverRunning = false;
    private final TelnetServer telnetServer;

    public TelnetShellCommands(@Lazy TelnetServer telnetServer) {
        this.telnetServer = telnetServer;
    }

    @ShellMethod("Help")
    public String help() {
        return "Available commands:\n"
                + "startServer - Start the Telnet server\n"
                + "stopServer - Stop the Telnet server\n"
                + "serverStatus - Get server status\n"
                + "listUsers - List all users\n"
                + "addUser <username> <password> - Add a new user\n"
                + "removeUser <username> - Remove an existing user\n"
                + "changePassword <username> <newPassword> - Change the password of an existing user";
    }

    @ShellMethod("Start the Telnet server")
    public String startServer() {
        if (serverRunning) {
            return "Telnet server is already running.";
        }
        // Logic to start the Telnet server
        serverRunning = true;
        return "Telnet server started.";
    }

    @ShellMethod("Stop the Telnet server")
    public String stopServer() {
        if (!serverRunning) {
            return "Telnet server is not running.";
        }
        // Logic to stop the Telnet server
        serverRunning = false;
        return "Telnet server stopped.";
    }

    @ShellMethod("Get server status")
    public String serverStatus() {
        if (serverRunning) {
            return "Telnet server is running.";
        } else {
            return "Telnet server is not running.";
        }
    }

    @ShellMethod("List all users")
    public String listUsers() {
        Set<String> users = telnetServer.getUserCredentials().keySet();
        return "Users: " + String.join(", ", users);
    }

    @ShellMethod("Add a new user")
    public String addUser(String username, String password) {
        Map<String, String> userCredentials = telnetServer.getUserCredentials();
        if (userCredentials.containsKey(username)) {
            return "User " + username + " already exists.";
        }
        userCredentials.put(username, password);
        return "User " + username + " added.";
    }

    @ShellMethod("Remove an existing user")
    public String removeUser(String username) {
        Map<String, String> userCredentials = telnetServer.getUserCredentials();
        if (!userCredentials.containsKey(username)) {
            return "User " + username + " does not exist.";
        }
        userCredentials.remove(username);
        return "User " + username + " removed.";
    }

    @ShellMethod("Change the password of an existing user")
    public String changePassword(String username, String newPassword) {
        Map<String, String> userCredentials = telnetServer.getUserCredentials();
        if (!userCredentials.containsKey(username)) {
            return "User " + username + " does not exist.";
        }
        userCredentials.put(username, newPassword);
        return "Password for user " + username + " changed.";
    }
}