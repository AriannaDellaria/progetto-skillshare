package it.unibo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Verifica il servizio RPC dedicato agli utenti.
 */
@ExtendWith(MockitoExtension.class)
public class UtenteServiceImplTest {

    @Mock
    private UtenteRepository utenteRepository;

    private UtenteServiceImpl service;

    /**
     * Crea il servizio con un repository simulato.
     */
    @BeforeEach
    public void setUp() {
        service = new UtenteServiceImpl(utenteRepository);
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
}