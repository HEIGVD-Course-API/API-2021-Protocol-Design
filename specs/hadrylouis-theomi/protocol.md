# Objectifs

Ce protocole a pour but d'effectuer les quatre opérations arithémtiques de base sur deux valeurs à virgule.

# Comportement général

- Le protocole de transport utilisé est TCP
  - Ce protocole a pour avantage de garantir que l'information arrive au serveur ou retourne au client.
- L'adresse du serveur dépend du réseau utilisé, le serveur écoute sur toutes les interfaces réseau disponibles
- Le port utilisé est `23000`
  - D'après cette liste des ports les plus connus, ce port n'est pas utilisé pour une application répandue.
- Le serveur est le premier à renvoyer un message
- Le client décide de fermer la connexion lorsqu'il n'a plus de calculs à effectuer.

# Messages

## Introduire un calcul

- La syntaxe d'un calcul est la suivante :

```
<operand1> [+,-,*,/,^] <operand2>
```

## Opérations supportées

- addition
- soustraction
- multiplication
- division
- puissance entière

- Le serveur ne prend pas en compte la priorité des opérations

## Diagramme de séquence

![Diagramme de séquence](Diagramme_Sequence.svg "Diagramme de séquence")

## Séquence d'analyse

Lorsque le serveur reçoit un calcul valide :

- Le serveur analyse caractère par caractère le contenu du calcul
- Tant que la valeur est entière, il stocke cette valeur comme premier opérande
- Dès lors que le caractère n'est plus un entier, il vérifie s'il s'agit d'un opérateur valide (soit `+, -, *, / ou ^`).
- Ensuite, le serveur récupère le deuxième opérande
- Le calcul est ensuite effectué et le résultat retourné
  Si le serveur reçoit un calcul non valide, soit :
- Le premier opérande n'est pas une valeur numérique valide
- L'opérateur n'est pas valide
- Le deuxième o"pérande n'est pas une valeur numérique valide
  - Le serveur renvoie alors une erreur générique ("Calcul non valide")

# Extensibilité

- la gestion du parenthésage pourra être ajoutée afin de prendre en compte la priorité des opérations, ou d'effectuer des calculs plus complexes
- la fonction racine x-ème pourra être ajoutée
- l'erreur renvoyée pourrait être plus détaillée (quel opérande est faux, ou alors si l'opérateur est faux)

# Exemples

## Exemple de calculs corrects

```
nc 192.168.1.2 23000
--- Welcome to the calculator ---
Please input your calculation :

calculator% 3*4+2
Result : 14

calculator% 6-3*0.2
Result : 0.4

calculator% quit
Bye !
```

## Exemple de calculs incorrects

```
nc 192.168.1.2 23000
--- Welcome to the calculator ---
Please input your calculation :

calculator% 3&4
Invalid calculation!

calculator% 3a+2
Invalid calculation!

calculator% 3/0
Are you kidding me?

calculator% quit
Bye !
```
