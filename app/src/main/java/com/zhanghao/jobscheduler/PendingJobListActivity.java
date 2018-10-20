package com.zhanghao.jobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhanghao on 2018/10/14.
 */
public class PendingJobListActivity extends AppCompatActivity {
    private static final String TAG = "PendingJobListActivity";
    private ListView mListView;
    private InnerAdapter mInnerAdapter;
    private InnerJobNotifyReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_jobs_main);
        mListView = findViewById(R.id.pending_jobs_list);
        mInnerAdapter = new InnerAdapter();
        mListView.setAdapter(mInnerAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerOnChangeListener();
        mInnerAdapter.setJobInfo(obtainJobScheduler().getAllPendingJobs());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterOnChangeListener();
    }

    private void unregisterOnChangeListener() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    public static final String ACTION_JOB_NOTIFY = "action_job_notify";

    private void registerOnChangeListener() {
        mReceiver = new InnerJobNotifyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_JOB_NOTIFY);
        registerReceiver(mReceiver, intentFilter);
    }

    private JobScheduler obtainJobScheduler() {
        return (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    private class InnerJobNotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            mInnerAdapter.setJobInfo(obtainJobScheduler().getAllPendingJobs());
        }
    }

    private static class InnerAdapter extends BaseAdapter {

        private static final String TAG = "InnerAdapter";
        private List<JobInfo> mJobInfos;

        public void setJobInfo(List<JobInfo> infoList) {
            if (infoList == null) {
                return;
            }
            this.mJobInfos = infoList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mJobInfos != null ? mJobInfos.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mJobInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mJobInfos.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_jobs_item, parent, false);
                holder = new ViewHolder();
                holder.mTextView = convertView.findViewById(R.id.job_content_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            JobInfo info = mJobInfos.get(position);
            int id = info.getId();
            long minLatencyMillis = info.getMinLatencyMillis();
            long maxExecutionDelayMillis = info.getMaxExecutionDelayMillis();
            boolean isPeriodic = info.isPeriodic();
            boolean isRequireCharging = info.isRequireCharging();
            boolean isRequireDeviceIdle = info.isRequireDeviceIdle();
            String content = "id: %s , minLatencyMillis %d , maxExecutionDelayMillis %d , isPeriodic: %s , isRequireCharging: %s , isRequireDeviceIdle: %s , ";
            content = String.format(content,
                    id,
                    minLatencyMillis,
                    maxExecutionDelayMillis,
                    isPeriodic,
                    isRequireCharging,
                    isRequireDeviceIdle);
            holder.mTextView.setText(content);
            return convertView;
        }

        private static class ViewHolder {
            TextView mTextView;
        }
    }

}
