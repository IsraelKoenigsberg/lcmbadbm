package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class BadBMTest {
    // Instantiate a DiskWorker of Non-Swing Type
    DiskWorker worker = new DiskWorker(new NonSwingImplementation());
    // Create a cast of Non-Swing Implementation to access non-overridden methods in NonSwingImplementation class
    NonSwingImplementation currUI = (NonSwingImplementation) worker.guiInterface;

    @Test
    void progress(){
        worker.guiInterface.executeBM();
        ArrayList<Integer> percentList = currUI.getPercentList();
        for (Integer integer : percentList) {
            Assertions.assertTrue(integer <= 100 && integer >= 0);
        }
    }
    @Test
    void finished(){
        worker.guiInterface.executeBM();
       Assertions.assertTrue(currUI.lastStatus);
    }

    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     *
     * @author lcmcohen
     */
    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     *
     * @author lcmcohen
     */
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
