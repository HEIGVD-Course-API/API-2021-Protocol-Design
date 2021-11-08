# Damien Maier - Nelson Jeanrenaud
# ACI - API Calculator Protocol

## Comportement général
* Le protocole utilise TCP. Le serveur attend des connexions sur le port 7589.
* Le client peut envoyer des requêtes. Pour chaque requête, le serveur envoie exactement une réponse.
* Pour terminer la connexion, le client envoie une commande de fermeture. Après avoir reçu cette commande, le serveur ferme la connexion TCP.

## Messages
* Le client envoie des **requêtes**. Quand le serveur reçoit une requête, il répond avec une **réponse**.
### Requête
* Une requête est faite d'une **commande** suivie d'un nombre indéterminé d'**arguments**.
* Une commande est faite de trois caractères en majuscule.
* La commande et chaque argument sont séparés par exactement un espace.
* Une requête se termine par un caractère de retour à la ligne.
* Les commandes possibles sont :
  * ADD, SUB, MUL, DIV
    * Chacune de ces commandes peut prendre un nombre indéterminé d'arguments. Ces arguments doivent être des nomnbres.
  * CLO
    * Cette commande ne prend pas d'argument. Elle sert à demander au serveur de terminer la connexion.
* Un nombre passé en argument doit être :
  * Composé d'un ou plusieurs chiffres
  * Éventuellement précédé du caractère "-", pour indiquer un nombre négatif
  * Un caractère "." peut être présent à n'importe quelle position, excepté devant le "-".
### Réponse
* Une réponse est faite d'un **code de statut** suivi d'un espace, éventuellement suivi d'un **contenu** et se termine par un caractère de retour à la ligne.
  * Un code de statut est fait de trois caractères en majuscule.
  * Les codes de statut possibles sont :
    * RES pour indiquer le résultat d'un calcul
    * ERR pour indiquer une erreur
    * CLO pour acknowledge la demande de fermeture du client
  * Dans le cas de RES, le contenu est exactement un nombre qui suit le même format qui décrit ci-dessus.
  * Dans le cas de ERR, le contenu est un texte donnant des informations sur l'erreur.
  * Dans le cas de CLO, il n'y a pas de contenu.
## Exemples
Client : ADD 10 20 4

Serveur : RES 34

Client : SUB 100 10 5

Serveur : RES 85

Client : AAA

Serveur : ERR bad request

Client : CLO

Serveur : CLO
