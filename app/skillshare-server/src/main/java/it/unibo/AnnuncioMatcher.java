package it.unibo;

/**
 * Utilizzo del pattern strategy per i criteri usati durante la ricerca
 * degli annunci. Disaccoppia il Repository dalla logica di matching,
 * così da poter introdurre nuovi criteri di ricerca senza
 * modificare AnnuncioRepository.
 */
public interface AnnuncioMatcher {

    // Indica se l'annuncio soddisfa il criterio di ricerca.
    boolean matches(Annuncio annuncio);
}