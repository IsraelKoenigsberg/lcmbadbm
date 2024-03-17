package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.dataDir;

public class NonSwingImplementation implements GUIInterface {
    Callable<Boolean> callable;
    Boolean cancelled = false;
    Boolean lastStatus = null;
    public ArrayList<Integer> percentList = new ArrayList<>();
    @Override
    public boolean isBMCancelled() {
        return cancelled;
    }
    @Override
    public void cancelBM(boolean bool) {
        this.setIsCancelled(bool);

    }
    private void setIsCancelled(Boolean isCancelled){
        cancelled = isCancelled;
    }

    @Override
    public void setBMProgress(int i) {
        setProgress(i);
    }
    public void setProgress(int i){

        if (i < 0 || i > 100){
            throw new IllegalArgumentException();
        }
        if (percentList.isEmpty()) percentList.add(i);
        else {
            if (!percentList.contains(i)) percentList.add(i);
        }

    }
    public ArrayList<Integer> getPercentList(){
        return percentList;
    }

    @Override
    public void publishToUI(DiskMark mark) {
        System.out.println(mark);
    }

    @Override
    public void BMDone() {
        // Obtain final status, might from doInBackground ret value, or SwingWorker error
        try {
            lastStatus = getStatus();   // record for future access
        } catch (Exception e) {
            Logger.getLogger(App.class.getName()).warning("Problem obtaining final status: " + e.getMessage());
        }

        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }

    @Override
    public boolean getStatus() {
        return lastStatus;

    }

    @Override
    public void executeBM() {

        try {
            lastStatus = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void addPropertyChangeListenerBM(PropertyChangeListener listener) {

    }

    @Override
    public void setWork(Callable<Boolean> bool) {
        callable = bool;

    }
}
