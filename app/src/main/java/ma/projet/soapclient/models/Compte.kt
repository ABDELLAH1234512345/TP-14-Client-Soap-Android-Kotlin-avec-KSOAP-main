package ma.projet.soapclient.models

import java.util.Date

/**
 * Classe de données représentant un compte bancaire dans l'application Android.
 *
 * Cette classe encapsule les informations essentielles d'un compte bancaire,
 * incluant l'identifiant unique, le solde, la date de création et le type de compte.
 * Elle est utilisée pour la sérialisation/désérialisation des données SOAP entre
 * le client Android et le serveur backend.
 *
 * Cette classe suit le pattern de données immutables de Kotlin (data class),
 * ce qui permet une manipulation efficace des objets compte dans l'application.
 *
 * @property id Identifiant unique du compte généré par le serveur.
 *              Peut être null pour les nouveaux comptes non encore persistés.
 * @property solde Solde actuel du compte en devise locale (Double).
 *                 Doit être positif ou nul, représenté en unités monétaires.
 * @property dateCreation Date de création du compte (java.util.Date).
 *                        Format ISO 8601 standard (yyyy-MM-dd).
 * @property type Type du compte bancaire (TypeCompte enum).
 *                Peut être COURANT ou EPARGNE selon le type de compte.
 *
 * @author Halmaoui Abdellah
 * @version 2.0
 * @since 2025
 * @see TypeCompte Pour les différents types de comptes disponibles
 */
data class Compte(
    /** Identifiant unique du compte (généré par le serveur) */
    val id: Long?,
    /** Solde actuel du compte en devise locale */
    val solde: Double,
    /** Date de création du compte */
    val dateCreation: Date,
    /** Type de compte bancaire (COURANT ou EPARGNE) */
    val type: TypeCompte
)
