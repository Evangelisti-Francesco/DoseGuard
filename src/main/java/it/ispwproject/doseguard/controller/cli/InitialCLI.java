package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;
import it.ispwproject.doseguard.view.cli.InitialView;


public class InitialCLI extends AbstractCLIState {

    private final InitialView view = new InitialView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraBenvenuto();
    }

    @Override
    public void action(CLIStateMachine context) {
        view.mostraMenu();
        switch (view.chiediScelta()) {
            case "1" -> goNext(context, new LoginCLI());
            case "2" -> goNext(context, new RegistrationCLI());
            case "0" -> context.setState(null); // Chiude l'applicazione
            default  -> {
                view.mostraErrore("Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}
