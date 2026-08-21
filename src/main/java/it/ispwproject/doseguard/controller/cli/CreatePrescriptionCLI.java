package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.controller.applicativo.PrescriptionController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.cli.CreatePrescriptionView;

public class CreatePrescriptionCLI extends AbstractCLIState {

    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final CreatePrescriptionView view = new CreatePrescriptionView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            int patientId = view.chiediPatientId();
            String drug = view.chiediCampo("Nome del Farmaco");
            String dosage = view.chiediCampo("Dosaggio");
            String frequency = view.chiediCampo("Frequenza");

            if (!view.chiediConferma("Confermi la creazione della prescrizione?")) {
                view.mostraMessaggio("Operazione annullata.");
                goBack(context);
                return;
            }

            prescriptionController.createPrescription(patientId, drug, dosage, frequency);
            view.mostraSuccesso("Prescrizione creata e registrata con successo!");

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }
}
