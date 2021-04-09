package io.androidx.ffmpeg;

import com.coder.ffmpeg.call.IFFmpegCallBack;
import com.coder.ffmpeg.jni.FFmpegCommand;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class FFmpegCommandImpl implements IRxFFmpegAgent<IFFmpegCallBack> {

    private static FFmpegCommand instance;
    private int tag = -1;

    @Override
    public void executeWithCallback(String[] commands, IFFmpegCallBack ifFmpegCallBack) {
        synchronized (FFmpegCommandImpl.class) {
            if (instance == null) {
                instance = FFmpegCommand.INSTANCE;
            }
            instance.setDebug(true);
            instance.runCmd(commands, ifFmpegCallBack);
        }
    }

    @Override
    public void executeWithUsrCallBack(String[] commands, UsrFFmpegCallBack usrFFmpegCallBack) {
        synchronized (FFmpegCommandImpl.class) {
            if (instance == null) {
                instance = FFmpegCommand.INSTANCE;
            }
            instance.setDebug(true);
            instance.runCmd(commands, usrFFmpegCallBack == null ? null : new IFFmpegCallBack() {

                @Override
                public void onStart() {
                    usrFFmpegCallBack.onStart();
                }

                @Override
                public void onProgress(int progress, long time) {
                    usrFFmpegCallBack.onProgress(progress, time);
                }

                @Override
                public void onError(int i, String s) {
                    usrFFmpegCallBack.onError(i, s);
                }

                @Override
                public void onComplete() {
                    usrFFmpegCallBack.onComplete();
                }

                @Override
                public void onCancel() {
                    usrFFmpegCallBack.onCancel();
                }
            });
        }
    }

    @Override
    public Flowable<RxStat> executeCommand(String[] commands) {
        return Flowable.create(emitter -> {
            executeWithCallback(commands, new IFFmpegCallBack() {

                @Override
                public void onStart() {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_START, tag));
                }

                @Override
                public void onProgress(int progress, long time) {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_PROGRESS, progress, time, tag));
                }

                @Override
                public void onError(int i, String s) {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_ERROR, s, tag));
                    emitter.onComplete();
                }

                @Override
                public void onComplete() {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_COMPLETE, tag));
                    emitter.onComplete();
                }

                @Override
                public void onCancel() {
                    emitter.onNext(new RxStat(RxStat.Status.STATUS_CANCEL, tag));
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
            instance.cancel();
        }
    }

    @Override
    public void cancelTask(long taskId) {
        if (instance != null) {
            instance.cancel();
        }
    }


}
