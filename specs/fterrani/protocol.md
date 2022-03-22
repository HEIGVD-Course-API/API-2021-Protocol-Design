
# Protocol objectives
Allow a client to send simple arithmetic expressions to a server and to receive the resulting value.


# Overall behavior
Server should use TCP and listen on port 23425.

Client must know the server IP in advance.


Once the connection is established, the server listens to client queries.

Protocol is ASCII-based. Any non-ASCII char will lead to unexpected results.
The client and the server both sends a message per line (a line ends with \n or \r\n).

# Messages

A line sent by the client to the server looks like this:

<op> <num> <num>

<op> must match this regex: (add|mul|sub|div|bye)
<num> must match this regex: [+|-][0-9]+(\.[0-9]+)?

If the server receives an add message, it returns the sum of the two received operands.
If the server receives an sub message, it returns the first operand minus the second.
If the server receives an mul message, it returns the product of the two received operands.
If the server receives an div message, it returns the value of the first operand divided by the second.
If the server receives a bye message, it closes the connection.


Server responses are also lines and can either be:

res <num>

to return a result to the client. Or it can be:

err <error-message>

where <error-message> is a string that contains only printable ASCII characters different from \r\n.
Client ignores unexpected res and err messages.

If the server receives any message that isn't a client message, it returns an err message.
If the server receives a div message with second operand equal to zero, it returns an err message.
If the server receives bye from a customer, it closes the connection.
The server always replies to a message before handling the next one. 

# Example

[CONNECTION OPEN !]
-> add 2 3
<- res 5
-> mul 5 7
<- res 35
-> sub 11 23
<- res -12
-> div 4234 0
<- err Division by zero!
-> bye
[CONNECTION CLOSED !]