# Projet de développement (idée temporaire à valider encore...) : QuickBorrow - Gestion d'inventaire par QR Code

## Description du projet
L'objectif est de développer une application de gestion de stock et de prêt de matériel pour un laboratoire ou une école (PCs, kits Arduino, capteurs, etc.). 

L'application automatise le suivi en attribuant à chaque objet une étiquette QR Code générée par le système. L'utilisation d'une douchette permet d'identifier instantanément le matériel pour effectuer une sortie ou un retour, éliminant ainsi les erreurs de saisie manuelle.

### Fonctionnalités principales
* **Inventaire dynamique :** Une TableView affiche l'intégralité du matériel, son état actuel (Disponible, Emprunté, En maintenance) et l'identité de l'emprunteur.
* **Génération d'étiquettes :** Un formulaire d'ajout d'objet intègre une librairie pour générer un QR Code unique, exportable en format image pour l'étiquetage physique.
* **Interface de scan :** Une vue dédiée à la douchette traite l'ID scanné pour afficher la fiche de l'objet et permettre une mise à jour rapide du statut.
* **Historique des mouvements :** Un log des transactions permet de consulter les derniers emprunts et retours effectués.

## Architecture et Patterns
Le projet respecte l'architecture **MVC** pour séparer les vues FXML de la logique métier.

* **Persistance :** Les données sont stockées dans une base de données SQLite.
* **Pattern Repository :** Un `ItemRepository` gère un cache via une `ObservableList`. Cela assure une synchronisation immédiate entre la base de données et l'interface JavaFX lors des scans.
* **Configuration :** L'utilisation d'un fichier `.properties` permet de configurer l'URL de la base de données et les chemins d'accès aux ressources sans modifier le code source.

## Multithreading
Le multithreading est utilisé pour maintenir la réactivité de l'interface utilisateur :
1. **Génération de QR Codes :** Utilisation de `Task` JavaFX pour que l'encodage et l'écriture du fichier image ne bloquent pas le thread principal.
2. **Accès aux données :** Les opérations de lecture et d'écriture en base de données sont déportées sur des threads secondaires lors des chargements initiaux ou des mises à jour massives.

## Qualité et Tests
Une couverture de tests supérieure à 80% est prévue sur le modèle :
* **Tests unitaires (JUnit 5) :** Validation des règles de gestion (par exemple, l'impossibilité d'emprunter un objet déjà marqué comme emprunté).
* **Mocks (Mockito) :** Isolation du Repository en simulant le comportement du DAO pour tester la logique de cache de manière indépendante.
* **Isolation :** Utilisation de bases de données en mémoire pour garantir que les tests sont reproductibles et n'affectent pas les données réelles.