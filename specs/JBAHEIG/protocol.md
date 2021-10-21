# Our Custom Calculator Protocol

## Overall behavior:
Basically, the client send a request containing a calculation to the server who will process it and send back the answer.  
### What transport protocol do we use?
As our service is not meant to be really fast, and there won't be that many requests, I'll use TCP.
### How does the client find the server (addresses and ports)?
The client needs to connect to a specific ip address(localhost) and a specific port(1337).
### Who speaks first?
The client must speak first.
### Who closes the connection and when?
The server closes the connection after giving a result to the client

# Message parts

# What is the syntax of the messages?
Our messages will have the following syntax: WELCOME, PROCESS, RESULT, ERROR and QUIT. Clients and the server
have to use CRLF sequence in order to indicate the end of a line.

WELCOME: first message sent when the client connects via TCP. This message contains a few lines describing
in brief which operations are supported and displays (one or a few) examples.

Example: WELCOME CRLF
           FOLLOWING OPERATIONS ARE AVAILABLE
            ADD
            SUB
            MULT
            DIV

PROCESS: sent by the server to the client. It indicated that the server is currently processing the calculus
and will soon return the result.

Example: PROCESS SUB 7 5

RESULT: sent by the server to the client. It contains the result of the operation requested by the client.

Example: RESULT 2

ERROR: sent by the server to the client indicating that there is an issue with the requested operation.
These messages are formatted to contain an error code and a custom text explaining what caused this error.

Example: ERROR 1 UNKNOWN OPERATION

QUIT: sent by the client to the server to indicate that the current session is over.

# Example of the entire thing

Server: WELCOME
Server: LIST OF SUPPORTED OPERATIONS 
Server: ADD X Y 
Server: SUB X Y
Server: MULT X Y
Server: DIV X Y 
Server: END OF LIST
C     : SUB 18 5
Server: PROCESS SUB 18 5
Server: RESULT 13
C     : DIV 20 5
Server: PROCESS DIV 20 5
Server: RESULT 4
C     : QUIT