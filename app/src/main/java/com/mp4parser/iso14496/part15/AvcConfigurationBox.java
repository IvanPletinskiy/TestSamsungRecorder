package com.mp4parser.iso14496.part15;
//
//import com.googlecode.mp4parser.AbstractBox;
//import com.googlecode.mp4parser.RequiresParseDetailAspect;
//import java.nio.ByteBuffer;
//import java.util.Collections;
//import java.util.List;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.runtime.internal.Conversions;
//import org.aspectj.runtime.reflect.Factory;
//
//public final class AvcConfigurationBox extends AbstractBox {
//    public static final String TYPE = "avcC";
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_10 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_11 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_12 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_13 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_14 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_15 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_16 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_17 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_18 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_19 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_20 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_21 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_22 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_23 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_24 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_25 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_26 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_27 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_28 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_29 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_6 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_7 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_8 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_9 = null;
//    public AvcDecoderConfigurationRecord avcDecoderConfigurationRecord = new AvcDecoderConfigurationRecord();
//
//    static {
//        ajc$preClinit();
//    }
//
//    private static /* synthetic */ void ajc$preClinit() {
//        Factory factory = new Factory("AvcConfigurationBox.java", AvcConfigurationBox.class);
//        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getConfigurationVersion", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "int"), 44);
//        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAvcProfileIndication", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "int"), 48);
//        ajc$tjp_10 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAvcLevelIndication", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "avcLevelIndication", "", "void"), 84);
//        ajc$tjp_11 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setLengthSizeMinusOne", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "lengthSizeMinusOne", "", "void"), 88);
//        ajc$tjp_12 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSequenceParameterSets", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "java.util.List", "sequenceParameterSets", "", "void"), 92);
//        ajc$tjp_13 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setPictureParameterSets", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "java.util.List", "pictureParameterSets", "", "void"), 96);
//        ajc$tjp_14 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getChromaFormat", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "int"), 100);
//        ajc$tjp_15 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setChromaFormat", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "chromaFormat", "", "void"), 104);
//        ajc$tjp_16 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getBitDepthLumaMinus8", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "int"), 108);
//        ajc$tjp_17 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setBitDepthLumaMinus8", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "bitDepthLumaMinus8", "", "void"), 112);
//        ajc$tjp_18 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getBitDepthChromaMinus8", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "int"), 116);
//        ajc$tjp_19 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setBitDepthChromaMinus8", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "bitDepthChromaMinus8", "", "void"), 120);
//        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getProfileCompatibility", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "int"), 52);
//        ajc$tjp_20 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSequenceParameterSetExts", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "java.util.List"), 124);
//        ajc$tjp_21 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSequenceParameterSetExts", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "java.util.List", "sequenceParameterSetExts", "", "void"), 128);
//        ajc$tjp_22 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "hasExts", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "boolean"), 132);
//        ajc$tjp_23 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setHasExts", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "boolean", "hasExts", "", "void"), 136);
//        ajc$tjp_24 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getContentSize", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "long"), 147);
//        ajc$tjp_25 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getContent", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "java.nio.ByteBuffer", "byteBuffer", "", "void"), 153);
//        ajc$tjp_26 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSPS", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "[Ljava.lang.String;"), 158);
//        ajc$tjp_27 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getPPS", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "[Ljava.lang.String;"), 162);
//        ajc$tjp_28 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getavcDecoderConfigurationRecord", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "com.mp4parser.iso14496.part15.AvcDecoderConfigurationRecord"), 167);
//        ajc$tjp_29 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "java.lang.String"), 172);
//        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAvcLevelIndication", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "int"), 56);
//        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getLengthSizeMinusOne", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "int"), 60);
//        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSequenceParameterSets", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "java.util.List"), 64);
//        ajc$tjp_6 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getPictureParameterSets", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "", "", "", "java.util.List"), 68);
//        ajc$tjp_7 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setConfigurationVersion", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "configurationVersion", "", "void"), 72);
//        ajc$tjp_8 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAvcProfileIndication", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "avcProfileIndication", "", "void"), 76);
//        ajc$tjp_9 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setProfileCompatibility", "com.mp4parser.iso14496.part15.AvcConfigurationBox", "int", "profileCompatibility", "", "void"), 80);
//    }
//
//    public AvcConfigurationBox() {
//        super(TYPE);
//    }
//
//    public int getConfigurationVersion() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
//        return this.avcDecoderConfigurationRecord.configurationVersion;
//    }
//
//    public int getAvcProfileIndication() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
//        return this.avcDecoderConfigurationRecord.avcProfileIndication;
//    }
//
//    public int getProfileCompatibility() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
//        return this.avcDecoderConfigurationRecord.profileCompatibility;
//    }
//
//    public int getAvcLevelIndication() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this));
//        return this.avcDecoderConfigurationRecord.avcLevelIndication;
//    }
//
//    public int getLengthSizeMinusOne() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
//        return this.avcDecoderConfigurationRecord.lengthSizeMinusOne;
//    }
//
//    public List<byte[]> getSequenceParameterSets() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
//        return Collections.unmodifiableList(this.avcDecoderConfigurationRecord.sequenceParameterSets);
//    }
//
//    public List<byte[]> getPictureParameterSets() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
//        return Collections.unmodifiableList(this.avcDecoderConfigurationRecord.pictureParameterSets);
//    }
//
//    public void setConfigurationVersion(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.avcDecoderConfigurationRecord.configurationVersion = i;
//    }
//
//    public void setAvcProfileIndication(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.avcDecoderConfigurationRecord.avcProfileIndication = i;
//    }
//
//    public void setProfileCompatibility(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.avcDecoderConfigurationRecord.profileCompatibility = i;
//    }
//
//    public void setAvcLevelIndication(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.avcDecoderConfigurationRecord.avcLevelIndication = i;
//    }
//
//    public void setLengthSizeMinusOne(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.avcDecoderConfigurationRecord.lengthSizeMinusOne = i;
//    }
//
//    public void setSequenceParameterSets(List<byte[]> list) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, (Object) this, (Object) this, (Object) list));
//        this.avcDecoderConfigurationRecord.sequenceParameterSets = list;
//    }
//
//    public void setPictureParameterSets(List<byte[]> list) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, (Object) this, (Object) this, (Object) list));
//        this.avcDecoderConfigurationRecord.pictureParameterSets = list;
//    }
//
//    public int getChromaFormat() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_14, this, this));
//        return this.avcDecoderConfigurationRecord.chromaFormat;
//    }
//
//    public void setChromaFormat(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_15, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.avcDecoderConfigurationRecord.chromaFormat = i;
//    }
//
//    public int getBitDepthLumaMinus8() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_16, this, this));
//        return this.avcDecoderConfigurationRecord.bitDepthLumaMinus8;
//    }
//
//    public void setBitDepthLumaMinus8(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_17, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.avcDecoderConfigurationRecord.bitDepthLumaMinus8 = i;
//    }
//
//    public int getBitDepthChromaMinus8() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_18, this, this));
//        return this.avcDecoderConfigurationRecord.bitDepthChromaMinus8;
//    }
//
//    public void setBitDepthChromaMinus8(int i) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_19, (Object) this, (Object) this, Conversions.intObject(i)));
//        this.avcDecoderConfigurationRecord.bitDepthChromaMinus8 = i;
//    }
//
//    public List<byte[]> getSequenceParameterSetExts() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_20, this, this));
//        return this.avcDecoderConfigurationRecord.sequenceParameterSetExts;
//    }
//
//    public void setSequenceParameterSetExts(List<byte[]> list) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_21, (Object) this, (Object) this, (Object) list));
//        this.avcDecoderConfigurationRecord.sequenceParameterSetExts = list;
//    }
//
//    public boolean hasExts() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_22, this, this));
//        return this.avcDecoderConfigurationRecord.hasExts;
//    }
//
//    public void setHasExts(boolean z) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_23, (Object) this, (Object) this, Conversions.booleanObject(z)));
//        this.avcDecoderConfigurationRecord.hasExts = z;
//    }
//
//    public void _parseDetails(ByteBuffer byteBuffer) {
//        this.avcDecoderConfigurationRecord = new AvcDecoderConfigurationRecord(byteBuffer);
//    }
//
//    public long getContentSize() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_24, this, this));
//        return this.avcDecoderConfigurationRecord.getContentSize();
//    }
//
//    public void getContent(ByteBuffer byteBuffer) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_25, (Object) this, (Object) this, (Object) byteBuffer));
//        this.avcDecoderConfigurationRecord.getContent(byteBuffer);
//    }
//
//    public String[] getSPS() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_26, this, this));
//        return this.avcDecoderConfigurationRecord.getSPS();
//    }
//
//    public String[] getPPS() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_27, this, this));
//        return this.avcDecoderConfigurationRecord.getPPS();
//    }
//
//    public AvcDecoderConfigurationRecord getavcDecoderConfigurationRecord() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_28, this, this));
//        return this.avcDecoderConfigurationRecord;
//    }
//
//    public String toString() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_29, this, this));
//        return "AvcConfigurationBox{avcDecoderConfigurationRecord=" + this.avcDecoderConfigurationRecord + '}';
//    }
//}
