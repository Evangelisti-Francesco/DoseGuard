package it.ispwproject.doseguard.view.cli;

import it.ispwproject.doseguard.bean.AppointmentResponseBean;
import it.ispwproject.doseguard.bean.DoctorBean;
import it.ispwproject.doseguard.bean.SpecializationBean;
import it.ispwproject.doseguard.bean.TimeSlotBean;

import java.util.List;

public class BookAppointmentView {

    private static final String DOCTOR_PREFIX = "Dott. ";

    public void mostraIntestazione() {
        CLIRenderer.intestazione("DoseGuard  –  Prenota Visita Medica");
    }

    public void mostraSpecializzazioni(List<SpecializationBean> specializations) {
        CLIRenderer.sezione("Specializzazioni Disponibili");
        for (int i = 0; i < specializations.size(); i++) {
            CLIRenderer.voceMenu(i + 1, specializations.get(i).getName());
        }
        CLIRenderer.voceMenuZero("Torna indietro");
    }

    public void mostraMedici(List<DoctorBean> favourites, List<DoctorBean> others) {
        int index = 1;
        if (!favourites.isEmpty()) {
            CLIRenderer.sezione("⭐ Medici Preferiti");
            for (DoctorBean doc : favourites) {
                CLIRenderer.voceMenu(index++, DOCTOR_PREFIX + doc.getFullName());
            }
        }
        if (!others.isEmpty()) {
            CLIRenderer.sezione("Altri Medici");
            for (DoctorBean doc : others) {
                CLIRenderer.voceMenu(index++, DOCTOR_PREFIX+ doc.getFullName());
            }
        }
        CLIRenderer.voceMenuZero("Torna indietro");
    }

    public void mostraSlot(List<TimeSlotBean> slots) {
        CLIRenderer.sezione("Slot disponibili");
        for (int i = 0; i < slots.size(); i++) {
            TimeSlotBean s = slots.get(i);
            System.out.printf(" [%d] %s   ore %s%n",
                    i + 1, s.getDate(), s.getStartTime());
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraRiepilogo(AppointmentResponseBean summary) {
        CLIRenderer.sezione("Riepilogo Prenotazione");
        CLIRenderer.campo("Medico", DOCTOR_PREFIX + summary.getDoctor());
        CLIRenderer.campo("Specializz.", summary.getSpecialization().getName());
        CLIRenderer.campo("Data", summary.getSlot().getDate().toString());
        CLIRenderer.campo("Orario", summary.getSlot().getStartTime().toString());
        CLIRenderer.separatore();
    }

    public void mostraConferma(AppointmentResponseBean response) {
        CLIRenderer.sezione("Prenotazione confermata");
        CLIRenderer.campo("Stato", response.getStatus() != null ? response.getStatus().toString() : "CONFIRMED");
        CLIRenderer.separatore();
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public String chiediNote() {
        return CLIRenderer.chiediCampo("Note aggiuntive per il medico (opzionale)");
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}