# Projet d'Analyse de Code Statique

#### Par GAIDO Tristan et VIGUIER Enzo

Ce projet implémente une analyse statique de code Java permettant d'identifier les couplages entre classes et de regrouper celles-ci en modules cohérents.   
L'objectif est de mieux comprendre la structure d'une application et d'en faciliter la maintenance.

## Prérequis
- Java JDK installé

## Instructions pour exécuter le projet

### 1. Télécharger ou cloner le projet
Vous pouvez soit télécharger l'archive ZIP du projet, soit cloner le dépôt Git si disponible. 

Le ZIP contient tout le code source ainsi que toutes les classes compilées.
Il contient également dans le package behavioral.visitor un exemple d'implémentation du pattern Visiteur qui peut être utilisé pour tester l'application, ainsi qu'une rapide implémentation d'un systeme de librairie dans le package library.

### 2. Exécution du projet
Pour exécuter l'analyse de votre projet, suivez ces étapes :

1. Ouvrez un terminal ou une invite de commandes.
2. Déplacez-vous dans le dossier du projet `/TP2-Comprehension_des_programmes/`.
3. Exécutez la commande suivante :

Commande d'exécution :
`java -jar target/StatsAppOO-1.0-SNAPSHOT.jar PATH_VERS_LE_DOSSIER_A_ANALYSER VALEUR_CP(Optionnelle)`
Exemple : `java -jar target/StatsAppOO-1.0-SNAPSHOT.jar src/main/java` pour analyser le propre code de l'application

### 3. Résultats
Le couplage, l'implémentation de l'algorithme de clustering et d'identification de module s'afficheront dans la console. Le graphe de couplage s'affichera dans une fenêtre à part.




