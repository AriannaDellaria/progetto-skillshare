package it.unibo;

/**
 * Rappresenta i criteri con cui è possibile ordinare gli annunci.
 * Contiene solo l'identificativo e l'etichetta da mostrare, senza
 * alcuna logica di confronto.
 * La logica di ordinamento è in ComparatorOrdinamento.
 */
public enum CriterioOrdinamento {

    TITOLO_CRESCENTE("Titolo (A-Z)"),
    TITOLO_DECRESCENTE("Titolo (Z-A)"),
    DATA_CRESCENTE("Data pubblicazione (meno recenti prima)"),
    DATA_DECRESCENTE("Data pubblicazione (più recenti prima)");

    private final String etichetta;

    CriterioOrdinamento(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getEtichetta() {
        return etichetta;
    }

    /**
     * Restituisce il criterio corrispondente all'etichetta mostrata
     * nel menu a tendina, oppure null se non trovato.
     */
    public static CriterioOrdinamento daEtichetta(String etichetta) {
        if (etichetta == null) {
            return null;
        }
        for (CriterioOrdinamento criterio : values()) {
            if (criterio.getEtichetta().equals(etichetta)) {
                return criterio;
            }
        }
        return null;
    }

}