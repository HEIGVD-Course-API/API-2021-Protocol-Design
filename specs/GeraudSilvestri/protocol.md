# Overall behavior

### What transport protocol do we use?
On utilise le protocole TCP, le client doit pouvoir envoyer un calcul au serveur et en recevoir la réponse. La perte éventuelle de données doit être rensigné au client / serveur.

### How does the client find the server (addresses and ports)?
Le serveur sera ouvert sur un port défini à l'avance, ainsi que le client 

### Who speaks first?
Ca sera le client qui enverra une requête au serveur et le serveur lui ne fera qu'attendre une éventuelle demande de clients.

### Who closes the connection and when?
La fermeture viendra du coté client. Il enverra une requête "close" au serveur. Ledit serveur recevant cette requête fermera la connexion de son coté. Le client attendra la confirmation de réception du serveur afin de s'assurer qu'il n'y ai de problèmes au niveau de la fermeture.

# Messages

### What is the syntax of the messages?
OPE 1+2 END
EXI END
EXS END (exit success)
COE END (command error)
RES END
CAE END (calcul error)

### What is the sequence of messages exchanged by the client and the server? (flow)
C - OPE END
S - RES ... / COE END
...
C - EXI END
S - EXS END

### What happens when a message is received from the other party? (semantics)
SERVER :
        Reçoit une commande, check si la commande est valide à l'aide d'un switch. Si la commande est invalide, le serveur renvoie un message de type COE. Si la commande est valide, le serveur effectue la commande spécfiée.

CLIENT :
        Vérifie quelle commande a été envoyée par le serveur via un switch puis s'il s'agit d'un RES ou une erreur, le client attend une nouvelle entrée utilisateur, sinon s'il reçoit un EXS le client ferme sa connection avec le serveur.

### Specific elements (if useful)
    Supported operations
    Addition / Soustraction

    Extensibility
    Facilité pour ajouter un nouveau message, juste l'ajouter dans un switch

### Examples: examples of some typical dialogs.
C - OPE 5+12*8 END
S - RES 101 END
C - OPE A+2 END
S - CAE END
C - EXI END
S - EXS END