package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.User;
import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import it.ispwproject.doseguard.controller.applicativo.PrescriptionController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.cli.ViewPrescriptionView;

import java.util.ArrayList;
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
            User loggedUser = SessionManager.getInstance().getLoggedUser();
            List<PrescriptionBean> prescriptions = new ArrayList<>();

            if (loggedUser instanceof Patient patient) {
                // Il paziente vede le sue prescrizioni
                prescriptions = prescriptionController.getPatientPrescriptions(patient.getFiscalCode());

            } else if (loggedUser instanceof Doctor) {
                // Il medico inserisce il codice fiscale del paziente di cui vuole vedere le ricette
                String fiscalCode = view.chiediCodiceFiscale("Inserisci il codice fiscale del paziente");
                prescriptions = prescriptionController.getPatientPrescriptions(fiscalCode);

            } else {
                view.mostraErrore("Ruolo non autorizzato ad accedere a questa sezione.");
                goBack(context);
                return;
            }

            view.mostraPrescrizioni(prescriptions);

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }
}