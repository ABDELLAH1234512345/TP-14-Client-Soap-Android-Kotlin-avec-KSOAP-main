package ma.projet.soapclient

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.projet.soapclient.ui.CompteAdapter
import ma.projet.soapclient.models.TypeCompte
import ma.projet.soapclient.network.Service

/**
 * Activité principale de l'application Android de gestion des comptes bancaires.
 *
 * Cette activité gère l'interface utilisateur principale (UI) de l'application et coordonne
 * toutes les interactions utilisateur avec les comptes bancaires. Elle implémente un pattern
 * MVVM simplifié avec une séparation claire entre la présentation et la logique métier.
 *
 * Fonctionnalités principales :
 * - Affichage de la liste des comptes bancaires dans un RecyclerView scrollable
 * - Ajout de nouveaux comptes via une boîte de dialogue Material Design
 * - Suppression de comptes existants avec confirmation utilisateur
 * - Gestion des erreurs réseau avec messages Toast informatifs
 * - Chargement automatique des comptes au démarrage de l'activité
 *
 * Architecture et technologies :
 * - RecyclerView avec adapter personnalisé (CompteAdapter) pour l'affichage optimisé
 * - Dialogues Material Design (MaterialAlertDialogBuilder) pour les interactions utilisateur
 * - Service SOAP (classe Service) pour la communication avec le backend
 * - Coroutines Kotlin (lifecycleScope) pour les opérations réseau asynchrones
 * - Pattern Observer via callbacks pour la communication adapter-activité
 *
 * Cycle de vie :
 * - onCreate() : Initialisation de l'UI, configuration du RecyclerView, chargement initial
 * - Les opérations réseau sont gérées via coroutines pour éviter le blocage du thread UI
 * - Les mises à jour de l'interface se font sur le thread principal (Dispatchers.Main)
 *
 * Gestion des erreurs :
 * - Les erreurs réseau sont catchées et affichées via Toast
 * - Les erreurs de validation sont gérées dans les dialogues
 * - Les erreurs SOAP sont loggées et retournent des valeurs par défaut
 *
 * @author Halmaoui Abdellah
 * @version 2.0
 * @since 2025
 * @see CompteAdapter Pour l'adapter RecyclerView utilisé
 * @see Service Pour le service SOAP de communication
 * @see Compte Pour le modèle de données
 */
class MainActivity : AppCompatActivity() {
    /** RecyclerView affichant la liste scrollable des comptes bancaires */
    private lateinit var recyclerView: RecyclerView
    
    /** Bouton flottant (FAB) permettant d'ajouter un nouveau compte */
    private lateinit var btnAdd: Button
    
    /** Adapter gérant l'affichage des comptes dans le RecyclerView */
    private val adapter = CompteAdapter()
    
    /** Service SOAP pour la communication avec le backend */
    private val service = Service()
    
    /**
     * Méthode appelée lors de la création de l'activité (lifecycle callback).
     *
     * Cette méthode est le point d'entrée principal de l'activité et effectue toutes
     * les initialisations nécessaires pour rendre l'interface utilisateur fonctionnelle.
     *
     * Ordre d'exécution :
     * 1. Appel à super.onCreate() pour l'initialisation Android standard
     * 2. Liaison du layout XML à l'activité via setContentView()
     * 3. Initialisation des références vers les vues (initViews)
     * 4. Configuration du RecyclerView avec layout manager et adapter (setupRecyclerView)
     * 5. Configuration des listeners d'événements (setupListeners)
     * 6. Chargement initial des comptes depuis le service SOAP (loadComptes)
     *
     * @param savedInstanceState État sauvegardé de l'activité lors d'une recréation
     *                           (rotation d'écran, retour depuis background, etc.).
     *                           Null si première création de l'activité.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupRecyclerView()
        setupListeners()
        loadComptes()
    }
    
    /**
     * Initialise les références vers les vues de l'interface utilisateur.
     *
     * Cette méthode utilise findViewById() pour récupérer les références des composants
     * graphiques définis dans le layout XML (activity_main.xml) et les stocke dans les
     * propriétés de classe (recyclerView, btnAdd) pour une utilisation ultérieure.
     *
     * Composants initialisés :
     * - recyclerView : RecyclerView pour l'affichage de la liste des comptes
     * - btnAdd : FloatingActionButton pour ajouter un nouveau compte
     *
     * Note : Cette méthode doit être appelée après setContentView() pour que les
     * vues soient disponibles dans la hiérarchie de vues de l'activité.
     */
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        btnAdd = findViewById(R.id.fabAdd)
    }
    
    /**
     * Configure le RecyclerView pour l'affichage de la liste des comptes bancaires.
     *
     * Cette méthode configure tous les aspects du RecyclerView nécessaires à son
     * fonctionnement : le layout manager, l'adapter personnalisé, et le callback
     * de suppression pour gérer les interactions utilisateur.
     *
     * Configuration :
     * - LinearLayoutManager : Layout vertical linéaire pour une liste simple
     * - CompteAdapter : Adapter personnalisé gérant l'affichage des comptes
     * - onDeleteClick callback : Gestionnaire d'événement pour la suppression
     *
     * Gestion de la suppression :
     * Lorsqu'un utilisateur clique sur le bouton de suppression d'un compte, une
     * boîte de dialogue Material Design s'affiche pour confirmation. Si confirmé,
     * la suppression est effectuée via le service SOAP en arrière-plan, puis l'UI
     * est mise à jour sur le thread principal avec un message de feedback.
     */
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        adapter.onDeleteClick = { compte ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Supprimer le compte")
                .setMessage("Voulez-vous vraiment supprimer ce compte ?")
                .setPositiveButton("Supprimer") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val success = service.deleteCompte(compte.id!!)
                        withContext(Dispatchers.Main) {
                            if (success) {
                                adapter.removeCompte(compte)
                                Toast.makeText(this@MainActivity, "Compte supprimé.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Erreur lors de la suppression.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }
    
    /**
     * Configure les listeners d'événements pour les composants interactifs de l'interface.
     *
     * Cette méthode attache les gestionnaires d'événements (listeners) aux composants
     * interactifs de l'interface utilisateur pour réagir aux actions de l'utilisateur.
     *
     * Listeners configurés :
     * - btnAdd.setOnClickListener : Déclenche l'affichage de la boîte de dialogue
     *   d'ajout de compte lorsqu'on clique sur le FloatingActionButton
     *
     * Note : Le callback de suppression du RecyclerView est configuré dans
     * setupRecyclerView() plutôt que dans cette méthode pour une meilleure organisation.
     */
    private fun setupListeners() {
        btnAdd.setOnClickListener { showAddCompteDialog() }
    }
    
    /**
     * Affiche la boîte de dialogue modale pour ajouter un nouveau compte bancaire.
     *
     * Cette méthode crée et affiche une boîte de dialogue Material Design personnalisée
     * contenant un formulaire pour saisir les informations nécessaires à la création
     * d'un nouveau compte bancaire.
     *
     * Composants du formulaire :
     * - TextInputEditText : Champ de saisie pour le solde initial (type numérique décimal)
     * - RadioGroup avec RadioButtons : Sélection du type de compte (Courant ou Épargne)
     *   - radioCourant : Compte courant (sélectionné par défaut)
     *   - radioEpargne : Compte épargne
     * - Boutons d'action : "Ajouter" (validation) et "Annuler" (fermeture)
     *
     * Processus de création :
     * 1. Récupération des valeurs saisies (solde, type de compte)
     * 2. Conversion du solde en Double (valeur par défaut 0.0 si invalide)
     * 3. Détermination du type de compte selon le RadioButton sélectionné
     * 4. Appel au service SOAP en arrière-plan via coroutine (Dispatchers.IO)
     * 5. Mise à jour de l'UI sur le thread principal (Dispatchers.Main)
     * 6. Affichage d'un message Toast de succès/échec
     * 7. Rechargement de la liste des comptes si création réussie
     *
     * Gestion des erreurs :
     * - Solde invalide : Utilise 0.0 par défaut (toDoubleOrNull())
     * - Erreur réseau : Affiche un Toast d'erreur
     * - Erreur serveur : Affiche un Toast d'erreur
     */
    private fun showAddCompteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.popup, null)
        
        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setTitle("Nouveau compte")
            .setPositiveButton("Ajouter") { _, _ ->
                val etSolde = dialogView.findViewById<TextInputEditText>(R.id.etSolde)
                val radioCourant = dialogView.findViewById<RadioButton>(R.id.radioCourant)
                
                val solde = etSolde.text.toString().toDoubleOrNull() ?: 0.0
                val type = if (radioCourant.isChecked) TypeCompte.COURANT else TypeCompte.EPARGNE
                
                lifecycleScope.launch(Dispatchers.IO) {
                    val success = service.createCompte(solde, type)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(this@MainActivity, "Compte ajouté.", Toast.LENGTH_SHORT).show()
                            loadComptes()
                        } else {
                            Toast.makeText(this@MainActivity, "Erreur lors de l'ajout.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    
    /**
     * Charge la liste des comptes bancaires depuis le service web SOAP.
     *
     * Cette méthode exécute une opération réseau asynchrone en utilisant les coroutines
     * Kotlin pour éviter de bloquer le thread principal (UI thread) pendant la communication
     * avec le serveur backend.
     *
     * Processus d'exécution :
     * 1. Lancement d'une coroutine sur le dispatcher IO (Dispatchers.IO) pour l'appel réseau
     * 2. Appel synchrone au service SOAP pour récupérer la liste des comptes
     * 3. Basculement vers le thread principal (Dispatchers.Main) pour mettre à jour l'UI
     * 4. Mise à jour de l'adapter du RecyclerView avec les nouvelles données
     * 5. Notification de l'adapter pour rafraîchir l'affichage (notifyDataSetChanged)
     *
     * Gestion des résultats :
     * - Liste non vide : Mise à jour de l'adapter avec les comptes récupérés
     * - Liste vide : Affichage d'un Toast informatif "Aucun compte trouvé"
     * - Exception réseau : Catch de l'exception, affichage d'un Toast d'erreur avec message
     *
     * Cycle de vie :
     * Cette méthode utilise lifecycleScope pour garantir que la coroutine est automatiquement
     * annulée si l'activité est détruite, évitant les fuites mémoire et les mises à jour
     * sur une activité invalide.
     *
     * Gestion des erreurs :
     * - Timeout réseau : Exception catchée, Toast avec message d'erreur
     * - Serveur indisponible : Exception catchée, Toast avec message d'erreur
     * - Erreur de parsing SOAP : Exception catchée, Toast avec message d'erreur
     * - Toutes les exceptions sont loggées pour le debugging
     */
    private fun loadComptes() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val comptes = service.getComptes()
                withContext(Dispatchers.Main) {
                    if (comptes.isNotEmpty()) {
                        adapter.updateComptes(comptes)
                    } else {
                        Toast.makeText(this@MainActivity, "Aucun compte trouvé.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
