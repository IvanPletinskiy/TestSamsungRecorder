package okio;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;

public final class Buffer implements BufferedSource, BufferedSink, Cloneable, ByteChannel {
    private static final byte[] DIGITS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    Segment head;
    long size;

    public void close() {
    }

    public void flush() {
    }

    public Buffer getBuffer() {
        return this;
    }

    public boolean isOpen() {
        return true;
    }

    public final long size() {
        return this.size;
    }

    public boolean exhausted() {
        return this.size == 0;
    }

    public boolean request(long j) {
        return this.size >= j;
    }

    public byte readByte() {
        long j = this.size;
        if (j != 0) {
            Segment segment = this.head;
            int i = segment.pos;
            int i2 = segment.limit;
            int i3 = i + 1;
            byte b = segment.data[i];
            this.size = j - 1;
            if (i3 == i2) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = i3;
            }
            return b;
        }
        throw new IllegalStateException("size == 0");
    }

    public final byte getByte(long j) {
        int i;
        Util.checkOffsetAndCount(this.size, j, 1);
        long j2 = this.size;
        if (j2 - j > j) {
            Segment segment = this.head;
            while (true) {
                int i2 = segment.limit;
                int i3 = segment.pos;
                long j3 = (long) (i2 - i3);
                if (j < j3) {
                    return segment.data[i3 + ((int) j)];
                }
                j -= j3;
                segment = segment.next;
            }
        } else {
            long j4 = j - j2;
            Segment segment2 = this.head;
            do {
                segment2 = segment2.prev;
                int i4 = segment2.limit;
                i = segment2.pos;
                j4 += (long) (i4 - i);
            } while (j4 < 0);
            return segment2.data[i + ((int) j4)];
        }
    }

    public int readInt() {
        long j = this.size;
        if (j >= 4) {
            Segment segment = this.head;
            int i = segment.pos;
            int i2 = segment.limit;
            if (i2 - i < 4) {
                return ((readByte() & 255) << 24) | ((readByte() & 255) << 16) | ((readByte() & 255) << 8) | (readByte() & 255);
            }
            byte[] bArr = segment.data;
            int i3 = i + 1;
            int i4 = i3 + 1;
            int b = ((bArr[i] & 255) << 24) | ((bArr[i3] & 255) << 16);
            int i5 = i4 + 1;
            int b2 = b | ((bArr[i4] & 255) << 8);
            int i6 = i5 + 1;
            int b3 = b2 | (bArr[i5] & 255);
            this.size = j - 4;
            if (i6 == i2) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            } else {
                segment.pos = i6;
            }
            return b3;
        }
        throw new IllegalStateException("size < 4: " + this.size);
    }

    public ByteString readByteString() {
        return new ByteString(readByteArray());
    }

    public int select(Options options) {
        int selectPrefix = selectPrefix(options, false);
        if (selectPrefix == -1) {
            return -1;
        }
        try {
            skip((long) options.byteStrings[selectPrefix].size());
            return selectPrefix;
        } catch (EOFException unused) {
            throw new AssertionError();
        }
    }

    /* access modifiers changed from: package-private */
    public int selectPrefix(Options options, boolean z) {
        int i;
        int i2;
        Options options2 = options;
        Segment segment = this.head;
        int i3 = -2;
        if (segment != null) {
            byte[] bArr = segment.data;
            int i4 = segment.pos;
            int i5 = segment.limit;
            int[] iArr = options2.trie;
            int i6 = i4;
            int i7 = i5;
            int i8 = -1;
            Segment segment2 = segment;
            byte[] bArr2 = bArr;
            int i9 = 0;
            loop0:
            while (true) {
                int i10 = i9 + 1;
                int i11 = iArr[i9];
                int i12 = i10 + 1;
                int i13 = iArr[i10];
                if (i13 != -1) {
                    i8 = i13;
                }
                if (segment2 == null) {
                    break;
                }
                if (i11 < 0) {
                    int i14 = i12 + (i11 * -1);
                    while (true) {
                        i2 = i6 + 1;
                        int i15 = i12 + 1;
                        if ((bArr2[i6] & 255) != iArr[i12]) {
                            return i8;
                        }
                        boolean z2 = i15 == i14;
                        if (i2 == i7) {
                            Segment segment3 = segment2.next;
                            int i16 = segment3.pos;
                            bArr2 = segment3.data;
                            i7 = segment3.limit;
                            if (segment3 != segment) {
                                int i17 = i16;
                                segment2 = segment3;
                                i2 = i17;
                            } else if (!z2) {
                                break loop0;
                            } else {
                                i2 = i16;
                                segment2 = null;
                            }
                        }
                        if (z2) {
                            i = iArr[i15];
                            break;
                        }
                        i6 = i2;
                        i12 = i15;
                    }
                } else {
                    int i18 = i6 + 1;
                    int b = bArr2[i6] & 255;
                    int i19 = i12 + i11;
                    while (i12 != i19) {
                        if (b == iArr[i12]) {
                            i = iArr[i12 + i11];
                            if (i18 == i7) {
                                Segment segment4 = segment2.next;
                                i2 = segment4.pos;
                                byte[] bArr3 = segment4.data;
                                int i20 = segment4.limit;
                                if (segment4 == segment) {
                                    i7 = i20;
                                    bArr2 = bArr3;
                                    segment2 = null;
                                } else {
                                    i7 = i20;
                                    bArr2 = bArr3;
                                    segment2 = segment4;
                                }
                            } else {
                                i2 = i18;
                            }
                        } else {
                            i12++;
                        }
                    }
                    return i8;
                }
                if (i >= 0) {
                    return i;
                }
                int i21 = -i;
                i6 = i2;
                i9 = i21;
                i3 = -2;
            }
            return z ? i3 : i8;
        } else if (z) {
            return -2;
        } else {
            return options2.indexOf(ByteString.EMPTY);
        }
    }

    public String readUtf8() {
        try {
            return readString(this.size, Util.UTF_8);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public String readUtf8(long j) throws EOFException {
        return readString(j, Util.UTF_8);
    }

    public String readString(long j, Charset charset) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, j);
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else if (j > 2147483647L) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + j);
        } else if (j == 0) {
            return "";
        } else {
            Segment segment = this.head;
            int i = segment.pos;
            if (((long) i) + j > ((long) segment.limit)) {
                return new String(readByteArray(j), charset);
            }
            String str = new String(segment.data, i, (int) j, charset);
            segment.pos = (int) (((long) segment.pos) + j);
            this.size -= j;
            if (segment.pos == segment.limit) {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            }
            return str;
        }
    }

    public byte[] readByteArray() {
        try {
            return readByteArray(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public byte[] readByteArray(long j) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, j);
        if (j <= 2147483647L) {
            byte[] bArr = new byte[((int) j)];
            readFully(bArr);
            return bArr;
        }
        throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + j);
    }

    public void readFully(byte[] bArr) throws EOFException {
        int i = 0;
        while (i < bArr.length) {
            int read = read(bArr, i, bArr.length - i);
            if (read != -1) {
                i += read;
            } else {
                throw new EOFException();
            }
        }
    }

    public int read(byte[] bArr, int i, int i2) {
        Util.checkOffsetAndCount((long) bArr.length, (long) i, (long) i2);
        Segment segment = this.head;
        if (segment == null) {
            return -1;
        }
        int min = Math.min(i2, segment.limit - segment.pos);
        System.arraycopy(segment.data, segment.pos, bArr, i, min);
        segment.pos += min;
        this.size -= (long) min;
        if (segment.pos == segment.limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return min;
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        Segment segment = this.head;
        if (segment == null) {
            return -1;
        }
        int min = Math.min(byteBuffer.remaining(), segment.limit - segment.pos);
        byteBuffer.put(segment.data, segment.pos, min);
        segment.pos += min;
        this.size -= (long) min;
        if (segment.pos == segment.limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        }
        return min;
    }

    public final void clear() {
        try {
            skip(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public void skip(long j) throws EOFException {
        while (j > 0) {
            Segment segment = this.head;
            if (segment != null) {
                int min = (int) Math.min(j, (long) (segment.limit - segment.pos));
                long j2 = (long) min;
                this.size -= j2;
                j -= j2;
                Segment segment2 = this.head;
                segment2.pos += min;
                if (segment2.pos == segment2.limit) {
                    this.head = segment2.pop();
                    SegmentPool.recycle(segment2);
                }
            } else {
                throw new EOFException();
            }
        }
    }

    public Buffer writeUtf8(String str) {
        writeUtf8(str, 0, str.length());
        return this;
    }

    public Buffer writeUtf8(String str, int i, int i2) {
        char c;
        if (str == null) {
            throw new IllegalArgumentException("string == null");
        } else if (i < 0) {
            throw new IllegalArgumentException("beginIndex < 0: " + i);
        } else if (i2 < i) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + i2 + " < " + i);
        } else if (i2 <= str.length()) {
            while (i < i2) {
                char charAt = str.charAt(i);
                if (charAt < 128) {
                    Segment writableSegment = writableSegment(1);
                    byte[] bArr = writableSegment.data;
                    int i3 = writableSegment.limit - i;
                    int min = Math.min(i2, 8192 - i3);
                    int i4 = i + 1;
                    bArr[i + i3] = (byte) charAt;
                    while (i4 < min) {
                        char charAt2 = str.charAt(i4);
                        if (charAt2 >= 128) {
                            break;
                        }
                        bArr[i4 + i3] = (byte) charAt2;
                        i4++;
                    }
                    int i5 = writableSegment.limit;
                    int i6 = (i3 + i4) - i5;
                    writableSegment.limit = i5 + i6;
                    this.size += (long) i6;
                    i = i4;
                } else {
                    if (charAt < 2048) {
                        writeByte((charAt >> 6) | 192);
                        writeByte((int) (charAt & '?') | 128);
                    } else if (charAt < 55296 || charAt > 57343) {
                        writeByte((charAt >> 12) | 224);
                        writeByte(((charAt >> 6) & 63) | 128);
                        writeByte((int) (charAt & '?') | 128);
                    } else {
                        int i7 = i + 1;
                        if (i7 < i2) {
                            c = str.charAt(i7);
                        } else {
                            c = 0;
                        }
                        if (charAt > 56319 || c < 56320 || c > 57343) {
                            writeByte(63);
                            i = i7;
                        } else {
                            int i8 = (((charAt & 10239) << 10) | (9215 & c)) + 0;
                            writeByte((i8 >> 18) | 240);
                            writeByte(((i8 >> 12) & 63) | 128);
                            writeByte(((i8 >> 6) & 63) | 128);
                            writeByte((i8 & 63) | 128);
                            i += 2;
                        }
                    }
                    i++;
                }
            }
            return this;
        } else {
            throw new IllegalArgumentException("endIndex > string.length: " + i2 + " > " + str.length());
        }
    }

    public int write(ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer != null) {
            int remaining = byteBuffer.remaining();
            int i = remaining;
            while (i > 0) {
                Segment writableSegment = writableSegment(1);
                int min = Math.min(i, 8192 - writableSegment.limit);
                byteBuffer.get(writableSegment.data, writableSegment.limit, min);
                i -= min;
                writableSegment.limit += min;
            }
            this.size += (long) remaining;
            return remaining;
        }
        throw new IllegalArgumentException("source == null");
    }

    public Buffer writeByte(int i) {
        Segment writableSegment = writableSegment(1);
        byte[] bArr = writableSegment.data;
        int i2 = writableSegment.limit;
        writableSegment.limit = i2 + 1;
        bArr[i2] = (byte) i;
        this.size++;
        return this;
    }

    public Buffer writeInt(int i) {
        Segment writableSegment = writableSegment(4);
        byte[] bArr = writableSegment.data;
        int i2 = writableSegment.limit;
        int i3 = i2 + 1;
        bArr[i2] = (byte) ((i >>> 24) & 255);
        int i4 = i3 + 1;
        bArr[i3] = (byte) ((i >>> 16) & 255);
        int i5 = i4 + 1;
        bArr[i4] = (byte) ((i >>> 8) & 255);
        bArr[i5] = (byte) (i & 255);
        writableSegment.limit = i5 + 1;
        this.size += 4;
        return this;
    }

    /* access modifiers changed from: package-private */
    public Segment writableSegment(int i) {
        if (i < 1 || i > 8192) {
            throw new IllegalArgumentException();
        }
        Segment segment = this.head;
        if (segment == null) {
            this.head = SegmentPool.take();
            Segment segment2 = this.head;
            segment2.prev = segment2;
            segment2.next = segment2;
            return segment2;
        }
        Segment segment3 = segment.prev;
        if (segment3.limit + i <= 8192 && segment3.owner) {
            return segment3;
        }
        Segment take = SegmentPool.take();
        segment3.push(take);
        return take;
    }

    public void write(Buffer buffer, long j) {
        int i;
        if (buffer == null) {
            throw new IllegalArgumentException("source == null");
        } else if (buffer != this) {
            Util.checkOffsetAndCount(buffer.size, 0, j);
            while (j > 0) {
                Segment segment = buffer.head;
                if (j < ((long) (segment.limit - segment.pos))) {
                    Segment segment2 = this.head;
                    Segment segment3 = segment2 != null ? segment2.prev : null;
                    if (segment3 != null && segment3.owner) {
                        long j2 = ((long) segment3.limit) + j;
                        if (segment3.shared) {
                            i = 0;
                        } else {
                            i = segment3.pos;
                        }
                        if (j2 - ((long) i) <= 8192) {
                            buffer.head.writeTo(segment3, (int) j);
                            buffer.size -= j;
                            this.size += j;
                            return;
                        }
                    }
                    buffer.head = buffer.head.split((int) j);
                }
                Segment segment4 = buffer.head;
                long j3 = (long) (segment4.limit - segment4.pos);
                buffer.head = segment4.pop();
                Segment segment5 = this.head;
                if (segment5 == null) {
                    this.head = segment4;
                    Segment segment6 = this.head;
                    segment6.prev = segment6;
                    segment6.next = segment6;
                } else {
                    segment5.prev.push(segment4);
                    segment4.compact();
                }
                buffer.size -= j3;
                this.size += j3;
                j -= j3;
            }
        } else {
            throw new IllegalArgumentException("source == this");
        }
    }

    public long read(Buffer buffer, long j) {
        if (buffer == null) {
            throw new IllegalArgumentException("sink == null");
        } else if (j >= 0) {
            long j2 = this.size;
            if (j2 == 0) {
                return -1;
            }
            if (j > j2) {
                j = j2;
            }
            buffer.write(this, j);
            return j;
        } else {
            throw new IllegalArgumentException("byteCount < 0: " + j);
        }
    }

    public long indexOf(ByteString byteString) throws IOException {
        return indexOf(byteString, 0);
    }

    public long indexOf(ByteString byteString, long j) throws IOException {
        byte[] bArr;
        Segment segment;
        if (byteString.size() != 0) {
            long j2 = 0;
            if (j >= 0) {
                Segment segment2 = this.head;
                long j3 = -1;
                if (segment2 == null) {
                    return -1;
                }
                long j4 = this.size;
                if (j4 - j >= j) {
                    while (true) {
                        j4 = j2;
                        j2 = ((long) (segment2.limit - segment2.pos)) + j4;
                        if (j2 >= j) {
                            break;
                        }
                        segment2 = segment2.next;
                    }
                } else {
                    while (j4 > j) {
                        segment2 = segment2.prev;
                        j4 -= (long) (segment2.limit - segment2.pos);
                    }
                }
                byte b = byteString.getByte(0);
                int size2 = byteString.size();
                long j5 = 1 + (this.size - ((long) size2));
                long j6 = j;
                Segment segment3 = segment2;
                long j7 = j4;
                while (j7 < j5) {
                    byte[] bArr2 = segment3.data;
                    int min = (int) Math.min((long) segment3.limit, (((long) segment3.pos) + j5) - j7);
                    int i = (int) ((((long) segment3.pos) + j6) - j7);
                    while (i < min) {
                        if (bArr2[i] == b) {
                            bArr = bArr2;
                            segment = segment3;
                            if (rangeEquals(segment3, i + 1, byteString, 1, size2)) {
                                return ((long) (i - segment.pos)) + j7;
                            }
                        } else {
                            bArr = bArr2;
                            segment = segment3;
                        }
                        i++;
                        segment3 = segment;
                        bArr2 = bArr;
                    }
                    Segment segment4 = segment3;
                    j6 = ((long) (segment4.limit - segment4.pos)) + j7;
                    segment3 = segment4.next;
                    j3 = -1;
                    j7 = j6;
                }
                return j3;
            }
            throw new IllegalArgumentException("fromIndex < 0");
        }
        throw new IllegalArgumentException("bytes is empty");
    }

    public long indexOfElement(ByteString byteString) {
        return indexOfElement(byteString, 0);
    }

    public long indexOfElement(ByteString byteString, long j) {
        int i = 0;
        int i2 = 0;
        long j2 = 0;
        if (j >= 0) {
            Segment segment = this.head;
            if (segment == null) {
                return -1;
            }
            long j3 = this.size;
            if (j3 - j >= j) {
                while (true) {
                    j3 = j2;
                    j2 = ((long) (segment.limit - segment.pos)) + j3;
                    if (j2 >= j) {
                        break;
                    }
                    segment = segment.next;
                }
            } else {
                while (j3 > j) {
                    segment = segment.prev;
                    j3 -= (long) (segment.limit - segment.pos);
                }
            }
            if (byteString.size() == 2) {
                byte b = byteString.getByte(0);
                byte b2 = byteString.getByte(1);
                while (j3 < this.size) {
                    byte[] bArr = segment.data;
                    i = (int) ((((long) segment.pos) + j) - j3);
                    int i3 = segment.limit;
                    while (i < i3) {
                        byte b3 = bArr[i];
                        if (b3 == b || b3 == b2) {
                            i2 = segment.pos;
                        } else {
                            i++;
                        }
                    }
                    j = ((long) (segment.limit - segment.pos)) + j3;
                    segment = segment.next;
                    j3 = j;
                }
                return -1;
            }
            byte[] internalArray = byteString.internalArray();
            while (j3 < this.size) {
                byte[] bArr2 = segment.data;
                int i4 = (int) ((((long) segment.pos) + j) - j3);
                int i5 = segment.limit;
                while (i < i5) {
                    byte b4 = bArr2[i];
                    int length = internalArray.length;
                    int i6 = 0;
                    while (i6 < length) {
                        if (b4 == internalArray[i6]) {
                            i2 = segment.pos;
                        } else {
                            i6++;
                        }
                    }
                    i4 = i + 1;
                }
                j = ((long) (segment.limit - segment.pos)) + j3;
                segment = segment.next;
                j3 = j;
            }
            return ((long) (i - i2)) + j3;
//            return -1;
        }
        throw new IllegalArgumentException("fromIndex < 0");
    }

    private boolean rangeEquals(Segment segment, int i, ByteString byteString, int i2, int i3) {
        int i4 = segment.limit;
        byte[] bArr = segment.data;
        while (i2 < i3) {
            if (i == i4) {
                segment = segment.next;
                byte[] bArr2 = segment.data;
                int i5 = segment.pos;
                bArr = bArr2;
                i = i5;
                i4 = segment.limit;
            }
            if (bArr[i] != byteString.getByte(i2)) {
                return false;
            }
            i++;
            i2++;
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Buffer)) {
            return false;
        }
        Buffer buffer = (Buffer) obj;
        long j = this.size;
        if (j != buffer.size) {
            return false;
        }
        long j2 = 0;
        if (j == 0) {
            return true;
        }
        Segment segment = this.head;
        Segment segment2 = buffer.head;
        int i = segment.pos;
        int i2 = segment2.pos;
        while (j2 < this.size) {
            long min = (long) Math.min(segment.limit - i, segment2.limit - i2);
            int i3 = i2;
            int i4 = i;
            int i5 = 0;
            while (((long) i5) < min) {
                int i6 = i4 + 1;
                int i7 = i3 + 1;
                if (segment.data[i4] != segment2.data[i3]) {
                    return false;
                }
                i5++;
                i4 = i6;
                i3 = i7;
            }
            if (i4 == segment.limit) {
                segment = segment.next;
                i = segment.pos;
            } else {
                i = i4;
            }
            if (i3 == segment2.limit) {
                segment2 = segment2.next;
                i2 = segment2.pos;
            } else {
                i2 = i3;
            }
            j2 += min;
        }
        return true;
    }

    public int hashCode() {
        Segment segment = this.head;
        if (segment == null) {
            return 0;
        }
        int i = 1;
        do {
            int i2 = segment.limit;
            for (int i3 = segment.pos; i3 < i2; i3++) {
                i = (i * 31) + segment.data[i3];
            }
            segment = segment.next;
        } while (segment != this.head);
        return i;
    }

    public String toString() {
        return snapshot().toString();
    }

    public Buffer clone() {
        Buffer buffer = new Buffer();
        if (this.size == 0) {
            return buffer;
        }
        buffer.head = this.head.sharedCopy();
        Segment segment = buffer.head;
        segment.prev = segment;
        segment.next = segment;
        Segment segment2 = this.head;
        while (true) {
            segment2 = segment2.next;
            if (segment2 != this.head) {
                buffer.head.prev.push(segment2.sharedCopy());
            } else {
                buffer.size = this.size;
                return buffer;
            }
        }
    }

    public final ByteString snapshot() {
        long j = this.size;
        if (j <= 2147483647L) {
            return snapshot((int) j);
        }
        throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.size);
    }

    public final ByteString snapshot(int i) {
        if (i == 0) {
            return ByteString.EMPTY;
        }
        return new SegmentedByteString(this, i);
    }
}
