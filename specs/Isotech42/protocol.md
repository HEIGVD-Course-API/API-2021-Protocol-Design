### Protocol objectives: what does the protocol do?
L'objectif du protocol est de traduire des opérations arithmétiques entre un client
et un serveur, à travers un réseau TCP/IP.
Les serveurs qui receptionnent les demandes doivent être capable de réaliser, au minimum,
des additions et des multiplications de deux chiffres.
Il peut proposer plus d'opérations telle que la division ou la soustraction, par exemple.
division par exemple.

### Overall behavior:
##### - What transport protocol do we use?
TCP

##### - How does the client find the server (addresses and ports)?
Le client peut faire une requête UDP et le serveur lui répond pour commencer la communication.
Ou le serveur peut avoir une adresse IP fixe et le client connaît dans ce cas l'adresse.

##### - Who speaks first?
Le client fait en premier la demande au serveur. Le serveur attend les demandes.

##### - Who closes the connection and when?
Le client envoie une requête en indiquant qu'il a fini ses demandes.

### Messages:
##### - What is the syntax of the messages?
OPERATION lhs rhs

##### - What is the sequence of messages exchanged by the client and the server? (flow)
1. Le serveur envoit au client les opérations disponibles.
2. Le client envoit l'opération suvivi des deux chiffres.
3. Le serveur lui répond avec le résultat de l'opération.
Puis, on boucle 2. et 3. jusqu'à ce que le client envoit
une requête pour fermer la connexion.

##### - What happens when a message is received from the other party? (semantics)
Le serveur effectue l'opération arithmétique, envoyé par le client, avec
les deux opérandes.

### Specific elements (if useful)
##### - Supported operations
Additions et multiplications au minumum.

##### - Error handling
SERVER:
- Un délais trop long pour une réponse.
- Une erreur/perte de la communication.

CLIENT:
- La syntaxe du message (OPERATION chiffre1 chiffre2).
C-à-d le nombre d'argument, les différents types, etc...
- Opération disponible.

##### - Extensibility  
Rajouter des opérations arithmétiques comme
la soustraction, la division, le modulo,
l'exponentiation, l'extraction de racine ou
l'extraction de logarithme.

##### - Examples: examples of some typical dialogs.
SERVER : Opération disponible : ADD, MUL, SUB, DIV
CLIENT : ADD 2 4
SERVER : RESULT 6
CLIENT : SUB 14 4
SERVER : RESULT 10
CLIENT : MUL one two
SERVER : SYNTAX ERROR
CLIENT : QUIT
SERVER : BYE !