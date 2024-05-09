package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.GUIInterface;
import edu.touro.mco152.bm.Util;
import edu.touro.mco152.bm.observerPattern.ObservableInterface;
import edu.touro.mco152.bm.observerPattern.ObserverInterface;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

/**
 * Writes to the benchmark.
 * A command class that utilizes SimpleInvoker and implements CommandInterface
 * to execute its writes.
 * An Observable subject class that notifies observers when action has been taken.
 */
public class WriteCommand implements CommandInterface, ObservableInterface {
    private ArrayList<ObserverInterface> observerList = new ArrayList<>();
    private final int marksNum;
    private final int blocksNum;
    private final int blockSizeInKb;
    private final DiskRun.BlockSequence blockSequence;
    private DiskRun run;

    /**
     * Constructor for WriteCommand.
     * Accepts benchmark variables to use for benchmark writes.
     *
     * @param marksNum      number of marks in a write
     * @param blocksNum     number of blocks in a write
     * @param blockSizeInKb size of a block in KB
     * @param blockSequence sequence of the blocks
     */
    public WriteCommand(int marksNum, int blocksNum, int blockSizeInKb,
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

        DiskMark wMark;
        int startFileNum = App.nextMarkNumber;

        run = new DiskRun(DiskRun.IOMode.WRITE, blockSequence);
        run.setNumMarks(marksNum);
        run.setNumBlocks(blocksNum);
        run.setBlockSize(blockSizeInKb);
        run.setTxSize(targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        // Tell logger and GUI to display what we know so far about the Run
        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        // Create a test data file using the default file system and config-specified location
        if (!multiFile) {
            testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        }

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
            wMark = new DiskMark(WRITE);    // starting to keep track of a new benchmark
            wMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (writeSyncEnable) {
                mode = "rwd";
            }

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < blocksNum; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, blocksNum - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.write(blockArr, 0, blockSize);
                        totalBytesWrittenInMark += blockSize;
                        wUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;

                         /*
                           Report to GUI what percentage level of Entire BM (#Marks * #Blocks) is done.
                          */
                        guiInterface.setBMProgress((int) percentComplete);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }

             /*
               Compute duration, throughput of this Mark's step of BM
              */
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);
            msg("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s     "
                    + "(" + Util.displayString(mbWritten) + "MB written in "
                    + Util.displayString(sec) + " sec)");
            updateMetrics(wMark);

             /*
               Let the GUI know the interim result described by the current Mark
              */
            guiInterface.publishToUI(wMark);

            // Keep track of statistics to be displayed and persisted after all Marks are done.
            run.setRunMax(wMark.getCumMax());
            run.setRunMin(wMark.getCumMin());
            run.setRunAvg(wMark.getCumAvg());
            run.setEndTime(new Date());
        } // END outer loop for specified duration (number of 'marks') for WRITE benchmark
        // Notify any observers of this subject class to update their data
        notifyObservers();
        return true;
    }

    /**
     * Registers an observer of the WriteCommand subject class to be notified
     * @param observer
     */
    @Override
    public void registerObserver(ObserverInterface observer) {
        observerList.add(observer);
    }
    /**
     * Unregisters an observer of the WriteCommand subject class to no longer be notified
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
