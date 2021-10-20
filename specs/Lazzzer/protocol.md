Jonathan Friedli & Lazar Pavicevic
# CALC Protocol

### Comportement général

Le protocole **CALC** permet à un client d'envoyer des opérations arithmétiques à résoudre vers un serveur. 

#### Quel protocole de transport est utilisé ?
CALC utilise la **Socket API** fournie par Java et repose donc sur le protocole **TCP**.

#### Comment trouver le serveur ?
Le serveur émet sur le port **8069**. Son adresse dépend d'où le client va tenter de le joindre.  
Si on part du principe que le serveur et le client tournent sur la même machine, le serveur est joignable avec les adresses **localhost** ou **127.0.0.1**

#### Qui parle en premier ?
Une fois la connexion établie, le serveur envoie un message de bienvenue qui sert de confirmation de connexion.
Il invite le client à taper l'opération à résoudre.

#### Qui ferme la connexion et quand ?
Le client demande une fermeture de la connexion avec la commande **BYE**. A la réception de cette dernière, le serveur s'exécute.

### Messages

#### Quelle est la syntaxe des messages ?
* Pour le client:  
Il est possible d'envoyer soit un mot-clé soit une opération arithmétique.  
Les mots-clés disponibles sont: **HELP**, **BYE**
* Pour le serveur:  
Il envoie soit un message d'information (de bienvenue ou d'erreur) soit le résultation de l'opération demandée.

#### Quelle est la séquence de messages entre le client et le serveur?
Le serveur accueille le client qui vient de se connecter avec un message de bienvenue. Si le client envoie une opération, le message renvoie le résultat.
Si le client envoie une commande, le serveur répond avec le message correspondant. Si le client envoie une mauvaise requête, le serveur transmet un message d'erreur.

### Elements spécifiques

#### Opérations supportées  
Le serveur envoie au début de la connexion, la liste des opérations supportées. Si l'utilisateur tape "HELP", on lui envoie la liste des opérations supportées ainsi qu'un example.

#### Erreur
Si l'opération ou la commande tapée par le client n'est pas valide, il y a un message d'erreur.

### Exemples d'intéractions

Exemple de diaglogue:
```
Server >
Welcome, you are now connected with our calculation server.
Type "HELP" to see example of commands.
Existing operations :
+ (addition)
- (substraction)
* (mutiplication)
/ (division)

Client >
HELP

Server >
Existing operations :
+ (addition)
- (substraction)
* (mutiplication)
/ (division)
Example: to execute an addition, type "1 + 2".

Existing commands:
BYE : Terminate the connection.

Client >
1 +

Server >
Error: invalid operation.

Client >
3 * 7

Server >
21

Client >
bye

Server >
Error: invalid operation.

Client >
BYE

Server >
Bye. End of connection.
```