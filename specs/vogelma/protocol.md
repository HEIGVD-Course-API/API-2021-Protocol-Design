# Maelle Vogel
* Protocol objectives: what does the protocol do?
  * Overall behavior:
      * What transport protocol do we use?
        * TCP
      * How does the client find the server (addresses and ports)?
        * Localhost (127.0.0.1) and port 4242 (any non-occupied ports)
      * Who speaks first?
        * Client with greeting
      * Who closes the connection and when?
        * Client says when he wants to stop but server close the connection when operations have finised.
    
* Messages:
    * What is the syntax of the messages?
      * number operator number
      * hello and bye for session start/end

    * What is the sequence of messages exchanged by the client and the server? (flow)
      * greetings
      * loop:
        * waiting for operation
        * operation
        * result or error
      * bye
      * end communication

    * What happens when a message is received from the other party? (semantics)
      * Server parses the message and respond with waiting, error or result
      * Client show the result
* Specific elements (if useful)
    * Supported operations
      * addition, subtraction, division and multiplication
      * only number operator number
    * Error handling
      * show error message but continue the communication
    * Extensibility
      * add more number like 2 + 6 * 3
* Examples: examples of some typical dialogs.

* hello
* Welcome to calculator, format number operator number with operator *,/,+ and -
* 2*4
* 8, waiting for operation
* 6/3
* 2, waiting for operation
* bye