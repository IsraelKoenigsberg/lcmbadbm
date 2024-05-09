package edu.touro.mco152.bm.observerPattern;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * Test observer created for testing the Observer Pattern
 * Sets flag to true if the overridden observer update method is called.
 */
public class TestObserver implements ObserverInterface{
    private boolean observed = false;
    @Override
    public void update(DiskRun run) {
        observed = true;
    }
    public boolean getObserved(){
        return observed;
    }
}
