package it.unibo;

/**
 * Criterio di ricerca per parola chiave:
 * se la keyword è contenuta nel titolo o nella categoria
 * allora verrà mostrato l'annuncio.
 */
public class KeywordAnnuncioMatcher implements AnnuncioMatcher {

    private final String keyword;

    public KeywordAnnuncioMatcher(String keyword) {
        this.keyword = keyword == null ? "" : keyword.trim().toLowerCase();
    }

    @Override
    public boolean matches(Annuncio annuncio) {
        if (annuncio == null) {
            return false;
        }

        String titolo = annuncio.getTitolo() == null ? "" : annuncio.getTitolo().toLowerCase();

        String categoria = annuncio.getCategoria() == null ? "" : annuncio.getCategoria().toLowerCase();

        return titolo.contains(keyword) || categoria.contains(keyword);
    }
}