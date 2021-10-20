# Marzullo Loris & Marengo Stéphane

# Protocol objectives
- Permet à un client de transmettre des opérations mathématiques.
- Permet à un serveur de transmettre le résultat d'une opération transmise par un client.

# Overall behavior
- TCP sur le port 6996.
- Le client envoie un message en broadcast sur le port et le serveur y répond.
- Le serveur parle en premier pour indiquer qu'il est prêt à travailler.
- La connexion est terminée par le client lorsqu'il désire quitter.

# Messages:
READY : ce message permet au serveur d'indiquer au client qu'il est prêt à travailler et 
   indique les opérations qu'il supporte. Le client doit lire ledit message ligne par ligne jusqu'à 
   obtenir 2 retour à la ligne (\n\n).

Exemple:
```
READY
Operations:
- ADD
- SUB
- ...
<- (ceci est un \n)
```

DO : ce message permet au client d'indiquer au serveur quel opération effectuer. Le message 
     est composé du premier opérande, puis de l'opération et enfin le second opérande, le tout 
     séparé par des espaces.

Exemple:
```
DO 2 ADD 3
```
     
RESULT : le serveur transmet le résultat au client, ce dernier étant séparé par un espace.

Exemple:
```
RESULT 5
```

EXIT : le client indique au serveur qu'il termine la connexion avec le serveur.

Exemple:
```
EXIT
```

ERROR : le serveur indique au client qu'une erreur est survenue et ne peut pas traiter 
     l'opération demandée tout en indiquant la raison. Le message est terminée par deux retours à 
     la ligne. Le serveur repasse en mode READY.
     -Exemple: ERROR: Opération non supportée.\n\n

Exemple:
```
ERROR
Opération non supportée
<- (ceci est un \n)
```
 
Flow: READY -> DO -> RESULT -> EXIT 
Semantics: 
- READY: Le serveur attends une commande DO ou EXIT du client.
- DO: Le serveur effectue l'opération demandée si elle est disponible. Le serveur répond avec 
  RESULT si l'opération s'est déroulée correctement ou ERROR dans le cas contraire.
- EXIT: Le serveur ferme la connexion.

# Specific elements (if useful)
## Supported operations
- ADD: addition
- SUB: soustraction
- MULT: multiplication
- DIV: division

## Error handling
Un message ERROR est envoyé par le serveur si:
- la commande demandée n'existe pas
- l'opération souhaitée n'est pas supportée
- les opérandes sont invalides
- l'opération demandée génère une erreur

## Extensibility
Des opérations peuvent facilement être rajoutée, celle-ci étant listées lorsque la connexion est 
établie.
# Examples
```
S: READY
S: Operations:
S: - ADD
S: - SUB
S:
C: DO 1 ADD 2
S: RESULT 3
C: EXIT

S: READY
S: Operations:
S: - ADD
S: - SUB
S:
C: DO 5 NON 1
S: ERROR
S: Opération non supportée
S: 
C: EXIT
```