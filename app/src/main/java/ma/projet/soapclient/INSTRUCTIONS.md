# TP 14 SOAP Android Client

Application Android Kotlin utilisant KSOAP2 pour consommer un service web SOAP de gestion de comptes bancaires.

**Auteur:** Halmaoui Abdellah  
**Version:** 2.0  
**Date:** Décembre 2025

## Structure du projet

Le projet suit une architecture modulaire avec séparation des responsabilités :

- **models/** : Modèles de données (Compte, TypeCompte)
- **network/** : Couche de communication réseau (Service SOAP)
- **ui/** : Composants d'interface utilisateur (Adapters)
- **MainActivity.kt** : Activité principale coordonnant l'ensemble

## Fichiers du projet

Ce dossier contient uniquement les fichiers source Kotlin essentiels:

- `models/Compte.kt` - Modèle de données pour les comptes bancaires
- `models/TypeCompte.kt` - Énumération des types de compte (COURANT, EPARGNE)
- `network/Service.kt` - Service de communication SOAP avec le backend
- `ui/CompteAdapter.kt` - Adapter RecyclerView pour l'affichage de la liste
- `MainActivity.kt` - Activité principale de l'application

## Pour un projet Android complet

1. Créer un nouveau projet Android Studio
2. Copier ces fichiers dans le bon package (ma.projet.soapclient)
3. Ajouter les dépendances KSOAP2 dans build.gradle
4. Créer les layouts XML (activity_main, item_compte, popup)
5. Ajouter les permissions dans AndroidManifest.xml

Voir README.md pour les instructions complètes.

## Notes de version 2.0

- Refactorisation de la structure des dossiers (beans → models, ws → network, adapter → ui)
- Amélioration de la documentation Javadoc complète
- Code mieux organisé et plus professionnel
- Commentaires détaillés pour faciliter la maintenance
