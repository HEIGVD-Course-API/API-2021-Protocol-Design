Specs MaximGY
=============

Connexion
---------

Le serveur écoute sur le port 42069 à son adresse locale.

Description du protocol
-----------------------

Le client initie une connexion.
Le serveur répond confirmant la bonne ouverture du canal de communication, style "WELCOME".

Le client peut ensuite envoyer des instructions au serveur suivant la convention suivante:

`{OPE} {NO1} {NO2}`

où `OPE` est un code à 3 lettres représentant l'opération désirée et `NOX` les opérandes de cette opération.

Pour s'éviter de trop complexifier la chose, on peut implémenter `ADD` et `MUL` comme opération et ne supporter que des
entiers positifs pour `NOX`.