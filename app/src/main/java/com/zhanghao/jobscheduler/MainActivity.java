package com.zhanghao.jobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Switch mPeriodicSw;
    private EditText mPeriodicIntervalMillisEt;

    private Switch mMinimumLatencySw;
    private EditText mMinLatencyMillisEt;

    private Switch mOverrideDeadlineSw;
    private EditText mMaxExecutionDelayMillisEt;

    private Switch mRequireWifiSw;

    private Switch mRequiresDeviceChargingSw;

    private Switch mRequiresDeviceIdleSw;

    private Button mExecuteBtn, mCancelBtn, mCancelAllBtn, mViewAllBtn;
    private EditText mCancelIdEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mPeriodicSw = findViewById(R.id.periodic_sw);
        mPeriodicIntervalMillisEt = findViewById(R.id.periodic_et);

        mMinimumLatencySw = findViewById(R.id.minimumLatency_sw);
        mMinLatencyMillisEt = findViewById(R.id.minimumLatency_et);
        mOverrideDeadlineSw = findViewById(R.id.override_dead_line_sw);
        mMaxExecutionDelayMillisEt = findViewById(R.id.override_dead_line_et);

        mRequireWifiSw = findViewById(R.id.require_wifi_sw);
        mRequiresDeviceChargingSw = findViewById(R.id.requires_device_charging_sw);
        mRequiresDeviceIdleSw = findViewById(R.id.requires_device_Idle_sw);

        mExecuteBtn = findViewById(R.id.execute_bt);
        mCancelBtn = findViewById(R.id.cancel_bt);
        mCancelIdEt = findViewById(R.id.cancel_id_et);
        mCancelAllBtn = findViewById(R.id.cancel_all_bt);
        mCancelIdEt = findViewById(R.id.cancel_id_et);
        mViewAllBtn = findViewById(R.id.view_all_bt);

        mPeriodicSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMinimumLatencySw.setEnabled(!isChecked);
                mMinLatencyMillisEt.setEnabled(!isChecked);
                mOverrideDeadlineSw.setEnabled(!isChecked);
                mMaxExecutionDelayMillisEt.setEnabled(!isChecked);
            }
        });

        mMinimumLatencySw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPeriodicIntervalMillisEt.setEnabled(!isChecked);
                mPeriodicSw.setEnabled(!isChecked);
            }
        });

        mExecuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndExecuteJob(MainActivity.this);
            }
        });

        mCancelAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAllJobs();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mCancelIdEt.getText().toString();
                if (!TextUtils.isEmpty(id)){
                    cancelJob(Integer.parseInt(id));
                }
            }
        });

        mViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PendingJobListActivity.class));
            }
        });
    }

    /**
     * @param context
     */
    private void createAndExecuteJob(Context context) {
        final JobInfo.Builder jobBuilder = new JobInfo.Builder(JobIdTable.generateUnusedId(),
                new ComponentName(context, AppJobService.class));

        if (mPeriodicSw.isChecked()) {
            String periodicIntervalMillis = mPeriodicIntervalMillisEt.getText().toString();
            if (!TextUtils.isEmpty(periodicIntervalMillis)) {
                /**
                 * {@link JobInfo#getFlexMillis()}
                 */
                jobBuilder.setPeriodic(Long.valueOf(periodicIntervalMillis));

            }
        }

        if (mMinimumLatencySw.isChecked()) {
            String minLatencyMillis = mMinLatencyMillisEt.getText().toString();
            if (!TextUtils.isEmpty(minLatencyMillis)) {
                jobBuilder.setMinimumLatency(Long.valueOf(minLatencyMillis));
            }
        }

        if (mOverrideDeadlineSw.isChecked()) {
            String maxExecutionDelayMillis = mMaxExecutionDelayMillisEt.getText().toString();
            if (!TextUtils.isEmpty(maxExecutionDelayMillis)) {
                jobBuilder.setOverrideDeadline(Long.valueOf(maxExecutionDelayMillis));
            }
        }

        if (mRequireWifiSw.isChecked()) {
            jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        } else {
            jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }

        if (mRequiresDeviceChargingSw.isChecked()) {
            jobBuilder.setRequiresCharging(true);
        }

        if (mRequiresDeviceIdleSw.isChecked()) {
            jobBuilder.setRequiresDeviceIdle(true);
        }
        // ... and so on
        obtainJobScheduler().schedule(jobBuilder.build());
        Log.d(TAG, "createAndExecuteJob: ");

    }

    private void cancelJob(int id) {
        obtainJobScheduler().cancel(id);
    }

    private void cancelAllJobs() {
        obtainJobScheduler().cancelAll();
    }

    private JobScheduler obtainJobScheduler() {
        return (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

}
