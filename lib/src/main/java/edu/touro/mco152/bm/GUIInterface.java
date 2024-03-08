package edu.touro.mco152.bm;

import java.util.List;

public interface GUIInterface <T> {
    boolean isBMCancelled();
    void setBMProgress(int i);
    void publishToUI(DiskMark mark);
    void BMDone();
    boolean getStatus();

}
