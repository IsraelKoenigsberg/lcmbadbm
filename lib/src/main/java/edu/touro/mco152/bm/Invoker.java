package edu.touro.mco152.bm;

public class Invoker {

    public void invoke(CommandInterface command, GUIInterface guiInterface) {
        command.execute(guiInterface);
    }
}

