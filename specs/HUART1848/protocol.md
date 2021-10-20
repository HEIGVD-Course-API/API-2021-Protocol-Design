#PROTOCOL
## Protocol objectives:
The protocol enables a client to send computations commands to a server.
## Behaviour
1. The protocol uses TCP as its transport protocol.
2. The client should know the ip/name of an available server. The protocol standard port is 11111.
3. The client sends the first message. The server acknowledges the client presence by answering and effectively starting the communication
4. The client sends the disconnect request. The server acknowledges the request by sending a final message and closes the connection.
## Messages
1. The client computation commands use the Reverse Polish Notation, for example "3 4 5 × −" would be a valid client command. To quit and stop the connection, the client may type "quit" or "exit". The server sends 3 types of message, all shown as a bracketed prefix
   1. [OK] : The last command syntax was valid. The message contains the answer
   2. [ERROR] : The last command syntax was not valid or another kind of problem happened. The message contains the error type.
   3. [ACK] : This the answer to the first message or the disconnect request. The message contains salutations.
2. Here's in an example of a standard conversation, annotated with C for client and S for server:
```
// We assume client initiates the connection with, for example "telnet localhost 11111"
S [ACK] : Welcome!
C 3 4 5 × −
S [OK] :  
```
