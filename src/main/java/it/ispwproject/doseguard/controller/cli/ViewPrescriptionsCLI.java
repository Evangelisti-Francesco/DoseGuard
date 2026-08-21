package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import it.ispwproject.doseguard.controller.applicativo.PrescriptionController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.cli.ViewPrescriptionView;

import java.util.List;

public class ViewPrescriptionsCLI extends AbstractCLIState {

    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final ViewPrescriptionView view = new ViewPrescriptionView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            // Verifichiamo che l'utente loggato sia un Paziente
            if (!(SessionManager.getInstance().getLoggedUser() instanceof Patient loggedPatient)) {
                view.mostraErrore("Questa sezione è accessibile solo ai pazienti.");
                goBack(context);
                return;
            }

            // Recuperiamo le prescrizioni usando il codice fiscale del paziente loggato
            String fiscalCode = loggedPatient.getFiscalCode();
            List<PrescriptionBean> prescriptions = prescriptionController.getPatientPrescriptions(fiscalCode);

            view.mostraPrescrizioni(prescriptions);

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        // Dopo la visualizzazione torna indietro al menu precedente
        goBack(context);
    }
}
