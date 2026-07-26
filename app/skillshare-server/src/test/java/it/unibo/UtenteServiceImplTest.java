package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Verifica il servizio RPC dedicato agli utenti.
 */
@ExtendWith(MockitoExtension.class)
public class UtenteServiceImplTest {

    @Mock
    private UtenteRepository utenteRepository;
    private UtenteServiceImpl service;
    private UtenteRepository repositoryReale;
    private UtenteServiceImpl serviceReale;

    /**
     * Crea il servizio con un repository simulato.
     */
    @BeforeEach
    public void setUp() {
        service = new UtenteServiceImpl(utenteRepository);
        repositoryReale = new UtenteRepository();
        serviceReale = new UtenteServiceImpl(repositoryReale);
    }

    /**
     * Verifica che il servizio restituisca l'utente registrato.
     */
    @Test
    public void registra_shouldReturnRegisteredUser() {
        Utente utenteAtteso = new Utente(
                "1",
                "Arianna",
                "arianna@example.com",
                "Password!"
        );

        when(
                utenteRepository.registra(
                        "Arianna",
                        "arianna@example.com",
                        "Password!"
                )
        ).thenReturn(utenteAtteso);

        Utente risultato = service.registra(
                "Arianna",
                "arianna@example.com",
                "Password!"
        );

        assertNotNull(risultato);
        assertEquals(
                "Arianna",
                risultato.getNomeUtente()
        );
        assertEquals(
                "arianna@example.com",
                risultato.getEmail()
        );
    }

    /**
     * Verifica che il servizio rifiuti un nome utente nullo.
     */
    @Test
    public void registra_shouldThrowWhenUsernameIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registra(
                        null,
                        "arianna@example.com",
                        "Password!"
                )
        );
    }

    /**
     * Verifica che il servizio rifiuti un'email non valida.
     */
    @Test
    public void registra_shouldThrowWhenEmailIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registra(
                        "Arianna",
                        "email-non-valida",
                        "Password!"
                )
        );
    }

    /**
     * Verifica che il servizio rifiuti una password non valida.
     */
    @Test
    public void registra_shouldThrowWhenPasswordIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registra(
                        "Arianna",
                        "arianna@example.com",
                        "password"
                )
        );
    }

    /**
     * Verifica la propagazione di un'email duplicata.
     */
    @Test
    public void registra_shouldThrowWhenEmailAlreadyExists() {
        when(
                utenteRepository.registra(
                        "Arianna",
                        "arianna@example.com",
                        "Password!"
                )
        ).thenThrow(
                new IllegalArgumentException(
                        "Email già registrata."
                )
        );

        IllegalArgumentException eccezione = assertThrows(
                IllegalArgumentException.class,
                () -> service.registra(
                        "Arianna",
                        "arianna@example.com",
                        "Password!"
                )
        );

        assertEquals(
                "Email già registrata.",
                eccezione.getMessage()
        );
    }

    /**
     * Verifica che gli spazi nel nome utente vengano ignorati.
     */
    @Test
    void loginConSpaziNelNomeUtente() {
        String nomeUtente =
                "utente_" + System.nanoTime();

        serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );

        Utente autenticato = serviceReale.login(
                "  " + nomeUtente + "  ",
                "Password!"
        );

        assertNotNull(autenticato);
    }

    /**
     * Verifica che un utente registrato possa effettuare il login.
     */
    @Test
    void loginConCredenzialiCorretteRestituisceUtente() {
        String nomeUtente =
                "utente_" + System.nanoTime();

        Utente registrato = serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );

        Utente autenticato = serviceReale.login(
                nomeUtente,
                "Password!"
        );

        assertNotNull(autenticato);
        assertEquals(
                registrato.getId(),
                autenticato.getId()
        );
    }

    /**
     * Verifica che una password errata impedisca il login.
     */
    @Test
    void loginConPasswordErrataRestituisceNull() {
        String nomeUtente =
                "utente_" + System.nanoTime();

        serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );

        Utente autenticato = serviceReale.login(
                nomeUtente,
                "PasswordSbagliata!"
        );

        assertNull(autenticato);
    }

    /**
     * Verifica che un utente inesistente non possa accedere.
     */
    @Test
    void loginConNomeUtenteInesistenteRestituisceNull() {
        Utente autenticato = serviceReale.login(
                "utenteInesistente",
                "Password!"
        );

        assertNull(autenticato);
    }

    /**
     * Verifica che credenziali vuote non permettano il login.
     */
    @Test
    void loginConCredenzialiVuoteRestituisceNull() {
        assertNull(serviceReale.login("", ""));
        assertNull(serviceReale.login(null, null));
    }

    /**
     * Verifica che la biografia venga aggiornata correttamente.
     */
    @Test
    void aggiornaProfiloConBioETagSalvaINuoviDati() {

        String nomeUtente =
                "utente_" + System.nanoTime();

        Utente registrato = serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );

        List<String> tag = List.of(
                "Java",
                "Programmazione"
        );

        Utente aggiornato = serviceReale.aggiornaProfilo(
                registrato.getId(),
                "Mi piace condividere conoscenze",
                "",
                tag
        );

        assertNotNull(aggiornato);
        assertEquals(
                "Mi piace condividere conoscenze",
                aggiornato.getBio()
        );
        assertEquals(
                2,
                aggiornato.getTagCompetenze().size()
        );

        assertTrue(
                aggiornato.getTagCompetenze()
                        .contains("Java")
        );
    }

    /**
     * Verifica che non venga aggiornato un profilo
     * di un utente non presente.
     */
    @Test
    void aggiornaProfiloConUtenteInesistenteRestituisceNull() {

        Utente aggiornato = serviceReale.aggiornaProfilo(
                "id inesistente",
                "Nuova bio",
                "",
                List.of("Java")
        );

        assertNull(aggiornato);
    }

    /**
     * Verifica che una biografia nulla venga salvata come vuota.
     */
    @Test
    void aggiornaProfiloConBiografiaNullSalvaBiografiaVuota() {

        String nomeUtente =
                "utente_" + System.nanoTime();

        Utente registrato = serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );

        Utente aggiornato = serviceReale.aggiornaProfilo(
                registrato.getId(),
                null,
                "",
                List.of()
        );

        assertNotNull(aggiornato);
        assertEquals(
                "",
                aggiornato.getBio()
        );
    }


    /**
     * Verifica che una nuova biografia sostituisca quella precedente.
     */
    @Test
    void aggiornaProfiloSostituisceLaBiografiaPrecedente() {

        String nomeUtente =
                "utente_" + System.nanoTime();

        Utente registrato = serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );

        serviceReale.aggiornaProfilo(
                registrato.getId(),
                "Prima descrizione",
                "",
                List.of()
        );

        Utente aggiornato = serviceReale.aggiornaProfilo(
                registrato.getId(),
                "Seconda descrizione",
                "",
                List.of()
        );

        assertEquals(
                "Seconda descrizione",
                aggiornato.getBio()
        );
    }

    /**
     * Verifica che la foto del profilo venga aggiornata correttamente.
     */
    @Test
    void aggiornaProfiloConNuovaFotoSalvaLaModifica() {

        String nomeUtente =
                "utente_" + System.nanoTime();

        Utente registrato = serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );

        Utente aggiornato = serviceReale.aggiornaProfilo(
                registrato.getId(),
                "",
                "foto.png",
                List.of()
        );

        assertNotNull(aggiornato);

        assertEquals(
                "foto.png",
                aggiornato.getFotoProfilo()
        );
    }

    /**
     * Verifica che aggiornando la foto venga mantenuta la biografia.
     */
    @Test
    void aggiornaProfiloConFotoMantieneBiografiaEsistente() {

        String nomeUtente =
                "utente_" + System.nanoTime();

        Utente registrato = serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );

        Utente aggiornato = serviceReale.aggiornaProfilo(
                registrato.getId(),
                "Appassionata di informatica",
                "profilo.jpg",
                List.of()
        );

        assertEquals(
                "Appassionata di informatica",
                aggiornato.getBio()
        );

        assertEquals(
                "profilo.jpg",
                aggiornato.getFotoProfilo()
        );
    }
    /**
     * Verifica che i tag di competenza vengano aggiornati correttamente.
     */
    @Test
    void aggiornaProfiloSalvaNuoviTagCompetenza() {

        String nomeUtente =
                "utente_" + System.nanoTime();

        Utente registrato = serviceReale.registra(
                nomeUtente,
                nomeUtente + "@email.it",
                "Password!"
        );


        Utente aggiornato = serviceReale.aggiornaProfilo(
                registrato.getId(),
                "",
                "",
                List.of(
                        "Java",
                        "Database"
                )
        );


        assertNotNull(aggiornato);

        assertEquals(
                2,
                aggiornato.getTagCompetenze().size()
        );

        assertTrue(
                aggiornato.getTagCompetenze()
                        .contains("Java")
        );

        assertTrue(
                aggiornato.getTagCompetenze()
                        .contains("Database")
        );
    }
}