package edu.touro.mco152.bm.observerPattern;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * Observer interface to force subclasses to update when subject notifies.
 */
public interface ObserverInterface {
    /**
     * Update method for all observers.
     * @param run
     */
    void update(DiskRun run);
}
