# Specification

### Infos générales
Le protocol permet de specifier comment le client et le serveur
peuvent communiquer.

TCP semple un bon protocol à utiliser.

Le client trouve le serveur en parcourant de serveur en 
serveur jusqu'à trouver le bon. ~

Le client commence à parler
Le client ferme le dialogue

### Message syntaxe :
Deux nombres et une opération, le tout séparé par des espaces
1. Le client se présente au serveur, et attend une réponse
2. Le serveur répond, le dialogue peut commencer
3. Le client demande un calcul
4. Le serveur revoit le résultat
5. Le client envoie un acquittement
6. On répète 3 à 5 le nombre de fois désiré
7. Le client interrompt le dialogue


### Element spécifique
On supporte les opérations {+, -, *, /}
On gère la division par zéro
On peut étendre les opérations avec un modulo et puissance

### Exemple
->client hello  
->server hello  
->client 2 + 3  
->server 5  
->client ack  
->client 1 / 0  
->server error   
->client ack  
->client close  