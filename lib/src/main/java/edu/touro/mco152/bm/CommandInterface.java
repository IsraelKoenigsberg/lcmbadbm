package edu.touro.mco152.bm;

/**
 * Command interface. Contains in execute method that spearheads the command pattern.
 * Implemented by the commands called in DiskWorker and passed to SimpleInvoker for execution.
 */
public interface CommandInterface {

    boolean execute(GUIInterface guiInterface);
}
