package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FiltroAnnuncioMatcherTest {

    private Annuncio annuncio;

    @BeforeEach
    void setUp() {
        annuncio = new Annuncio(
                "1",
                "autore-1",
                "Lezioni di chitarra",
                "Imparo le basi",
                "Musica",
                "Sì",
                "Cambio"
        );
    }

    /**
     * Verifica che un annuncio venga trovato quando la sua categoria
     * è tra quelle selezionate via checkbox.
     */
    @Test
    void matchesConCategoriaSelezionataRestituisceTrue() {

        Set<String> categorie = new HashSet<>();
        categorie.add("Musica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che un annuncio con categoria diversa
     * da quelle selezionate non venga trovato.
     */
    @Test
    void matchesConCategoriaDiversaRestituisceFalse() {

        Set<String> categorie = new HashSet<>();
        categorie.add("Informatica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertFalse(matcher.matches(annuncio));
    }

    /**
     * Verifica che il campo "Altro" trovi un annuncio
     * la cui categoria lo contiene, anche se non è tra le checkbox.
     */
    @Test
    void matchesConAltroCategoriaRestituisceTrue() {

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setAltroCategoria("musica");

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che una categoria selezionata e il campo "Altro"
     * siano combinati in OR: basta che uno dei due corrisponda.
     */
    @Test
    void matchesConCategoriaEAltroCombinatiInOrRestituisceTrue() {

        Set<String> categorie = new HashSet<>();
        categorie.add("Informatica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);
        filtro.setAltroCategoria("musica");

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che senza alcun criterio di categoria impostato
     * il filtro su categoria sia considerato soddisfatto.
     */
    @Test
    void matchesSenzaCriteriDiCategoriaRestituisceTrue() {

        FiltroAnnunci filtro = new FiltroAnnunci();

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che un annuncio con la disponibilità richiesta
     * venga trovato.
     */
    @Test
    void matchesConDisponibilitaCorrispondenteRestituisceTrue() {

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setDisponibilitaSelezionata("Sì");

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che un annuncio con disponibilità diversa
     * da quella richiesta non venga trovato.
     */
    @Test
    void matchesConDisponibilitaDiversaRestituisceFalse() {

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setDisponibilitaSelezionata("No");

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertFalse(matcher.matches(annuncio));
    }

    /**
     * Verifica che categoria e disponibilità siano
     * combinate in AND: entrambe devono corrispondere.
     */
    @Test
    void matchesConCategoriaEDisponibilitaEntrambeCorretteRestituisceTrue() {

        Set<String> categorie = new HashSet<>();
        categorie.add("Musica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);
        filtro.setDisponibilitaSelezionata("Sì");

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che se la categoria corrisponde ma la disponibilità no,
     * l'annuncio non venga trovato.
     */
    @Test
    void matchesConCategoriaCorrettaEDisponibilitaErrataRestituisceFalse() {

        Set<String> categorie = new HashSet<>();
        categorie.add("Musica");

        FiltroAnnunci filtro = new FiltroAnnunci();
        filtro.setCategorieSelezionate(categorie);
        filtro.setDisponibilitaSelezionata("No");

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertFalse(matcher.matches(annuncio));
    }

    /**
     * Verifica che senza alcun criterio impostato
     * il filtro consideri l'annuncio corrispondente.
     */
    @Test
    void matchesConFiltroVuotoRestituisceTrue() {

        FiltroAnnunci filtro = new FiltroAnnunci();

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che un annuncio nullo non corrisponda mai.
     */
    @Test
    void matchesConAnnuncioNullRestituisceFalse() {

        FiltroAnnunci filtro = new FiltroAnnunci();

        AnnuncioMatcher matcher = new FiltroAnnuncioMatcher(filtro);

        assertFalse(matcher.matches(null));
    }
}