package io.androidx.ffmpeg;

import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class ATProbeImpl implements IRxProbe<String> {

    @Override
    public String execute(String[] commands) {
        String path = commands[commands.length - 1];
        synchronized (ATProbeImpl.class) {
            MediaInformation info = FFprobe.getMediaInformation(path);
            return info.getAllProperties().toString();
        }
    }

    @Override
    public Flowable<String> executeCommand(String[] commands) {
        return Flowable.create(emitter -> {
            String ob = execute(commands);
            emitter.onNext(ob);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }
}
