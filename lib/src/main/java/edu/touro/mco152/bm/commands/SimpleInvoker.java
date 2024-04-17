package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.GUIInterface;

/**
 * Simple Invoker that executes commands. May accept any implementation of CommandInterface,
 * regardless of subclass type. Also accepts GUIInterface implementation to execute to allow multiple types
 * of interfaces to be executed.
 */
public class SimpleInvoker {

    public boolean invoke(CommandInterface command, GUIInterface guiInterface) {
        return command.execute(guiInterface);
    }
}

