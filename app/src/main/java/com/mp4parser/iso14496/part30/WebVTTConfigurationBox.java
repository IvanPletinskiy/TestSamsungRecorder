package com.mp4parser.iso14496.part30;
//
//import com.coremedia.iso.IsoTypeReader;
//import com.coremedia.iso.Utf8;
//import com.googlecode.mp4parser.AbstractBox;
//import com.googlecode.mp4parser.RequiresParseDetailAspect;
//import java.nio.ByteBuffer;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.runtime.reflect.Factory;
//
//public class WebVTTConfigurationBox extends AbstractBox {
//    public static final String TYPE = "vttC";
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
//    String config;
//
//    static {
//        ajc$preClinit();
//    }
//
//    private static /* synthetic */ void ajc$preClinit() {
//        Factory factory = new Factory("WebVTTConfigurationBox.java", WebVTTConfigurationBox.class);
//        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getConfig", "com.mp4parser.iso14496.part30.WebVTTConfigurationBox", "", "", "", "java.lang.String"), 36);
//        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setConfig", "com.mp4parser.iso14496.part30.WebVTTConfigurationBox", "java.lang.String", "config", "", "void"), 40);
//    }
//
//    public WebVTTConfigurationBox() {
//        super(TYPE);
//    }
//
//    /* access modifiers changed from: protected */
//    public long getContentSize() {
//        return (long) Utf8.utf8StringLengthInBytes(this.config);
//    }
//
//    /* access modifiers changed from: protected */
//    public void getContent(ByteBuffer byteBuffer) {
//        byteBuffer.put(Utf8.convert(this.config));
//    }
//
//    /* access modifiers changed from: protected */
//    public void _parseDetails(ByteBuffer byteBuffer) {
//        this.config = IsoTypeReader.readString(byteBuffer, byteBuffer.remaining());
//    }
//
//    public String getConfig() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
//        return this.config;
//    }
//
//    public void setConfig(String str) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) str));
//        this.config = str;
//    }
//}
