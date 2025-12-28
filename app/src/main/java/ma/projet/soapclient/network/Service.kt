package ma.projet.soapclient.network

import ma.projet.soapclient.models.Compte
import ma.projet.soapclient.models.TypeCompte
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service de communication SOAP pour la gestion des comptes bancaires.
 *
 * Cette classe gère toutes les interactions avec le service web SOAP distant
 * pour les opérations CRUD (Create, Read, Delete) sur les comptes bancaires.
 * Elle utilise la bibliothèque KSOAP2 pour la sérialisation et la communication
 * SOAP avec le serveur backend.
 *
 * Architecture SOAP :
 * - Utilise SOAP 1.1 (VER11) pour la compatibilité
 * - Namespace SOAP configuré pour identifier le service
 * - URL du service configurée pour environnement de développement local
 * - 10.0.2.2 correspond à localhost depuis l'émulateur Android
 * - Pour un appareil physique, remplacer par l'IP réelle de la machine
 *
 * Gestion des erreurs :
 * - Toutes les méthodes gèrent les exceptions de manière silencieuse
 * - Les erreurs sont loggées via printStackTrace()
 * - Retourne des valeurs par défaut en cas d'échec (liste vide, false)
 *
 * @author Halmaoui Abdellah
 * @version 2.0
 * @since 2025
 * @see Compte Pour le modèle de données utilisé
 * @see TypeCompte Pour les types de comptes supportés
 */
class Service {
    /** Espace de noms SOAP utilisé pour identifier le service web */
    private val NAMESPACE = "http://ws.soapAcount/"
    
    /** URL complète du service web SOAP (à adapter selon l'environnement) */
    private val URL = "http://10.0.2.2:8082/services/ws"
    
    /** Nom de la méthode SOAP pour récupérer tous les comptes */
    private val METHOD_GET_COMPTES = "getComptes"
    
    /** Nom de la méthode SOAP pour créer un nouveau compte */
    private val METHOD_CREATE_COMPTE = "createCompte"
    
    /** Nom de la méthode SOAP pour supprimer un compte existant */
    private val METHOD_DELETE_COMPTE = "deleteCompte"
    
    /**
     * Récupère la liste complète des comptes bancaires via le service SOAP.
     *
     * Cette méthode effectue un appel SOAP synchrone vers le serveur pour obtenir
     * tous les comptes existants dans la base de données. Les données reçues sont
     * désérialisées depuis le format SOAP et converties en objets Compte Kotlin.
     *
     * Processus de traitement :
     * 1. Création d'une requête SOAP avec le namespace et la méthode appropriés
     * 2. Enveloppe SOAP 1.1 configurée (dotNet = false pour compatibilité Java)
     * 3. Appel HTTP synchrone vers le service web
     * 4. Parsing de la réponse SOAP et extraction des propriétés de chaque compte
     * 5. Conversion des dates depuis le format ISO 8601 (yyyy-MM-dd)
     * 6. Création des objets Compte avec les données parsées
     *
     * En cas d'erreur réseau, de timeout, ou de parsing SOAP, une liste vide
     * est retournée et l'erreur est loggée via printStackTrace().
     *
     * @return Liste des comptes bancaires récupérés du serveur.
     *         Liste vide si aucun compte trouvé ou en cas d'erreur.
     * @throws Exception En cas d'erreur de réseau ou de parsing SOAP (gérée intérieurement)
     */
    fun getComptes(): List<Compte> {
        val request = SoapObject(NAMESPACE, METHOD_GET_COMPTES)
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = false
            setOutputSoapObject(request)
        }
        val transport = HttpTransportSE(URL)
        val comptes = mutableListOf<Compte>()
        
        try {
            transport.call("", envelope)
            val response = envelope.bodyIn as SoapObject
            for (i in 0 until response.propertyCount) {
                val soapCompte = response.getProperty(i) as SoapObject
                val compte = Compte(
                    id = soapCompte.getPropertyAsString("id")?.toLongOrNull(),
                    solde = soapCompte.getPropertyAsString("solde")?.toDoubleOrNull() ?: 0.0,
                    dateCreation = SimpleDateFormat("yyyy-MM-dd").parse(
                        soapCompte.getPropertyAsString("dateCreation")
                    ) ?: Date(),
                    type = TypeCompte.valueOf(soapCompte.getPropertyAsString("type"))
                )
                comptes.add(compte)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return comptes
    }
    
    /**
     * Crée un nouveau compte bancaire via le service SOAP.
     *
     * Cette méthode envoie une requête SOAP pour créer un nouveau compte bancaire
     * avec le solde initial et le type spécifiés. L'identifiant du compte sera
     * généré automatiquement par le serveur backend lors de la persistance.
     *
     * Paramètres de la requête SOAP :
     * - solde : Valeur numérique du solde initial (Double)
     * - type : Type du compte sous forme de chaîne (nom de l'enum TypeCompte)
     *
     * Processus :
     * 1. Création de la requête SOAP avec les propriétés solde et type
     * 2. Configuration de l'enveloppe SOAP 1.1
     * 3. Envoi de la requête HTTP vers le service web
     * 4. Retour du résultat de l'opération (succès/échec)
     *
     * Validation :
     * - Le solde doit être positif ou nul (validation à faire côté client)
     * - Le type doit être une valeur valide de l'enum TypeCompte
     *
     * @param solde Solde initial du nouveau compte en devise locale.
     *              Doit être positif ou nul. La validation côté serveur peut
     *              refuser les valeurs négatives.
     * @param type Type du compte à créer (COURANT ou EPARGNE).
     *             Doit correspondre à une valeur de l'enum TypeCompte.
     * @return true si la création a réussi côté serveur, false en cas d'erreur
     *         (réseau, validation, serveur indisponible, etc.)
     * @throws Exception En cas d'erreur de réseau ou de parsing SOAP (gérée intérieurement)
     */
    fun createCompte(solde: Double, type: TypeCompte): Boolean {
        val request = SoapObject(NAMESPACE, METHOD_CREATE_COMPTE).apply {
            addProperty("solde", solde)
            addProperty("type", type.name)
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = false
            setOutputSoapObject(request)
        }
        val transport = HttpTransportSE(URL)
        
        return try {
            transport.call("", envelope)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Supprime un compte bancaire existant via le service SOAP.
     *
     * Cette méthode envoie une requête SOAP pour supprimer définitivement le compte
     * identifié par l'ID spécifié. L'opération est irréversible et doit être utilisée
     * avec précaution. Il est recommandé d'afficher une confirmation utilisateur
     * avant d'appeler cette méthode.
     *
     * Paramètres de la requête SOAP :
     * - id : Identifiant unique (Long) du compte à supprimer
     *
     * Processus :
     * 1. Création de la requête SOAP avec la propriété id
     * 2. Configuration de l'enveloppe SOAP 1.1
     * 3. Envoi de la requête HTTP vers le service web
     * 4. Lecture de la réponse SOAP (Boolean indiquant le succès)
     * 5. Retour du résultat de l'opération
     *
     * Comportement en cas d'erreur :
     * - Si le compte n'existe pas, le serveur peut retourner false
     * - Si une erreur réseau survient, false est retourné
     * - Les exceptions sont catchées et loggées silencieusement
     *
     * @param id Identifiant unique du compte à supprimer.
     *           Ne doit pas être null. Doit correspondre à un compte existant
     *           dans la base de données backend.
     * @return true si la suppression a réussi côté serveur, false en cas d'erreur
     *         (compte inexistant, erreur réseau, serveur indisponible, etc.)
     * @throws Exception En cas d'erreur de réseau ou de parsing SOAP (gérée intérieurement)
     */
    fun deleteCompte(id: Long): Boolean {
        val request = SoapObject(NAMESPACE, METHOD_DELETE_COMPTE).apply {
            addProperty("id", id)
        }
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
            dotNet = false
            setOutputSoapObject(request)
        }
        val transport = HttpTransportSE(URL)
        
        return try {
            transport.call("", envelope)
            envelope.response as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
