package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.bean.PatientBean;
import it.ispwproject.doseguard.bean.PatientProgressBean;
import it.ispwproject.doseguard.bean.TimeSlotBean;
import it.ispwproject.doseguard.controller.applicativo.PatientManagementController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.cli.ManagePatientsView;

import java.util.List;

public class ManagePatientsCLI extends AbstractCLIState {

    private final PatientManagementController patientController = new PatientManagementController();
    private final ManagePatientsView view = new ManagePatientsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            List<PatientBean> patients = patientController.getPatients();
            view.mostraPazienti(patients);

            if (patients.isEmpty()) {
                goBack(context);
                return;
            }

            int choice = view.chiediScelta("Seleziona un paziente", 0, patients.size());
            if (choice == 0) {
                goBack(context);
                return;
            }

            managePatient(context, patients.get(choice - 1));

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
            goBack(context);
        }
    }

    private void managePatient(CLIStateMachine context, PatientBean patient) throws DAOException {
        while (true) {
            List<TimeSlotBean> upcoming = patientController.getUpcomingAppointments(patient.getId());
            PatientProgressBean progress = patientController.getProgress(patient.getId());

            view.mostraSchedaPaziente(patient, upcoming, progress);
            view.mostraMenuPaziente();

            int choice = view.chiediScelta("Scelta", 0, 2);
            switch (choice) {
                case 1 -> annotaProgressi(patient);
                case 2 -> visualizzaStorico(patient);
                case 0 -> {
                    return; // Torna alla lista pazienti
                }
                default -> view.mostraMessaggio("❌ Scelta non valida.");
            }
        }
    }

    private void annotaProgressi(PatientBean patient) throws DAOException {
        PatientProgressBean existing = patientController.getProgress(patient.getId());
        if (existing != null && existing.getNotes() != null) {
            view.mostraMessaggio("Note attuali: " + existing.getNotes());
            view.mostraMessaggio("Riscrivi il testo modificato:");
        }
        String notes = view.chiediTesto("Note");
        if (notes.isBlank()) {
            view.mostraMessaggio("Note non valide.");
            return;
        }
        patientController.updateProgress(new PatientProgressBean(patient, notes));
        view.mostraSuccesso("Note/progressi aggiornati.");
    }

    private void visualizzaStorico(PatientBean patient) throws DAOException {
        List<TimeSlotBean> completed = patientController.getCompletedAppointments(patient.getId());
        view.mostraStoricoVisite(completed);
        view.attesaInvio();
    }
}