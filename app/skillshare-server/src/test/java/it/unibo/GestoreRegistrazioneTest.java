package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Verifica la logica applicativa della registrazione.
 */
@ExtendWith(MockitoExtension.class)
public class GestoreRegistrazioneTest {

    @Mock
    private UtenteRepository utenteRepository;

    private GestoreRegistrazione gestoreRegistrazione;

    /**
     * Crea un gestore prima di ogni test.
     */
    @BeforeEach
    public void setUp() {
        gestoreRegistrazione =
                new GestoreRegistrazione(utenteRepository);
    }

    /**
     * Verifica la registrazione con dati validi.
     */
    @Test
    public void registra_shouldReturnUserWithValidData() {
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

        Utente risultato = gestoreRegistrazione.registra(
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

        verify(utenteRepository).registra(
                "Arianna",
                "arianna@example.com",
                "Password!"
        );
    }

    /**
     * Verifica che il nome utente sia obbligatorio.
     */
    @Test
    public void registra_shouldThrowWhenUsernameIsEmpty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gestoreRegistrazione.registra(
                        "",
                        "arianna@example.com",
                        "Password!"
                )
        );

        verifyNoInteractions(utenteRepository);
    }

    /**
     * Verifica che un nome composto da spazi venga rifiutato.
     */
    @Test
    public void registra_shouldThrowWhenUsernameContainsOnlySpaces() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gestoreRegistrazione.registra(
                        "   ",
                        "arianna@example.com",
                        "Password!"
                )
        );

        verifyNoInteractions(utenteRepository);
    }

    /**
     * Verifica che l'email sia obbligatoria.
     */
    @Test
    public void registra_shouldThrowWhenEmailIsEmpty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gestoreRegistrazione.registra(
                        "Arianna",
                        "",
                        "Password!"
                )
        );

        verifyNoInteractions(utenteRepository);
    }

    /**
     * Verifica che un'email non valida venga rifiutata.
     */
    @Test
    public void registra_shouldThrowWhenEmailIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gestoreRegistrazione.registra(
                        "Arianna",
                        "email-non-valida",
                        "Password!"
                )
        );

        verifyNoInteractions(utenteRepository);
    }

    /**
     * Verifica che una password non valida venga rifiutata.
     */
    @Test
    public void registra_shouldThrowWhenPasswordIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gestoreRegistrazione.registra(
                        "Arianna",
                        "arianna@example.com",
                        "password"
                )
        );

        verifyNoInteractions(utenteRepository);
    }

    /**
     * Verifica che gli spazi esterni vengano rimossi.
     */
    @Test
    public void registra_shouldTrimUsernameAndEmail() {
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

        gestoreRegistrazione.registra(
                "  Arianna  ",
                "  arianna@example.com  ",
                "Password!"
        );

        verify(utenteRepository).registra(
                "Arianna",
                "arianna@example.com",
                "Password!"
        );
    }

    /**
     * Verifica la propagazione degli errori del repository.
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
                () -> gestoreRegistrazione.registra(
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
     * Verifica che il nome utente debba essere univoco.
     */
    @Test
    public void registra_shouldThrowWhenUsernameAlreadyExists() {
        when(
                utenteRepository.registra(
                        "Arianna",
                        "arianna@example.com",
                        "Password!"
                )
        ).thenThrow(
                new IllegalArgumentException(
                        "Nome utente già utilizzato."
                )
        );

        IllegalArgumentException eccezione = assertThrows(
                IllegalArgumentException.class,
                () -> gestoreRegistrazione.registra(
                        "Arianna",
                        "arianna@example.com",
                        "Password!"
                )
        );

        assertEquals(
                "Nome utente già utilizzato.",
                eccezione.getMessage()
        );
    }
}