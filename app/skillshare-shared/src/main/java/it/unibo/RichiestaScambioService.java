package it.unibo;

import java.util.List;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Definisce le operazioni sulle richieste di scambio.
 */
@RemoteServiceRelativePath("richiestaScambio")
public interface RichiestaScambioService extends RemoteService {

    /**
     * Invia una richiesta di scambio per l'annuncio indicato.
     */
    RichiestaScambio invia(
            String idAnnuncio,
            String idMittente,
            String messaggio
    )throws IllegalArgumentException;

    List<RichiestaScambio> richiesteRicevute(String idProprietario);

    /**
     * Recupera le richieste di scambio inviate dal mittente indicato.
     */
    List<RichiestaScambio> richiesteInviate(String idMittente);

    RichiestaScambio accetta(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException;

    RichiestaScambio rifiuta(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException;

    /**
     * Recupera una richiesta di scambio, se l'utente indicato
     * ne è partecipante (mittente o proprietario).
     */
    RichiestaScambio trovaPerId(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException;

    /**
     * Termina uno scambio accettato. Consentito solo al proprietario dell'annuncio.
     */
    RichiestaScambio termina(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException;

}