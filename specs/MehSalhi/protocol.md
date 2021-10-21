# Protocol
* Protocol objectives: what does the protocol do?
* Overall behavior:
    * What transport protocol do we use?
      * TCP
    * How does the client find the server (addresses and ports)?
      * The client must have the server's IP and port number
    * Who speaks first?
      * The Server
    * Who closes the connection and when?
      * The client
* Messages:
    * What is the syntax of the messages?
      * OPERATION ARGUMENTS CR
    * What is the sequence of messages exchanged by the client and the server? (flow)
      * The client connects
      * The Server sends the first message with by displaying the available commands
      * The client send a command
      * The server treats the command and give the result
        * RESULT 2
      * The client end the connexion with "BYE"
    * What happens when a message is received from the other party? (semantics)
      * The message is parsed and interpreted
        * Send an error if syntax or arguments are wrong
        * Send a result if required
* Specific elements (if useful)
    * Supported operations
      * ADD
      * MULT
      * DIV
      * POW
    * Error handling
      * Unknown command
      * Unknown Operation
    * Extensibility
* Examples: examples of some typical dialogs.
  - Clients connect to server
  - Server sends:
    - ELHO
    - AVAILABLE COMMANDS
      - MATH 
      - Description: perform math operation
      - Syntax: MATH OPERATION op1 op2
        - Available OPERATION:
          - ADD
          - MULT
          - DIV
          - POW
      - LIST
      - Description: display this message
      - Syntax: LIST
