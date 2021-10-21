# Our Custom Calculator Protocol

## Overall behavior:
Basically, the client send a request containing a calculation to the server who will process it and send back the answer.  
### What transport protocol do we use?
As our service is not meant to be really fast, and there won't be that much requests, I'll use TCP.
### How does the client find the server (addresses and ports)?
The client needs to connect to a specific ip adress(localhost) and a specific port(1337).
### Who speaks first?
The client must speak first.
### Who closes the connection and when?
The server closes the connection after giving a result to the client
