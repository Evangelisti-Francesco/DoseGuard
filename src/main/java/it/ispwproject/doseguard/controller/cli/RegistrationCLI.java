package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.bean.SpecializationBean;
import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.bean.RegistrationBean;
import it.ispwproject.doseguard.controller.applicativo.RegistrationController;
import it.ispwproject.doseguard.enumerator.Role;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.exception.RegistrationException;
import it.ispwproject.doseguard.view.cli.RegistrationView;

import java.util.List;

public class RegistrationCLI extends AbstractCLIState {

    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationView view = new RegistrationView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            RegistrationBean bean = new RegistrationBean();
            bean.setName(view.chiediCampo("Nome"));
            bean.setSurname(view.chiediCampo("Cognome"));
            bean.setEmail(view.chiediCampo("Email"));
            bean.setPassword(view.chiediPassword("Password"));
            bean.setConfirmPassword(view.chiediPassword("Conferma password"));

            Role role = view.chiediRuolo();
            bean.setRole(role);

            // Gestione campi specifici in base al ruolo scelto
            if (role == Role.PATIENT) {
                bean.setFiscalCode(view.chiediCampo("Codice Fiscale"));
            } else if (role == Role.DOCTOR) {
                String specName = view.chiediCampo("Specializzazione");
                SpecializationBean specBean = new SpecializationBean(0, specName);
                bean.setSpecializations(List.of(specBean));
            } else if (role == Role.PHARMACIST) {
                // Aggiungi qui la richiesta del nome della farmacia
                String pharmacyName = view.chiediCampo("Nome Farmacia");
                bean.setPharmacyName(pharmacyName);
            }

            registrationController.register(bean);
            view.mostraSuccesso();
            goNext(context, new LoginCLI());

        } catch (RegistrationException e) {
            view.mostraErrore(e.getMessage());
            goNext(context, this);
        } catch (DAOException e) {
            view.mostraErrore("Errore di sistema: " + e.getMessage());
            goNext(context, new LoginCLI());
        }
    }
}