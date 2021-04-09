package io.androidx.ffmpeg;

import io.reactivex.rxjava3.core.Flowable;

public interface IRxProbe<T> {

    T execute(String[] commands);

    Flowable<T> executeCommand(String[] commands);
}
