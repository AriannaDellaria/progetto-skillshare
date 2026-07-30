package it.unibo;

import java.util.Collections;
import java.util.List;

/**
 * Elenco condiviso delle categorie consigliate per gli annunci.
 * Usato sia nella creazione dell'annuncio (come suggerimento) sia
 * nella schermata dei filtri (come opzioni selezionabili).
 */
public final class CategorieSuggerite {

    private static final List<String> CATEGORIE = Collections.unmodifiableList(
            java.util.Arrays.asList(
                    "Informatica",
                    "Musica",
                    "Lingue",
                    "Sport",
                    "Cucina",
                    "Artigianato",
                    "Scienze",
                    "Arte"
            )
    );

    private CategorieSuggerite() {
        // Classe di sole costanti, non istanziabile.
    }

    /**
     * Restituisce l'elenco delle categorie consigliate.
     */
    public static List<String> elenco() {
        return CATEGORIE;
    }
}