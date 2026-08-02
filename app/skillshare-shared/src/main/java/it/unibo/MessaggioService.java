package it.unibo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * Definisce le operazioni sulla chat di uno scambio.
 */
@RemoteServiceRelativePath("messaggio")
public interface MessaggioService extends RemoteService {

    /**
     * Apre la chat associata alla richiesta indicata, restituendo
     * la cronologia dei messaggi.
     */
    List<Messaggio> apriChat(String idRichiesta, String idUtenteCorrente) throws IllegalArgumentException;
}