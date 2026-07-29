package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AnnuncioRepositoryTest {

    @Mock
    private AnnuncioMatcher matcher;

    private AnnuncioRepository annuncioRepository;

    @BeforeEach
    void setUp() {
        annuncioRepository = new AnnuncioRepository();
    }

    /**
     * Verifica che la ricerca restituisca solo
     * gli annunci attivi che soddisfano il matcher.
     */
    @Test
    void cercaRestituisceSoloAnnunciAttiviCorrispondentiAlMatcher() {
        when(matcher.matches(any(Annuncio.class))).thenReturn(true);

        Annuncio attivo = annuncioRepository.crea(
                "autore-1", "Titolo attivo", "Descrizione",
                "Categoria", "Weekend", "Cambio"
        );

        List<Annuncio> risultati = annuncioRepository.cerca(matcher);

        // Si confronta per ID
        boolean presente = risultati.stream().anyMatch(a -> a.getId().equals(attivo.getId()));

        assertTrue(presente);
    }

    /**
     * Verifica che gli annunci non corrispondenti
     * al matcher vengano esclusi dai risultati.
     */
    @Test
    void cercaEscludeAnnunciNonCorrispondentiAlMatcher() {
        when(matcher.matches(any(Annuncio.class))).thenReturn(false);

        annuncioRepository.crea(
                "autore-1", "Titolo qualsiasi", "Descrizione",
                "Categoria", "Weekend", "Cambio"
        );

        List<Annuncio> risultati = annuncioRepository.cerca(matcher);

        assertTrue(risultati.isEmpty());
    }

    /**
     * Verifica che gli annunci inattivi vengano esclusi
     * anche quando il matcher restituisce true.
     */
    @Test
    void cercaEscludeAnnunciInattiviAncheSeIlMatcherRestituisceTrue() {
        when(matcher.matches(any(Annuncio.class))).thenReturn(true);

        Annuncio annuncio = annuncioRepository.crea(
                "autore-1", "Titolo da disattivare", "Descrizione",
                "Categoria", "Weekend", "Cambio"
        );
        annuncioRepository.rimuovi(annuncio.getId(), "autore-1");

        List<Annuncio> risultati = annuncioRepository.cerca(matcher);

        // Si confronta per ID
        boolean presente = risultati.stream().anyMatch(a -> a.getId().equals(annuncio.getId()));

        assertFalse(presente);
    }

    /**
     * Verifica che la ricerca restituisca
     * una lista vuota quando il matcher è null.
     */
    @Test
    void cercaRestituisceListaVuotaSeIlMatcherENull() {
        annuncioRepository.crea(
                "autore-1", "Titolo", "Descrizione",
                "Categoria", "Weekend", "Cambio"
        );

        List<Annuncio> risultati = annuncioRepository.cerca(null);

        assertTrue(risultati.isEmpty());
    }
}