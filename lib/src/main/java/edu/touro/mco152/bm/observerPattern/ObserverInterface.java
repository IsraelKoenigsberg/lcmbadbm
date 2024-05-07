package edu.touro.mco152.bm.observerPattern;

import edu.touro.mco152.bm.persist.DiskRun;

public interface ObserverInterface {
    void update(DiskRun run);
}
