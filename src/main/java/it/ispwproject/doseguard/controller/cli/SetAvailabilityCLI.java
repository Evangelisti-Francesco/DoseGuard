package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.bean.TimeSlotBean;
import it.ispwproject.doseguard.controller.applicativo.AvailabilityController;
import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.exception.AvailabilityException;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.cli.SetAvailabilityView;

import java.time.LocalDate;
import java.time.LocalTime;

public class SetAvailabilityCLI extends AbstractCLIState {

    private final AvailabilityController availabilityController = new AvailabilityController();
    private final SetAvailabilityView view = new SetAvailabilityView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            LocalDate date      = view.chiediData();
            LocalTime startTime = view.chiediOra("Ora inizio");

            TimeSlotBean slotBean = new TimeSlotBean(0, date, startTime, true);
            view.mostraMessaggio("  Data     : " + date);
            view.mostraMessaggio("  Orario   : " + startTime);

            if (!view.chiediConferma("Confermare lo slot?")) {
                view.mostraMessaggio("Operazione annullata.");
                goBack(context);
                return;
            }

            availabilityController.addSlot(slotBean);
            view.mostraSuccesso();

        } catch (DAOException | AvailabilityException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}
