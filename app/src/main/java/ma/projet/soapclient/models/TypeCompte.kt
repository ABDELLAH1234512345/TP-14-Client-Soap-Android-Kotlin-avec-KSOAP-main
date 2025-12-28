package ma.projet.soapclient.models

/**
 * Énumération représentant les différents types de comptes bancaires disponibles dans l'application.
 *
 * Cette énumération définit les types principaux de comptes supportés par l'application
 * de gestion bancaire. Chaque type de compte peut avoir des règles métier spécifiques
 * et des caractéristiques différentes au niveau du serveur SOAP.
 *
 * L'utilisation d'une énumération garantit la type-safety et facilite la validation
 * des données lors de la communication avec le service web SOAP.
 *
 * @author Halmaoui Abdellah
 * @version 2.0
 * @since 2025
 * @see Compte Pour la classe utilisant cette énumération
 */
enum class TypeCompte {
    /**
     * Compte courant (checking account).
     *
     * Type de compte utilisé pour les transactions quotidiennes, les retraits,
     * les dépôts et les opérations bancaires courantes. Généralement sans
     * restrictions sur le nombre de transactions.
     */
    COURANT,

    /**
     * Compte épargne (savings account).
     *
     * Type de compte utilisé pour l'épargne à long terme. Généralement associé
     * à des intérêts et peut avoir des limitations sur le nombre de transactions
     * mensuelles selon la réglementation bancaire.
     */
    EPARGNE
}
