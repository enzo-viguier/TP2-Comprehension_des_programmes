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
Il contient également dans le package behavioral.visitor un exemple d'implémentation du pattern Visiteur qui peut être utilisé pour tester l'application

### 2. Exécution du projet
Pour exécuter l'analyse de votre projet, suivez ces étapes :

1. Ouvrez un terminal ou une invite de commandes.
2. Déplacez-vous dans le dossier `/out/artifacts/` où se trouve le fichier JAR compilé.
3. Lancez le fichier JAR StatsAppOO.jar avec la commande java -jar et passez le chemin vers le dossier de code source à analyser en argument.

Commande d'exécution :
`java -jar .\StatsAppOO.jar "PATH_VERS_LE_DOSSIER_A_ANALYSER"`
Exemple : `java -jar .\StatsAppOO.jar C:/Users/minez/IdeaProjects/AppliCommerce/src/java`

### 3. Résultats
Les différentes statistiques sur le code (nombre de classes, méthodes, lignes de code, etc.) s'afficheront directement dans le terminal et le graphe d'appels sera visualisé dans une fenêtre séparée.





