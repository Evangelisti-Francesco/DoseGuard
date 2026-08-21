package it.ispwproject.doseguard.pattern.state;

public interface CLIStateMachine {

    void start();
    void goNext();
    void goBack();
    void transition(AbstractCLIState nextState);
    AbstractCLIState getState();
    void setState(AbstractCLIState state);
}
