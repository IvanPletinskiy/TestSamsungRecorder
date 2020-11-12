package com.sec.android.app.voicenote.p007ui.recognizer;

import android.text.SpannableStringBuilder;
import com.sec.android.app.voicenote.common.util.TextData;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.SimpleEngine;
import com.sec.android.app.voicenote.service.SimpleMetadataRepository;
import com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager;
import com.sec.android.app.voicenote.service.helper.Bookmark;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.recognizer.RecognizerData */
public class RecognizerData {
    public static final long DURATION_BLOCK = 1200000;
    private static final String TAG = "RecognizerData";
    private ArrayList<Bookmark> mBookmarks;
    private ArrayList<TextData> mDisplayedSttData;
    private int mOverWriteEndIdx;
    private int mOverWriteStartIdx;
    private int mOverwriteStartTime;
    private ArrayList<TextData> mPartialSttData;
    private int mProgress;
    private int mRepeatATime;
    private int mRepeatBTime;
    private ArrayList<TextData> mSavedSttData;
    private int mScene = 0;
    private String mSession;
    private SimpleEngine mSimpleEngine = null;
    private SpannableStringBuilder mSttStringBuilder;
    private ArrayList<SpannableStringBuilder> mSttStringBuilderList;
    private String mTouchedWord;
    private int mTouchedWordCharStart;
    private int mTouchedWordIndex;
    private int mTouchedX;
    private int mTouchedY;
    private int mTrimEndIndex;
    private int mTrimEndTime;
    private int mTrimStartIndex;
    private int mTrimStartTime;

    /* renamed from: com.sec.android.app.voicenote.ui.recognizer.RecognizerData$PaintIndexInfo */
    public static class PaintIndexInfo {
        public static final int UNSET = -1;
        private int mEndIndex;
        private int mPaintedLength;
        private int mStartIndex;

        private PaintIndexInfo() {
            this.mStartIndex = -1;
            this.mEndIndex = -1;
            this.mPaintedLength = -1;
        }

        private PaintIndexInfo(int i, int i2) {
            this.mStartIndex = i;
            this.mEndIndex = i2;
            this.mPaintedLength = -1;
        }

        /* access modifiers changed from: private */
        public void setStartIndex(int i) {
            this.mStartIndex = i;
        }

        /* access modifiers changed from: private */
        public void setEndIndex(int i) {
            this.mEndIndex = i;
        }

        /* access modifiers changed from: private */
        public void setPaintedLength(int i) {
            this.mPaintedLength = i;
        }

        public int getStartIndex() {
            return this.mStartIndex;
        }

        public int getEndIndex() {
            return this.mEndIndex;
        }

        public int getPaintedLength() {
            return this.mPaintedLength;
        }
    }

    public RecognizerData() {
        activateVariables();
        initializeVariables();
    }

    public void setSession(String str) {
        this.mSession = str;
    }

    private void activateVariables() {
        this.mSavedSttData = new ArrayList<>();
        this.mDisplayedSttData = new ArrayList<>();
        this.mSttStringBuilder = new SpannableStringBuilder();
        this.mSttStringBuilderList = new ArrayList<>();
    }

    private boolean isVariableReleased() {
        boolean z = this.mSavedSttData == null || this.mDisplayedSttData == null || this.mSttStringBuilder == null || this.mSttStringBuilderList == null;
        if (z) {
            Log.m22e(TAG, "Already released");
        }
        return z;
    }

    private void initializeVariables() {
        this.mPartialSttData = null;
        this.mTouchedWordIndex = -1;
        this.mTouchedWordCharStart = -1;
        this.mTouchedWord = "";
    }

    private boolean isOverWriteMode() {
        return Engine.getInstance().getContentItemCount() > 1;
    }

    private boolean isWordAvailable(TextData textData) {
        String[] strArr = textData.mText;
        return (strArr == null || strArr[0] == null || textData.dataType != 0) ? false : true;
    }

    private void loadSttDataFromFile(ArrayList<TextData> arrayList) {
        ArrayList<TextData> arrayList2;
        TextData textData;
        if (arrayList != null) {
            arrayList.clear();
        } else {
            arrayList = new ArrayList<>();
        }
        if (this.mSession != null) {
            SimpleMetadataRepository metadataRepository = SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession);
            Log.m29v(TAG, "loadSttDataFromFile() : metadata path = " + metadataRepository.getPath());
            arrayList2 = metadataRepository.getSttData();
        } else {
            MetadataRepository instance = MetadataRepository.getInstance();
            Log.m29v(TAG, "loadSttDataFromFile() : metadata path = " + instance.getPath());
            arrayList2 = instance.getSttData();
        }
        if (arrayList2 != null) {
            Iterator<TextData> it = arrayList2.iterator();
            while (it.hasNext()) {
                TextData next = it.next();
                TextData textData2 = new TextData();
                try {
                    textData = (TextData) next.clone();
                } catch (CloneNotSupportedException unused) {
                    Log.m22e(TAG, "loadSttDataFromFile() : clone fail");
                    textData = textData2;
                }
                arrayList.add(textData);
            }
            Collections.sort(arrayList);
        }
    }

    private void makeCloneOfSttData(ArrayList<TextData> arrayList, ArrayList<TextData> arrayList2) {
        TextData textData;
        if (arrayList != null) {
            if (arrayList2 != null) {
                arrayList2.clear();
            } else {
                arrayList2 = new ArrayList<>();
            }
            Iterator<TextData> it = arrayList.iterator();
            while (it.hasNext()) {
                TextData next = it.next();
                TextData textData2 = new TextData();
                try {
                    textData = (TextData) next.clone();
                } catch (CloneNotSupportedException unused) {
                    Log.m22e(TAG, "makeCloneOfSttData() : clone fail");
                    textData = textData2;
                }
                arrayList2.add(textData);
            }
            Collections.sort(arrayList2);
        }
    }

    private void combineDisplayedSttData(ArrayList<TextData> arrayList) {
        Iterator<TextData> it;
        ArrayList<TextData> arrayList2 = arrayList;
        Log.m19d(TAG, "combineDisplayedSttData()");
        if (arrayList2 != null && !arrayList.isEmpty()) {
            ArrayList arrayList3 = new ArrayList();
            makeCloneOfSttData(arrayList2, arrayList3);
            Iterator it2 = arrayList3.iterator();
            while (it2.hasNext()) {
                TextData textData = (TextData) it2.next();
                long j = textData.timeStamp;
                Log.m29v(TAG, "      result : <" + textData.mText[0] + '>' + " startTime : " + j + " endTime : " + (textData.duration + j));
            }
            int size = arrayList3.size();
            int size2 = this.mDisplayedSttData.size();
            if (size2 > 0) {
                long j2 = ((TextData) arrayList3.get(0)).timeStamp;
                TextData textData2 = (TextData) arrayList3.get(size - 1);
                long j3 = textData2.timeStamp + textData2.duration;
                TextData textData3 = this.mDisplayedSttData.get(size2 - 1);
                long j4 = textData3.timeStamp + textData3.duration;
                Log.m19d(TAG, "combineDisplayedSttData() : originalSttDataCount = " + size2);
                Log.m19d(TAG, "combineDisplayedSttData() : originalSttEndTime = " + j4);
                Log.m19d(TAG, "combineDisplayedSttData() : resultSttDataCount = " + size);
                Log.m19d(TAG, "combineDisplayedSttData() : resultSttDataStartTime = " + j2);
                Log.m19d(TAG, "combineDisplayedSttData() : resultSttDataEndTime = " + j3);
                if (j2 < j4) {
                    Iterator<TextData> it3 = this.mDisplayedSttData.iterator();
                    while (it3.hasNext()) {
                        TextData next = it3.next();
                        long j5 = next.timeStamp;
                        long j6 = next.duration + j5;
                        if ((j2 > j6 || j6 > j3) && ((j2 > j5 || j5 > j3) && (j5 > j2 || j3 > j6))) {
                            it = it3;
                            Log.m29v(TAG, "remain : <" + next.mText[0] + '>' + " startTime : " + j5 + " endTime : " + j6);
                        } else {
                            it3.remove();
                            StringBuilder sb = new StringBuilder();
                            it = it3;
                            sb.append("      remove : <");
                            sb.append(next.mText[0]);
                            sb.append('>');
                            sb.append(" startTime : ");
                            sb.append(j5);
                            sb.append(" endTime : ");
                            sb.append(j6);
                            Log.m29v(TAG, sb.toString());
                        }
                        it3 = it;
                    }
                }
            }
            this.mDisplayedSttData.addAll(arrayList3);
            Collections.sort(this.mDisplayedSttData);
            calculateForOverwrite();
        }
        Log.m19d(TAG, "combineDisplayedSttData() : mOverWriteStartIdx = " + this.mOverWriteStartIdx + ", mOverWriteEndIdx = " + this.mOverWriteEndIdx);
    }

    private void calculateForOverwrite() {
        this.mOverWriteStartIdx = 0;
        this.mOverWriteEndIdx = 0;
        if (isOverWriteMode()) {
            for (int i = 0; i < this.mDisplayedSttData.size(); i++) {
                TextData textData = this.mDisplayedSttData.get(i);
                long j = textData.timeStamp + textData.duration;
                if (j < ((long) Engine.getInstance().getOverwriteStartTime())) {
                    this.mOverWriteStartIdx += textData.mText[0].length();
                    this.mOverWriteEndIdx = this.mOverWriteStartIdx;
                } else if (j <= ((long) Engine.getInstance().getOverwriteEndTime())) {
                    this.mOverWriteEndIdx += textData.mText[0].length();
                }
            }
        }
    }

    public ArrayList<TextData> getDisplayedSttData() {
        return this.mDisplayedSttData;
    }

    private long getLastDisplayTime() {
        if (this.mDisplayedSttData.size() == 0) {
            return 0;
        }
        ArrayList<TextData> arrayList = this.mDisplayedSttData;
        TextData textData = arrayList.get(arrayList.size() - 1);
        return textData.timeStamp + textData.duration;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.mSttStringBuilder.length();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int checkEnd(int r2) {
        /*
            r1 = this;
            boolean r0 = r1.isVariableReleased()
            if (r0 != 0) goto L_0x000f
            android.text.SpannableStringBuilder r0 = r1.mSttStringBuilder
            int r0 = r0.length()
            if (r2 <= r0) goto L_0x000f
            return r0
        L_0x000f:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.recognizer.RecognizerData.checkEnd(int):int");
    }

    public boolean hasSavedSttData() {
        ArrayList<TextData> arrayList = this.mSavedSttData;
        return arrayList != null && arrayList.size() > 0;
    }

    public void loadSttDataFromFile() {
        Log.m19d(TAG, "loadSttDataFromFile");
        if (!isVariableReleased()) {
            loadSttDataFromFile(this.mSavedSttData);
            ArrayList<TextData> arrayList = this.mPartialSttData;
            if (arrayList != null && !arrayList.isEmpty()) {
                MetadataRepository.getInstance().addSttData(this.mPartialSttData);
                loadSttDataFromFile(this.mSavedSttData);
            }
            this.mDisplayedSttData.clear();
            this.mDisplayedSttData.addAll(this.mSavedSttData);
            calculateForOverwrite();
        }
    }

    public synchronized void addResultText() {
        Log.m19d(TAG, "addResultText");
        if (!isVariableReleased()) {
            loadSttDataFromFile();
            this.mPartialSttData = null;
        }
    }

    public synchronized void addLastWordText(ArrayList<TextData> arrayList) {
        Log.m19d(TAG, "addLastWordText");
        if (!isVariableReleased()) {
            if (arrayList != null) {
                if (!arrayList.isEmpty()) {
                    combineDisplayedSttData(arrayList);
                }
            }
            this.mPartialSttData = null;
        }
    }

    public synchronized void addPartialText(ArrayList<TextData> arrayList) {
        Log.m19d(TAG, "addPartialText");
        if (!isVariableReleased()) {
            combineDisplayedSttData(arrayList);
            this.mPartialSttData = arrayList;
        }
    }

    public void addLastWordPartialText() {
        Log.m19d(TAG, "addLastWordPartialText mPartialSttData : " + this.mPartialSttData);
        if (this.mPartialSttData != null) {
            if (this.mSession != null) {
                SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession).addSttData(this.mPartialSttData);
            } else {
                MetadataRepository.getInstance().addSttData(this.mPartialSttData);
            }
            this.mPartialSttData = null;
        }
    }

    public synchronized SpannableStringBuilder getDisplayText() {
        if (isVariableReleased()) {
            return null;
        }
        this.mSttStringBuilder.clear();
        this.mSttStringBuilderList.clear();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int size = this.mDisplayedSttData.size();
        int i = 1;
        int i2 = 0;
        SpannableStringBuilder spannableStringBuilder2 = spannableStringBuilder;
        for (int i3 = 0; i3 < size; i3++) {
            TextData textData = this.mDisplayedSttData.get(i3);
            long j = textData.timeStamp;
            long j2 = textData.duration + j;
            if (isWordAvailable(textData)) {
                i2 += textData.mText[0].length();
                Log.m29v(TAG, "display : <" + textData.mText[0] + '>' + " startTime : " + j + " endTime : " + j2 + " Length : " + i2);
                this.mSttStringBuilder.append(textData.mText[0]);
                StringBuilder sb = new StringBuilder();
                sb.append("disPlayText ");
                sb.append(textData.mText[0]);
                sb.append("@");
                Log.m19d(TAG, sb.toString());
                if (j < ((long) i) * DURATION_BLOCK) {
                    spannableStringBuilder2.append(textData.mText[0]);
                } else {
                    this.mSttStringBuilderList.add(spannableStringBuilder2);
                    spannableStringBuilder2 = new SpannableStringBuilder();
                    spannableStringBuilder2.append(textData.mText[0]);
                    i++;
                }
            }
        }
        this.mSttStringBuilder.append(" ");
        this.mSttStringBuilderList.add(spannableStringBuilder2);
        Log.m19d(TAG, "disPlayText = " + this.mSttStringBuilder.toString() + ":" + this.mSttStringBuilder.toString().length());
        return this.mSttStringBuilder;
    }

    public SpannableStringBuilder getDisplayTextList() {
        return this.mSttStringBuilderList.get((int) (((long) this.mProgress) / DURATION_BLOCK));
    }

    private long getDurationOfBlock() {
        return (((long) this.mProgress) / DURATION_BLOCK) * DURATION_BLOCK;
    }

    public String getTouchedWord() {
        return this.mTouchedWord;
    }

    public int getTouchedIndex() {
        if (isVariableReleased()) {
            return -1;
        }
        int size = this.mSavedSttData.size();
        if (size == 0 && this.mTouchedWordIndex == 0) {
            return 0;
        }
        int i = this.mTouchedWordIndex;
        if (size > i) {
            return i;
        }
        return -1;
    }

    public int getTouchedCharStart() {
        return this.mTouchedWordCharStart;
    }

    public int getDotIdx() {
        int i;
        int currentTime = Engine.getInstance().getCurrentTime();
        int overwriteStartTime = Engine.getInstance().getOverwriteStartTime();
        if (this.mScene == 6 && currentTime == 0 && overwriteStartTime != -1) {
            currentTime = overwriteStartTime;
        }
        if (isOverWriteMode()) {
            Iterator<TextData> it = this.mDisplayedSttData.iterator();
            i = 0;
            while (it.hasNext()) {
                TextData next = it.next();
                long j = next.timeStamp + next.duration;
                if (isWordAvailable(next)) {
                    if (j >= ((long) currentTime)) {
                        break;
                    }
                    i += next.mText[0].length();
                }
            }
        } else {
            i = this.mSttStringBuilder.length();
        }
        int i2 = i - 1;
        if (i2 < 0) {
            return 0;
        }
        return i2;
    }

    public int getCurrentTextIdx() {
        if (isVariableReleased()) {
            return 0;
        }
        int currentTime = Engine.getInstance().getCurrentTime();
        if (currentTime == 0) {
            currentTime = (int) getLastDisplayTime();
        }
        Iterator<TextData> it = this.mDisplayedSttData.iterator();
        int i = 0;
        while (it.hasNext()) {
            TextData next = it.next();
            if (isWordAvailable(next) && ((long) currentTime) > next.timeStamp) {
                i += next.mText[0].length();
            }
        }
        return i;
    }

    public PaintIndexInfo getRepeatIndexInfo() {
        int i;
        int i2;
        int i3;
        PaintIndexInfo paintIndexInfo = new PaintIndexInfo();
        if (isVariableReleased()) {
            paintIndexInfo.setStartIndex(-1);
            paintIndexInfo.setEndIndex(-1);
            return paintIndexInfo;
        }
        if (this.mSavedSttData.size() != 0 && (i = this.mRepeatATime) != (i2 = this.mRepeatBTime) && i >= 0 && i2 >= 0) {
            if (i >= i2) {
                int i4 = i2;
                i2 = i;
                i = i4;
            }
            Iterator<TextData> it = this.mSavedSttData.iterator();
            int i5 = 0;
            loop0:
            while (true) {
                i3 = i5;
                while (it.hasNext()) {
                    TextData next = it.next();
                    if (isWordAvailable(next) && next.timeStamp > getDurationOfBlock()) {
                        long j = (long) i;
                        long j2 = next.timeStamp;
                        if (j > j2) {
                            i5 += next.mText[0].length();
                        } else if (j < j2 && ((long) i2) > j2) {
                            i3 += next.mText[0].length();
                        }
                    }
                }
                break loop0;
            }
            paintIndexInfo.setStartIndex(i5);
            paintIndexInfo.setEndIndex(checkEnd(i3));
        }
        return paintIndexInfo;
    }

    public PaintIndexInfo getPlayedIndexInfo() {
        int i;
        int i2;
        PaintIndexInfo paintIndexInfo = new PaintIndexInfo();
        int i3 = 0;
        if (!hasSavedSttData() || this.mProgress <= 0) {
            i2 = 0;
            i = 0;
        } else {
            Iterator<TextData> it = this.mSavedSttData.iterator();
            i = 0;
            while (it.hasNext()) {
                TextData next = it.next();
                if (isWordAvailable(next) && next.timeStamp > getDurationOfBlock() && ((long) this.mProgress) > next.timeStamp) {
                    i += next.mText[0].length();
                }
            }
            if (this.mScene == 6) {
                int i4 = this.mTrimStartIndex;
                if (i4 > 0) {
                    i3 = i4;
                }
                int i5 = this.mTrimEndIndex;
                if (i5 > 0 && i5 < i) {
                    int i6 = i;
                    i = i5;
                    i2 = i6;
                }
            }
            i2 = i;
        }
        paintIndexInfo.setStartIndex(i3);
        paintIndexInfo.setEndIndex(checkEnd(i));
        paintIndexInfo.setPaintedLength(i2);
        return paintIndexInfo;
    }

    public PaintIndexInfo getTrimIndexInfo() {
        int i;
        PaintIndexInfo paintIndexInfo = new PaintIndexInfo();
        int i2 = -1;
        if (this.mScene != 6 || !hasSavedSttData()) {
            i = -1;
        } else {
            Iterator<TextData> it = this.mSavedSttData.iterator();
            int i3 = 0;
            loop0:
            while (true) {
                i = i3;
                while (it.hasNext()) {
                    TextData next = it.next();
                    if (isWordAvailable(next) && next.timeStamp > getDurationOfBlock()) {
                        int i4 = this.mTrimStartTime;
                        long j = next.timeStamp;
                        if (((long) i4) > j) {
                            i3 += next.mText[0].length();
                        } else if (((long) i4) <= j && ((long) this.mTrimEndTime) > j + next.duration) {
                            i += next.mText[0].length();
                        }
                    }
                }
                break loop0;
            }
            this.mTrimStartIndex = i3;
            this.mTrimEndIndex = i;
            i2 = i3;
        }
        paintIndexInfo.setStartIndex(i2);
        paintIndexInfo.setEndIndex(checkEnd(i));
        return paintIndexInfo;
    }

    public ArrayList<PaintIndexInfo> getBookmarkIndexInfo() {
        ArrayList<Bookmark> arrayList;
        if (isVariableReleased() || (arrayList = this.mBookmarks) == null || arrayList.size() == 0) {
            return null;
        }
        ArrayList<PaintIndexInfo> arrayList2 = new ArrayList<>();
        Iterator<Bookmark> it = this.mBookmarks.iterator();
        while (it.hasNext()) {
            Bookmark next = it.next();
            if (((long) next.getElapsed()) >= getDurationOfBlock()) {
                Iterator<TextData> it2 = this.mSavedSttData.iterator();
                int i = 0;
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    TextData next2 = it2.next();
                    if (isWordAvailable(next2) && next2.timeStamp > getDurationOfBlock()) {
                        if (((long) next.getElapsed()) < next2.timeStamp + next2.duration) {
                            arrayList2.add(new PaintIndexInfo(i, checkEnd(next2.mText[0].length() + i)));
                            break;
                        }
                        i += next2.mText[0].length();
                    }
                }
            }
        }
        return arrayList2;
    }

    public ArrayList<PaintIndexInfo> getSearchedIndexInfo(String str) {
        if (isVariableReleased()) {
            return null;
        }
        ArrayList<PaintIndexInfo> arrayList = new ArrayList<>();
        String[] split = str.toLowerCase(Locale.US).split(" ");
        String lowerCase = this.mSttStringBuilder.toString().toLowerCase(Locale.US);
        for (String str2 : split) {
            int length = str2.length();
            int i = 0;
            while (i != -1) {
                i = lowerCase.indexOf(str2, i);
                int i2 = i + length;
                if (i != -1) {
                    arrayList.add(new PaintIndexInfo(i, checkEnd(i2)));
                    i = i2;
                }
            }
        }
        return arrayList;
    }

    public void updateTouchedXY(int i, int i2) {
        this.mTouchedX = i;
        this.mTouchedY = i2;
    }

    public int[] getTouchedXY() {
        return new int[]{this.mTouchedX, this.mTouchedY};
    }

    public int[] getOverwriteIndex() {
        Log.m19d(TAG, "OVERWRITE start = " + this.mOverWriteStartIdx + " end = " + this.mOverWriteEndIdx);
        return new int[]{this.mOverWriteStartIdx, this.mOverWriteEndIdx};
    }

    public void updatePlayedTime(int i) {
        this.mProgress = i;
    }

    public void updateRepeatTime(int i, int i2) {
        this.mRepeatATime = i;
        this.mRepeatBTime = i2;
    }

    public void updateTrimTime() {
        this.mTrimStartTime = Engine.getInstance().getTrimStartTime();
        this.mTrimEndTime = Engine.getInstance().getTrimEndTime();
        Log.m19d(TAG, "updateTrimTime : " + this.mTrimStartTime + " to " + this.mTrimEndTime);
    }

    public void updateOverwriteTime() {
        if (isOverWriteMode()) {
            int overwriteStartTime = Engine.getInstance().getOverwriteStartTime();
            if (this.mOverwriteStartTime != overwriteStartTime) {
                this.mOverwriteStartTime = overwriteStartTime;
                this.mOverWriteStartIdx = 0;
                this.mOverWriteEndIdx = 0;
                return;
            }
            return;
        }
        this.mOverWriteStartIdx = 0;
        this.mOverWriteEndIdx = 0;
    }

    public void updateTouchedWordIndex(int i) {
        Log.m19d(TAG, "updateTouchedWordIndex : " + i);
        if (!isVariableReleased()) {
            int size = this.mSavedSttData.size();
            int i2 = 0;
            int i3 = 0;
            while (i2 < size) {
                if (!isWordAvailable(this.mSavedSttData.get(i2)) || this.mSavedSttData.get(i2).timeStamp < getDurationOfBlock() || (i3 = i3 + this.mSavedSttData.get(i2).mText[0].length()) <= i) {
                    i2++;
                } else {
                    this.mTouchedWord = this.mSavedSttData.get(i2).mText[0];
                    this.mTouchedWordIndex = i2;
                    this.mTouchedWordCharStart = i3 - this.mTouchedWord.length();
                    return;
                }
            }
        }
    }

    public long getTouchedWordTimeStamp(int i) {
        if (isVariableReleased()) {
            return -1;
        }
        int size = this.mSavedSttData.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            if (isWordAvailable(this.mSavedSttData.get(i3)) && this.mSavedSttData.get(i3).timeStamp > getDurationOfBlock() && (i2 = i2 + this.mSavedSttData.get(i3).mText[0].length()) > i) {
                return this.mSavedSttData.get(i3).timeStamp;
            }
        }
        return -1;
    }

    public void updateBookmark() {
        this.mBookmarks = MetadataRepository.getInstance().getBookmarkList();
    }

    public void editText(String str) {
        int i = this.mTouchedWordIndex;
        if (i == -1 || i > this.mSavedSttData.size()) {
            Log.m22e(TAG, "touched area is abnormal");
            return;
        }
        if (str.isEmpty()) {
            this.mSavedSttData.remove(this.mTouchedWordIndex);
            int i2 = this.mTouchedWordIndex;
            if (i2 > 0) {
                this.mTouchedWordIndex = i2 - 1;
            }
        } else {
            this.mSavedSttData.get(this.mTouchedWordIndex).mText[0] = str;
        }
        if (this.mSession != null) {
            SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession).setSttData(this.mSavedSttData);
        } else {
            MetadataRepository.getInstance().setSttData(this.mSavedSttData);
        }
        loadSttDataFromFile();
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00d9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:40:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeMetaData(android.content.Context r6) {
        /*
            r5 = this;
            java.lang.String r0 = "RecognizerData"
            java.lang.String r1 = "writeMetaData()"
            com.sec.android.app.voicenote.provider.Log.m26i(r0, r1)
            if (r6 == 0) goto L_0x0023
            boolean r1 = r6 instanceof com.sec.android.app.voicenote.activity.SimpleActivity
            if (r1 == 0) goto L_0x0023
            java.lang.String r1 = r5.mSession
            if (r1 != 0) goto L_0x0017
            java.lang.String r1 = com.sec.android.app.voicenote.uicore.VoiceNoteApplication.getSimpleActivitySession()
            r5.mSession = r1
        L_0x0017:
            com.sec.android.app.voicenote.service.SimpleEngineManager r1 = com.sec.android.app.voicenote.service.SimpleEngineManager.getInstance()
            java.lang.String r2 = r5.mSession
            com.sec.android.app.voicenote.service.SimpleEngine r1 = r1.getEngine(r2)
            r5.mSimpleEngine = r1
        L_0x0023:
            com.sec.android.app.voicenote.service.SimpleEngine r1 = r5.mSimpleEngine
            if (r1 != 0) goto L_0x0040
            com.sec.android.app.voicenote.service.Engine r1 = com.sec.android.app.voicenote.service.Engine.getInstance()
            int r1 = r1.getContentItemCount()
            com.sec.android.app.voicenote.service.Engine r2 = com.sec.android.app.voicenote.service.Engine.getInstance()
            java.lang.String r2 = r2.getLastSavedFilePath()
            com.sec.android.app.voicenote.service.Engine r3 = com.sec.android.app.voicenote.service.Engine.getInstance()
            java.lang.String r3 = r3.getRecentFilePath()
            goto L_0x0050
        L_0x0040:
            int r1 = r1.getContentItemCount()
            com.sec.android.app.voicenote.service.SimpleEngine r2 = r5.mSimpleEngine
            java.lang.String r2 = r2.getLastSavedFilePath()
            com.sec.android.app.voicenote.service.SimpleEngine r3 = r5.mSimpleEngine
            java.lang.String r3 = r3.getRecentFilePath()
        L_0x0050:
            r4 = 1
            if (r1 < r4) goto L_0x008e
            if (r3 == 0) goto L_0x00c8
            boolean r1 = r3.isEmpty()
            if (r1 != 0) goto L_0x00c8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "writeMetaData() : recent file path = "
            r1.append(r2)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r1)
            java.lang.String r0 = r5.mSession
            if (r0 == 0) goto L_0x0083
            com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager r0 = com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager.getInstance()
            java.lang.String r1 = r5.mSession
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r0.getMetadataRepository(r1)
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r1 = r5.mDisplayedSttData
            r0.setSttData(r1)
            goto L_0x008c
        L_0x0083:
            com.sec.android.app.voicenote.service.MetadataRepository r0 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r1 = r5.mDisplayedSttData
            r0.setSttData(r1)
        L_0x008c:
            r2 = r3
            goto L_0x00c9
        L_0x008e:
            if (r2 == 0) goto L_0x00c8
            boolean r1 = r2.isEmpty()
            if (r1 != 0) goto L_0x00c8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "writeMetaData() : last saved path = "
            r1.append(r3)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r1)
            java.lang.String r0 = r5.mSession
            if (r0 == 0) goto L_0x00be
            com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager r0 = com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager.getInstance()
            java.lang.String r1 = r5.mSession
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r0 = r0.getMetadataRepository(r1)
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r1 = r5.mDisplayedSttData
            r0.writeSttDataInFile(r2, r1)
            goto L_0x00c9
        L_0x00be:
            com.sec.android.app.voicenote.service.MetadataRepository r0 = com.sec.android.app.voicenote.service.MetadataRepository.getInstance()
            java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r1 = r5.mDisplayedSttData
            r0.writeSttDataInFile(r2, r1)
            goto L_0x00c9
        L_0x00c8:
            r2 = 0
        L_0x00c9:
            if (r2 == 0) goto L_0x00f9
            boolean r0 = r2.isEmpty()
            if (r0 != 0) goto L_0x00f9
            r0 = 46
            int r0 = r2.lastIndexOf(r0)
            if (r0 <= 0) goto L_0x00f9
            if (r6 == 0) goto L_0x00f9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
            java.lang.String r0 = r2.substring(r3, r0)
            r1.append(r0)
            java.lang.String r0 = "_memo.txt"
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            com.sec.android.app.voicenote.common.util.VNMediaScanner r1 = new com.sec.android.app.voicenote.common.util.VNMediaScanner
            r1.<init>(r6)
            r1.startScan(r0)
        L_0x00f9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.recognizer.RecognizerData.writeMetaData(android.content.Context):void");
    }

    public void resetAdvancedPlayer() {
        this.mRepeatATime = -1;
        this.mRepeatBTime = -1;
        this.mTrimStartTime = -1;
        this.mTrimEndTime = -1;
        this.mTrimStartIndex = -1;
        this.mTrimEndIndex = -1;
    }

    public void resetTouchedArea() {
        this.mTouchedX = -1;
        this.mTouchedY = -1;
        this.mTouchedWord = "";
        this.mTouchedWordIndex = -1;
        this.mTouchedWordCharStart = -1;
    }

    public void resetOverwriteArea() {
        this.mOverWriteStartIdx = 0;
        this.mOverWriteEndIdx = 0;
    }

    public void clearVariables(boolean z) {
        this.mSttStringBuilder.clear();
        this.mSttStringBuilderList.clear();
        this.mSavedSttData.clear();
        this.mDisplayedSttData.clear();
        if (z) {
            this.mSttStringBuilder = null;
            this.mSttStringBuilderList = null;
            this.mSavedSttData = null;
            this.mDisplayedSttData = null;
        }
    }

    public void updateScene(int i) {
        Log.m26i(TAG, "updateScene = " + i);
        this.mScene = i;
    }
}
