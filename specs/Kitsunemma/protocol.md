# Communication client-server protocol 

## Objectives

This communication protocol will define the communication rules between the client and the server in a calculator application. 

## Overall behavior

This protocol will use the TCP protocol. 
The client will find the server at the address: xxx.xxx.xxx.xxx port XXXX
The server will speak first to welcom the client. Then the client will ask requests to the server and the server will answer.
The communication will be closed when the client send an EXIT request to the server. Then the client and the server will close their connection.

## Messages

There is six types of messages, four for the server and two for the client.
Server: 
- a welcome message containing only WELCOME
- a result message containing the result and the operation like the following example: ADD X Y RESULT Z. This example is the result of the addition of the X and Y number and the result is in Z. (X, Y and Z are number!)
- an error message containing the ERROR flag and a string describing the error.
- an aquitment message containing OK to send after recieved the EXIT request of a client.
Client:
- an operation request containing the operation and the two operands like in the following example: ADD X Y (where X and Y are numbers)
- an exit request containing only EXIT

The sequence of messages between the client and the server will begin with a WELCOME from the server. Then the client will send OPERATION requests and for each OPERATION request of the client the server will send to its the RESULT message or an ERROR message with a description. If the client recive an ERROR, it will display it and ask a new request. At the end, when the client has finished, it will send an EXIT request to the server and waiting for the OK message of the server.

If the server receives a wrong request, it will only send an ERROR message with a string "Wrong request".
If the client recieves a wrong request, it will send again the last request.

## Specific elements

The suported oparations are +, -, *, /, div and % for the begening. (div is an integer division when / is a real division)
It can be extends with the boolean operations: <, =<, > and =>
Another extentibility can be the multiple operation where an operand can be another operation.

If the calcul is impossible to do (like division by zero), the server will send an error messsage tho the client.

