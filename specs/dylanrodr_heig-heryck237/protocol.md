# Specifications
dylanrodr-heig : Dylan Rodrigues Alves
heryck237 : AKOUMBA Erica Ludivine

## Protocol objectives: what does the protocol do?
The protocol CALC was made to answer at basic arithmetical equations from the user sent to a server.
The communication uses a TCP/IP network. All servers using this specification must calculate additions
and multiplications. The client must know the specifications to communicate with the server.
The CALC protocol is stateless. The server doesn't need to keep track of the messages sent by the client.

## Overall behavior:
### What transport protocol do we use?
In this application, we want a **contract** between the server and the client. Which means we need TCP as transport
protocol.

### How does the client find the server (addresses and ports)?
When creating the client socket, we give him the hostname and the port number of the server to communicate with.

In addition :
The default communication port is 2021.

### Who speaks first?
The client. He needs to start the connection (`"Hello"`).

### Who closes the connection and when?
The client sends a request (Bye) to end the connection and the server ends it.

## Messages:
### What is the syntax of the messages?
A string containing the operator and the 2 numbers. `"operator number1 number2"`  
Example : `"+ 1235 455"`
The message is ended with a CRLF sequence (ACII 13, ASCII 10) to indicate the end line.  

### What is the sequence of messages exchanged by the client and the server? (flow)
Client sends `"Hello"`  
Server responds `"Hello"` if not busy and nothing if busy.  
Client sends `"operator number1 number2"`  
Server responds `"result"`
Client sends `"Bye"` (if he wants to end the connection)  

### What happens when a message is received from the other party? (semantics)
When a message is received the party reads it and execute an action.

## Specific elements (if useful)
### Supported operations
We will implement : `+`, `*`  
It enables the client to request additions or multiplications.  

### Error handling
The responses to the errors are sent by the server.

Errors :
- **Bad request**  
If the client doesn't respect the flow, the server will send an error msg.
- **Bad syntax**  
If the client doesn't respect the message syntax, the server will send an error msg.

### Extensibility
NONE

## Examples: examples of some typical dialogs.
### Normal dialog :
Client sends `"Hello"`  
Server responds `"Hello"`  
Client sends `"+ 134 -56"`  
Server responds `"78"`  
Client sends `"/ 1 0"`  
Server sends `"Cannot divide by 0"`  
Client sends `"Bye"` (if he wants to end the connection)
