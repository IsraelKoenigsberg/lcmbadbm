package edu.touro.mco152.bm;

import javax.swing.*;

public class SwingImplementation extends SwingWorker<Boolean, DiskMark> implements GUIInterface{

    @Override
    public boolean isBMCancelled() {
        return isCancelled();
    }

    @Override
    public void setBMProgress(int i) {

        setProgress(i);
    }

    @Override
    public void publishToUI(DiskMark mark) {
        publish(mark);
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return null;
    }
}
