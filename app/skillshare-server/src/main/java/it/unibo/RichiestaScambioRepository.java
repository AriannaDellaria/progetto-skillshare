package it.unibo;

import org.mapdb.DB;
import org.mapdb.Serializer;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * Gestisce il salvataggio delle richieste di scambio.
 */
public class RichiestaScambioRepository {

    private static final DB db = DatabaseCore.getDB();

    // Contiene le richieste di scambio salvate, indicizzate tramite ID.
    private static final ConcurrentMap<String, RichiestaScambio> richieste =
            db.hashMap("richiesteScambio", Serializer.STRING, Serializer.JAVA)
                    .createOrOpen();

    /**
     * Crea e salva una nuova richiesta di scambio.
     */
    public RichiestaScambio crea(
            String idAnnuncio,
            String idMittente,
            String idProprietario,
            String messaggio
    ) {
        String id = UUID.randomUUID().toString();

        RichiestaScambio richiesta = new RichiestaScambio(
                id,
                idAnnuncio,
                idMittente,
                idProprietario,
                messaggio
        );

        richieste.put(id, richiesta);
        DatabaseCore.commit();

        return richiesta;
    }

    /**
     * Cerca una richiesta tramite il suo ID.
     */
    public RichiestaScambio trovaPerId(String id) {
        return richieste.get(id);
    }
}

