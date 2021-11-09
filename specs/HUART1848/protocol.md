# PROTOCOL

## Protocol objectives:

The protocol enables a client to send computations commands to a server.

## Behaviour

1. The protocol uses TCP as its transport protocol.
2. The client should know the ip/name of an available server. The protocol standard port is 11111.
3. The client sends the first message. The server acknowledges the client presence by answering and effectively starting
   the communication
4. The client sends the disconnect request. The server acknowledges the request by sending a final message and closes
   the connection.

## Messages

1. The client computation commands are made of a prefix operator and two integers, for example, "+ 3 4" would be a valid
   client command. To quit and stop the connection, the client may type "exit". The server sends 3 types of messages,
   all with as a bracketed prefix
    1. [OK] : The last command syntax was valid. The message contains the answer
    2. [ERROR] : The last command syntax was not valid or another kind of problem happened. The message contains the
       error type.
    3. [ACK] : This is the answer to the first connection or the disconnect request. The message contains salutations.
2. Here's in an example of a standard conversation, annotated with C for client and S for server:

```
// Assuming the client initiates the connection with, for example, "telnet localhost 11111"
S [ACK] : Welcome!
C + 10 11
S [OK] : 21
C quit
S [ACK] : Goodbye!
```

3. Here's an example of a conversation featuring errors

```
S [ACK] : Welcome!
C bonjour
S [ERROR] : Bad expression, expected 'operator int1 int2'
C + 2 a
S [ERROR] : Right operand is not a valid integer
C / 1 0
S [ERROR] : Division by zero
C exit
S [ACK] : Goodbye!
```

## Specifics

1. Supported commands are + - * /, all as binary operators
2. Errors do not close the connection
3. The protocol could be extended with new operators if they are binary
