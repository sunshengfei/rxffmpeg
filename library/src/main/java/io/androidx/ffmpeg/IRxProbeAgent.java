package io.androidx.ffmpeg;

import org.json.JSONObject;

import io.reactivex.rxjava3.core.Flowable;

public interface IRxProbeAgent {

    JSONObject getMediaInfoSync(String filePath);

    Flowable<JSONObject> getMediaInfo(String filePath);

}
