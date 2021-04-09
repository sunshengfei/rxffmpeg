package io.androidx.ffmpeg;

import io.reactivex.rxjava3.core.Flowable;

public interface IRxFFmpegAgent<T> {

    void executeWithUsrCallBack(String[] commands, UsrFFmpegCallBack usrFFmpegCallBack);

    void executeWithCallback(String[] commands, T callBack);

    Flowable<RxStat> executeCommand(String[] commands);

    Flowable<RxStat> executeCommandWithTag(String[] commands, int tag);

    void cancelAllTask();

    void cancelTask(long taskId);
}
