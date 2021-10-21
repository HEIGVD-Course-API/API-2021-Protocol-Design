# Protocol

## Objectives

The goal of the protocol is to define implement
rules server and client side.

## Overall behavior

We use TCP as a transport protocol.
The server listens on port 3003 and wait for requests.
The server address depends on the machine which is running the service.
To establish a connection, the client simply make a request to the server.
The same behaviour applies for closing the
connection.

## Messages

Once the connection is established, the client sends the
arithmetic to compute, which has this format:
`<operation> <n> <m>`. The following operations are allowed:
- `add` (addition)
- `mul` (multiplication)

Once received, the server executes the operation and returns the
result to the client using the following format:
`0 <n>`

## Specific elements

### Error handling
If the arithmetic to compute is invalid, e.g. `add 2`, the server
returns the following message: `1`.

#### Error codes

- `1` : Invalid operation.

### Extensibility

More operations and error codes can be added.

### Examples

#### Example 1

| Sent by | Message     | Received by |
|:--------|:-----------:| ------------:|
| client  | `add 1.5 2` | server |
| server  | `0 3.5`     | client |

#### Example 2

| Sent by | Message   | Received by |
|:--------|:---------:| ------------:|
| client  | `mul 0.6` | server |
| server  | `1`       | client |