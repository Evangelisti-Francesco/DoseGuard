package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.bean.MedicationBean;
import it.ispwproject.doseguard.controller.applicativo.MedicationController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.cli.ViewMedicationsView;

import java.util.List;

public class ViewMedicationsCLI extends AbstractCLIState {

    private final MedicationController medicationController = new MedicationController();
    private final ViewMedicationsView view = new ViewMedicationsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            List<MedicationBean> medications = medicationController.getPatientMedications();
            view.mostraFarmaci(medications);

            if (medications.isEmpty()) {
                view.mostraMessaggio("Nessun farmaco o terapia in elenco.");
                goBack(context);
                return;
            }

            if (!view.chiediConferma("Vuoi confermare l'assunzione di un farmaco?")) {
                goBack(context);
                return;
            }

            List<MedicationBean> pending = medications.stream()
                    .filter(m -> !m.isTaken()).toList();

            if (pending.isEmpty()) {
                view.mostraMessaggio("Tutti i farmaci programmati sono stati già assunti!");
                goBack(context);
                return;
            }

            view.mostraPendingPerSelezione(pending);
            int choice = view.chiediScelta("Seleziona farmaco", 0, pending.size());
            if (choice == 0) {
                goBack(context);
                return;
            }

            medicationController.markMedicationAsTaken(pending.get(choice - 1).getId());
            view.mostraSuccesso("Assunzione del farmaco registrata con successo.");

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}