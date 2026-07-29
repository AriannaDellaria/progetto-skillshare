package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeywordAnnuncioMatcherTest {

    private Annuncio annuncio;

    @BeforeEach
    void setUp() {
        annuncio = new Annuncio(
                "1",
                "autore-1",
                "Lezioni di chitarra",
                "Imparo le basi della chitarra classica",
                "Musica",
                "Weekend",
                "Lezioni di cucina"
        );
    }

    /**
     * Verifica che il matcher restituisca true
     * quando la parola chiave è contenuta nel titolo.
     */
    @Test
    void matchesRestituisceTrueSeKeywordNelTitolo() {
        AnnuncioMatcher matcher = new KeywordAnnuncioMatcher("chitarra");

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che il matcher restituisca true
     * quando la parola chiave è contenuta nella categoria.
     */
    @Test
    void matchesRestituisceTrueSeKeywordNellaCategoria() {
        AnnuncioMatcher matcher = new KeywordAnnuncioMatcher("musica");

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che il confronto non sia sensibile
     * a maiuscole e minuscole.
     */
    @Test
    void matchesECaseInsensitive() {
        AnnuncioMatcher matcher = new KeywordAnnuncioMatcher("CHITARRA");

        assertTrue(matcher.matches(annuncio));
    }

    /**
     * Verifica che il matcher restituisca false
     * quando la parola chiave non è presente nell'annuncio.
     */
    @Test
    void matchesRestituisceFalseSeKeywordNonTrovata() {
        AnnuncioMatcher matcher = new KeywordAnnuncioMatcher("cucina italiana");

        assertFalse(matcher.matches(annuncio));
    }

    /**
     * Verifica che il matcher restituisca false
     * quando l'annuncio passato è null.
     */
    @Test
    void matchesRestituisceFalseSeAnnuncioENull() {
        AnnuncioMatcher matcher = new KeywordAnnuncioMatcher("chitarra");

        assertFalse(matcher.matches(null));
    }

    /**
     * Verifica che una parola chiave null
     * venga trattata come stringa vuota,
     * producendo un match su qualsiasi annuncio.
     */
    @Test
    void matchesTrattaKeywordNullaComeStringaVuota() {
        AnnuncioMatcher matcher = new KeywordAnnuncioMatcher(null);

        assertTrue(matcher.matches(annuncio));
    }
}