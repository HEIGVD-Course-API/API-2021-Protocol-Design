1) Le protocole réuni toutes les informations nécessaires
à la communication entre le client et le server. Dans le but
d'offrir la possibilité de faire des calculs.

2) C'est le server qui parle en premier pour demander 
d'établir la connection avec le server. Il
envoie la liste des operations supportés. Opérations possibles : +, -, *, / 
nombre d'opérande nécessaire est deux. C'est le client 
qui termine la connection grace au message QUITTER. Le 
protocole de transport est TCP. 
Adresse IP : 121.10.122.130
Choix d'un port fixe par défaut : 3131
L'état est STATELESS le server n'a pas besoin de mémoriser les
données sur un client.

4) Les messages définis sont : HELLO, CALCUL, RESULTAT, 
ERREUR, QUITTER
Sequence de message : 
   1) HELLO envoyé par le server après que la connection 
   soit établit et est suivi par la liste des opérations possibles.
   2) CALCUL envoyé par le client et doit être suivi de deux opérandes 
   et un opérateur.
   3) RESULT envoyé par le server, calcul et renvoie la solution 
   4) ERREUR message erreur si l'opérateur est pas reconnu 
   ou si le nombre opérande n'est pas correcte.
   5) QUITTER permet de fermer la connection entre le server et le client

5) exemple d'échange de message
   1) HELLO liste des operations possibles (server)
   2) CALCUL 2 * 3 (client)
   3) RESULT 6 (server)
   4) CALCUL 3 % 2
   5) ERREUR "cet opérateur n'est pas supporté" (server)
   6) QUITTER (client)
   
6) Elements spécifiques
   1) operations supportées : + * - /
   2) Il y a deux types d'erreurs possibles :
   "le nombre d'opérande n'est pas correcte" et 
   "cet opérateur n'est pas supporté"
   3) Il est possible pour un server d'ajouter des 
   opérateurs 