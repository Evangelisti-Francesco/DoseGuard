package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;
import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.cli.DashboardPatientView;

public class DashboardPatientCLI extends AbstractCLIState {

    private final DashboardPatientView view = new DashboardPatientView();

    @Override
    public void entry(CLIStateMachine context) {
        String nome = SessionManager.getInstance().getLoggedUser().getName();
        view.mostraBenvenuto(nome);
    }

    @Override
    public void action(CLIStateMachine context) {
        view.mostraMenu();
        switch (view.chiediScelta()) {
            case "1" -> goNext(context, new BookAppointmentCLI());
            case "2" -> goNext(context, new ViewBookingsCLI());
            case "3" -> goNext(context, new CancelBookingCLI());
            case "4" -> goNext(context, new ViewMedicationsCLI());
            case "5" -> goNext(context, new ViewPrescriptionsCLI());
            case "6" -> goNext(context, new EditProfileCLI());
            case "0" -> {
                try {
                    ConnectionFactory.clearRole();
                    SessionManager.getInstance().clearSession();
                    view.mostraMessaggio(" Logout effettuato.");
                    goNext(context, new InitialCLI());
                } catch (java.sql.SQLException ex) {
                    view.mostraMessaggio(" Errore: impossibile effettuare il logout in sicurezza. Riprova.");
                    goNext(context, this);
                }
            }
            default -> {
                view.mostraMessaggio(" Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}