# Communication client-server protocol 

## Objectives

This communication protocol will define the communication rules between the client and the server in a calculator application. 

## Overall behavior

This protocol will use the TCP protocol. 
The client will find the server at the address: xxx.xxx.xxx.xxx port 1997
The server will speak first to welcom the client. Then the client will ask requests to the server and the server will answer.
The communication will be closed when the client send an EXIT request to the server. Then the client and the server will close their connection.

## Messages

There are six types of messages, four for the server and two for the client.
Server: 
- a welcome message containing only WELCOME
- a result message containing the result and the operation like the following example: OP op X Y RESULT Z. This example is the result of the addition of the X and Y number and the result is in Z. (X, Y and Z are number and op the charactere of the operation)
- an error message containing the ERROR flag and a string describing the error.
- an acknowlegment message containing OK to send after recieved the EXIT request of a client.
Client:
- an operation request containing the operation and the two operands like in the following example: OP op X Y (where X and Y are numbers and op the charactere of the operation)
- an exit request containing only EXIT

The sequence of messages between the client and the server will begin with a WELCOME from the server. Then the client will send OPERATION requests and for each OPERATION request of the client the server will send to its the RESULT message or an ERROR message with a description. At the end, when the client has finished, it will send an EXIT request to the server and wait for the OK message of the server.

If the server receives a wrong request, it will only send an ERROR message with a description.
If the client recieves a wrong request, it will ignore it.

## Specific elements

The supported operations are +, -, *, /, div and % for the beginning. (div is an integer division when / is a real division)
It can be extended with the boolean operations: <, =<, >, => and ==.
Another extensibility can be the multiple operation where an operand can be another operation.

If the computation is impossible to do (like division by zero), the server will send an error messsage to the client.

