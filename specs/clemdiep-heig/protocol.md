# Protocol objectives: what does the protocol do?

Le protocol permettra de faire des calculs de type `lhs op rhs`
via une connection TCP/IP.

# Overall behavior

- _What transport protocol do we use?_ `TCP`
- _How does the client find the server (addresses and ports)?_  
Le serveur a un nom connu via DNS. On mettra son adresse IP directement pour ce projet.
- _Who speaks first?_  
C'est le client qui initie la connection.
- _Who closes the connection and when?_  
Le client, quand il le décide.

# Messages

- _What is the syntax of the messages?_
  - Opération:
  `op lhs rhs`
    - `op`: add, sub, div, mul, mod, ...
    - `lhs` et `rhs`: Les deux nombres.
    - Si besoin, le `rhs` peut ne pas être défini.
  - Résultat:  
  `code result`
    - `code`: Code d'erreur ou de réussite
      - 0: réussite
      - 1: opération non connue
      - 2: erreur de syntax
    - `result`: Résultat ou message d'erreur
  - Fin de communication.
  `bye`
- _What is the sequence of messages exchanged by the client and the server? (flow)_  
Le client demande une opération, le serveur lui répond et ainsi de suite, 
jusqu'à ce le client dise `bye`.
- _What happens when a message is received from the other party? (semantics)_  
Si la syntax est correct, fait le calcul  
Sinon renvoie un message en fonction de l'erreur  

# Specific elements (if useful)

- _Supported operations_: 
  - Addition: `add`
  - Soustraction: `sub`
  - Multiplication: `mul`
  - Division: `div`
  - Modulo: `mod`
- _Error handling_  
Les erreurs sont informée via un code d'erreur.
- _Extensibility_  
Comme le premier élément est l'opération, on peut facilement en ajouter des nouvelles
même si elle ont besoin de plus ou moins d'opérande.

# Examples

```
Client : add 5 105
Serveur: 0 110
Client : sub 9 20
Serveur: 0 -11
Client : mzl 10 3
Serveur: 1 mzl is not an operation.
Client : mul 1o 3
Serveur: 2 1o is not a number.
Client : mul 10 3 4
Serveur: 2 Too much operand.
Client : bye
```
