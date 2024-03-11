package edu.touro.mco152.bm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.Callable;

public interface GUIInterface  {
    boolean isBMCancelled();
    void setBMProgress(int i);
    void publishToUI(DiskMark mark);
    void BMDone();
    boolean getStatus();
    void cancelBM(boolean bool);
    void executeBM();
    void addPropertyChangeListenerBM(PropertyChangeListener listener);
    void setWork(Callable<Boolean> bool);
}
