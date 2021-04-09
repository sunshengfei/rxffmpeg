package io.androidx.ffmpeg;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.androidx.ffmpeg.RxStat.Status.STATUS_CANCEL;
import static io.androidx.ffmpeg.RxStat.Status.STATUS_COMPLETE;
import static io.androidx.ffmpeg.RxStat.Status.STATUS_ERROR;
import static io.androidx.ffmpeg.RxStat.Status.STATUS_LOG;
import static io.androidx.ffmpeg.RxStat.Status.STATUS_LOG_PRINT;
import static io.androidx.ffmpeg.RxStat.Status.STATUS_PREPARE;
import static io.androidx.ffmpeg.RxStat.Status.STATUS_PROGRESS;
import static io.androidx.ffmpeg.RxStat.Status.STATUS_START;

public class RxStat {

    private long taskId;

    private int tag;

    @Status
    private int status;

    private CharSequence description;

    private int progress;

    private long progressUts;

    public RxStat(@Status int status) {
        this.status = status;
    }

    public RxStat(@Status int status, @NonNull CharSequence description) {
        this.status = status;
        this.description = description;
    }

    public RxStat(@Status int status, int progress, long progressUts) {
        this.status = status;
        this.progress = progress;
        this.progressUts = progressUts;
    }

    public RxStat(int status, int tag) {
        this.tag = tag;
        this.status = status;
    }

    public RxStat(@Status int status, @NonNull CharSequence description, int tag) {
        this.tag = tag;
        this.status = status;
        this.description = description;
    }

    public RxStat(@Status int status, int progress, long progressUts, int tag) {
        this.tag = tag;
        this.status = status;
        this.progress = progress;
        this.progressUts = progressUts;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(@Status int status) {
        this.status = status;
    }


    public CharSequence getDescription() {
        return description;
    }

    public void setDescription(CharSequence description) {
        this.description = description;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getProgressUts() {
        return progressUts;
    }

    public void setProgressUts(long progressUts) {
        this.progressUts = progressUts;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean isSuccessful() {
        return (status & STATUS_COMPLETE) == STATUS_COMPLETE && (status ^ STATUS_COMPLETE) == 0;
    }

    @IntDef({
            STATUS_PREPARE,
            STATUS_START,
            STATUS_PROGRESS,
            STATUS_COMPLETE,
            STATUS_LOG,
            STATUS_LOG_PRINT,
            STATUS_CANCEL,
            STATUS_ERROR
    })
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    public @interface Status {
        int STATUS_PREPARE = -1;
        int STATUS_START = 1;
        int STATUS_PROGRESS = 1 << 1;
        int STATUS_COMPLETE = 1 << 2;
        int STATUS_LOG = 1 << 7;
        int STATUS_LOG_PRINT = 1 << 7 | 1;
        int STATUS_CANCEL = STATUS_COMPLETE | 1 << 3;
        int STATUS_ERROR = STATUS_COMPLETE | 1 << 4;
    }
}
