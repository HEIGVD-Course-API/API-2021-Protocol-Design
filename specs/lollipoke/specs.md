* Protocol objectives: what does the protocol do?
	The protocol allows the client to contact the server and ask for basic math operations, such as multiplication, addition, substraction, division, ..., and allow the server to provide the client with the result of the requested operation.

* Overall behavior:
	** What transport protocol do we use?
		TCP
	** How does the client find the server (addresses and ports)?
		It will use the IP address of my machine 10.192.92.197 and a custom port that we arbitrarily set to 4242.
	** Who speaks first?
		The client speaks first to connect to the server.
	** Who closes the connection and when?
		The client closes the connection when it is done with the math operations by sending a specific message.

* Messages:
	** What is the syntax of the messages?
		The client will send "operand operator operand", or "Mischief managed" if it wants to close the connection. The server will simply send the result of the computation.
	** What is the sequence of messages exchanged by the client and the server? (flow)
		S: "Waiting for a request"
		C: "operand operator operand"
		S: "result" or "Invalid format" depending on the validity of the request
		S: "Waiting for a request"
		C: "operand operator operand" or "Mischief managed" if it wants to close the connection.
	** What happens when a message is received from the other party? (semantics)
		The server parses the messages. If it corresponds to "Mischief managed", close the connection. If it corresponds to the format "operand operator operand", switch on the operator and provide the result. Otherwise send an invalid command message.
		The client simply reads the results, no need to process them.

* Specific elements (if useful)
	** Supported operations
		Addition, substraction, multiplication, division, power
	** Error handling
		If the format is incorrect, simply send an invalid command message
	** Extensibility
		---
* Examples: examples of some typical dialogs.
		S: "Waiting for a request"
		C: "3*2"
		S: "3*2 = 6"
		S: "Waiting for a request"
		C: "4/2"
		S: "4/2 = 2"
		S: "Waiting for a request"
		C: "Test"
		S: "Invalid format"
		S: "Waiting for a request"
		C: "Mischief managed"
		S: "Closing connection..."