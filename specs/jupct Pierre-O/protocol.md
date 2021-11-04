# Teaching-HEIGVD-RES-2021-Exercise-Protocol-Design

## Specification

* Protocol objectives: what does the protocol do?

The protocol is about a client and a server. The client is allowed to ask the server to compute a calculation and the server must return the result. The server must be able to add, subtract, multiply and divide. He can offer additional operations later. The list of operations known by the server is given to the client during the connection
* Overall behavior:
  * What transport protocol do we use?
  
  We will use the TCP transport protocol.
  * How does the client find the server (addresses and ports)?
  
  For this laboratory, we will directly give to the client the address, which is localhost, and the port, which will be 9907. But normally the client must know beforehand those.
  * Who speaks first?
  
  The server speaks first when he accepts the connection and says “WELCOME CRLF”
  * Who closes the connection and when?
  
  The server closes the connection once the client says “BYE CRLF”. 
* Messages:
  * What is the syntax of the messages?
	There are 5 messages: WELCOME, COMPUTE, RESULT, ERROR, BYE This is a protocol line by line. The client and server need to send CRLF to end a line.  
  * What is the sequence of messages exchanged by the client and the server? (flow)
	WELCOME: The server sends this message to the client once the connection is available. He sends a new message with the list of operations it knows: “AVAILABLE OPERATION (+) (-) (*) (/) “.
  
	COMPUTE: The client must say that to ask for an operation. Then must define the operator with the two numbers. All the values need to be separated with a space.
  
	RESULT: This message is sent by the server followed by a space and the result. 
  
	ERROR: This message is sent to the client if the syntax of is message isn’t available.

	BYE: The client sent this to end the connection. 

  * What happens when a message is received from the other party? (semantics)
  When a message is received from the other party, the party reads it and then execute an action.
  
* Specific elements (if useful)
  * Supported operations
	(+) addition
	(-) soustraction
	(*) multiplication
	(/) division
  * Error handling
  
  If the client sends an invalid request, the server must respond with an ERROR message.
  ERROR ‘300’ (UNKNOWN OPERATIONS)
  ERROR ‘400’ (SYNTAX ERROR)

  * Extensibility
  
  We will implement only 4 operations, but it will be possible to add more in the future. 
* Examples: examples of some typical dialogs.

S: WELCOME CRLF
S: AVAILABLE OPERATION (+) (-) (*) (/) CRLF
C: COMPUTE + 3 6 CRLF
S: RESULT 9 CRLF
C: COMPUTE / 4 2 CRLF
S: RESULT 2 CRLF
C: COMPUTE 4 7
S: ERROR 400 SYNTAX ERROR CRLF
C: COMPUTE % 4 7
S: ERROR 300 UNKNOWN OPERATIONS CRLF
C: BYE CRLF

  
