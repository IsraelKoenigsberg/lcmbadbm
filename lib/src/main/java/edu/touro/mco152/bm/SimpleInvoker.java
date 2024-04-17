package edu.touro.mco152.bm;

public class SimpleInvoker {

    public boolean invoke(CommandInterface command, GUIInterface guiInterface) {
        return command.execute(guiInterface);
    }
}

