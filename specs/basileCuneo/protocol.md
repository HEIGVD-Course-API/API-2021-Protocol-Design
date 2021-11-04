##Protocol objectives: what does the protocol do?
Once the connection is established the server will say hello.
The client will then answer with the calcul that he wants to do.
The server will calculate the result and give it to the client.
If the client asks to stop, the communication ends.
The client will always print the server answers.

##Overall behavior:
###What transport protocol do we use?
We will use TCP protocol, it is more appropriate to a "conversation" with multiple request and answers.

###How does the client find the server (addresses and ports)?
When the server will start he will get an IP adress hardcoded or through DNS, and we will have to manually give it to 
the client. The port will always be the same (2341).

###Who speaks first?
The server will speak first with a sentence like "Hello"

###Who closes the connection and when?
The client can choose to ask for a calcul or he also can ask to stop the communication, when the server will get the 
stop message, he will end the communication.

##Messages:
###What is the syntax of the messages?
Messages won't take care of case. White spaces will be ignored (from client to server).
The client will send the following messages:
to stop : Stop\n  
to ask for a calcul: `<operator>;<value1>;<value2>\n` (example: 15 + 2 => +;15;2\n)
The server will send the following messages:
at communcation start: Hello\n
when a calcul is done: The answer is <answer>\n
before the communication ends: Bye\n

###What is the sequence of messages exchanged by the client and the server? (flow)
Communication starts.
client: 15 + 2
server: the answer is 17
client: 8/4
server: the answer is 2
client: stop

###What happens when a message is received from the other party? (semantics)
When the client receives the message he will know that it's his turn to ask something
When the server receives the message, he will process the message and do what needs to be done (calcul or stop)

##Specific elements (if useful)
###Supported operations
The supported operations will be +, -, *, /, %, ^.

###Error handling
The client can't receive error and if he get's a wrong message he won't really care of it.
If the server receives a bad message, he will answer with a "bad request" and wait for a new one.

###Examples: examples of some typical dialogs
client: 15 + a
server: bad request
client: 15 + 2
server: 17
client: 16/4
server: 4
client: stop


























