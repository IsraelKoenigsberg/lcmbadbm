package edu.touro.mco152.bm;

public interface GUIInterface {
    boolean isBMCancelled();
    void setBMProgress(int i);
    void publishToUI(DiskMark mark);
}
