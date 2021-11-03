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
[OK] Welcome. 
HELP MENU
Syntax : OP N1 N2
Supported operations :
- ADD
- MULT
To end the connexion, enter END
To print the help menu, enter HELP
Enter your calcul.
>ADD 2 3
[OK] The result is 5.
Enter your calcul.
>MULT 4 5
[OK] The result is 20.
Enter your calcul.
>END
```
- Sémantique :
    - Ouvrir connexion = HELLO
    - Choix de l'opération = ADD pour addition ou MULT pour multiplication,
      suivi de 2 nombres entiers. Les nombres peuvent être signés.
    - Fin de la connexion = END
    - Afficher le menu d'aide = HELP

### Specific elements
- Opérations supportées :
    - HELLO
    - ADD N1 N2
    - MULT N1 N2
    - END
    - HELP
- Gestion des erreurs :
    - [OK] signifie que la requête a été acceptée.
    - [ERR] signifie qu'il y a eu un problème avec la requête.
            Par exemple : problème de syntaxe ou commande inexistante.
            S'il y a une erreur, le potocole reprend son exécution sans interrompre la connexion.

### Exemple 
```
>HELLO
[OK] Welcome. 
HELP MENU
Syntax : OP N1 N2
Supported operations :
- ADD
- MULT
To end the connexion, enter END
To print the help menu, enter HELP
Enter your calcul.
>SOUS 3 4
[ERR] Bad syntax
>ADD 2 hihi
[ERR] Bad syntax
>HELP
HELP MENU
Syntax : OP N1 N2
Supported operations :
- ADD
- MULT
To end the connexion, enter END
To print the help menu, enter HELP
>ADD 5 4
[OK] The result is 9.
Enter your calcul.
>END
```