package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Mostra il form per registrare un nuovo utente.
 */
public class RegistrazioneGui {

    private final UtenteServiceAsync utenteService =
            GWT.create(UtenteService.class);

    /**
     * Costruisce e visualizza la schermata di registrazione.
     */
    public void mostra() {
        RootPanel.get().clear();

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        Label titolo = new Label("Registrazione SkillShare");

        TextBox nomeUtenteBox = new TextBox();
        nomeUtenteBox.getElement().setPropertyString(
                "placeholder",
                "Nome utente"
        );

        TextBox emailBox = new TextBox();
        emailBox.getElement().setPropertyString(
                "placeholder",
                "Email"
        );

        PasswordTextBox passwordBox = new PasswordTextBox();
        passwordBox.getElement().setPropertyString(
                "placeholder",
                "Password"
        );

        Label requisitoPassword = new Label(
                ValidatoreCredenziali.requisitoPassword()
        );

        Button registraButton = new Button("Registrati");
        Label messaggio = new Label();

        registraButton.addClickHandler(event -> {
            String nomeUtente = nomeUtenteBox.getText().trim();
            String email = emailBox.getText().trim();
            String password = passwordBox.getText();

            if (nomeUtente.isEmpty()) {
                messaggio.setText( "Il nome utente è obbligatorio.");
                return;
            }

            if (email.isEmpty()) {
                messaggio.setText("L'email è obbligatoria.");
                return;
            }
            if (!ValidatoreCredenziali.emailValida(email)) {
                messaggio.setText("Formato email non valido.");
                return;
            }

            if (!ValidatoreCredenziali.passwordValida(password)) {
                messaggio.setText(
                        ValidatoreCredenziali.requisitoPassword()
                );
                return;
            }

            utenteService.registra(
                    nomeUtente,
                    email,
                    password,
                    new AsyncCallback<Utente>() {

                        /**
                         * Mostra l'errore ricevuto dal server.
                         */
                        @Override
                        public void onFailure(Throwable caught) {
                            if (caught.getMessage() != null
                                    && !caught.getMessage()
                                    .trim()
                                    .isEmpty()) {
                                messaggio.setText(
                                        caught.getMessage()
                                );
                            } else {
                                messaggio.setText(
                                        "Errore di comunicazione con il server."
                                );
                            }
                        }

                        /**
                         * Conferma la registrazione completata.
                         */
                        @Override
                        public void onSuccess(Utente utente) {
                            if (utente == null) {
                                messaggio.setText(
                                        "Registrazione non completata."
                                );
                                return;
                            }

                            messaggio.setText(
                                    "Registrazione completata. "
                                            + "Benvenuto/a "
                                            + utente.getNomeUtente()
                            );
                        }
                    }
            );
        });

        panel.add(titolo);
        panel.add(nomeUtenteBox);
        panel.add(emailBox);
        panel.add(passwordBox);
        panel.add(requisitoPassword);
        panel.add(registraButton);
        panel.add(messaggio);
        panel.add(NavigationHelper.creaBottoneVaiAlLogin());

        RootPanel.get().add(panel);
    }
}