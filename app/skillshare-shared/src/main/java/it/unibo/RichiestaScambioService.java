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

    List<RichiestaScambio> richiesteInviate(String idMittente);

    RichiestaScambio accetta(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException;

    RichiestaScambio rifiuta(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException;
}