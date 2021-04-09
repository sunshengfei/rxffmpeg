package io.androidx.ffmpeg;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.Locale;

public class RxFFmpegCommand {

    private static RxFFmpegCommand INSTANCE;

    private static final String COPY = "copy";
    private static final String CODEC = "-c";
    private static final String VCODEC = "-c:v";
    private static final String ACODEC = "-c:a";
    private static final String VFILTER = "-vf";
    private static final String AudioNO = "-an";
    private static final String FILTER_COMPLEX = "-filter_complex";

    public static RxFFmpegCommand INSTANCE() {
        return new RxFFmpegCommand();
    }

    public static RxFFmpegCommand Builder() {
        synchronized (RxFFmpegCommand.class) {
            if (INSTANCE == null) {
                INSTANCE = new RxFFmpegCommand();
            }
            INSTANCE.commands.clear();
        }
        return INSTANCE;
    }

    private final LinkedList<String> commands = new LinkedList<>();


    public static String[] slot(String formatter, String... needles) {
        String[] commands = formatter.split(" ");
        int needIndex = 0;
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].startsWith("%")) {
                if (needIndex >= needles.length) {
                    break;
                }
                commands[i] = needles[needIndex];
                needIndex++;
            }
        }
        return commands;
    }

    public RxFFmpegCommand append(String string) {
        commands.add(string);
        return this;
    }

    public RxFFmpegCommand appendMore(String... string) {
        for (String ins : string) {
            if (ins != null && ins.trim().length() > 0) {
                append(ins.trim());
            }
        }
//        commands.addAll(Arrays.asList(string));
        return this;
    }

    public String[] build() {
        String[] a = new String[commands.size()];
        return commands.toArray(a);
    }

    private final static String empty = "'\\u00a0'";

    public RxFFmpegCommand $emptyProcessor() {
        append("EMPTY=" + empty + "&&");
        append("&&");
        return this;
    }

    private static String quoteIfContainsEmpty(@NonNull String input) {
        if (input.contains(" ")) {
            return String.format(Locale.getDefault(), "\"%s\"", input);
        }
        return input;
    }

    private static String emptyReplace(@NonNull String input) {
        input = quoteIfContainsEmpty(input);
        if (input.contains(" ")) {
            return input.replaceAll(" ", "\u00a0");
        }
        return input;
    }

    public RxFFmpegCommand $begin() {
        commands.clear();
        return this;
    }

    public RxFFmpegCommand $ffmpeg(boolean withLog) {
        $begin();
//        $emptyProcessor();
//        append("EMPTY=" + empty + "&&" + "ffmpeg");
        append("ffmpeg");
        if (withLog) {
            return $proceed();
        }
        return this;
    }

    public RxFFmpegCommand $proceed() {
        append("-y");
        return this;
    }

    public RxFFmpegCommand $input(@NonNull String input) {
        append("-i");
        append(emptyReplace(input));
        return this;
    }

    public RxFFmpegCommand $output(@NonNull String output) {
        append(emptyReplace(output));
        return this;
    }

    public RxFFmpegCommand $codecAudioNo() {
        append(AudioNO);
        return this;
    }

    public RxFFmpegCommand $params(@NonNull String... input) {
        appendMore(input);
        return this;
    }

    public RxFFmpegCommand $resolution(String resolution) {
        if (resolution != null && resolution.trim().length() > 0) {
            append("-s");
            append(resolution);
        }
        return this;
    }

    /**
     * 采样率
     *
     * @param rate
     * @return
     */
    public RxFFmpegCommand $sampleRate(@NonNull String rate) {
        append("-ar");
        append(rate);
        return this;
    }

    public RxFFmpegCommand $audioRate(@NonNull String rate) {
        append("-b:a");
        append(rate);
        return this;
    }

    public RxFFmpegCommand $videoRate(@NonNull String rate) {
        append("-b:v");
        append(rate);
        return this;
    }

    public RxFFmpegCommand $codecVideo(@NonNull String codec) {
        append(VCODEC);//-codec:v == -vcodec
        append(codec);
        return this;
    }

    public RxFFmpegCommand $codecVideoCopy() {
        $codecVideo(COPY);
        return this;
    }

    public RxFFmpegCommand $codecAudio(@NonNull String codec) {
        append(ACODEC);//-codec:a == -acodec
        append(codec);
        return this;
    }

    public RxFFmpegCommand $codecAudioCopy() {
        $codecAudio(COPY);
        return this;
    }

    public RxFFmpegCommand $codecCopy() {
        append(CODEC);//-codec:a == -acodec
        append(COPY);
        return this;
    }

    public RxFFmpegCommand $filterComplex(RxFFmpegCommand command) {
        if (command != null) {
            if (command.commands.size() > 1) {
                $params(command.build());
            }
        }
        return this;
    }

    public RxFFmpegCommand $filterComplex() {
        return $params(FILTER_COMPLEX);
    }

    public RxFFmpegCommand $filterReverseVideo() {
        $filterComplex(RxFFmpegCommand.INSTANCE()
                .$begin()
                .$filterComplex()
                .$params("[0:v]reverse[v]", "-map", "[v]"));
        return this;
    }

    public RxFFmpegCommand $filterReverseVideoAudio() {
        $filterComplex(RxFFmpegCommand.INSTANCE()
                .$begin()
                .$params(VFILTER, "reverse"));
        return this;
    }

    public RxFFmpegCommand $addFilter(String... params) {
        if (params.length > 0) {
            RxFFmpegCommand instance = RxFFmpegCommand.INSTANCE()
                    .$params(VFILTER)
                    .$params(params);
            if (instance.commands.size() > 1) {
                //valid
                String currentCommands = StringUtils.join(INSTANCE.build(), " ");
                if (currentCommands.contains(VCODEC + " " + COPY)) {
                    $$remove(VCODEC, INSTANCE.commands.size() - 1, 2);
                }
                appendMore(instance.build());
            }
        }
        return this;
    }

    private RxFFmpegCommand $$remove(String needle, int beforeIndex, int count) {
        int index = -1;
        for (int i = beforeIndex; i >= 0; i--) {
            String command = commands.get(i);
            if (command.equalsIgnoreCase(needle)) {
                index = i;
                break;
            }
        }
        int i = count;
        if (index > -1) {
            while (i-- > 0) {
                commands.remove(index);
            }
        }
        return this;
    }

    public static String $scaleString(String scale) {
        if (scale.contains("x")) {
            Transformer<String> transformer = source -> {
                String[] whs = source.split("x");
                return "scale=" + whs[0] + ":" + whs[1];
            };
            return transformer.transform(scale);
        }
        return null;
    }

    public RxFFmpegCommand $scale(String scale) {
        if (scale.contains("x")) {
            String[] whs = scale.split("x");
            append("scale=" + whs[0] + ":" + whs[1]);
        }
        return this;
    }


    public interface Transformer<T> {
        String transform(T source);
    }

    public final static String FORMATTER_CONVERT_VIDEO = "ffmpeg -y -i %s -vcodec copy -acodec copy %s";
}
