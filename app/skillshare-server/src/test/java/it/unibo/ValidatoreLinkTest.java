package it.unibo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatoreLinkTest {

    @Test
    public void linkValido_shouldReturnTrueForValidHttpsLink() {
        assertTrue(ValidatoreLink.linkValido("https://esempio.it/incontro"));
    }

    @Test
    public void linkValido_shouldReturnTrueForValidHttpLink() {
        assertTrue(ValidatoreLink.linkValido("http://esempio.it"));
    }

    @Test
    public void linkValido_shouldReturnFalseWithoutProtocol() {
        assertFalse(ValidatoreLink.linkValido("esempio.it"));
    }

    @Test
    public void linkValido_shouldReturnFalseForPlainText() {
        assertFalse(ValidatoreLink.linkValido("non è un link"));
    }

    @Test
    public void linkValido_shouldReturnFalseWhenNull() {
        assertFalse(ValidatoreLink.linkValido(null));
    }
}
