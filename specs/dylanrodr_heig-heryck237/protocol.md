# Specifications
dylanrodr-heig : Dylan Rodrigues Alves
heryck237 : AKOUMBA Erica Ludivine

## Protocol objectives: what does the protocol do?
The protocol role is to establish a communication between 2 network devices.
It allows the server to answer the request of the client.
The client should be able to ask the server to compute a calculation and to return the result.

## Overall behavior:
### What transport protocol do we use?
In this application, we want a **contract** between the server and the client. Which mean that we need a protocol using
TCP.

### How does the client find the server (addresses and ports)?
When creating the client socket, we give him the hostname and the port number of the server to communicate with.

### Who speaks first?
The client. He needs to start the connection.

### Who closes the connection and when?
The client sends a request to end the connection and the server ends it.

## Messages:
### What is the syntax of the messages?
A string containing the operator and the 2 numbers. `"operator number1 number2"`  
Example : `"+ 1235 455"`

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
We will implement : `+`, `-`, `*`, `/`

### Error handling
The responses to the errors are sent by the server.

Errors :
- Bad request  
If the client doesn't respect the flow, the server will send an error msg.
- Bad syntax  
If the client doesn't respect the message syntax, the server will send an error msg.

### Extensibility
?

## Examples: examples of some typical dialogs.
### Normal dialog :
Client sends `"Hello"`  
Server responds `"Hello"`  
Client sends `"+ 134 -56"`  
Server responds `"78"`  
Client sends `"/ 1 0"`  
Server sends `"Cannot divide by 0"`  
Client sends `"Bye"` (if he wants to end the connection)
