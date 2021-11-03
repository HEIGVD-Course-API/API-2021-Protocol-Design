#Calculator Protocol

##Introduction

###Objectif

Le but de ce protocol est de mettre en place un serveur qui permet à un client de faire des requêtes afin des pouvoir
effectuer des operations. Cela est fait à travers d'un réseau TCP/IP. Le serveur doit être capable de réaliser 
des additions et des multiplications, voir ajouter par la suite d'autres operations de calcul. Les opérations fourni 
par le serveur sont fourni au client suite à la connexion.

###Fonctionnement

Ce protocol utilise TCP pour le transport. Le port 2021 a été choisi comme port par défaut. Après avoir reçu et accepté 
une demande de connexion de la part du client, le serveur donne la liste des opérations disponibles.

Le client lors de chaque opération, envoie un message COMPUTE qui est accompagné de l'opération choisi ainsi que deux 
opérandes. Si le message est correct, le serveur effectue le calul demandé et renvoie un message avec le résultat. 
À l'inverse, si le message est erroné le serveur envoie un message ERROR.

C'est le client qui doit fermer la connection avec le message QUIT.

### Messages

La syntaxe des messages est {HELLO, COMPUTE, RESULT, ERROR, QUIT}. Chaque message du client doit être fini avec un END 
pour indiquer la fin de la ligne.

####HELLO

Ce message est envoyer par le serveur au client après la connection grâce à TCP. Ce message affiche les opérations 
disponibles. Chaque ligne des operations disponibles est composées de son nom du nombre operande à fournir et un END.

####COMPUTE

Ce message est envoyé au serveur par le client suivi de l'opération qu'il souhaite effectuer avec les 2 opérandes puis 
un END pour finir la ligne

####RESULT

Ce message est envoyé par le serveur au client afin de lui retourner le résultat obtenu suite à l'opération voulue.

####ERROR

Ce message est envoyé par le serveur au client, dans le cas où un message envoyé par le client n'est pas la bonne syntaxe


####QUIT

Ce message est envoyé par le client au serveur pour fermer la connection entre les deux et terminer la session en cours.

##Éléments spécifiques

###Opérations supportées

Les opérations supportées pour le moment sont ADD et MULT. Le premier permet d'effectuer une addition et le deuxième 
des multiplications.

###Extensibilité

Chaque serveur peut implémenter des opérations supplémentaires  par la suite. Celle-ci s'afficheront dans la liste des 
opérations après le message HELLO.

####Traitement des erreurs

Si le client envoie un message erroné, le serveur doit lui envoyer un message ERROR lui indiquant ce qui est faux dans 
sa requête de calcul. ça sera soi (UNKNOWN OPERATION) soi (SYNTAX ERROR).

##Exemple

S: HELLO END

S: - ADD 2 END

S: - MULT 2 END

C: ADD 2 2 END

S: RESULT 4 END

C: MULT 2 5 END

S: RESULT 10 END

C: DIV 10 2 END

S: ERROR UNKNOWN OPERATION END

C: ADD 1 2 4 END

S: ERROR SYNTAX ERROR END

C: QUIT END
