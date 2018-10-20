package com.zhanghao.jobscheduler;

/**
 * Created by zhanghao on 2018/10/14.
 */

import android.content.ContentResolver;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * jobId Application-provided id for this job. Subsequent calls to cancel, or
 * jobs created with the same jobId, will update the pre-existing job with
 * the same id.  This ID must be unique across all clients of the same uid
 * (not just the same package).  You will want to make sure this is a stable
 * id across app updates, so probably not based on a resource ID.
 */

public class JobIdTable implements Serializable {

    private final static List<Integer> sJobIds = new ArrayList<>();

    private static final String TAG = "JobIdTable";
    private static final int MIN_JOB_ID = 0;
    private static final int MAX_JOB_ID = 100;

    private JobIdTable() {

    }

    /**
     * only work on app is alive,just for test
     *
     * @return
     */
    public static int generateUnusedId() {
        int newJobId;
        do {
            newJobId = MIN_JOB_ID + (int) (Math.random() * (MAX_JOB_ID - MIN_JOB_ID + 1));
        } while (isJobIdsUsedLocked(newJobId));
        sJobIds.add(newJobId);
        return newJobId;
    }

    private static boolean isJobIdsUsedLocked(int newJobId) {
        synchronized (sJobIds) {
            return sJobIds.contains(newJobId);
        }
    }

}
