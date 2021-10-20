# Specs
## PrazTobie 

### Overall behavior
* Protocol: TCP
* Discovery: broadcast on 255.255.255.255
* First: client
* Closing: server

### Messages
* Syntax: JSON
  * operation request: {"operation": String, operands: Number[]}
  * repsonse: {"result": Number}
  * error: {"error": String}
* Sequence
  * Client: request operation
  * Server: respond with operation result
  
###Misc
* Error handling: return error reason (invalid operation)

## damianomondaini

### Protocol objectives

* The protocol allow a client to ask the server a simple math operation

### Overall behavior

* Using TCP
* The client connects on the server IP adress and port number 7788
* The client speaks first with his query
* The server closes the connection when the query is successful

### Messages
* JSON ({operation: "+", a: 1, b: 2})
* Flow
  * Client send query
  * If query is invalid
    * Server answer "Invalid query"
  * If query is valid
    * Server answer the query and closes the connection
* Process it

### Specific elements
* Additions, substractions
* Error if query is malformed
* What ?

### Exemple
* Client: 1 * a
* Server: Invalid query
* Client: 1 * 2
* Server: 2
* Server: Connection closed
