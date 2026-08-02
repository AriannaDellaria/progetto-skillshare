package it.unibo;

import org.mapdb.DB;
import org.mapdb.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Gestisce il salvataggio dei messaggi di chat.
 */
public class MessaggioRepository {

    private static final DB db = DatabaseCore.getDB();

    private static final ConcurrentMap<String, Messaggio> messaggi =
            db.hashMap("messaggi", Serializer.STRING, Serializer.JAVA)
                    .createOrOpen();

    /**
     * Cerca tutti i messaggi associati a una richiesta di scambio.
     */
    public List<Messaggio> trovaPerRichiesta(String idRichiesta) {

        List<Messaggio> risultati = new ArrayList<>();

        if (idRichiesta == null) {
            return risultati;
        }

        for (Messaggio messaggio : messaggi.values()) {
            if (idRichiesta.equals(messaggio.getIdRichiesta())) {
                risultati.add(messaggio);
            }
        }

        return risultati;
    }
}