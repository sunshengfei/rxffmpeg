package io.androidx.ffmpeg;


import io.reactivex.rxjava3.core.Flowable;

public class RxProbe {

    private static IRxProbe<String> INSTANCE;

    private static IRxProbe<String> getAgent() {
        IRxProbe<String> iRxFFmpegAgent = null;
        if (C.FFmpegCmdSdkMode()) {
            iRxFFmpegAgent = new FSProbeImpl();
        } else if (C.isMobileffmpegMode()) {
            iRxFFmpegAgent = new ATProbeImpl();
        }
        return iRxFFmpegAgent;
    }


    public static Flowable<String> getMediaDetail(String source) {
        if (INSTANCE == null) {
            synchronized (RxProbe.class) {
                INSTANCE = getAgent();
            }
        }
        if (INSTANCE == null) return Flowable.empty();
        if (INSTANCE instanceof FSProbeImpl) {
            return executeCommand(RxFFmpegCommand.INSTANCE().$params("ffprobe", "-v",
                    "quiet",
                    "-print_format",
                    "json",
                    "-show_format",
                    "-i",
                    source
            ).build());
        }
        return executeCommand(new String[]{source});
    }

    public static Flowable<String> executeCommand(String[] source) {
        if (INSTANCE == null) {
            synchronized (RxProbe.class) {
                INSTANCE = getAgent();
            }
        }
        if (INSTANCE == null) return Flowable.empty();
        return INSTANCE.executeCommand(source);
    }

}
