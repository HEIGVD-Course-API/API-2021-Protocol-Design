# API - Protocol Design

Authors: Anthony Coke

Date: 2021-10-21

## Protocol objectives

### What does the protocol do ?

The protocol lets clients submit arithmetical operations to one or more servers.

## Overall behavior

### What transport protocol do we use?

The TCP/IP protocol will be used.

### How does the client find the server (addresses and ports)?

The server will be listening on default port **4242**. The address should be provided 
by the user.

### Who speaks first?

The client does an initial connection request, but the server is the first to 
speak when the connection is established. It sends the list of its supported 
operations to the client.

### Who closes the connection and when?

The client closes the connection after the message `END` was sent to the server.

## Messages

### What is the syntax of the messages?

Our protocol is a "line by line" protocol. Each line ends with a CRLF sequence.

#### WELCOME

Once the connection is established, the server greets the client with a WELCOME 
message. The first line will be `WELCOME CRLF` and the second `SYNTAX: REQUEST 
{OP} {V1} {V2} CRLF`. The client MUST read a line `AVAILABLE_OPS` and loop until 
it reads a line of value `END_OF_OPS CRLF`. Each line between indicates the name of 
an operation supported by the server.

#### REQUESTS

Each mathematical operation is sent by the client to the server and is formatted 
like this : `REQUEST OP V1 V2 CRLF`. 

Example : `REQUEST ADD 4 2 CRLF` which will add 2 to 4.

#### RESULT 

Once the server has successfully executed the request. It sends the result back 
to the client. It is formatted like this : `RESULT IS {VALUE} CRLF`.

Example : `RESULT IS 3 CRLF`

#### END
This message is sent from the client to the server. It should stop the session 
and close the connection. Its format is : `END CRLF`.

### What is the sequence of messages exchanged by the client and the server? (flow)

We will be expecting the following sequence :

1. The server sends a WELCOME message with available ops
2. The client sends a REQUEST message with a valid operation
3. The server sends a RESULT message
4. The client sends an END message

### What happens when a message is received from the other party? (semantics)
If the message is correctly formatted, the server will process the request, 
otherwise it'll send back an ERROR message with the corresponding string.

## Specific elements

### Supported operations

The server MUST implement at least two operations : ADD and MUL.

### Error handling

The server MUST answer with an ERROR message if the syntax of the request is invalid.

### Extensibility

The server MAY implement additional operations. The complete list of supported 
operations will be sent in the WELCOME message on connection. Other operations 
can be easily added on the long term.

## Examples
```
SRV: WELCOME CRLF
SRV: SYNTAX: REQUEST {OP} {V1} {V2} CRLF 
SRV: AVAILABLE_OPS CRLF
SRV: ADD V1 V2 CRLF
SRV: MUL V1 V2 CRLF
SRV: END_OF_OPS CRLF
CLI: RQUEST ADD 1 2 CRLF
SRV: RESULT IS 3 CRLF
CLI: REQUEST MUL 3 4 CRLF
SRV: RESULT IS 12 CRLF
CLI: END
SRV: SEE_YOU_SOON BRO
```

```
SRV: WELCOME CRLF
SRV: SYNTAX: REQUEST {OP} {V1} {V2} CRLF 
SRV: AVAILABLE_OPS CRLF
SRV: ADD V1 V2 CRLF
SRV: MUL V1 V2 CRLF
SRV: END_OF_OPS CRLF
CLI: MUT 3 4 CRLF
SRV: ERROR OP_NOT_SUPPORTED CRLF
CLI: MUL A 3 CRLF
SRV: ERROR INVALID_SYNTAX CRLF
CLI: END
SRV: SEE_YOU_SOON BRO
```

