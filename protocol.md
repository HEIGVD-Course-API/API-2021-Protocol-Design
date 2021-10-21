# Protocol specifications

## Protocol objectives: what does the protocol do?

### Overall behavior

* What transport protocol do we use?
  * TCP
* How does the client find the server (addresses and ports)?
* Who speaks first?
  * The client opens the connection and the server responds with a greeting message.
    * supported operation
    * syntax
* Who closes the connection and when?
  * The server can close the connection after a timeout.
  * The client can close the connection on demand.

### Messages:
* What is the syntax of the messages?
  * Client : 4 possibilities
    * `OP NUMBER`
    * `OP FUN NUMBER`
    * `NUMBER`
    * close message
  * Server : 4 possibilities
    * greeting
    * current value
    * error message
    * close message
* What is the sequence of messages exchanged by the client and the server? (flow)

```
S - "Hello ! Supported commands : ... current value is :"
S - "0"
C - "40"
S - "40"
C - "/ 3"
S - "13.33"
C - "+ 0.67"
S - "14"
C - "-90"
S - "-90"
C - "- sqrt 100"
S - "-100"
C - "CLOSE"
S - "BYE"
```

* What happens when a message is received from the other party? (semantics)
  * The server executes the mathematical operation and responds with the result.
  * The client does nothing. He just  sends messages and receive answers.

### Specific elements (if useful)
* Supported operations
  * basics operations : +, -, *, /
  * Functions : sin, cos, tan, sqrt, power
* Error handling
  * On error, the server save the current value
  * Syntax : 
```
ERR: [code] [message]
The current value is : ...
```

* Extensibility
  * It were possible to save variables
  * It were possible to define default constants like PI, gravity ...
### Examples: examples of some typical dialogs.
cf flow, see above