# TP 14 - Client Android SOAP pour Gestion des Comptes Bancaires

## Vue d'ensemble / Overview

Application Android native développée en Kotlin qui consomme un service web SOAP pour la gestion des comptes bancaires. L'application permet d'afficher, ajouter et supprimer des comptes via une interface utilisateur moderne utilisant Material Design.

A native Android application developed in Kotlin that consumes a SOAP web service for bank account management. The app allows displaying, adding, and deleting accounts through a modern user interface using Material Design.

**Auteur / Author:** Halmaoui Abdellah
**Date:** Décembre 2025  
**Technologies:** Kotlin, Android SDK, KSOAP2, Material Design, SOAP Web Services

## Fonctionnalités / Features

- ✅ Affichage de la liste des comptes bancaires
- ✅ Ajout de nouveaux comptes (Courant/Épargne)
- ✅ Suppression de comptes existants avec confirmation
- ✅ Interface Material Design responsive
- ✅ Communication SOAP sécurisée avec le backend
- ✅ Gestion d'erreurs et messages utilisateur

## Architecture du projet / Project Architecture

```
TP14-SOAP-Android-Client/
├── app/src/main/java/ma/projet/soapclient/
│   ├── models/
│   │   ├── Compte.kt                   # Modèle de données compte
│   │   └── TypeCompte.kt               # Énumération types de compte
│   ├── network/
│   │   └── Service.kt                  # Service de communication SOAP
│   ├── ui/
│   │   └── CompteAdapter.kt            # Adapter RecyclerView pour l'affichage
│   └── MainActivity.kt                 # Activité principale
├── screens/                            # Captures d'écran
│   └── listee.png
└── README.md                           # Documentation
```

## Configuration requise / Requirements

- **Android Studio:** Arctic Fox ou version supérieure
- **SDK Android:** API 21 (Android 5.0) minimum
- **Kotlin:** 1.5.0 ou supérieur
- **Service SOAP:** Backend SOAP fonctionnel (ex: TP_13)

## Installation et configuration / Installation & Setup

### 1. Créer un nouveau projet Android Studio

1. Ouvrez Android Studio
2. Créez un nouveau projet : `File > New > New Project`
3. Sélectionnez `Empty Activity`
4. Configurez :
   - **Name:** SoapClientApp
   - **Package name:** ma.projet.soapclient
   - **Language:** Kotlin
   - **Minimum SDK:** API 21

### 2. Copier les fichiers source

Copiez les fichiers suivants dans votre projet :

```
app/src/main/java/ma/projet/soapclient/
├── models/Compte.kt
├── models/TypeCompte.kt
├── network/Service.kt
├── ui/CompteAdapter.kt
└── MainActivity.kt
```

### 3. Ajouter les dépendances dans build.gradle (Module: app)

Ajoutez ces dépendances dans le fichier `app/build.gradle` :

```gradle
dependencies {
    // KSOAP2 pour SOAP
    implementation 'com.google.code.ksoap2:ksoap2-android:3.6.4'

    // Material Design
    implementation 'com.google.android.material:material:1.6.1'

    // RecyclerView
    implementation 'androidx.recyclerview:recyclerview:1.2.1'

    // View Binding (optionnel mais recommandé)
    implementation 'androidx.activity:activity-ktx:1.6.0'
    implementation 'androidx.fragment:fragment-ktx:1.5.2'

    // Coroutines pour les opérations asynchrones
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'
}
```

### 4. Créer les layouts XML

#### activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAdd"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        android:contentDescription="Ajouter un compte"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:srcCompat="@android:drawable/ic_input_add" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### popup.xml (pour la boîte de dialogue d'ajout)
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Solde initial">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etSolde"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="numberDecimal" />

    </com.google.android.material.textfield.TextInputLayout>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Type de compte"
        android:textStyle="bold" />

    <RadioGroup
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">

        <RadioButton
            android:id="@+id/radioCourant"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:checked="true"
            android:text="Compte Courant" />

        <RadioButton
            android:id="@+id/radioEpargne"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Compte Épargne" />

    </RadioGroup>

</LinearLayout>
```

#### item_compte.xml (pour les éléments RecyclerView)
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/tvId"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="ID: "
            android:textStyle="bold" />

        <TextView
            android:id="@+id/tvSolde"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Solde: " />

        <TextView
            android:id="@+id/tvType"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Type: " />

        <TextView
            android:id="@+id/tvDate"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Date: " />

        <Button
            android:id="@+id/btnDelete"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:text="Supprimer" />

    </LinearLayout>

</androidx.cardview.widget.CardView>
```

### 5. Créer l'Adapter RecyclerView

Créez le fichier `app/src/main/java/ma/projet/soapclient/ui/CompteAdapter.kt` :

```kotlin
package ma.projet.soapclient.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.projet.soapclient.R
import ma.projet.soapclient.models.Compte
import java.text.SimpleDateFormat
import java.util.*

class CompteAdapter : RecyclerView.Adapter<CompteAdapter.CompteViewHolder>() {

    private var comptes = mutableListOf<Compte>()
    var onDeleteClick: ((Compte) -> Unit)? = null

    fun updateComptes(newComptes: List<Compte>) {
        comptes.clear()
        comptes.addAll(newComptes)
        notifyDataSetChanged()
    }

    fun removeCompte(compte: Compte) {
        val position = comptes.indexOf(compte)
        if (position != -1) {
            comptes.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compte, parent, false)
        return CompteViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        holder.bind(comptes[position])
    }

    override fun getItemCount() = comptes.size

    inner class CompteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvSolde: TextView = itemView.findViewById(R.id.tvSolde)
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(compte: Compte) {
            tvId.text = "ID: ${compte.id}"
            tvSolde.text = "Solde: ${compte.solde} €"
            tvType.text = "Type: ${compte.type}"
            tvDate.text = "Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(compte.dateCreation)}"

            btnDelete.setOnClickListener {
                onDeleteClick?.invoke(compte)
            }
        }
    }
}
```

### 6. Configurer les permissions dans AndroidManifest.xml

Ajoutez la permission Internet dans `app/src/main/AndroidManifest.xml` :

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="ma.projet.soapclient">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.SoapClientApp">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

## Configuration du backend SOAP

L'application est configurée pour communiquer avec un service SOAP local. Assurez-vous que :

1. Le service SOAP (ex: TP_13) est démarré sur `http://10.0.2.2:8082/services/ws`
2. Le namespace SOAP correspond : `http://ws.soapAcount/`
3. Les méthodes SOAP sont disponibles :
   - `getComptes()` - Récupère tous les comptes
   - `createCompte(solde, type)` - Crée un nouveau compte
   - `deleteCompte(id)` - Supprime un compte

## Utilisation / Usage

1. **Démarrer l'application** : Lancez l'app sur un émulateur ou appareil Android
2. **Voir les comptes** : La liste des comptes s'affiche automatiquement
3. **Ajouter un compte** : Cliquez sur le bouton "+" (FAB) en bas à droite
4. **Supprimer un compte** : Cliquez sur "Supprimer" dans la carte du compte

## Dépannage / Troubleshooting

### Erreur de connexion réseau
- Vérifiez que le service SOAP backend est démarré
- Vérifiez l'URL dans `Service.kt` (10.0.2.2 pour émulateur)
- Pour un appareil physique, remplacez par l'IP de votre machine

### Problèmes de dépendances
- Synchronisez le projet : `File > Sync Project with Gradle Files`
- Nettoyez et rebuild : `Build > Clean Project` puis `Build > Rebuild Project`

### Erreurs KSOAP2
- Vérifiez les namespaces SOAP
- Assurez-vous que les types de données correspondent entre client et serveur

## Captures d'écran / Screenshots

- `liste-comptes.png` : Affichage de la liste des comptes
- `add-compte.png` : Boîte de dialogue d'ajout de compte
- `delete-compte.png` : Confirmation de suppression

## Technologies utilisées / Technologies Used

- **Kotlin** : Langage de programmation principal
- **Android SDK** : Framework de développement Android
- **KSOAP2** : Bibliothèque pour les communications SOAP
- **Material Design** : Composants UI modernes
- **RecyclerView** : Affichage de listes optimisé
- **Coroutines** : Programmation asynchrone
- **SOAP Web Services** : Protocole de communication

## Auteur / Author

**Halmaoui Abdellah**  
Développeur Android - Architecture des composants  
Version 2.0 - Décembre 2025

## Licence / License

Ce projet est destiné à des fins éducatives dans le cadre du cours d'Architecture des Composants.