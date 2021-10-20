## Specifications
### Overall behavior

- Nous allons utiliser le protocole de transport TCP.
- Le client peut trouver le serveur grâce à son adresse IP et au port qu'il utilise.
- Le client fait la première requête au serveur et ouvre ainsi la connexion.
- Le client ferme lui-même la connexion, ce qui lui permet de faire plusieurs requêtes de calculs au serveur sans devoir
refaire à chaque fois la procédure de connexion.

### Messages
- Syntaxe : tous les messages du client doivent être écrit en majuscule.
- Séquence de message :
```
>HELLO
[OK] ADD or MULT?
>ADD
[OK] What is your fisrt number?
>3
[OK] What is your second number?
>6
[OK] The result is 9.
ADD or MULT?
>MULT
[OK] What is your first number?
>4
[OK] What is your second number?
>3
[OK] The result is 12.
ADD or MULT?
>END
```
- Sémantique :
    - Connexion = HELLO
    - Choix de l'opération = ADD pour addition ou MULT pour multiplication
    - Fin de la connexion = END

### Specific elements
- Opérations supportées :
    - HELLO
    - ADD
    - SOUS
    - 'nombre' (peuvent être signés)
    - END
- Gestion des erreurs :
    - [OK] signifie que la requête a été acceptée.
    - [ERR] signifie qu'il y a eu un problème avec la requête.
            Par exemple : problème de syntaxe ou commande inexistante.
            S'il y a une erreur, le potocole reprend son exécution sans interrompre la connexion.

### Exemple 
```
>HELLO
[OK] ADD or MULT?
>SOUS
[ERR] Bad syntax
>MULT
[OK] What is your first number?
>4
[OK] What is your second number?
>hihi
[ERR] Bad syntax
>3
[OK] The result is 9.
ADD or MULT?
>END
```