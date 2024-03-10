package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.dataDir;

public class SwingImplementation extends SwingWorker<Boolean, DiskMark> implements GUIInterface{
    DiskWorker diskWorker;
    Boolean lastStatus = null;
    SwingImplementation(){
        diskWorker = new DiskWorker();
    }
    @Override
    public boolean isBMCancelled() {
        boolean canc = isCancelled();
        System.out.println("Canc " + canc);
        return isCancelled();
    }
    @Override
    public void cancelBM(boolean bool) {

       cancel(bool);

    }
    @Override
    public void setBMProgress(int i) {

        setProgress(i);
    }

    @Override
    public void publishToUI(DiskMark mark) {
        publish(mark);
    }
    /**
     * Called when doInBackGround method of SwingWorker successfully or unsuccessfully finishes or is aborted.
     * This method is called by Swing and has access to the get method within it's scope, which returns the computed
     * result of the doInBackground method.
     */
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

        try {
            lastStatus = get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
return lastStatus;
    }


    @Override
    public void executeBM(){
        execute();
    }

    @Override
    public void addPropertyChangeListenerBM(PropertyChangeListener listener) {
        addPropertyChangeListener(listener);
    }

    @Override
    protected void process(List<DiskMark> markList) {

                markList.stream().forEach((dm) -> {
                    if (dm.type == DiskMark.MarkType.WRITE) {
                        Gui.addWriteMark(dm);
                    } else {
                        Gui.addReadMark(dm);
                    }
                });
    }

    @Override
    protected Boolean doInBackground() throws Exception {

       return diskWorker.doWork();

    }
    @Override
    protected void done(){
        BMDone();
    }

}
