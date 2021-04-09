package io.androidx.ffmpeg;


import com.yyl.ffmpeg.FFmpegUtils;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class FSProbeImpl implements IRxProbe<String> {


    @Override
    public Flowable<String> executeCommand(String[] commands) {
        return Flowable.create(emitter -> {
            String ob = execute(commands);
            emitter.onNext(ob);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public String execute(String[] commands) {
        synchronized (FSProbeImpl.class) {
            return FFmpegUtils.getInstance().execffprobe(commands);
        }
    }

}
