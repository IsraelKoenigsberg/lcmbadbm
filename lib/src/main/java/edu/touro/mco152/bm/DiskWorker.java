package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;

/**
 * Run the disk benchmarking. Can be run using any user interface type.
 * <p>
 * User interface depends on {@link GUIInterface} abstraction to allow any user interface approach to run it.
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks, all of which is done in the doInBackground method.
 * It is instantiated by the startBenchmark method.
 * <p>
 */

public class DiskWorker {
    GUIInterface guiInterface;
    CommandInterface write = new Write(numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
    CommandInterface read = new Read(numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
       Invoker invoker = new Invoker();
    DiskWorker(GUIInterface guiInterface) {
        this.guiInterface = guiInterface;
        guiInterface.setWork(() -> {
            Logger.getLogger(App.class.getName()).log(Level.INFO, "*** New worker thread started ***");
            msg("Running readTest " + readTest + "   writeTest " + writeTest);
            msg("num files: " + numOfMarks + ", num blks: " + numOfBlocks
                    + ", blk size (kb): " + blockSizeKb + ", blockSequence: " + blockSequence);
     /*
       init local vars that keep track of benchmarks, and a large read/write buffer
      */
            int blockSize = blockSizeKb * KILOBYTE;
            byte[] blockArr = new byte[blockSize];
            for (int b = 0; b < blockArr.length; b++) {
                if (b % 2 == 0) {
                    blockArr[b] = (byte) 0xFF;
                }
            }
            Gui.updateLegend();  // init chart legend info
            if (autoReset) {
                resetTestData();
                Gui.resetTestData();
            }

            if (writeTest) invoker.invoke(write, guiInterface);
            tryRenamingAllFilesToClearCatch();
            // Same as above, just for Read operations instead of Writes.

            if (readTest) invoker.invoke(read, guiInterface);
            nextMarkNumber += numOfMarks;
            return true;
        });
    }
    public void tryRenamingAllFilesToClearCatch(){
         /*
       Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
       make it more 'fair'. For example a networking benchmark might close and re-open sockets,
       a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
      */
        // try renaming all files to clear catch
        if (readTest && writeTest && !guiInterface.isBMCancelled()) {
            JOptionPane.showMessageDialog(Gui.mainFrame,
                    """
                            For valid READ measurements please clear the disk cache by
                            using the included RAMMap.exe or flushmem.exe utilities.
                            Removable drives can be disconnected and reconnected.
                            For system drives use the WRITE and READ operations\s
                            independantly by doing a cold reboot after the WRITE""",
                    "Clear Disk Cache Now", JOptionPane.PLAIN_MESSAGE);
        }
    }
}