package io.androidx.ffmpeg;

public class C {

    public static boolean isMobileffmpegMode() {
        try {
            Class.forName("com.arthenica.mobileffmpeg.FFmpeg");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isFFmpegCommandMode() {
        try {
            Class.forName("com.coder.ffmpeg.jni.FFmpegCommand");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

//    https://github.com/mengzhidaren/FFmpegCmdSdk

    //有时间 我会自己编译一个，目前都有坑，崩溃的概率很大
    public static boolean FFmpegCmdSdkMode() {
        try {
            Class.forName("com.yyl.ffmpeg.FFmpegUtils");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
