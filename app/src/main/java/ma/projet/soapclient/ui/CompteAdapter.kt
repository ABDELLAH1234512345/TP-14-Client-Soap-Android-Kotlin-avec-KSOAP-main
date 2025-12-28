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

/**
 * Adapter RecyclerView pour l'affichage de la liste des comptes bancaires.
 *
 * Cette classe gère l'affichage des comptes dans un RecyclerView en utilisant le pattern
 * ViewHolder pour optimiser les performances et réduire la consommation mémoire.
 * Chaque élément de la liste représente un compte bancaire avec toutes ses informations
 * (ID, solde, type, date de création) ainsi qu'un bouton de suppression interactif.
 *
 * Architecture :
 * - Utilise le pattern ViewHolder pour réutiliser les vues et améliorer les performances
 * - Implémente RecyclerView.Adapter pour l'intégration avec le RecyclerView Android
 * - Utilise un callback pour la communication avec l'activité parente (découplage)
 * - Formatage des dates avec SimpleDateFormat pour un affichage lisible
 *
 * Fonctionnalités :
 * - Mise à jour complète de la liste via updateComptes()
 * - Suppression d'un élément spécifique avec animation via removeCompte()
 * - Gestion des événements de clic sur le bouton de suppression via callback
 *
 * @author Halmaoui Abdellah
 * @version 2.0
 * @since 2025
 * @see Compte Pour le modèle de données affiché
 * @see RecyclerView.ViewHolder Pour le pattern ViewHolder utilisé
 */
class CompteAdapter : RecyclerView.Adapter<CompteAdapter.CompteViewHolder>() {

    /**
     * Liste mutable des comptes bancaires à afficher dans le RecyclerView.
     *
     * Cette liste est mise à jour dynamiquement lors des opérations CRUD
     * (Create, Read, Delete) et est utilisée comme source de données
     * pour le RecyclerView.
     */
    private var comptes = mutableListOf<Compte>()

    /**
     * Callback appelé lorsqu'un utilisateur clique sur le bouton de suppression d'un compte.
     *
     * Ce callback permet à l'activité parente de gérer la logique de suppression
     * (affichage d'une boîte de dialogue de confirmation, appel au service SOAP, etc.)
     * tout en maintenant l'adapter découplé de la logique métier.
     *
     * Utilisation :
     * ```kotlin
     * adapter.onDeleteClick = { compte ->
     *     // Logique de suppression (dialog, appel service, etc.)
     * }
     * ```
     */
    var onDeleteClick: ((Compte) -> Unit)? = null

    /**
     * Met à jour la liste des comptes affichés
     *
     * Remplace complètement la liste actuelle et notifie l'adapter
     * du changement pour rafraîchir l'affichage.
     *
     * @param newComptes Nouvelle liste de comptes à afficher
     */
    fun updateComptes(newComptes: List<Compte>) {
        comptes.clear()
        comptes.addAll(newComptes)
        notifyDataSetChanged()
    }

    /**
     * Supprime un compte spécifique de la liste
     *
     * Recherche la position du compte dans la liste et le supprime
     * avec une animation si trouvé.
     *
     * @param compte Le compte à supprimer de l'affichage
     */
    fun removeCompte(compte: Compte) {
        val position = comptes.indexOf(compte)
        if (position != -1) {
            comptes.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * Crée un nouveau ViewHolder pour un élément de la liste
     *
     * @param parent Le ViewGroup parent
     * @param viewType Le type de vue (non utilisé ici)
     * @return Un nouveau CompteViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compte, parent, false)
        return CompteViewHolder(view)
    }

    /**
     * Lie les données d'un compte à un ViewHolder
     *
     * @param holder Le ViewHolder à mettre à jour
     * @param position La position du compte dans la liste
     */
    override fun onBindViewHolder(holder: CompteViewHolder, position: Int) {
        holder.bind(comptes[position])
    }

    /**
     * Retourne le nombre d'éléments dans la liste
     *
     * @return Le nombre de comptes à afficher
     */
    override fun getItemCount() = comptes.size

    /**
     * ViewHolder pour les éléments de compte dans le RecyclerView.
     *
     * Cette classe interne gère la liaison entre les données d'un compte bancaire
     * et les vues de l'interface utilisateur pour un élément de la liste.
     * Elle utilise le pattern ViewHolder pour optimiser les performances en évitant
     * les appels répétés à findViewById().
     *
     * Composants de la vue :
     * - TextView pour l'ID du compte
     * - TextView pour le solde (formaté avec devise)
     * - TextView pour le type de compte
     * - TextView pour la date de création (formatée en dd/MM/yyyy)
     * - Button pour déclencher la suppression du compte
     *
     * @param itemView La vue racine de l'élément (layout item_compte.xml)
     */
    inner class CompteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /** TextView affichant l'identifiant unique du compte */
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        
        /** TextView affichant le solde actuel du compte avec symbole € */
        private val tvSolde: TextView = itemView.findViewById(R.id.tvSolde)
        
        /** TextView affichant le type de compte (COURANT ou EPARGNE) */
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        
        /** TextView affichant la date de création formatée en français */
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        
        /** Bouton permettant de déclencher la suppression du compte */
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        /**
         * Lie les données du compte aux vues de l'élément du RecyclerView.
         *
         * Cette méthode est appelée par l'adapter lors du recyclage des vues.
         * Elle met à jour tous les TextViews avec les données du compte et
         * configure le listener du bouton de suppression.
         *
         * Formatage :
         * - ID : "ID: {id}"
         * - Solde : "Solde: {solde} €"
         * - Type : "Type: {type}"
         * - Date : "Date: {date}" au format dd/MM/yyyy (français)
         *
         * @param compte Le compte bancaire dont les données doivent être affichées.
         *               Ne doit pas être null.
         */
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