package io.androidx.ffmpeg;

import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class MobileFFmpegImpl implements IRxFFmpegAgent<ExecuteCallback> {

    private int tag = -1;

    public void executeWithCallback(String[] commands, ExecuteCallback ifFmpegCallBack) {
        synchronized (MobileFFmpegImpl.class) {
            List<String> list = new LinkedList<>(Arrays.asList(commands));
            if ("ffmpeg".equalsIgnoreCase(list.get(0))) {
                list.remove(0);
            }
            String[] cmds = new String[list.size()];
            cmds = list.toArray(cmds);
            FFmpeg.executeAsync(cmds, ifFmpegCallBack);
        }
    }

    @Override
    public void executeWithUsrCallBack(String[] commands, UsrFFmpegCallBack usrFFmpegCallBack) {
        synchronized (FFmpegCommandImpl.class) {
            List<String> list = new LinkedList<>(Arrays.asList(commands));
            if ("ffmpeg".equalsIgnoreCase(list.get(0))) {
                list.remove(0);
            }
            String[] cmds = new String[list.size()];
            cmds = list.toArray(cmds);
            FFmpeg.executeAsync(commands, usrFFmpegCallBack == null ? null : new ExecuteCallback() {

                @Override
                public void apply(final long executionId, final int returnCode) {
                    if (returnCode == RETURN_CODE_SUCCESS) {
                        usrFFmpegCallBack.onComplete();
                    } else if (returnCode == RETURN_CODE_CANCEL) {
                        usrFFmpegCallBack.onCancel();
                    } else {
                        usrFFmpegCallBack.onError(returnCode, String.format(Locale.getDefault(), "Failed with returnCode=%d.", returnCode));
                    }
                }
            });
        }
    }

    @Override
    public Flowable<RxStat> executeCommand(String[] commands) {
        return Flowable.create(emitter -> {
            emitter.onNext(new RxStat(RxStat.Status.STATUS_START, tag));
            Config.enableStatisticsCallback(statistics -> {
                Log.i(Config.TAG, statistics.toString());
//                emitter.onNext(new RxStat(RxStat.Status.STATUS_PROGRESS));
            });
            Config.enableLogCallback(logMessage -> {
                Log.i(Config.TAG, logMessage.toString());
                emitter.onNext(new RxStat(RxStat.Status.STATUS_LOG, logMessage.getText(), tag));
            });
            executeWithCallback(commands, new ExecuteCallback() {
                @Override
                public void apply(final long executionId, final int returnCode) {
                    if (returnCode == RETURN_CODE_SUCCESS) {
                        emitter.onNext(new RxStat(RxStat.Status.STATUS_COMPLETE, tag));
                        emitter.onComplete();
                        Log.i(Config.TAG, "Async command execution completed successfully.");
                    } else if (returnCode == RETURN_CODE_CANCEL) {
                        emitter.onNext(new RxStat(RxStat.Status.STATUS_CANCEL, tag));
                        emitter.onComplete();
                        Log.i(Config.TAG, "Async command execution cancelled by user.");
                    } else {
                        emitter.onNext(new RxStat(RxStat.Status.STATUS_ERROR, String.format(Locale.getDefault(), "Failed with returnCode=%d.", returnCode), tag));
                        emitter.onComplete();
                        Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                    }
                }
            });
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<RxStat> executeCommandWithTag(String[] commands, int tag) {
        this.tag = tag;
        return executeCommand(commands);
    }

    @Override
    public void cancelAllTask() {
        FFmpeg.cancel();

    }

    @Override
    public void cancelTask(long taskId) {
        FFmpeg.cancel(taskId);
    }
}
