package edu.touro.mco152.bm.slack;

import edu.touro.mco152.bm.observerPattern.ObserverInterface;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.slack.SlackManager;

public class SlackObserver implements ObserverInterface {
    @Override
    public void update(DiskRun run) {
        if (exceedsThreePercent(run) && readIoMode(run)) {
            sendSlackMessage();
        }
    }

    private void sendSlackMessage() {
        SlackManager slackManager = new SlackManager("lcmBadBm");
        slackManager.postMsg2OurChannel("WARNING!! The max iteration time has exceeded 3 percent of average time!!!");
    }

    private boolean exceedsThreePercent(DiskRun run) {
        return run.getRunMax() > run.getRunAvg() * 1.03;
    }

    private boolean readIoMode(DiskRun run) {
        return run.getIoMode() == DiskRun.IOMode.READ;
    }
}
