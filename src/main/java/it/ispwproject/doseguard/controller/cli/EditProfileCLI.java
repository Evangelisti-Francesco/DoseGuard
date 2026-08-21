package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.controller.applicativo.UserController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.cli.EditProfileView;

public class EditProfileCLI extends AbstractCLIState {

    private final UserController userController = new UserController();
    private final EditProfileView view = new EditProfileView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
        var user = SessionManager.getInstance().getLoggedUser();
        view.mostraDatiAttuali(user.getName(), user.getSurname(), user.getEmail());
    }

    @Override
    public void action(CLIStateMachine context) {
        String scelta = "";

        while (!scelta.equals("0")) {
            view.mostraMenu();
            scelta = view.chiediScelta();

            switch (scelta) {
                case "1" -> editEmail();
                case "0" -> { /* esce dal ciclo */ }
                default  -> view.mostraErrore("Scelta non valida.");
            }
        }

        goBack(context);
    }

    private void editEmail() {
        String newEmail = view.chiediCampo("Nuova email");
        if (!view.chiediConferma("Confermare il cambio email a " + newEmail + "?")) {
            view.mostraMessaggio("Operazione annullata.");
            return;
        }
        try {
            userController.updateEmail(newEmail);
            view.mostraSuccesso("Email aggiornata con successo.");
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
    }
}
