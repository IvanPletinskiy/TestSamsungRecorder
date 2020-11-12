package com.sec.android.app.voicenote.service;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.codec.M4aInfo;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class M4AEditor extends AbsEditor {
    private static final String TAG = "M4AEditor";
    private static M4AEditor mInstance;

    private M4AEditor() {
        Log.m26i(TAG, "M4AEditor creator !!");
    }

    public static M4AEditor getInstance() {
        if (mInstance == null) {
            mInstance = new M4AEditor();
        }
        return mInstance;
    }

    public boolean trim(String str, String str2, int fromTime, int toTime) {
        FileOutputStream fileOutputStream;
        String str3 = str2;
//        int i3 = fromTime;
//        int i4 = toTime;
        if (str == null || str3 == null) {
            Log.m22e(TAG, "trim filepath is NULL");
            return false;
        }
        try {
            Movie build = MovieCreator.build(str);
            Movie movie = new Movie();
            if (build != null) {
                Track track = build.getTracks().get(0);
                int size = track.getSamples().size();
                long mediaDuration = M4aInfo.getMediaDuration(str) / 1000;

                int fromSample = (int) ((float) fromTime * 1.0f / mediaDuration * size);
                int toSample = (int) (size * ((((float) toTime) * 1.0f) / mediaDuration));
                Log.m19d(TAG, "trim original : " + mediaDuration + '[' + size + "] trim :" + fromTime + '[' + fromSample + "] ~ " + toTime + '[' + toSample + ']');
                if (fromSample < 0) {
                    fromSample = 0;
                }
                if (toSample > size) {
                    toSample = size;
                }
                movie.addTrack(new AppendTrack(new CroppedTrack(track, (long) fromSample, (long) toSample)));
                Container build2 = new DefaultMp4Builder().build(movie);
                fileOutputStream = new FileOutputStream(new File(str3));
                try {
                    build2.writeContainer(fileOutputStream.getChannel());
                } catch (Exception e) {
                    e = e;
                }
            } else {
                fileOutputStream = null;
            }
//            closeQuietly(fileOutputStream);
            return true;
        } catch (Exception e2) {
//            e = e2;
            fileOutputStream = null;
            try {
//                Log.m24e(TAG, "trim Exception", (Throwable) e);
                closeQuietly(fileOutputStream);
                return false;
            } catch (Throwable th) {
//                th = th;
//                closeQuietly(fileOutputStream);
//                throw th;
            }
        } catch (Throwable th2) {
//            th = th2;
//            fileOutputStream = null;
//            closeQuietly(fileOutputStream);
//            throw th;
        }
        return true;
    }

    public boolean delete(String str, String str2, int i, int i2) {
        FileOutputStream fileOutputStream;
        String str3 = str2;
        int i3 = i;
        int i4 = i2;
        if (str == null || str3 == null) {
            Log.m22e(TAG, "delete filepath is NULL");
            return false;
        }
        try {
            Movie build = MovieCreator.build(str);
            Movie movie = new Movie();
            if (build != null) {
                Track track = build.getTracks().get(0);
                int size = track.getSamples().size();
                long mediaDuration = M4aInfo.getMediaDuration(str) / 1000;
                float f = (float) size;
                float f2 = (float) mediaDuration;
                int i5 = (int) (((((float) i3) * 1.0f) / f2) * f);
                int i6 = (int) (f * ((((float) i4) * 1.0f) / f2));
                Log.m19d(TAG, "delete original : " + mediaDuration + '[' + size + "] delete :" + i3 + '[' + i5 + "] ~ " + i4 + '[' + i6 + ']');
                if (i5 < 0) {
                    i5 = 0;
                }
                if (i6 > size) {
                    i6 = size;
                }
                LinkedList linkedList = new LinkedList();
                linkedList.add(new CroppedTrack(track, 0, (long) i5));
                linkedList.add(new CroppedTrack(track, (long) i6, (long) size));
                if (!linkedList.isEmpty()) {
                    movie.addTrack(new AppendTrack((Track[]) linkedList.toArray(new Track[linkedList.size()])));
                }
                Container build2 = new DefaultMp4Builder().build(movie);
                fileOutputStream = new FileOutputStream(new File(str3));
                try {
                    build2.writeContainer(fileOutputStream.getChannel());
                } catch (RuntimeException e) {
                    e = e;
                } catch (Exception e2) {
                    e = e2;
                    try {
                        Log.m24e(TAG, "delete Exception", (Throwable) e);
                        closeQuietly(fileOutputStream);
                        return false;
                    } catch (Throwable th) {
                        th = th;
                    }
                }
            } else {
                fileOutputStream = null;
            }
//            closeQuietly(fileOutputStream);
            return true;
        } catch (RuntimeException e3) {
//            e = e3;
//            fileOutputStream = null;
//            throw e;
        } catch (Exception e4) {
//            e = e4;
//            fileOutputStream = null;
//            Log.m24e(TAG, "delete Exception", (Throwable) e);
//            closeQuietly(fileOutputStream);
            return false;
        } catch (Throwable th2) {
//            th = th2;
//            fileOutputStream = null;
//            closeQuietly(fileOutputStream);
//            throw th;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:40:0x011b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x011c, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x011f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0120, code lost:
        r18 = TAG;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x011b A[ExcHandler: all (th java.lang.Throwable), Splitter:B:4:0x0013] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean overwrite(java.lang.String r20, java.lang.String r21, java.lang.String r22, int r23, int r24) {
        /*
            r19 = this;
            r1 = r19
            r0 = r22
            r2 = r23
            r3 = r24
            java.lang.String r4 = "M4AEditor"
            r5 = 0
            if (r20 == 0) goto L_0x013b
            if (r21 == 0) goto L_0x013b
            if (r0 != 0) goto L_0x0013
            goto L_0x013b
        L_0x0013:
            com.googlecode.mp4parser.authoring.Movie r7 = com.googlecode.mp4parser.authoring.container.mp4.MovieCreator.build((java.lang.String) r20)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            com.googlecode.mp4parser.authoring.Movie r8 = com.googlecode.mp4parser.authoring.container.mp4.MovieCreator.build((java.lang.String) r21)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            com.googlecode.mp4parser.authoring.Movie r9 = new com.googlecode.mp4parser.authoring.Movie     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r9.<init>()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            if (r7 == 0) goto L_0x0111
            if (r8 == 0) goto L_0x0111
            java.util.List r7 = r7.getTracks()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.lang.Object r7 = r7.get(r5)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            com.googlecode.mp4parser.authoring.Track r7 = (com.googlecode.mp4parser.authoring.Track) r7     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.util.List r8 = r8.getTracks()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.lang.Object r8 = r8.get(r5)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            com.googlecode.mp4parser.authoring.Track r8 = (com.googlecode.mp4parser.authoring.Track) r8     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.util.List r10 = r7.getSamples()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            int r14 = r10.size()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.util.List r10 = r8.getSamples()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            int r15 = r10.size()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            long r10 = com.sec.android.app.voicenote.service.codec.M4aInfo.getMediaDuration(r20)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r12 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 / r12
            float r12 = (float) r14     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r13 = 1065353216(0x3f800000, float:1.0)
            float r12 = r12 * r13
            float r13 = (float) r2     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            float r13 = r13 * r12
            float r6 = (float) r10     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            float r13 = r13 / r6
            int r13 = (int) r13     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            float r5 = (float) r3     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            float r12 = r12 * r5
            float r12 = r12 / r6
            int r5 = (int) r12     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.<init>()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.lang.String r12 = "overwrite original : "
            r6.append(r12)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.append(r10)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r10 = 91
            r6.append(r10)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.append(r14)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.lang.String r11 = "] overwrite : "
            r6.append(r11)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.append(r2)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.append(r10)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.append(r13)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.lang.String r2 = "] ~ "
            r6.append(r2)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.append(r3)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.append(r10)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r6.append(r5)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r2 = 93
            r6.append(r2)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            java.lang.String r2 = r6.toString()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            com.sec.android.app.voicenote.provider.Log.m19d(r4, r2)     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            if (r13 >= 0) goto L_0x009c
            r13 = 0
        L_0x009c:
            if (r13 <= r14) goto L_0x00a0
            r2 = r14
            goto L_0x00a1
        L_0x00a0:
            r2 = r13
        L_0x00a1:
            java.util.LinkedList r3 = new java.util.LinkedList     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r3.<init>()     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            com.googlecode.mp4parser.authoring.tracks.CroppedTrack r5 = new com.googlecode.mp4parser.authoring.tracks.CroppedTrack     // Catch:{ Exception -> 0x011f, all -> 0x011b }
            r12 = 0
            long r10 = (long) r2
            r16 = r10
            r10 = r5
            r11 = r7
            r18 = r4
            r6 = r14
            r4 = r15
            r14 = r16
            r10.<init>(r11, r12, r14)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            r3.add(r5)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            r3.add(r8)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            int r14 = r6 - r2
            if (r14 <= r4) goto L_0x00cf
            com.googlecode.mp4parser.authoring.tracks.CroppedTrack r5 = new com.googlecode.mp4parser.authoring.tracks.CroppedTrack     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            int r2 = r2 + r4
            long r12 = (long) r2     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            long r14 = (long) r6     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            r10 = r5
            r11 = r7
            r10.<init>(r11, r12, r14)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            r3.add(r5)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
        L_0x00cf:
            boolean r2 = r3.isEmpty()     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            if (r2 != 0) goto L_0x00e9
            com.googlecode.mp4parser.authoring.tracks.AppendTrack r2 = new com.googlecode.mp4parser.authoring.tracks.AppendTrack     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            int r4 = r3.size()     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            com.googlecode.mp4parser.authoring.Track[] r4 = new com.googlecode.mp4parser.authoring.Track[r4]     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            java.lang.Object[] r3 = r3.toArray(r4)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            com.googlecode.mp4parser.authoring.Track[] r3 = (com.googlecode.mp4parser.authoring.Track[]) r3     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            r2.<init>(r3)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            r9.addTrack(r2)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
        L_0x00e9:
            com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder r2 = new com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            r2.<init>()     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            com.coremedia.iso.boxes.Container r2 = r2.build(r9)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            java.io.RandomAccessFile r6 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            java.lang.String r3 = "rw"
            r6.<init>(r0, r3)     // Catch:{ Exception -> 0x010f, all -> 0x011b }
            java.nio.channels.FileChannel r3 = r6.getChannel()     // Catch:{ Exception -> 0x010c, all -> 0x0109 }
            r2.writeContainer(r3)     // Catch:{ Exception -> 0x0105, all -> 0x0101 }
            goto L_0x0113
        L_0x0101:
            r0 = move-exception
            r2 = r6
            r6 = r3
            goto L_0x0134
        L_0x0105:
            r0 = move-exception
            r2 = r6
            r6 = r3
            goto L_0x0124
        L_0x0109:
            r0 = move-exception
            r2 = r6
            goto L_0x011d
        L_0x010c:
            r0 = move-exception
            r2 = r6
            goto L_0x0123
        L_0x010f:
            r0 = move-exception
            goto L_0x0122
        L_0x0111:
            r3 = 0
            r6 = 0
        L_0x0113:
            r1.closeQuietly(r3)
            r1.closeQuietly(r6)
            r0 = 1
            return r0
        L_0x011b:
            r0 = move-exception
            r2 = 0
        L_0x011d:
            r6 = 0
            goto L_0x0134
        L_0x011f:
            r0 = move-exception
            r18 = r4
        L_0x0122:
            r2 = 0
        L_0x0123:
            r6 = 0
        L_0x0124:
            java.lang.String r3 = "overwrite Exception"
            r4 = r18
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r4, (java.lang.String) r3, (java.lang.Throwable) r0)     // Catch:{ all -> 0x0133 }
            r1.closeQuietly(r6)
            r1.closeQuietly(r2)
        L_0x0131:
            r2 = 0
            return r2
        L_0x0133:
            r0 = move-exception
        L_0x0134:
            r1.closeQuietly(r6)
            r1.closeQuietly(r2)
            throw r0
        L_0x013b:
            java.lang.String r0 = "overwrite filepath is NULL"
            com.sec.android.app.voicenote.provider.Log.m22e(r4, r0)
            goto L_0x0131
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.service.M4AEditor.overwrite(java.lang.String, java.lang.String, java.lang.String, int, int):boolean");
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.m24e(TAG, "closeQuietly fail - class : " + closeable.getClass().getSimpleName(), (Throwable) e);
            }
        }
    }
}
