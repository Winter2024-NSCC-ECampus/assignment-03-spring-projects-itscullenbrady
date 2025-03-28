# A03Spring Telnet Server Application

## Overview

This project is a Telnet server application built using Spring Boot and Spring Shell. The application allows users to connect via Telnet, authenticate using predefined credentials, and execute various shell commands. The server supports multiple clients and provides a set of commands to manage the server and user accounts.

## Features

- **Telnet Server**: Allows multiple clients to connect and interact with the server.
- **User Authentication**: Users must authenticate with a username and password.
- **Shell Commands**: Provides a set of commands to manage the server and user accounts.
- **Spring Boot**: Utilizes Spring Boot for easy setup and configuration.
- **Spring Shell**: Integrates Spring Shell to handle command execution.

## Getting Started

### Prerequisites

- Java 17 or higher
- Apache Maven 3.6.0 or higher

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/itscullenbrady/A03Spring.git
    cd A03Spring
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    start-shell.bat
    ```

### Configuration

The application uses environment variables for default user credentials. You can set these variables in the `start-shell.bat` file:
```bat
@echo off
set USERNAME=admin
set PASSWORD=123
start cmd /k "set USERNAME=%USERNAME% && set PASSWORD=%PASSWORD% && mvn spring-boot:run"
```

## User Credentials

User credentials are stored in the `src/main/resources/Users.txt` file. The format is:
```
username,password
```

## Usage

### Connecting to the Telnet Server

Open a Telnet client and connect to the server:

```sh
telnet localhost 23
```

Enter your username and password when prompted.

### Available Shell Commands

Once connected, you can use the following commands:

- `help`: Display available commands.
- `startServer`: Start the Telnet server.
- `stopServer`: Stop the Telnet server.
- `serverStatus`: Get the current status of the server.
- `listUsers`: List all registered users.
- `addUser <username> <password>`: Add a new user.
- `removeUser <username>`: Remove an existing user.
- `changePassword <username> <newPassword>`: Change the password of an existing user.

### Example Commands

Start the server:
```sh
startServer
```

List all users:
```sh
listUsers
```

Add a new user:
```sh
addUser newuser newpassword
```

Change a user's password:
```sh
changePassword existinguser newpassword
```

## Spring Shell

Spring Shell is a framework that allows you to create interactive command-line applications. It provides a simple way to define commands and their behavior. In this application, Spring Shell is used to handle Telnet commands.

### Defining Commands

Commands are defined in the `TelnetShellCommands` class using the `@ShellComponent` and `@ShellMethod` annotations. Each method represents a command that can be executed by the user.

Example:
```java
@ShellMethod("Start the Telnet server")
public String startServer() {
    if (serverRunning) {
        return "Telnet server is already running.";
    }
    serverRunning = true;
    return "Telnet server started.";
}
```

### Executing Commands

When a user enters a command, the application parses the input and executes the corresponding method. The result is then sent back to the user.

## Additional Resources

For further reference, please consider the following sections:

- [Official Apache Maven documentation](https://maven.apache.org/)
- [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/)
- [Spring Shell Reference Guide](https://docs.spring.io/spring-shell/docs/current/reference/html/)
- [Spring Web](https://spring.io/projects/spring-boot)

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Acknowledgments

- Spring Boot
- Spring Shell
- Apache Maven

