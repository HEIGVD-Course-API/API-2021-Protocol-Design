# Protocol objectives: what does the protocol do?

L'objectif du protocol est de traduire des opérations arithmétiques entre un client
et un serveur, à travers un réseau TCP/IP.
Les serveurs qui receptionnent les demandes doivent être capable de réaliser, au minimum,
des additions et des multiplications de deux chiffres.
Il peut proposer plus d'opérations telle que la division ou la soustraction, par exemple.

# Overall behavior
- ***What transport protocol do we use?***  
  TCP

- ***How does the client find the server (addresses and ports)?***  
  Le serveur a un nom connu via DNS. On mettra son adresse IP directement pour ce projet.

- ***Who speaks first?***  
  Le client fait en premier la demande au serveur. Le serveur attend les demandes.

- ***Who closes the connection and when?***    
  Le client envoie une requête en indiquant qu'il a fini ses demandes.

# Messages

## _What is the syntax of the messages?_
- Opération: `op a b ...`
    - `op`: add, mul, sub, div, mod, ...
    - `a b ...`: Les opérandes pour l'opération (le nombre d'opérande est définie par l'opération).
- Résultat: `code result`
    - `code`: Code d'erreur ou de réussite
        - `res`: réussite
        - `err 1`: opération non connue
        - `err 2`: pas le bon nombre d'opérande
        - `err 3`: erreur de syntax
    - `result`: Résultat ou message d'erreur
- Welcome, premier message du serveur après la connexion établie :
  ```
  welcome
  disp op
  name nbOperands
  name nbOperands
  ...
  end
  ```
    - `name`: nom de l'opération (ex. sub)
    - `nbOperands`: nombre d'opérande pour faire l'opération
    - Change opération sont séparées par des CRLF
- Fin de communication.
  `bye`

## _What is the sequence of messages exchanged by the client and the server? (flow)_
1. Le serveur envoit au client les opérations disponibles.
2. Le client envoit l'opération suvivi des deux chiffres.
3. Le serveur lui répond avec le résultat de l'opération.
   Puis, on boucle 2. et 3. jusqu'à ce que le client envoit
   une requête pour fermer la connexion.

## _What happens when a message is received from the other party? (semantics)_
Le serveur effectue l'opération arithmétique, envoyé par le client.

# Specific elements (if useful)

##_Supported operations_:
- Obligatoire:
    - Addition: `add`
    - Multiplication: `mul`
- Exemple d'opération optionnel
    - Soustraction: `sub`
    - Division: `div`
    - Modulo: `mod`

## _Error handling_
- SERVER:
    - Un délais trop long pour une réponse.
    - Une erreur/perte de la communication.
- CLIENT:
    - La syntaxe du message (OPERATION chiffre1 chiffre2).  
      C-à-d le nombre d'argument, les différents types, etc...
    - Opération disponible.

## _Extensibility_
Rajouter des opérations arithmétiques comme
la soustraction, la division, le modulo,
l'exponentiation, l'extraction de racine ou
l'extraction de logarithme.

# Examples: examples of some typical dialogs.

```
C: hello
S: welcome
S: disp op
S: add 2
S: mul 2
S: sub 2
S: div 2
s: neg 1
s: end disp op
C: add 2 4
S: res 6
C: sub 14 4
S: res 10
C: neg 10
S: res -10
C: mul one two
S: err 3 'one' is not a number
C: log 10
S: err 1 'log' is not an operation
C: bye
S: bye
```