package com.mp4parser.iso14496.part12;
//
//import com.coremedia.iso.IsoFile;
//import com.coremedia.iso.IsoTypeReader;
//import com.coremedia.iso.IsoTypeWriter;
//import com.googlecode.mp4parser.AbstractFullBox;
//import com.googlecode.mp4parser.RequiresParseDetailAspect;
//import com.googlecode.mp4parser.util.CastUtils;
//import java.nio.ByteBuffer;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.runtime.reflect.Factory;
//
//public class SampleAuxiliaryInformationOffsetsBox extends AbstractFullBox {
//    public static final String TYPE = "saio";
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
//    private String auxInfoType;
//    private String auxInfoTypeParameter;
//    private long[] offsets = new long[0];
//
//    static {
//        ajc$preClinit();
//    }
//
//    private static /* synthetic */ void ajc$preClinit() {
//        Factory factory = new Factory("SampleAuxiliaryInformationOffsetsBox.java", SampleAuxiliaryInformationOffsetsBox.class);
//        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAuxInfoType", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox", "", "", "", "java.lang.String"), 107);
//        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAuxInfoType", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox", "java.lang.String", "auxInfoType", "", "void"), 111);
//        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAuxInfoTypeParameter", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox", "", "", "", "java.lang.String"), 115);
//        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAuxInfoTypeParameter", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox", "java.lang.String", "auxInfoTypeParameter", "", "void"), 119);
//        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getOffsets", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox", "", "", "", "[J"), 123);
//        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setOffsets", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox", "[J", "offsets", "", "void"), 127);
//    }
//
//    public SampleAuxiliaryInformationOffsetsBox() {
//        super(TYPE);
//    }
//
//    /* access modifiers changed from: protected */
//    public long getContentSize() {
//        int i = 8;
//        int length = (getVersion() == 0 ? this.offsets.length * 4 : this.offsets.length * 8) + 8;
//        if ((getFlags() & 1) != 1) {
//            i = 0;
//        }
//        return (long) (length + i);
//    }
//
//    /* access modifiers changed from: protected */
//    public void getContent(ByteBuffer byteBuffer) {
//        writeVersionAndFlags(byteBuffer);
//        if ((getFlags() & 1) == 1) {
//            byteBuffer.put(IsoFile.fourCCtoBytes(this.auxInfoType));
//            byteBuffer.put(IsoFile.fourCCtoBytes(this.auxInfoTypeParameter));
//        }
//        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.offsets.length);
//        for (long valueOf : this.offsets) {
//            Long valueOf2 = Long.valueOf(valueOf);
//            if (getVersion() == 0) {
//                IsoTypeWriter.writeUInt32(byteBuffer, valueOf2.longValue());
//            } else {
//                IsoTypeWriter.writeUInt64(byteBuffer, valueOf2.longValue());
//            }
//        }
//    }
//
//    public void _parseDetails(ByteBuffer byteBuffer) {
//        parseVersionAndFlags(byteBuffer);
//        if ((getFlags() & 1) == 1) {
//            this.auxInfoType = IsoTypeReader.read4cc(byteBuffer);
//            this.auxInfoTypeParameter = IsoTypeReader.read4cc(byteBuffer);
//        }
//        int l2i = CastUtils.l2i(IsoTypeReader.readUInt32(byteBuffer));
//        this.offsets = new long[l2i];
//        for (int i = 0; i < l2i; i++) {
//            if (getVersion() == 0) {
//                this.offsets[i] = IsoTypeReader.readUInt32(byteBuffer);
//            } else {
//                this.offsets[i] = IsoTypeReader.readUInt64(byteBuffer);
//            }
//        }
//    }
//
//    public String getAuxInfoType() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
//        return this.auxInfoType;
//    }
//
//    public void setAuxInfoType(String str) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) str));
//        this.auxInfoType = str;
//    }
//
//    public String getAuxInfoTypeParameter() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
//        return this.auxInfoTypeParameter;
//    }
//
//    public void setAuxInfoTypeParameter(String str) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) str));
//        this.auxInfoTypeParameter = str;
//    }
//
//    public long[] getOffsets() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
//        return this.offsets;
//    }
//
//    public void setOffsets(long[] jArr) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, (Object) this, (Object) this, (Object) jArr));
//        this.offsets = jArr;
//    }
//}
