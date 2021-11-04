## The spec for a remote calculation center

## Overall behaviour :

We will be using tcp and send text over the network.

The connection will be initiated by the client.
The handshake will be handled by tcp. 
at the Application level there is no handshake.
The client send something to compute ,

The communication will be on port 4242 and the ip addresse will be resolved via DNS.
the name is myAwsomeCalculator.net
## Message spec
An expression is something of the form : `(operand operation operand)`
operand can be a real value or an integer in the range of java BigDecimal.
operation is one of : +-*/
The client send a textual representation of a mathematical expression made of expression like that :
`((operand operation operand) operation operand)`
There can be as many expression as the client likes, but the query must be less than 4Ko.
The request must always put the parentheses on all  `operand operation operand` 
the server returns the answer. 
The answer can be of two type:  

1. value 
2. error

The response is a string with `<Error|Value><white char><reason|computedValue>`
there cannot be a response like <Error><white char><computedValue> or <Value><white char><reason>

if the Value key is present, the value(computedValue)is the string representation of the computed value.
if the Error key is present, the value(reason) is the reason causing the error. 

There must be a key or either Value or Error present
The response is finished when the connection is closed by the server

The connection is closed by the server.

The server should use the Shunting yard algorithm and use bigDecimal to store operand


