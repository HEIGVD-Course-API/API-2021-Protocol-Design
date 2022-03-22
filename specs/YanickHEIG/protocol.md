# RES/API - Labo 03 - Protocol specifications
Yanick Thomann
---

- Protocol objectives: what does the protocol do?
> The objective of the protocol is to allow a client to connect to a server to make calculation on his behalf. 

- Overall behavior:
  - What transport protocol do we use?
    > We are going to use standard TCP to transport the messages
  - How does the client find the server (addresses and ports)?
    > The client has to know the IP address and port of the server where the compute application is running
  - Who speaks first?
    > The establishes the connection, but a successful connection will be announced to the client by the server by sending a "WELCOME !" message. Then the server prompts user to enter a command.
  - Who closes the connection and when?
    > The client closes the connection manually once he does not want to use it anymore.
- Messages:
  - What is the syntax of the messages?
    > Each message sent to the server must respect the following syntax: <OPERATION> <VALUE1> <VALUE2>
  - What is the sequence of messages exchanged by the client and the server? - (flow)
    > Once the connection is established:
    ```mermaid
    sequenceDiagram
    participant client
    participant server

    server->>client: "WELCOME !"
    server->>client: "PLEASE ENTER COMMAND"
    client->>server: ADD 1 2
    server->>client: "The result of the operation ADD 1 2 is 3"
    server->>client: "PLEASE ENTER COMMAND"
    client->>server: SUB 3 3
    server->>client: "The result of the operation SUB 3 3 is 0"
    server->>client: "PLEASE ENTER COMMAND"
    client->>server: QUIT

    ```
  - What happens when a message is received from the other party? (semantics)
    > The accepted commands are:
    > - ADD <value_1> <value_2>: Asks the server to do an addition of value_1 + value_2 and return the result.
    > - SUB <value_1> <value_2>: Asks the server to do a subtraction of value_1 - value_2 and returns the result.
    > - QUIT : Informs the server that the connection can be closed.
- Specific elements (if useful)
  - Supported operations
    > To keep it simple, the application will only support addition and subtraction.
  - Error handling
    > Errors that will be treated
  - Extensibility
    > Further operation could be implemented for the application, if required.

- Examples: examples of some typical dialogs.
    ```mermaid
    sequenceDiagram
    participant client
    participant server

    server->>client: "WELCOME !"
    server->>client: "PLEASE ENTER COMMAND"
    client->>server: ADD 1 2
    server->>client: "The result of the operation ADD 1 2 is 3"
    server->>client: "PLEASE ENTER COMMAND"
    client->>server: SUB 3 3
    server->>client: "The result of the operation SUB 3 3 is 0"
    server->>client: "PLEASE ENTER COMMAND"
    client->>server: SU 10 8
    server->>client: "UNRECOGNIZED COMMAND"
    server->>client: "PLEASE ENTER COMMAND"
    client->>server: SUB 10 8
    server->>client: "The result of the opration SUB 10 8 is 2"
    client->>server: QUIT

    ```