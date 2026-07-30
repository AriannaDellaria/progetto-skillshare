package it.unibo;

/**
 * Criterio di filtro basato su categoria e disponibilità.
 * Implementa la stessa interfaccia usata per la ricerca per parola chiave, così
 * il Repository resta invariato e indipendente dal criterio applicato.
 */
public class FiltroAnnuncioMatcher implements AnnuncioMatcher {

    private final FiltroAnnunci filtro;

    public FiltroAnnuncioMatcher(FiltroAnnunci filtro) {
        this.filtro = filtro;
    }

    @Override
    public boolean matches(Annuncio annuncio) {
        if (annuncio == null || filtro == null) {
            return false;
        }

        return matchCategoria(annuncio) && matchDisponibilita(annuncio);
    }

    // Un annuncio corrisponde se la sua categoria è tra quelle
    // selezionate via checkbox, oppure contiene il testo libero
    // inserito in "Altro". Se nessun criterio di categoria è stato
    // impostato, il filtro su categoria è considerato soddisfatto.
    private boolean matchCategoria(Annuncio annuncio) {
        boolean nessunaCategoriaSelezionata =
                (filtro.getCategorieSelezionate() == null
                        || filtro.getCategorieSelezionate().isEmpty())
                        && (filtro.getAltroCategoria() == null
                        || filtro.getAltroCategoria().trim().isEmpty());

        if (nessunaCategoriaSelezionata) {
            return true;
        }

        String categoriaAnnuncio = annuncio.getCategoria() == null
                ? "" : annuncio.getCategoria();

        boolean matchCheckbox = filtro.getCategorieSelezionate() != null
                && filtro.getCategorieSelezionate().contains(categoriaAnnuncio);

        boolean matchTestoLibero = filtro.getAltroCategoria() != null
                && !filtro.getAltroCategoria().trim().isEmpty()
                && categoriaAnnuncio.toLowerCase()
                .contains(filtro.getAltroCategoria().trim().toLowerCase());

        return matchCheckbox || matchTestoLibero;
    }

    // Un annuncio corrisponde se la disponibilità coincide con quella
    // selezionata. Se non è stato impostato alcun criterio, il filtro
    // su disponibilità è considerato soddisfatto.
    private boolean matchDisponibilita(Annuncio annuncio) {
        String selezionata = filtro.getDisponibilitaSelezionata();

        if (selezionata == null || selezionata.trim().isEmpty()) {
            return true;
        }

        String disponibilitaAnnuncio = annuncio.getDisponibilita() == null
                ? "" : annuncio.getDisponibilita();

        return selezionata.trim().equalsIgnoreCase(disponibilitaAnnuncio);
    }
}