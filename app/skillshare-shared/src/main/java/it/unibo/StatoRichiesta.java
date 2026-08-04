package it.unibo;

/**
 * Rappresenta lo stato di una richiesta di scambio.
 */
public enum StatoRichiesta {

    IN_ATTESA("In attesa"),
    ACCETTATA("Accettata"),
    RIFIUTATA("Rifiutata"),
    CONCLUSA("Conclusa");

    private final String etichetta;

    StatoRichiesta(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getEtichetta() {
        return etichetta;
    }
}
