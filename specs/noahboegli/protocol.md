# Calculator protocol

## Objectives
This protocol defines the possible operations as well as their syntax in the calculator protocol.

## Overall behavior
The protocol uses the TCP transport protocol over port 42069. The client speaks first when connecting, and then the
server acknowledges that it is ready to perform arithmetic. Then the client simply sends an operation to compute and the
server answers with the result. When disconnecting gracefully, the client should send a disconnection message. The
server may also cancel the connection gracefully at any time by sending a disconnection message.

## Message syntax

| Message                 | Syntax                                                      |
|-------------------------|-------------------------------------------------------------|
| Client hello            | HELLO                                                       |
| Server hello            | LISTENING                                                   |
| Client binary operation | OPERATION BIN (ADD/SUB/DIV/MUL/EXP/MOD)  `[0-9]* \n [0-9]*` |
| Client unary operation  | OPERATION UNA (INV)  `[0-9]*`                               |
| Result                  | `[0-9]*`                                                    |
| Client disconnect       | GOODBYE                                                     |
| Server disconnect       | GOODBYE                                                     |

## Error handling
If an error is detected by the server, it will reset to a state where it is waiting for an operation