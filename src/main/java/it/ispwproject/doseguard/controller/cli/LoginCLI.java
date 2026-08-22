package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;
import it.ispwproject.doseguard.controller.applicativo.LoginController;
import it.ispwproject.doseguard.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.doseguard.exception.LoginException;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.cli.LoginView;

public class LoginCLI extends AbstractCLIState {

    private final LoginController loginController = new LoginController();
    private final LoginView view = new LoginView();

    @Override
    public void action(CLIStateMachine context) {
        String[] credenziali = view.chiediCredenziali();
        String email    = credenziali[0];
        String password = credenziali[1];

        if (email.isEmpty() || password.isEmpty()) {
            view.mostraErroreInput();
            goNext(context, this);
            return;
        }

        try {
            LoginResult result = loginController.login(email, password);
            String nome = SessionManager.getInstance().getLoggedUser().getName();
            view.mostraSuccesso(nome);

            switch (result) {
                case SUCCESSO_PATIENT -> goNext(context, new DashboardPatientCLI());
                case SUCCESSO_DOCTOR  -> goNext(context, new DashboardDoctorCLI());
                case SUCCESSO_PHARMACIST   -> goNext(context, new DashboardPharmacistCLI());
            }
        } catch (LoginException e) {
            view.mostraErrore(e.getMessage());
            goNext(context, this);
        }
    }
}
