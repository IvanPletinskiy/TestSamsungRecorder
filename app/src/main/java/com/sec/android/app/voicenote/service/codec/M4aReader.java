package com.sec.android.app.voicenote.service.codec;

import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.AudioFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class M4aReader {
    private static final String INVALID_NAME = "Invalid_name";
    private static final String TAG = "M4aReader";
    private ByteBuffer buff;
    private FileChannel channel;
    private final String path;

    public M4aReader(String str) {
        this.path = str;
    }

    public M4aInfo readFile() {
        M4aInfo m4aInfo;
        synchronized (M4aConsts.FILE_LOCK) {
            m4aInfo = null;
            if (!isM4A()) {
                return null;
            }
            this.buff = ByteBuffer.allocate(4);
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(this.path));
                this.channel = fileInputStream.getChannel();
                M4aInfo m4aInfo2 = new M4aInfo();
                m4aInfo2.path = this.path;
                AtomProperties findOuterAtoms = findOuterAtoms("moov");
                if (findOuterAtoms == null) {
                    return null;
                }
                m4aInfo2.fileMoovLength = findOuterAtoms.length;
                m4aInfo2.moovPos = findOuterAtoms.position;
                AtomProperties findOuterAtoms2 = findOuterAtoms("udta");
                if (findOuterAtoms2 == null) {
                    return null;
                }
                m4aInfo2.fileUdtaLength = findOuterAtoms2.length;
                m4aInfo2.udtaPos = findOuterAtoms2.position;
                findCustomAtom(m4aInfo2, M4aConsts.BOOK, m4aInfo2.udtaPos);
                if (m4aInfo2.hasCustomAtom.get(M4aConsts.BOOK).booleanValue()) {
                    findCustomAtom(m4aInfo2, M4aConsts.BOOKMARKS_NUMBER, m4aInfo2.customAtomPosition.get(M4aConsts.BOOK).longValue());
                }
                findCustomAtom(m4aInfo2, M4aConsts.AMPL, m4aInfo2.udtaPos);
                findCustomAtom(m4aInfo2, M4aConsts.METD, m4aInfo2.udtaPos);
                findCustomAtom(m4aInfo2, M4aConsts.STTD, m4aInfo2.udtaPos);
                findCustomAtom(m4aInfo2, M4aConsts.VRDT, m4aInfo2.udtaPos);
                findCustomAtom(m4aInfo2, "smta", m4aInfo2.udtaPos);
                if (m4aInfo2.hasCustomAtom.get("smta").booleanValue()) {
                    findCustomAtom(m4aInfo2, M4aConsts.SAUT, m4aInfo2.customAtomPosition.get("smta").longValue());
                }
                fileInputStream.close();
                m4aInfo = m4aInfo2;
            } catch (FileNotFoundException e) {
                Log.m24e(TAG, "FileNotFoundException", (Throwable) e);
            } catch (IOException e2) {
                Log.m24e(TAG, "IOException", (Throwable) e2);
            }
        }
        return m4aInfo;
    }

    private AtomProperties findOuterAtoms(String str) throws IOException {
        synchronized (M4aConsts.FILE_LOCK) {
            String str2 = "";
            AtomProperties atomProperties = new AtomProperties();
            ByteBuffer allocate = ByteBuffer.allocate(8);
            while (!str2.equals(str)) {
                try {
                    if (this.channel.read(this.buff) < 0) {
                        return null;
                    }
                    this.buff.rewind();
                    long j = (long) this.buff.getInt();
                    this.buff.rewind();
                    if (this.channel.read(this.buff) < 0) {
                        return null;
                    }
                    this.buff.rewind();
                    str2 = arrToAscii(this.buff.array());
                    int i = 1;
                    if ("mdat".equals(str2) && j == 1) {
                        if (this.channel.read(allocate) < 0) {
                            return null;
                        }
                        allocate.rewind();
                        j = allocate.getLong();
                        allocate.rewind();
                        i = 2;
                    }
                    if (!str2.equals(str)) {
                        long j2 = j - (((long) i) * 8);
                        int i2 = (j2 > 0 ? 1 : (j2 == 0 ? 0 : -1));
                        if (i2 > 0 && this.channel.position() + j2 <= this.channel.size()) {
                            this.channel.position(this.channel.position() + j2);
                        } else if (i2 == 0) {
                            Log.m32w(TAG, "skip value is 0 : " + str);
                        } else {
                            Log.m32w(TAG, "Wrong skip value finding OuterAtom: " + str + " ! Returning from function");
                            return null;
                        }
                    }
                } catch (IOException e) {
                    Log.m24e(TAG, "Error reading the file", (Throwable) e);
                    throw e;
                }
            }
            atomProperties.position = this.channel.position() - 8;
            this.buff.rewind();
            this.channel.position(atomProperties.position);
            if (this.channel.read(this.buff) < 0) {
                return null;
            }
            this.buff.rewind();
            atomProperties.length = this.buff.getInt();
            this.buff.rewind();
            this.channel.position(atomProperties.position + 8);
            return atomProperties;
        }
    }

    private void findCustomAtom(M4aInfo m4aInfo, String str, long j) throws IOException {
        try {
            this.channel.position(j + 8);
            this.buff.rewind();
            String str2 = "";
            while (!str2.equals(str)) {
                if (this.channel.read(this.buff) == -1) {
                    m4aInfo.hasCustomAtom.put(str, false);
                    return;
                }
                this.buff.rewind();
                long j2 = (long) this.buff.getInt();
                this.buff.rewind();
                if (this.channel.read(this.buff) >= 0) {
                    this.buff.rewind();
                    String arrToAscii = arrToAscii(this.buff.array());
                    if (!arrToAscii.equals(str)) {
                        long j3 = j2 - 8;
                        if (j3 <= 0 || this.channel.position() + j3 > this.channel.size()) {
                            Log.m32w(TAG, "Wrong skip value finding STTD! Possibly we are out of the file. Returning from function");
                            m4aInfo.hasCustomAtom.put(str, false);
                            return;
                        }
                        this.channel.position(this.channel.position() + j3);
                    }
                    str2 = arrToAscii;
                } else {
                    return;
                }
            }
            m4aInfo.customAtomPosition.put(str, Long.valueOf(this.channel.position() - 8));
            long position = this.channel.position();
            this.channel.position(m4aInfo.customAtomPosition.get(str).longValue());
            if (this.channel.read(this.buff) >= 0) {
                this.buff.rewind();
                m4aInfo.customAtomLength.put(str, Integer.valueOf(this.buff.getInt()));
                this.buff.rewind();
                this.channel.position(position);
                m4aInfo.hasCustomAtom.put(str, true);
            }
        } catch (IOException e) {
            Log.m24e(TAG, "Error reading the file", (Throwable) e);
            throw e;
        }
    }

    private String arrToAscii(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArr.length);
        for (byte b : bArr) {
            if (b < 0) {
                return INVALID_NAME;
            }
            sb.append((char) b);
        }
        return sb.toString();
    }

    private boolean isM4A() {
        return isM4A(this.path);
    }

    static class AtomProperties {
        int length = 0;
        long position = 0;

        AtomProperties() {
        }
    }

    public static boolean isM4A(String str) {
        return str != null && (str.endsWith(AudioFormat.ExtType.EXT_M4A) || str.endsWith(".M4A"));
    }

    public final String getPath() {
        return this.path;
    }
}
