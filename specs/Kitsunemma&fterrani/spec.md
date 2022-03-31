# Protocole pour calculatrice client-serveur
*Auteurs:* Emmanuelle Comte et Fabien Terrani

## Introduction

### Objectifs du protocole
Le protocole défini dans ce document permet à un client d'envoyer des opérations mathématiques à un serveur pour qu'il les réalise et en renvoie les résultats.

### Fonctionnement général
Ce protocole utilise le protocole TCP avec le port par défaut 1997. 

Une fois la connexion établie, le serveur envoie un message de bienvenue au client contenant les opérations disponibles. Un serveur **doit** implémenter les opération `ADD`, `SUB`, et `MUL` et **peut** implémenter d'autres opérations (comme `DIV`, `MOD`...).

Ensuite, le client peut envoyer des opérations à effectuer (et accompagnées de paramètres) au serveur. Le serveur va calculer le résultat de l'opération et retourner un résultat (un par opération) ou un message d'erreur.

Quand le client n'a plus d'opérations à envoyer au serveur, il lui envoie un au revoir.

Le protocole est sans état.

## Syntaxe des messages
Le protocole définit 5 types de messages: `WELCOME`, des messages d'opérations, `RESULT`, `ERROR` and `GOODBYYE`.

Le protocole utilise le jeu de caractères ASCII. La plupart du temps, une ligne contient un et un seul message. Cependant certains messages (comme le message `WELCOME`) s'étendent sur plusieurs lignes. 

### `WELCOME`
Le serveur envoie ce message au client juste après l'établissement de la connexion. Ce message contient la liste des opérations disponibles avec le nombre de paramètres que chaque opération attend et se termine par `END`.

Exemple: 
```
WELCOME
OPERATIONS: 
    ADD 2
    SUB 2
    MUL 2
END
```

### Messages de types opération
Message utilisé par le client pour faire une demande de calcul au serveur. Sauf contre-indication, tous les paramètres **doivent** suivre le format suivant : 

1. (*Optionnel*) Signe `+` ou `-`
1. une séquence d'au minimum un chiffre décimal
1. (*Optionnel*) une partie fractionnaire commençant par un point `.` et suivie d'une séquence de chiffres décimaux 

Les opérations sont définies par un mot-clé et un nombre de paramètres séparés par des espaces.

Exemples: 
- `ADD +5 3.5`
- `SUB 3.986 2`
- `MUL -7 17`

### `RESULT`
Message envoyé par le serveur au client en réponse à un message de type opération dont le traitement s'est bien déroulé. Le nombre résultat suit le même format que les paramètres d'une opération.

Exemple: 
- `RESULT 8.5`

### `ERROR`
Message envoyé par le serveur au client si un problème est survenu lors du traitement du dernier message envoyé par le client. Par exemple une faute de syntaxe, une opération non connue ou une division par zéro.

Exemple: 
- `ERROR Division by 0`

### `GOODBYYE`
Message envoyé par le client au serveur pour mettre fin à la connexion.

Exemple:
- `GOODBYYE`

## Éléments spécifiques

### Les opérations supportées
Le serveur doit au minimum contenir les opérations suivantes: `ADD` (2 paramètres, addition), `SUB` (2 paramètres, soustraction) et `MUL` (2 paramètres, multiplication). 

### Extensibilité
Le serveur peut implémenter d'autres opérations, comme la division. S'il le fait, il doit toutes les spécifier dans le message `WELCOME` envoyé après le début de la connexion.

### Traitement des erreurs
Un message `ERROR` est envoyé du serveur au client en cas d'erreur de syntaxe, de calcul, de message non reconnu ou de tout autre type d'erreur.

## Exemples

### Communication sans erreur

```
S: WELCOME
S: OPERATIONS:
S:     ADD 2
S:     SUB 2
S:     MUL 2
S:     DIV 2
S: END
C: ADD 2 3
S: RESULT 5
C: SUB +3.234 7
S: RESULT -4.234
C: MUL 7 43
S: RESULT 301
C: DIV 45 5
S: RESULT 9
C: GOODBYYE
```



### Communication avec différentes erreurs 

```
S: WELCOME
S: OPERATIONS:
S:     ADD 2
S:     SUB 2
S:     MUL 2
S:     DIV 2
S: END
C: CLAFOUTIS
S: ERROR Unknown operation
C: ADD 5
S: ERROR Invalid number of parameters for operation ADD (2 expected, 1 provided)
C: DIV 3 0
S: ERROR Division by zero
C: GOODBYYE
```