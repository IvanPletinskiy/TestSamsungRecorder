package com.mp4parser.iso14496.part12;
//
//import com.coremedia.iso.IsoFile;
//import com.coremedia.iso.IsoTypeReader;
//import com.coremedia.iso.IsoTypeWriter;
//import com.googlecode.mp4parser.AbstractFullBox;
//import com.googlecode.mp4parser.RequiresParseDetailAspect;
//import com.googlecode.mp4parser.util.CastUtils;
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.runtime.internal.Conversions;
//import org.aspectj.runtime.reflect.Factory;
//
//public class SampleAuxiliaryInformationSizesBox extends AbstractFullBox {
//    static final /* synthetic */ boolean $assertionsDisabled = false;
//    public static final String TYPE = "saiz";
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_10 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_11 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_6 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_7 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_8 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_9 = null;
//    private String auxInfoType;
//    private String auxInfoTypeParameter;
//    private short defaultSampleInfoSize;
//    private int sampleCount;
//    private short[] sampleInfoSizes = new short[0];
//
//    static {
//        ajc$preClinit();
//    }
//
//    private static /* synthetic */ void ajc$preClinit() {
//        Factory factory = new Factory("SampleAuxiliaryInformationSizesBox.java", SampleAuxiliaryInformationSizesBox.class);
//        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSize", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "int", "index", "", "short"), 57);
//        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAuxInfoType", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "", "", "", "java.lang.String"), 107);
//        ajc$tjp_10 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSampleCount", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "int", "sampleCount", "", "void"), 144);
//        ajc$tjp_11 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "", "", "", "java.lang.String"), 149);
//        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAuxInfoType", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "java.lang.String", "auxInfoType", "", "void"), 111);
//        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAuxInfoTypeParameter", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "", "", "", "java.lang.String"), 115);
//        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAuxInfoTypeParameter", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "java.lang.String", "auxInfoTypeParameter", "", "void"), 119);
//        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getDefaultSampleInfoSize", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "", "", "", "int"), 123);
//        ajc$tjp_6 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setDefaultSampleInfoSize", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "int", "defaultSampleInfoSize", "", "void"), 127);
//        ajc$tjp_7 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSampleInfoSizes", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "", "", "", "[S"), 132);
//        ajc$tjp_8 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSampleInfoSizes", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "[S", "sampleInfoSizes", "", "void"), 136);
//        ajc$tjp_9 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSampleCount", "com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox", "", "", "", "int"), 140);
//    }
//
//    public SampleAuxiliaryInformationSizesBox() {
//        super(TYPE);
//    }
//
//    /* access modifiers changed from: protected */
//    public long getContentSize() {
//        return (long) (((getFlags() & 1) == 1 ? 12 : 4) + 5 + (this.defaultSampleInfoSize == 0 ? this.sampleInfoSizes.length : 0));
//    }
//
//    public short getSize(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, (Object) this, (Object) this, Conversions.intObject(i)));
//        if (getDefaultSampleInfoSize() == 0) {
//            return this.sampleInfoSizes[i];
//        }
//        return this.defaultSampleInfoSize;
//    }
//
//    /* access modifiers changed from: protected */
//    public void getContent(ByteBuffer byteBuffer) {
//        writeVersionAndFlags(byteBuffer);
//        if ((getFlags() & 1) == 1) {
//            byteBuffer.put(IsoFile.fourCCtoBytes(this.auxInfoType));
//            byteBuffer.put(IsoFile.fourCCtoBytes(this.auxInfoTypeParameter));
//        }
//        IsoTypeWriter.writeUInt8(byteBuffer, this.defaultSampleInfoSize);
//        if (this.defaultSampleInfoSize == 0) {
//            IsoTypeWriter.writeUInt32(byteBuffer, (long) this.sampleInfoSizes.length);
//            for (short writeUInt8 : this.sampleInfoSizes) {
//                IsoTypeWriter.writeUInt8(byteBuffer, writeUInt8);
//            }
//            return;
//        }
//        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.sampleCount);
//    }
//
//    public void _parseDetails(ByteBuffer byteBuffer) {
//        parseVersionAndFlags(byteBuffer);
//        if ((getFlags() & 1) == 1) {
//            this.auxInfoType = IsoTypeReader.read4cc(byteBuffer);
//            this.auxInfoTypeParameter = IsoTypeReader.read4cc(byteBuffer);
//        }
//        this.defaultSampleInfoSize = (short) IsoTypeReader.readUInt8(byteBuffer);
//        this.sampleCount = CastUtils.l2i(IsoTypeReader.readUInt32(byteBuffer));
//        if (this.defaultSampleInfoSize == 0) {
//            this.sampleInfoSizes = new short[this.sampleCount];
//            for (int i = 0; i < this.sampleCount; i++) {
//                this.sampleInfoSizes[i] = (short) IsoTypeReader.readUInt8(byteBuffer);
//            }
//        }
//    }
//
//    public String getAuxInfoType() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
//        return this.auxInfoType;
//    }
//
//    public void setAuxInfoType(String str) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, (Object) str));
//        this.auxInfoType = str;
//    }
//
//    public String getAuxInfoTypeParameter() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this));
//        return this.auxInfoTypeParameter;
//    }
//
//    public void setAuxInfoTypeParameter(String str) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, (Object) this, (Object) this, (Object) str));
//        this.auxInfoTypeParameter = str;
//    }
//
//    public int getDefaultSampleInfoSize() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
//        return this.defaultSampleInfoSize;
//    }
//
//    public void setDefaultSampleInfoSize(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.defaultSampleInfoSize = (short) i;
//    }
//
//    public short[] getSampleInfoSizes() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this));
//        short[] sArr = this.sampleInfoSizes;
//        return Arrays.copyOf(sArr, sArr.length);
//    }
//
//    public void setSampleInfoSizes(short[] sArr) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, (Object) this, (Object) this, (Object) sArr));
//        this.sampleInfoSizes = Arrays.copyOf(sArr, sArr.length);
//    }
//
//    public int getSampleCount() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, this, this));
//        return this.sampleCount;
//    }
//
//    public void setSampleCount(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.sampleCount = i;
//    }
//
//    public String toString() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, this, this));
//        return "SampleAuxiliaryInformationSizesBox{defaultSampleInfoSize=" + this.defaultSampleInfoSize + ", sampleCount=" + this.sampleCount + ", auxInfoType='" + this.auxInfoType + '\'' + ", auxInfoTypeParameter='" + this.auxInfoTypeParameter + '\'' + '}';
//    }
//}
