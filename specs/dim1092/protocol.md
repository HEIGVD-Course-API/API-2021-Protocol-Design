##transport protocol : 
tcp
##How does the client find the server (addresses and ports)?
?

##Who speaks first?
client
##Who closes the connection and when?
client when there are no new calculations

#Messages :
1. client asks for connection
2. server responds connected
3. if connected client sends operation (number and operators separated by spaces), else try again
4. server sends acknowledge
5. client waits...
6. server sends operation result
7. client sends acknowledge
8. repeat steps 3 to 7 as many times as needed
9. client sends close connection request

##Specific elements (if useful)
 suported operatoins : integrer +, -, *, /
 Error handling : respond with error message when / 0 , multiple operators or numbers in a row
 extansibility :  %, ^ (pow)

- client-> yo
- server-> sup
- client-> 1 + 1
- server-> ugh wait
- server-> 2
- client-> thanks
- client-> cheers