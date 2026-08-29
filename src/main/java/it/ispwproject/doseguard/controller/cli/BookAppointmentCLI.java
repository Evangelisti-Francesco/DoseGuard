package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.bean.*;
import it.ispwproject.doseguard.controller.applicativo.BookingController;
import it.ispwproject.doseguard.exception.BookingException;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.cli.BookAppointmentView;

import java.util.ArrayList;
import java.util.List;

public class BookAppointmentCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final BookAppointmentView view = new BookAppointmentView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        Patient loggedPatient = (Patient) SessionManager.getInstance().getLoggedUser();
        PatientBean patientBean = new PatientBean(
                loggedPatient.getId(), loggedPatient.getName(),
                loggedPatient.getSurname(), loggedPatient.getEmail(),loggedPatient.getFiscalCode());

        TimeSlotBean slot = null;

        try {
            // Specializzazione
            List<SpecializationBean> specializations = bookingController.getAvailableSpecializations();
            if (specializations.isEmpty()) {
                view.mostraMessaggio("Nessuna specializzazione disponibile.");
                goBack(context); return;
            }
            view.mostraSpecializzazioni(specializations);
            int sc = view.chiediScelta("Seleziona una specializzazione", 0, specializations.size());
            if (sc == 0) { goBack(context); return; }
            SpecializationBean specialization = specializations.get(sc - 1);

            // Medico
            List<DoctorBean> allDoctors = bookingController.getDoctorsBySpecialization(specialization);
            if (allDoctors.isEmpty()) {
                view.mostraMessaggio("Nessun medico disponibile per questa specializzazione.");
                goBack(context); return;
            }
            List<DoctorBean> favourites = allDoctors.stream().filter(DoctorBean::isFavourite).toList();
            List<DoctorBean> others     = allDoctors.stream().filter(d -> !d.isFavourite()).toList();
            view.mostraMedici(favourites, others);
            int dc = view.chiediScelta("Seleziona un medico", 0, allDoctors.size());
            if (dc == 0) { goBack(context); return; }
            List<DoctorBean> ordered = new ArrayList<>(favourites);
            ordered.addAll(others);
            DoctorBean doctor = ordered.get(dc - 1);

            // Slot
            List<TimeSlotBean> available = bookingController.getDoctorAvailability(doctor)
                    .stream().filter(TimeSlotBean::isAvailable).toList();
            if (available.isEmpty()) {
                view.mostraMessaggio("Nessuno slot disponibile per questo medico.");
                goBack(context); return;
            }
            view.mostraSlot(available);
            int slc = view.chiediScelta("Seleziona uno slot", 0, available.size());
            if (slc == 0) { goBack(context); return; }
            slot = available.get(slc - 1);

            // Note
            String notes = view.chiediNote();

            // Riepilogo
            AppointmentRequestBean request = new AppointmentRequestBean(patientBean, doctor, specialization, slot, notes);
            AppointmentResponseBean summary = bookingController.prepareBookingSummary(request);
            view.mostraRiepilogo(summary);

            if (!view.chiediConferma("Confermare? (hai 3 minuti per decidere)")) {
                bookingController.releaseSlot(slot.getId());
                view.mostraMessaggio("Prenotazione annullata.");
                goBack(context); return;
            }

            // Creazione
            AppointmentResponseBean response = bookingController.createBooking(request);
            view.mostraConferma(response);

            if (!doctor.isFavourite() && view.chiediConferma(
                    "Vuoi aggiungere il Dott. " + doctor.getFullName() + " ai medici preferiti?")) {
                bookingController.addDoctorToFavourites(doctor.getId());
                view.mostraMessaggio("⭐ Medico aggiunto ai preferiti.");
            }

        } catch (BookingException e) {
            if (slot != null) {
                try { bookingController.releaseSlot(slot.getId()); }
                catch (DAOException ex) { /* ignora */ }
            }
            view.mostraMessaggio("❌ Errore: " + e.getMessage());
        } catch (DAOException e) {
            view.mostraMessaggio("❌ Errore: " + e.getMessage());
        }

        goBack(context);
    }
}
