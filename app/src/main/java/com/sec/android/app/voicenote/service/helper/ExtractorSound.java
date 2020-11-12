package com.sec.android.app.voicenote.service.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExtractorSound {
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    static HashMap<String, Factory> sExtensionMap = new HashMap<>();
    static Factory[] sSubclassFactories = {ExtractorAAC.getFactory(), ExtractorAMR.getFactory()};
    static ArrayList<String> sSupportedExtensions = new ArrayList<>();
    protected File mInputFile = null;
    protected ProgressListener mProgressListener = null;

    public interface Factory {
        ExtractorSound create();

        String[] getSupportedExtensions();
    }

    public interface ProgressListener {
        boolean reportProgress(double d);
    }

    public int[] getFrameGains() {
        return null;
    }

    public int[] getFrameLens() {
        return null;
    }

    public int[] getFrameOffsets() {
        return null;
    }

    public int getNumFrames() {
        return 0;
    }

    public int getSampleRate() {
        return 0;
    }

    public int getSamplesPerFrame() {
        return 0;
    }

    static {
        for (Factory factory : sSubclassFactories) {
            for (String str : factory.getSupportedExtensions()) {
                sSupportedExtensions.add(str);
                sExtensionMap.put(str, factory);
            }
        }
    }

    public static ExtractorSound create(String str, ProgressListener progressListener) throws IOException {
        Factory factory;
        File file = new File(str);
        if (file.exists()) {
            String[] split = file.getName().toLowerCase().split("\\.");
            if (split.length < 2 || (factory = sExtensionMap.get(split[split.length - 1])) == null) {
                return null;
            }
            ExtractorSound create = factory.create();
            create.setProgressListener(progressListener);
            create.readFile(file);
            return create;
        }
        throw new FileNotFoundException(str);
    }

    protected ExtractorSound() {
    }

    public void readFile(File file) throws IOException {
        this.mInputFile = file;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.mProgressListener = progressListener;
    }
}
