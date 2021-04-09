package io.androidx.ffmpeg;

import com.yyl.ffmpeg.FFmpegCallBack;
import com.yyl.ffmpeg.FFmpegUtils;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class FFmpegSdkImpl implements IRxFFmpegAgent<FFmpegCallBack> {

    private static FFmpegUtils instance;
    private int tag = -1;

    @Override
    public void executeWithCallback(String[] commands, FFmpegCallBack ifFmpegCallBack) {
        synchronized (FFmpegSdkImpl.class) {
            if (instance == null) {
                instance = FFmpegUtils.getInstance();
            }
            if (instance.isRun()) {
                cancelAllTask();
            }
            instance.setDebugMode(true);
            instance.isShowLogcat(true);
            instance.execffmpeg(commands, ifFmpegCallBack);
        }
    }

    @Override
    public void executeWithUsrCallBack(String[] commands, UsrFFmpegCallBack usrFFmpegCallBack) {
        synchronized (FFmpegCommandImpl.class) {
            if (instance == null) {
                instance = FFmpegUtils.getInstance();
            }
            if (instance.isRun()) {
                cancelAllTask();
            }
            instance.setDebugMode(true);
            instance.isShowLogcat(true);
            instance.execffmpeg(commands, usrFFmpegCallBack == null ? null : new FFmpegCallBack() {

                @Override
                public void onStart() {
                    usrFFmpegCallBack.onStart();
                }

                @Override
                public void onCallBackLog(String log) {

                }

                @Override
                public void onCallBackPrint(String msg) {

                }

                @Override
                public void onProgress(int frame_number, int milli_second) {
                    usrFFmpegCallBack.onProgress(frame_number, milli_second);
                }

                @Override
                public void onSuccess() {
                    usrFFmpegCallBack.onComplete();
                }

                @Override
                public void onFailure(int result) {
                    usrFFmpegCallBack.onError(result, "");
                }
            });
        }
    }

    @Override
    public Flowable<RxStat> executeCommand(String[] commands) {
        return Flowable.create(emitter -> {
            executeWithCallback(commands, new FFmpegCallBack() {

                @Override
                public void onStart() {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_START, tag));
                }

                @Override
                public void onCallBackLog(String log) {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_LOG_PRINT, log, tag));
                }

                @Override
                public void onCallBackPrint(String msg) {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_LOG, msg, tag));
                }

                @Override
                public void onProgress(int frame_number, int milli_second) {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_PROGRESS, frame_number, milli_second, tag));
                }

                @Override
                public void onSuccess() {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_COMPLETE, tag));
                    emitter.onComplete();
                }

                @Override
                public void onFailure(int result) {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_ERROR, String.valueOf(result), tag));
                    emitter.onComplete();
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
        if (instance != null) {
            instance.exitffmpeg();
        }
    }

    @Override
    public void cancelTask(long taskId) {
        if (instance != null) {
            instance.exitffmpeg();
        }
    }


}
