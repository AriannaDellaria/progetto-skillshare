package it.unibo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica le regole di validazione delle credenziali.
 */
public class ValidatoreCredenzialiTest {

    /**
     * Verifica che una password valida venga accettata.
     */
    @Test
    public void passwordValida_shouldReturnTrueForValidPassword() {
        boolean risultato =
                ValidatoreCredenziali.passwordValida("Password!");

        assertTrue(risultato);
    }

    /**
     * Verifica che una password troppo corta venga rifiutata.
     */
    @Test
    public void passwordValida_shouldReturnFalseWhenTooShort() {
        boolean risultato = ValidatoreCredenziali.passwordValida("Pass!");

        assertFalse(risultato);
    }

    /**
     * Verifica che la password contenga un carattere speciale.
     */
    @Test
    public void passwordValida_shouldReturnFalseWithoutSpecialCharacter() {
        boolean risultato = ValidatoreCredenziali.passwordValida("Password1");

        assertFalse(risultato);
    }

    /**
     * Verifica che una password nulla venga rifiutata.
     */
    @Test
    public void passwordValida_shouldReturnFalseWhenNull() {
        assertFalse(
                ValidatoreCredenziali.passwordValida(null)
        );
    }

    /**
     * Verifica che un indirizzo email valido venga accettato.
     */
    @Test
    public void emailValida_shouldReturnTrueForValidEmail() {
        boolean risultato = ValidatoreCredenziali.emailValida("arianna@example.com");

        assertTrue(risultato);
    }

    /**
     * Verifica che un indirizzo privo di chiocciola venga rifiutato.
     */
    @Test
    public void emailValida_shouldReturnFalseWithoutAtSymbol() {
        boolean risultato = ValidatoreCredenziali.emailValida("ariannaexample.com");

        assertFalse(risultato);
    }

    /**
     * Verifica che un indirizzo privo di dominio venga rifiutato.
     */
    @Test
    public void emailValida_shouldReturnFalseWithoutDomain() {
        boolean risultato = ValidatoreCredenziali.emailValida("arianna@");

        assertFalse(risultato);
    }

    /**
     * Verifica che un indirizzo email nullo venga rifiutato.
     */
    @Test
    public void emailValida_shouldReturnFalseWhenNull() {
        assertFalse(
                ValidatoreCredenziali.emailValida(null)
        );
    }
}