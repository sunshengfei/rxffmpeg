package io.androidx.ffmpeg;

public interface UsrFFmpegCallBack {
    /**
     * 开始回调
     */
    void onStart();

    /**
     * 进度回调
     *
     * @param progress 当前执行进度
     * @param pts      当前执行长度
     */
    void onProgress(int progress, long pts);

    /**
     * 取消回调
     */
    void onCancel();

    /**
     * 完成回调
     */
    void onComplete();

    /**
     * 错误回调
     *
     * @param errorCode 错误码
     * @param errorMsg  错误内容
     */
    void onError(int errorCode, String errorMsg);
}