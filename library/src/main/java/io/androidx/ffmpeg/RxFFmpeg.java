package io.androidx.ffmpeg;


import com.coder.ffmpeg.call.IFFmpegCallBack;

import io.reactivex.rxjava3.core.Flowable;

public class RxFFmpeg {

    public static boolean preferMFFMode = false;
    private static IRxFFmpegAgent<IFFmpegCallBack> INSTANCE;

    private static IRxFFmpegAgent getAgent() {
        IRxFFmpegAgent<?> iRxFFmpegAgent = null;
        if (!preferMFFMode && C.FFmpegCmdSdkMode()) {
            iRxFFmpegAgent = new FFmpegSdkImpl();
        } else if (!preferMFFMode && C.isFFmpegCommandMode()) {
            iRxFFmpegAgent = new FFmpegCommandImpl();
        } else if (C.isMobileffmpegMode()) {
            iRxFFmpegAgent = new MobileFFmpegImpl();
        }
        return iRxFFmpegAgent;
    }


    public static void executeCommandPure(String[] commands, UsrFFmpegCallBack callBack) {
        if (INSTANCE == null) {
            synchronized (RxFFmpeg.class) {
                INSTANCE = getAgent();
            }
        }
        if (INSTANCE == null) return;
        INSTANCE.executeWithUsrCallBack(commands, callBack);
    }

    public static Flowable<RxStat> executeCommand(String[] commands) {
        return executeCommandWithTag(commands, -1);
    }

    public static Flowable<RxStat> executeCommandWithTag(String[] commands, int tag) {
        if (INSTANCE == null) {
            synchronized (RxFFmpeg.class) {
                INSTANCE = getAgent();
            }
        }
        if (INSTANCE == null) return Flowable.empty();
        if (tag != -1)
            synchronized (RxFFmpeg.class) {
                return INSTANCE.executeCommandWithTag(commands, tag);
            }
        else return INSTANCE.executeCommandWithTag(commands, tag);
    }

    public static void cancelTask(long taskId) {
        if (INSTANCE != null) {
            INSTANCE.cancelTask(taskId);
        }
    }

    public static void cancelTask() {
        if (INSTANCE != null) {
            INSTANCE.cancelAllTask();
        }
    }
}
