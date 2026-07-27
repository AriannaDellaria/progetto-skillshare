package it.unibo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Definisce le operazioni sugli annunci.
 */
@RemoteServiceRelativePath("annuncio")
public interface AnnuncioService extends RemoteService {
    /**
     * Crea un nuovo annuncio.
     */
    Annuncio crea(
            String idAutore,
            String titolo,
            String descrizione,
            String categoria,
            String disponibilita,
            String controprestazione
    );
}
