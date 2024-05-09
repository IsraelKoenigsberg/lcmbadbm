package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.GUIInterface;
import edu.touro.mco152.bm.Util;
import edu.touro.mco152.bm.observerPattern.ObservableInterface;
import edu.touro.mco152.bm.observerPattern.ObserverInterface;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.App.updateMetrics;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;

/**
 * Reads from the benchmark.
 * A command class that utilizes SimpleInvoker and implements CommandInterface
 * to execute its writes.
 * An Observable subject class that notifies observers when action has been taken.
 */
public class ReadCommand implements CommandInterface, ObservableInterface {
    ArrayList<ObserverInterface> observerList = new ArrayList<>();
    private final int marksNum;
    private final int blocksNum;
    private final int blockSizeInKb;
    private final DiskRun.BlockSequence blockSequence;
    private DiskRun run;

    /**
     * Constructor for ReadCommand.
     * Accepts benchmark variables to use for benchmark writes.
     *
     * @param marksNum      number of marks in a read
     * @param blocksNum     number of blocks in a read
     * @param blockSizeInKb size of a block in KB
     * @param blockSequence sequence of the blocks
     */
    public ReadCommand(int marksNum, int blocksNum, int blockSizeInKb,
                       DiskRun.BlockSequence blockSequence) {
        this.marksNum = marksNum;
        this.blocksNum = blocksNum;
        this.blockSizeInKb = blockSizeInKb;
        this.blockSequence = blockSequence;

    }

    // Overrides the execute method from CommandInterface, in line with the Command Pattern.
    @Override
    public boolean execute(GUIInterface guiInterface) {
        int wUnitsComplete = 0,
                rUnitsComplete = 0,
                unitsComplete;

        int wUnitsTotal = App.writeTest ? blocksNum * marksNum : 0;
        int rUnitsTotal = App.readTest ? blocksNum * marksNum : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        int blockSize = blockSizeInKb * KILOBYTE;
        byte[] blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }

        DiskMark rMark;
        int startFileNum = App.nextMarkNumber;

        run = new DiskRun(DiskRun.IOMode.READ, blockSequence);
        run.setNumMarks(marksNum);
        run.setNumBlocks(blocksNum);
        run.setBlockSize(blockSizeInKb);
        run.setTxSize(targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));
        // Tell logger and GUI to display what we know so far about the Run
        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());
        /*
           Begin an outer loop for specified duration (number of 'marks') of benchmark,
           that keeps writing data (in its own loop - for specified # of blocks). Each 'Mark' is timed
           and is reported to the GUI for display as each Mark completes.
          */
        for (int m = startFileNum; m < startFileNum + marksNum && !guiInterface.isBMCancelled(); m++) {

            if (multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            rMark = new DiskMark(READ);  // starting to keep track of a new benchmark
            rMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesReadInMark = 0;

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, "r")) {
                    for (int b = 0; b < blocksNum; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, blocksNum - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.readFully(blockArr, 0, blockSize);
                        totalBytesReadInMark += blockSize;
                        rUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;
                        guiInterface.setBMProgress((int) percentComplete);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                String emsg = "May not have done Write Benchmarks, so no data available to read." +
                        ex.getMessage();
                JOptionPane.showMessageDialog(Gui.mainFrame, emsg, "Unable to READ", JOptionPane.ERROR_MESSAGE);
                msg(emsg);
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesReadInMark / (double) MEGABYTE;
            rMark.setBwMbSec(mbRead / sec);
            msg("m:" + m + " READ IO is " + rMark.getBwMbSec() + " MB/s    "
                    + "(MBread " + mbRead + " in " + sec + " sec)");
            updateMetrics(rMark);
            guiInterface.publishToUI(rMark);

            run.setRunMax(rMark.getCumMax());
            run.setRunMin(rMark.getCumMin());
            run.setRunAvg(rMark.getCumAvg());
            run.setEndTime(new Date());
        }
        // Notify any observers of this subject class to update their data
        notifyObservers();
        return true;
    }
    /**
     * Registers an observer of the ReadCommand subject class to be notified
     * @param observer
     */
    @Override
    public void registerObserver(ObserverInterface observer) {
        observerList.add(observer);
    }
    /**
     * Unregisters an observer of the ReadCommand subject class to no longer be notified
     * @param observer
     */
    @Override
    public void unregisterObserver(ObserverInterface observer) {
        observerList.remove(observer);
    }
    /**
     * Notifies any registered observers
     */
    @Override
    public void notifyObservers() {
        for (ObserverInterface observer : observerList) {
            observer.update(run);
        }
    }
}
