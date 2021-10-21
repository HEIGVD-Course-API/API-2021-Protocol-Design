## Objectifs du protocol

Le protocol a pour but de permettre à un client d'envoyer des requêtes de calcules simple à un serveur.
Le serveur devra alors effectuer le calcul et renvoyer le résultat au client.

### Comportement global

- Ce protocol sera basé sur le protocol TCP afin de devoir établir une connexion.
- Le client trouvera le serveur à partir d'une adresse ip fournie par l'utilisateur 
et d'un port prédéfini dans l'application.
- Le client enverra la première requête de connexion.
- Le client fermera la connexion.

### Messages

- La syntaxe du message sera un nombre, un espace, une opération, 
un espace et un autre nombre (exemple: 2 + 3).
- Le client envoi la requête, le serveur répond à la requête, le client ferme la connexion.
- Lorsque un autre partie envoie un message, le serveur le reçoit, traite les deux 
messages en attente puis envoie les deux réponses.

### Eléments spécifique

- Les opérations supportées sont: +, - et / 
- Lorsque une erreur survient le serveur répond par une
chaîne de caractère "error" suivi de la raison de l'erreur.
- L'application sera évolutive car les fonctions seront appelées en fonction
de l'opération du calcul. Il sera donc facile de rajouter des fonctions.

### Exemple de dialogue

**Fonctionne**:

Client: 1 + 3

Serveur: 4

**Erreur**:

Client: 1 *+jfhdkh

Serveur: erreur: invalid syntaxe
