package edu.touro.mco152.bm.observerPattern;

import edu.touro.mco152.bm.persist.DiskRun;

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
