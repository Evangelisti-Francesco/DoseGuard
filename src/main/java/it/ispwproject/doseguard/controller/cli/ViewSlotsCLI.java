package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.bean.TimeSlotBean;
import it.ispwproject.doseguard.controller.applicativo.AvailabilityController;
import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.cli.ViewSlotsView;

import java.util.List;
import java.util.Map;

public class ViewSlotsCLI extends AbstractCLIState {

    private final AvailabilityController availabilityController = new AvailabilityController();
    private final ViewSlotsView view = new ViewSlotsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            Map<Integer, String> patientBySlot = availabilityController.getSpecializationBySlot();
            List<TimeSlotBean> futuri  = availabilityController.getSlots();
            List<TimeSlotBean> passati = availabilityController.getPastSlots();

            List<TimeSlotBean> disponibili = futuri.stream().filter(TimeSlotBean::isAvailable).toList();
            List<TimeSlotBean> prenotati   = futuri.stream().filter(s -> !s.isAvailable()).toList();

            boolean running = true;
            while (running) {
                view.mostraTab(disponibili.size(), prenotati.size(), passati.size());
                int scelta = view.chiediScelta("Scelta", 0, 4);
                switch (scelta) {
                    case 1 -> view.mostraDisponibili(disponibili);
                    case 2 -> view.mostraPrenotati(prenotati, patientBySlot);
                    case 3 -> view.mostraPassati(passati, patientBySlot);
                    case 4 -> {
                        if (!disponibili.isEmpty()) {
                            view.mostraSlotDisponibili(disponibili);
                            int choice = view.chiediScelta("Seleziona slot da eliminare", 0, disponibili.size());
                            if (choice != 0) {
                                if (view.chiediConferma("Sei sicuro di voler eliminare questo slot?")) {
                                    availabilityController.deleteSlot(disponibili.get(choice - 1).getId());
                                    view.mostraSuccessoEliminazione();
                                    running = false;
                                } else {
                                    view.mostraMessaggio("Operazione annullata.");
                                }
                            }
                        } else {
                            view.mostraMessaggio("Nessuno slot disponibile da eliminare.");
                        }
                    }
                    case 0 -> running = false;
                }
            }
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}
