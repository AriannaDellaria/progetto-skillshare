package it.unibo;

/**
 * Distingue le due direzioni in cui può essere assegnata una
 * valutazione al termine di uno scambio concluso.
 */
public enum TipoValutazione {

    /**
     * Assegnata dal mittente della richiesta al proprietario
     * dell'annuncio, valuta la prestazione offerta.
     */
    PRESTAZIONE("Prestazione"),

    /**
     * Assegnata dal proprietario dell'annuncio al mittente della
     * richiesta, valuta la controprestazione offerta in cambio.
     */
    CONTROPRESTAZIONE("Controprestazione");

    private final String etichetta;

    TipoValutazione(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getEtichetta() {
        return etichetta;
    }
}