package edu.touro.mco152.bm;

import edu.touro.mco152.bm.commands.CommandInterface;
import edu.touro.mco152.bm.commands.ReadCommand;
import edu.touro.mco152.bm.commands.SimpleInvoker;
import edu.touro.mco152.bm.commands.WriteCommand;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the command pattern logic. Uses hardcoded benchmark variables to test. Instantiates different
 * CommandInterface subtypes to run tests.
 */
public class CommandTest {
    SimpleInvoker invoker = new SimpleInvoker();
    CommandInterface write = new WriteCommand(25, 128, 2048, DiskRun.BlockSequence.SEQUENTIAL);
    CommandInterface read = new ReadCommand(25, 128, 2048, DiskRun.BlockSequence.SEQUENTIAL);
    GUIInterface guiInterface = new NonSwingImplementation();
    // Tests the WriteCommand logic using SimpleInvoker
    @Test
    void writeBenchmarkTest(){
       assertTrue(invoker.invoke(write, guiInterface));
    }
    // Tests the ReadCommand logic using SimpleInvoker
    @Test
    void readBenchmarkTest(){
        assertTrue(invoker.invoke(read, guiInterface));
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
