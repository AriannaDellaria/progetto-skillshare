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

            registraButton.setEnabled(false);
            messaggio.setText("");

            utenteService.emailGiaRegistrata(
                    email,
                    new AsyncCallback<Boolean>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            registraButton.setEnabled(true);

                            if (caught instanceof IllegalArgumentException
                                    && caught.getMessage() != null
                                    && !caught.getMessage().trim().isEmpty()) {

                                messaggio.setText(caught.getMessage());
                                return;
                            }

                            messaggio.setText(
                                    "Non è stato possibile completare la registrazione."
                            );
                        }

                        @Override
                        public void onSuccess(Boolean giaRegistrata) {
                            if (Boolean.TRUE.equals(
                                    giaRegistrata
                            )) {
                                registraButton.setEnabled(true);

                                messaggio.setText(
                                        "Mail già registrata "
                                                + "per un altro account."
                                );
                                return;
                            }
                            registraUtente(
                                    nomeUtente,
                                    email,
                                    password,
                                    registraButton,
                                    messaggio
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

    /**
     * Invia al server la richiesta di registrazione.
     */
    private void registraUtente(
            String nomeUtente,
            String email,
            String password,
            Button registraButton,
            Label messaggio
    ) {
        utenteService.registra(
                nomeUtente,
                email,
                password,
                new AsyncCallback<Utente>() {

                    @Override
                    public void onFailure(
                            Throwable caught
                    ) {
                        registraButton.setEnabled(true);

                        messaggio.setText(
                                "Errore di comunicazione "
                                        + "con il server."
                        );
                    }

                    @Override
                    public void onSuccess(Utente utente) {
                        registraButton.setEnabled(true);

                        if (utente == null) {
                            messaggio.setText(
                                    "Nome utente già utilizzato "
                                            + "o dati non validi."
                            );
                            return;
                        }

                        messaggio.setText(
                                "Registrazione completata. "
                                        + "Benvenuto/a "
                                        + utente.getNomeUtente()
                        );

                        new LoginGui().mostra();
                    }
                }
        );
    }
}