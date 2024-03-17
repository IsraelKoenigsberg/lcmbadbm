package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.File;
import java.util.Properties;


import static org.junit.jupiter.api.Assertions.*;

public class SwingImplementationTest {
    DiskWorker swingWorker = new DiskWorker(new SwingImplementation());

    // Create a cast of Swing Implementation to access non-overridden methods in SwingImplementation class
    SwingImplementation swingUI = (SwingImplementation) swingWorker.guiInterface;
    /**
     * Tests the cancel and isCancelled methods of Swing. Ensures that when the cancel method is
     * set to true that isCanceled gets set to true also and vice versa
     * @param booleans true if cancel is set to true and false if cancel is set to false
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void cancelBM(Boolean booleans){
        swingWorker.guiInterface.executeBM();
        swingWorker.guiInterface.cancelBM(booleans);
        assertTrue(swingWorker.guiInterface.isBMCancelled());
    }

    /**
     * Tests the completion status of BadBM Swing Implementation.
     * Cross-Checks (C of Bicep) the result by checking both the result of doInBackground and getStatus,
     * as both should be true if BadBM completes.
     */
    @Test
    void status(){
        swingWorker.guiInterface.executeBM();
        try {
            boolean backgroundCheck = swingUI.doInBackground();
            boolean statusCheck = swingUI.getStatus();
            assertTrue(backgroundCheck);
            assertTrue(statusCheck);
            assertEquals(backgroundCheck, statusCheck);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Forces an error to happen.
     * The progress method cannot accept negative numbers
     * This test will FAIL and throw illegal argument exception
     */
    @Test
    void setProgress(){
        int badNum = -1;
        assertDoesNotThrow(() -> {
            swingWorker.guiInterface.executeBM();
            swingWorker.guiInterface.setBMProgress(badNum);
        }, "Illegal Argument Exception" );
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


