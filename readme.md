# QuickBorrow - Gestion d'Inventaire par QR Code

## 1. Description des besoins
L'objectif est de développer une application robuste pour la gestion du stock et du prêt de matériel technique (PCs, kits Arduino, capteurs, etc.) au sein d'un laboratoire ou d'une école.

**Problématique :** La saisie manuelle lors des emprunts est source d'erreurs, de pertes de données et de lenteur administrative.
**Solution :** Automatiser le suivi via des étiquettes QR Code uniques et une douchette USB (D5100Y). L'application permet :
* L'enregistrement du matériel et des emprunteurs (étudiants/profs).
* La génération et l'exportation de QR Codes d'identification.
* Le scan rapide pour effectuer un prêt ou un retour instantané.
* Une navigation intuitive via un menu centralisé (Dashboard).

---

## 2. Modèle de Données (Schéma SQL)
La base de données **SQLite** est structurée autour de trois tables principales pour assurer l'intégrité référentielle et un historique complet :

### Table `items` (Le Matériel)
* `id` (PK, String) : Identifiant unique contenu dans le QR Code.
* `name` (String) : Nom de l'objet.
* `category` (String) : Type de matériel (ex: Microcontrôleur, Accessoire).
* `status` (String) : État actuel (AVAILABLE, BORROWED, MAINTENANCE).
* `qr_path` (String) : Chemin local vers l'image du QR Code généré.

### Table `borrowers` (Les Emprunteurs)
* `id` (PK, Integer) : ID unique de l'étudiant ou du professeur.
* `first_name` (String) : Prénom.
* `last_name` (String) : Nom de famille.
* `email` (String) : Adresse de contact pour les relances.

### Table `loans` (Journal des Emprunts & Logs)
* `id` (PK, Auto-increment) : ID unique de la transaction.
* `item_id` (FK) : Référence à l'objet concerné.
* `borrower_id` (FK) : Référence à la personne qui emprunte.
* `date_out` (DateTime) : Date et heure de sortie.
* `date_in` (DateTime, Nullable) : Date et heure de retour (clôture du log).

---

## 3. Architecture Logicielle & Diagramme de Classes
L'application respecte les principes **SOLID** et l'architecture **MVC** pour garantir une séparation nette entre l'interface et la logique métier.

### Organisation du Projet :
* **`Model`** : Classes `Item`, `Borrower` et `Loan` (objets de données).
* **`Repository`** : 
    * `ItemRepository` (Interface) / `SQLiteItemRepository` (Implémentation).
    * `BorrowerRepository` & `LoanRepository`.
    * Gestion d'une `ObservableList` servant de cache pour une mise à jour immédiate de l'UI.
* **`Controller`** : Un contrôleur dédié pour chaque vue FXML (Home, Inventory, Scan, etc.).
* **`Service`** : 
    * `QRService` : Génération asynchrone des QR Codes (librairie ZXing).
    * `NavigationService` : Centralisation des changements de scènes JavaFX.
* **`Util`** : `DatabaseManager` (Singleton) pour la gestion de la connexion JDBC et lecture du fichier `.properties`.

---

## 4. Interfaces FXML (Vues principales)
L'interface graphique est décomposée en cinq modules ergonomiques :
1.  **Home View (Menu) :** Tableau de bord central avec boutons de navigation rapide vers les différents services.
2.  **Inventory View :** TableView interactive permettant de visualiser et gérer l'intégralité du stock.
3.  **Scan View :** Interface dédiée à la douchette pour un traitement "mains libres" des emprunts et retours.
4.  **Borrower View :** Gestion simplifiée de l'annuaire des utilisateurs.
5.  **History/Logs View :** Journalisation complète permettant de tracer chaque mouvement de matériel.

---

## 5. Plan de Tests (JUnit 5 & Mockito)
Une couverture de tests supérieure à **80%** est ciblée sur le modèle et la logique métier :
* **Tests de Règles Métier :** Validation des états (ex: impossible d'emprunter un objet en maintenance).
* **Tests de Persistance :** Utilisation d'une base de données **H2 (In-Memory)** pour garantir des tests isolés et reproductibles.
* **Tests de l'UI (Mocking) :** Utilisation de **Mockito** pour simuler les repositories et tester la réactivité des contrôleurs sans dépendance à la base de données.

---

## 6. CALENDRIER HEBDOMDAIRE (TEMPORAIRE ET FLEXIBLE)

| Semaine | Équipier A (Data & Architecture) | Équipier B (UI & Services) |
| :--- | :--- | :--- |
| **S1** | Analyse, UML, Création du schéma SQL. | Maquettes FXML (Home & Navigation). |
| **S2** | Implémentation des Repositories (CRUD). | Intégration ZXing & génération de QR. |
| **S3** | Multithreading & Gestion du Cache. | Finalisation UI & Bindings de données. |
| **S4** | Tests Unitaires & Mockito (> 80%). | Documentation finale, Vidéo & README. |

---

## 7. Configuration Technique & Installation
* **Gestionnaire de dépendances :** Maven.
* **Système de Modules :** Fichier `module-info.java` configuré pour l'accès aux modules `javafx.fxml` et `java.sql`.
* **Persistance :** SQLite (fichier de base de données inclus dans les ressources).
* **Installation :**
    1. `mvn clean install`
    2. `mvn javafx:run`

---

## 8. Suivi et Bugs
* **Bugs connus :** Aucun pour le moment.
* **Écarts analyse/implémentation :** (À compléter à la fin du projet).