# Protocol pour calculatrice client server
*Auteurs:* Emmanuelle Comte et Fabien Terrani

## Introduction

### Objectifs du protocole
Le protocole défini dans ce document permet à un client d'envoyer des opérations mathématiques à un serveur pour qu'il les réalise et en revoie les résultats.

### Fonctionnement général
Ce protocole utilise le protocole TCP avec le port 1997 définit par défaut. 
Une fois la connexion établie, le serveur envoie un message de bienvenue au client contennant les opérations disponibles. Un serveur **doit** implementer les opération ADD, SUB, et MUL et **peut** implémenter d'autres opérations (comme DIV, MOD,...).
Ensuite, le client peut lui envoyer des opérations avec des opérandes auxquelles le serveur va retourner un résultat (un résultat par opération) ou un message d'erreur.
Quand le client n'a plus d'opérations à envoyer au server, il lui envoie un au revoir.

Le protocole est sans état.

## Syntaxe des messages
Le protocole définit 5 types de messages: WELCOME, des messages d'opérations, RESULT, ERROR and GOODBYYE. C'est un protocole ligne à ligne. 

### WELCOME
Le serveur envoie ce message au client juste après l'établissement de la connexion. Ce message contient la liste des opérations disponible avec le nombre de paramètres que l'opération attend. 

Exemple: 
WELCOME
OPERATIONS: 
    ADD 2
    SUB 2
    MUL 2
END

### Messages de types opération
Message utlisé par le client pour faire une demande de calcule au server. Sauf contre indication, tous les paramètres doivent suivrent le format suivante: 

1. Singe + ou - optionnel
1. une séquence d'au minimum un chiffre décimal
1. une partie fractionnaire commancant par un point et suivi d'une séquence de chiffres décimaux optionnels. 

Les opérations sont définit par un mot clé et un nombre de paramètres séparés par des espaces.

Exemples: 
- ADD +5 3,5
- SUB 3,986 2
- MUL -7 17

### RESULT
Message envoyé par le seveur au client en réponse à un message de type opération dont le traitement s'est bien déroulé. Le nombre résultat suit le même format que les paramètres d'une opération.

Exemple: 
- RESULT 8,5

### ERROR
Message envoyé par le serveur au client si un problème est survenu lors du traitement du drnier message envoyé par le client. Par exemple, faute de syntaxe, opération non connue ou division par zéro.

Exemple: 
- ERROR Division by 0

### GOODBYYE
Message envoyé par le client au serveur pour mettre fin à la connexion. 

Exemple:
- GOODBYYE

## Elements spécifiques

### Les opérations supportées
Le seveur doit au minimum contenire les opération suivante: ADD (2 paramètres, addition), SUB (2 paramètres, soustraction) et MUL (2 paramètres, multiplication). 

### Extensibilité
Le serveur peut implémenter d'autres opérations, comme la division. S'il le fait, il doit toutes les spécifiées dans le message WELCOME envoyé après le début de la connexion.

### Traitement des erreurs
Un message ERROR est envoyé du serveur au client en cas d'erreur de syntaxe, de calcul, de message non reconnu ou de tout autre type d'erreur.

## Exemples

### Communication sans erreur

### Communication avec différentes erreurs 