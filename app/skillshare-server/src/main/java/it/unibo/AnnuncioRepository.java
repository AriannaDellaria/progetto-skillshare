package it.unibo;

import org.mapdb.DB;
import org.mapdb.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * Gestisce il salvataggio e la ricerca degli annunci.
 */
public class AnnuncioRepository {

    private static final DB db = DatabaseCore.getDB();

    // Contiene gli annunci salvati, indicizzati tramite ID.
    private static final ConcurrentMap<String, Annuncio> annunci =
            db.hashMap("annunci", Serializer.STRING, Serializer.JAVA)
                    .createOrOpen();

    /**
     * Crea e salva un nuovo annuncio
     */
    public Annuncio crea(
            String idAutore,
            String titolo,
            String descrizione,
            String categoria,
            String disponibilita,
            String controprestazione
    ) {
        String id = UUID.randomUUID().toString();

        Annuncio annuncio = new Annuncio(
                id,
                idAutore,
                titolo,
                descrizione,
                categoria,
                disponibilita,
                controprestazione
        );

        annunci.put(id, annuncio);
        DatabaseCore.commit();

        return annuncio;
    }

    /**
     * Cerca tutti gli annunci pubblicati da un utente.
     */
    public List<Annuncio> trovaPerAutore(String idAutore) {

        List<Annuncio> risultati = new ArrayList<>();

        if (idAutore == null) {
            return risultati;
        }

        for (Annuncio annuncio : annunci.values()) {

            if (idAutore.equals(annuncio.getIdAutore())) {
                risultati.add(annuncio);
            }
        }

        return risultati;
    }
}
