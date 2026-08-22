package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.CLIStateMachine;
import it.ispwproject.doseguard.pattern.state.CLIStateMachineImpl;

public class MainCLI {

    private MainCLI() {}

    public static void start() {
        CLIStateMachine machine = new CLIStateMachineImpl();
        machine.start();
    }
}
