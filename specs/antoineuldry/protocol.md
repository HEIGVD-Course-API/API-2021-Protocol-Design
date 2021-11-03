# Laboratoire 03 - Protocol-Design - API
Auteur : Antoine Uldry
### Protocol objectives: what does the protocol do?
Le protocole, que je nommerais __ArithmeticCalculator__, est conçu pour soumettre des opérations arithmétiques entre ```client serveur``` (de calcul). Le ```Serveur``` doit au moins pouvoir réaliser des additions et des multiplications. Toutefois il peut aussi permettre d'effectuer d'autres opérations.
## Overall behavior:
Le ```Client``` se connecte au ```Serveur```, le ```Serveur``` indique que la connexion est établie à l'aide d'un message de bienvenue. Le ```Client``` soumet les calculs souhaités et attend la réponse du ```Serveur```. Pendant que le ```Serveur``` effetue une opération, un message d'état apparait chez le ```Client```. Une fois le résultat obtenu, le résultat est envoyé au ```Client```. Finalement, lorsque le ```Client``` n'a plus de calcul à effectuer, il peut terminer la connexion avec une commande.
#### What transport protocol do we use?
Il fonctionne à l'aide du protocole TCP/IP comme protocole de transport.
#### How does the client find the server (addresses and ports)?
Le client peut accéder au serveur par le port 2021.
#### Who speaks first?
Directement après la connexion TCP, le ```Serveur``` souhaite la bienvenue à l'aide d'un message.
#### Who closes the connection and when?
Le ```Client``` ferme la connexion TCP avec une commande lorsque ce dernier n'a plus de calcul à faire.
## Messages:
#### What is the syntax of the messages?
- __WELCOME__ : message de bienvenue après la connexion ```Client-Serveur``` établie
Après le message __WELCOME__ une liste des opérations possibles par le ```Serveur``` sera affichée.
- __RUNNING__ : message de l'opération arithmétique en cours
- __RESULT__ : message du résultat de l'opération arithmétique demandée
- __ERROR__ : message d'errerur si la requête du ```Client``` n'est pas connue
- __QUIT__ : message, ou plutôt commande, que le ```Client``` envoie au ```Serveur``` pour terminer la connexion

Afin d'indiquer la fin d'une ligne, les ```Clients``` et ```Serveurs``` doivent utiliser une séquence particulière. 
La séquence ```CRLF``` (ASCII 13 & ASCII 10), respectivement (Carriage Return & Line Feed).

#### What is the sequence of messages exchanged by the client and the server? (flow)
Exemple de message après connexion : (__$__ indique les commandes du ```Client``` et chaque ligne est terminée par __CRLF__)
```sh
WELCOME
- ADD X Y
- SUB X Y
- MULT X Y
- DIV X Y
$ ADD 2 5
RUNNING ADD 2 5
RESULT 7
$ POW 2 3
ERROR UNKNOWN OPERATION
$ ADD 1 2 3
ERROR TOO MANY ARGUMENTS
$ QUIT
```
#### What happens when a message is received from the other party? (semantics)
## Specific elements (if useful)
#### Supported operations
Il doit être possible d'effectuer une addition et une multiplication
#### Error handling
Un message d'erreur indique le ```Client```si une opération est inconnue ou que trop d'arguments sont passées en paramètre.
#### Extensibility
Il est possible d'ajouter des opérations arithmétiques. 
La liste des opérations qui peuvent être effectuées est listé après la connexion établie.
## Examples: examples of some typical dialogs.
```sh
WELCOME
- ADD X Y
- SUB X Y
- MULT X Y
- DIV X Y
$ ADD 2 5
RUNNING ADD 2 5
RESULT 7
$ POW 2 3
ERROR UNKNOWN OPERATION
$ ADD 1 2 3
ERROR TOO MANY ARGUMENTS
$ QUIT
```