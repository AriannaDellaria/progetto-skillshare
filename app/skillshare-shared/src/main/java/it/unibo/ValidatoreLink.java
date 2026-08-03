package it.unibo;

/**
 * Verifica la validità di un link condiviso in chat.
 */
public class ValidatoreLink {

    private ValidatoreLink() {
    }

    /**
     * Controlla che il link sia una URL http/https con un dominio valido.
     */
    public static boolean linkValido(String url) {
        if (url == null) {
            return false;
        }

        String valore = url.trim();

        if (!valore.startsWith("http://") && !valore.startsWith("https://")) {
            return false;
        }

        return valore.matches("^https?://[\\w-]+(\\.[\\w-]+)+.*$");
    }
}

