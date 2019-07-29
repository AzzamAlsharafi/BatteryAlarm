package multiplies.batteryalarm.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class BroadcastReceiverControllerJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("BTAG", "JobService, onStartJob(1), Line 11");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("BTAG", "JobService, onStopJop(1), Line 17");
        return true;
    }
}
