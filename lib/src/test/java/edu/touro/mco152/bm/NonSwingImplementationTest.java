package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

public class NonSwingImplementationTest {

    // Instantiate a DiskWorker of Non-Swing Type
    DiskWorker NonSwingWorker = new DiskWorker(new NonSwingImplementation());
    // Create a cast of Non-Swing Implementation to access non-overridden methods in NonSwingImplementation class
    NonSwingImplementation NonSwingUI = (NonSwingImplementation) NonSwingWorker.guiInterface;

    /**
     * Boundary Condition check - numeric overflows.
     * Checks the non-Swing implementation of the progress method to ensure progress does not exceed 100
     */
    @Test
    void progressLessThan100(){
        NonSwingWorker.guiInterface.executeBM();
        ArrayList<Integer> percentList = NonSwingUI.getPercentList();
        for (Integer integer : percentList) {
            assertTrue(integer <= 100);
        }
    }
    /**
     * Boundary Condition check - numeric overflows.
     * Checks the non-Swing implementation of the progress method to ensure progress does not less than zero
     */
    @Test
    void progressMoreThanZero(){
        NonSwingWorker.guiInterface.executeBM();
        ArrayList<Integer> percentList = NonSwingUI.getPercentList();
        for (Integer integer : percentList) {
            assertTrue(integer >= 0);
        }
    }

    /**
     * Tests for Performance.
     * Ensures that the Non-Swing Implementation of BadBM takes less 10 seconds
     */
    @Test
    void execute(){
        long performanceBoundary = 10000;
        for (int i = 0; i < 5; i ++){
            long currTime = System.currentTimeMillis();
            NonSwingWorker.guiInterface.executeBM();
            long finishTime = System.currentTimeMillis();
            assertTrue(finishTime - currTime < performanceBoundary);
        }
    }

    @BeforeAll
    public static void setupDefaultAsPerProperties()
    {
        /// Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();

        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference

        // may be null when tests not run in original proj dir, so use a default area
        if (App.locationDir == null) {
            App.locationDir = new File(System.getProperty("user.home"));
        }

        App.dataDir = new File(App.locationDir.getAbsolutePath()+File.separator+App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }
}
