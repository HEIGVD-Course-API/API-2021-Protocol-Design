
Protocol objectives: what does the protocol do?
 - le protocol permet de demander à un serveur d'effectuer des calculs. 
   Il doit supporter au minimum l'addition et la multiplication.
Overall behavior:
- What transport protocol do we use?
	- nous allons utiliser TCP comme protocol de transport
- How does the client find the server (addresses and ports)?
	- l'adresse sera le localhost et le port 1024
- Who speaks first?
	- le serveur
- Who closes the connection and when?
    - le client

Messages:
- What is the syntax of the messages?
    - BIENVENU
        - message envoyé par le serveur au client qui permet d'afficher les calculs supportés. Exemple : BIENVENU
    - CALCUL
        - message envoyé par le client vers le serveur qui permet d'effectuer les calculs, il doit être composé de 2 nombres et 1 opérateur. 
          Ils sont tous séparés d'un espace. Exemple : CALCUL 2 * 3 
    - RESULT
        - message envoyé par le serveur au client qui renvoie le résultat. RESULT 6
    - ERREUR
        - message envoyé par le serveur au client qui renvoie un message d'erreur avec un numéro d'erreur. ERREUR 404 FORMAT INVALIDE
    - QUITTER
        - message envoyé par le client au serveur qui permet d'arrêter la connexion entre le client et le serveur
- What is the sequence of messages exchanged by the client and the server? (flow)
    - BIENVENU -> CALCUL -> RESULT -> RESULT -> ERREUR -> QUITTER
	   
- What happens when a message is received from the other party? (semantics)
    déjà explicité au point 2.1

Specific elements (if useful)
- Supported operations
    + * au minimum
- Error handling
    si le format envoyé est erroné (code 100)
    si l'opération n'est pas supportée (code 200)
	
- Extensibility
    supporter plus d'opération, comme la soustraction et la division. (attention pour la division rajout de la division par 0)
	
Examples: examples of some typical dialogs.

Exemple 1:
serveur -> BIENVENU -> client
client -> CALCUL 2 * 3 -> serveur
serveur -> RESULT 6 -> client
client -> CALCUL 2 + 1 -> serveur
serveur -> RESULT 3 -> client
client -> QUIT -> serveur

Exemple 2:
serveur -> BIENVENU -> client
client -> CALCUL 2 +1 -> serveur
serveur -> ERREUR 100 MAUVAIS FORMAT -> client
client -> CALCUL 2 + 1 -> serveur
serveur -> RESULT 3 -> client
client -> QUIT -> serveur

Exemple 3:
serveur -> BIENVENU -> client
client -> CALCUL 2 / 1 -> serveur
serveur -> ERREUR 200 OPERATION PAS GEREE -> client
client -> QUIT -> serveur

Exemple 4:
serveur -> BIENVENU -> client
client -> CALCUL 2 / a -> serveur
serveur -> ERREUR 100 MAUVAIS FORMAT -> client
client -> QUIT -> serveur

