# Protocol
This is to explain how this client-server interaction will work.

## Objectives
We want to connect to a server and ask it to do mathematical operations for us: the client sends an operation to the 
server and the server sends back the result

## Overall behavior:
* We will use TCP as our transport protocol and both client and sever will use sockets.
* The client user will know the server's IP address, and it's port
* The server will speak first when a connection is established between the client and server, it will ask what 
operation the client wants done.
* The client will be the one to close the connection using a specific command like `exit` at any time

## Messages
When a connection is established the server will send a simple message that will look like this:

`What operation do you want to make ? (+, *)`

and the client must answer with a symbole presented in the brackets. If the client's answer is "legal" the server 
will then send a message that looks like this:

`What is the 1st number of the operation?`

and then, when the client answers a number:

`What is the 2nd number of the operation?`

It will then send the answere to the operation and ask for a new operation.

## Supported operations
For starters we will implement only addition and multiplication.

## Error handling
If the server receives an answer at any time that doesn't correspond to what is expected it will signal that to the 
client and then ask for a new input, after 5 wrong inputs it will inform the client that if 5 more wrong inputs the
server will close the connection, and it will do so when necessary.

## Example interaction
`S%` is the server and `C%` is the client

```
S% What operation do you want to make ? (+, *) 

C% addition

S% I'm sorry but this is not an answer I understand please send a symbole as presented in the brackets
S% What operation do you want to make ? (+, *)

C% +

%S What is the 1st number of the operation?

C% 23

S% What is the 2nd number of the operation?

C% 67

S% The answer to the addition of 23 by 67 is:
S% 100
S% What operation do you want to make ? (+, *) 

C% exit
```
