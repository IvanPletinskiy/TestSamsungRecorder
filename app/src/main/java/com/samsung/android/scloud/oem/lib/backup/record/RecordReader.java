package com.samsung.android.scloud.oem.lib.backup.record;

import android.util.JsonReader;
import java.io.IOException;
import java.util.List;

public class RecordReader {
    private List<JsonReader> jsonReaderList;
    private int location = 0;
    private JsonReader reader;
    private int size = 0;
    private final String sourceKey;

    public RecordReader(List<JsonReader> list, String str) {
        this.sourceKey = str;
        this.jsonReaderList = list;
        this.location = 0;
        this.size = list.size();
        List<JsonReader> list2 = this.jsonReaderList;
        int i = this.location;
        this.location = i + 1;
        this.reader = list2.get(i);
        try {
            this.reader.beginArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
