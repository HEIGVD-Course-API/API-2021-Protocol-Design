# Labo API - Protocol

Authors: Alexandre Jaquier & Valentin Kaelin  
Date: 20.10.2021

## Protocol objectives: what does the protocol do?
The main goal is of the protocol is to allow a client to ask a server to compute a calculation and to return the result.

## Overall behavior:
### What transport protocol do we use?  
We will use the TCP/IP protocol.

### How does the client find the server (addresses and ports)?  
The client needs this information from the user.

### Who speaks first?  
The server will send the list of available operations to the client.

### Who closes the connection and when?  
The client decides when he wants to close the connection (when he does not want to compute calculations anymore).

## Messages:

Each message is a complete line, terminated by the a CRLF end of line character.  
Here is the syntax we have chosen, it's basically following the format: ``Operation leftValue rightValue``
Example: 

``ADD n1 n2``

If the client sends the message `QUIT`, the connection will be terminated.

### Supported operations

* ADD
* SUB
* MULT
* DIV

### What is the sequence of messages exchanged by the client and the server? (flow)

1. Server listens to an adress and a port
2. Client tries to connect to the adress and the port
3. Server accepts and wait for a calculation's request
4. Client send a calculation to compute to the Server
5. Server receives the request and compute it.
6. Server sends back to the Client the result
7. Client recives the result
8. Steps **4.**, **5.**, **6.** and **7.** can be repeated.
9. Client closes the connection

### What happens when a message is received from the other party? (semantics)

If the message follows the format we need, it will be computed then the result will be returned.
Otherwise, an error message will be sent.

### Error handling
The error format is simply a string send back to the Client if the format is not the expected one.

### Extensibility
It's possible to add more operations in the future without too much hasle. On a more long term vision, it would also be possible to delegate the computation to another party.

## Examples: examples of some typical dialogs.

Server: Welcome! Here are the available operations:
Server: ADD  
Server: SUB  
Server: MULT  
Server: DIV  
Server: END_OF_OPERATIONS
Client: ADD 5 6  
Server: 11  
Client: DIV 8 0  
Server: ERROR: Calculation impossible 
Client: DIV 8 2  
Server: 4  
Client: FBEFIBFUIEBF  
Server: ERROR: Calculation impossible 
Client: QUIT 

Connection is now closed.
