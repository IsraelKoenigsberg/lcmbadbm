package edu.touro.mco152.bm;

import java.beans.PropertyChangeListener;
import java.util.concurrent.Callable;
public interface GUIInterface  {
    /**
     Checks if the benchmarking has been cancelled.
     @return true if cancelled, false otherwise.
     */
    boolean isBMCancelled();
    /**
     Tracks and sets the progress of the benchmark system.
     @param i an integer representing the progress of the benchmark system, ranging from 1 to 100
     where 1 indicates the starting point and 100 indicates completion.
     */
    void setBMProgress(int i);

    /**
     * Displays benchmark information.
     * @param mark a DiskMark chunk of benchmark checking to be displayed or processed.
     */
    void publishToUI(DiskMark mark);
    /**
     * Called after background processing is finished. Handles cleanup tasks,
     * updates the UI, and records final task status. If autoRemoveData is
     * true, deletes the data directory. Adjusts the application state and
     * UI sensitivity.
     */
    void BMDone();

    /**
     * Checks completion status of BadBm.
     * @return true if system completed successfully or unsuccessfully.
     */
    boolean getStatus();

    /**
     * Cancels Benchmark system from completing checks.
     * @param bool is always true to cancel system.
     */
    void cancelBM(boolean bool);

    /**
     * Runs Benchmark system. Called from App.
     */
    void executeBM();

    /**
     *Adds a PropertyChangeListener to the listener list. The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called as many times as it is added.
     * If listener is null, no exception is thrown and no action is taken.
     * @param listener the PropertyChangeListener to be added
     */
    void addPropertyChangeListenerBM(PropertyChangeListener listener);

    /**
     * Runs the setWork method in DiskWorker.
     * The GUI Implementation type, Swing or other, is passed to DiskWorker through the Callable.
     * @param bool the return type of DiskWorker - true if task completed.
     */
    void setWork(Callable<Boolean> bool);
}
