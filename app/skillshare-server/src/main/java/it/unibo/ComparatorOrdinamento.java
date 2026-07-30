package it.unibo;

import java.util.Comparator;

/**
 * Fornisce il Comparator corrispondente a ciascun CriterioOrdinamento.
 */
public final class ComparatorOrdinamento {

    private ComparatorOrdinamento() {
        // Classe di sole utility, non istanziabile.
    }

    /**
     * Restituisce il Comparator associato al criterio indicato,
     * oppure null se il criterio è null o non riconosciuto (nessun
     * ordinamento da applicare).
     */
    public static Comparator<Annuncio> perCriterio(CriterioOrdinamento criterio) {
        if (criterio == null) {
            return null;
        }

        switch (criterio) {
            case TITOLO_CRESCENTE:
                return Comparator.comparing(
                        Annuncio::getTitolo, String.CASE_INSENSITIVE_ORDER
                );
            case TITOLO_DECRESCENTE:
                return Comparator.comparing(
                        Annuncio::getTitolo, String.CASE_INSENSITIVE_ORDER
                ).reversed();
            case DATA_CRESCENTE:
                return Comparator.comparingLong(Annuncio::getDataPubblicazione);
            case DATA_DECRESCENTE:
                return Comparator.comparingLong(Annuncio::getDataPubblicazione).reversed();
            default:
                return null;
        }
    }
}