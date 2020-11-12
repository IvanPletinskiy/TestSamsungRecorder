package com.mp4parser.iso14496.part12;
//
////import com.coremedia.iso.IsoTypeReader;
////import com.coremedia.iso.IsoTypeWriter;
////import com.googlecode.mp4parser.AbstractBox;
////import com.googlecode.mp4parser.RequiresParseDetailAspect;
////import java.nio.ByteBuffer;
////import org.aspectj.lang.JoinPoint;
////import org.aspectj.runtime.internal.Conversions;
////import org.aspectj.runtime.reflect.Factory;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.runtime.internal.Conversions;
//import org.aspectj.runtime.reflect.Factory;
//
//import java.nio.ByteBuffer;
//
//public final class BitRateBox extends AbstractBox {
//    public static final String TYPE = "btrt";
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
//    private long avgBitrate;
//    private long bufferSizeDb;
//    private long maxBitrate;
//
//    static {
//        ajc$preClinit();
//    }
//
//    private static /* synthetic */ void ajc$preClinit() {
//        Factory factory = new Factory("BitRateBox.java", BitRateBox.class);
//        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getBufferSizeDb", "com.mp4parser.iso14496.part12.BitRateBox", "", "", "", "long"), 74);
//        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setBufferSizeDb", "com.mp4parser.iso14496.part12.BitRateBox", "long", "bufferSizeDb", "", "void"), 82);
//        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getMaxBitrate", "com.mp4parser.iso14496.part12.BitRateBox", "", "", "", "long"), 90);
//        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setMaxBitrate", "com.mp4parser.iso14496.part12.BitRateBox", "long", "maxBitrate", "", "void"), 98);
//        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAvgBitrate", "com.mp4parser.iso14496.part12.BitRateBox", "", "", "", "long"), 106);
//        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAvgBitrate", "com.mp4parser.iso14496.part12.BitRateBox", "long", "avgBitrate", "", "void"), 114);
//    }
//
//    /* access modifiers changed from: protected */
//    public long getContentSize() {
//        return 12;
//    }
//
//    public BitRateBox() {
//        super(TYPE);
//    }
//
//    public void _parseDetails(ByteBuffer byteBuffer) {
//        this.bufferSizeDb = IsoTypeReader.readUInt32(byteBuffer);
//        this.maxBitrate = IsoTypeReader.readUInt32(byteBuffer);
//        this.avgBitrate = IsoTypeReader.readUInt32(byteBuffer);
//    }
//
//    /* access modifiers changed from: protected */
//    public void getContent(ByteBuffer byteBuffer) {
//        IsoTypeWriter.writeUInt32(byteBuffer, this.bufferSizeDb);
//        IsoTypeWriter.writeUInt32(byteBuffer, this.maxBitrate);
//        IsoTypeWriter.writeUInt32(byteBuffer, this.avgBitrate);
//    }
//
//    public long getBufferSizeDb() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
//        return this.bufferSizeDb;
//    }
//
//    public void setBufferSizeDb(long j) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.longObject(j)));
//        this.bufferSizeDb = j;
//    }
//
//    public long getMaxBitrate() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
//        return this.maxBitrate;
//    }
//
//    public void setMaxBitrate(long j) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, Conversions.longObject(j)));
//        this.maxBitrate = j;
//    }
//
//    public long getAvgBitrate() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
//        return this.avgBitrate;
//    }
//
//    public void setAvgBitrate(long j) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, (Object) this, (Object) this, Conversions.longObject(j)));
//        this.avgBitrate = j;
//    }
//}
