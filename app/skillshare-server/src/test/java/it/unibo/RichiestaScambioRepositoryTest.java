package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RichiestaScambioRepositoryTest {

    private RichiestaScambioRepository richiestaScambioRepository;

    @BeforeEach
    void setUp() {
        richiestaScambioRepository = new RichiestaScambioRepository();
        DatabaseCore.getDB().hashMap("richiesteScambio").createOrOpen().clear();
        DatabaseCore.commit();
    }

    /**
     * Verifica che trovaPerId recuperi la richiesta salvata.
     */
    @Test
    void trovaPerIdRestituisceLaRichiestaSalvata() {

        RichiestaScambio richiesta = richiestaScambioRepository.crea(
                "annuncio-1",
                "mittente-1",
                "proprietario-1",
                "Messaggio"
        );

        RichiestaScambio trovata =
                richiestaScambioRepository.trovaPerId(richiesta.getId());

        assertNotNull(trovata);
        assertEquals(richiesta.getId(), trovata.getId());
    }

    /**
     * Verifica che trovaPerId restituisca null
     * per un id inesistente.
     */
    @Test
    void trovaPerIdConIdInesistenteRestituisceNull() {
        RichiestaScambio trovata =
                richiestaScambioRepository.trovaPerId("id-inesistente");

        assertNull(trovata);
    }
}
