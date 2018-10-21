package com.zhanghao.jobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by zhanghao on 2018/10/14.
 */
public class AppJobService extends JobService {

    private static final String TAG = "AppJobService";
    private InnerTask mTask;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: invoked");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: invoked");
    }

    // invoked on main thread
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: ");
        mTask = new InnerTask(this);
        mTask.execute(jobParameters);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob: ");
        if (mTask != null) {
            mTask.cancel(true);
        }
        sendMessage();
        return false;
    }

    private void sendMessage() {
        Intent intent = new Intent();
        intent.setAction(PendingJobListActivity.ACTION_JOB_NOTIFY);
        sendBroadcast(intent);
    }

    private static class InnerTask extends AsyncTask<JobParameters, Void, JobParameters> {

        private WeakReference<JobService> mService;

        public InnerTask(JobService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        protected JobParameters doInBackground(JobParameters... jobParameters) {
            Log.d(TAG, "doInBackground: ");
            if (isCancelled()) {
                return null;
            }
            SystemClock.sleep(2 * 1000);
            return jobParameters[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            super.onPostExecute(jobParameters);
            Log.d(TAG, "onPostExecute: done");
            if (jobParameters != null) {
                AppJobService service = (AppJobService) mService.get();
                if (service != null) {
                    service.jobFinished(jobParameters, false);
                    service.sendMessage();
                }
            }
        }
    }

}
