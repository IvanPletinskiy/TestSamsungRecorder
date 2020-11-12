package com.mp4parser.iso23009.part1;
//
//import com.coremedia.iso.IsoTypeReader;
//import com.coremedia.iso.IsoTypeWriter;
//import com.coremedia.iso.Utf8;
//import com.googlecode.mp4parser.AbstractFullBox;
//import com.googlecode.mp4parser.RequiresParseDetailAspect;
//import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
//import java.nio.ByteBuffer;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.runtime.internal.Conversions;
//import org.aspectj.runtime.reflect.Factory;
//
//public class EventMessageBox extends AbstractFullBox {
//    public static final String TYPE = "emsg";
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_10 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_11 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_12 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_13 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_6 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_7 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_8 = null;
//    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_9 = null;
//    long eventDuration;
//
//    /* renamed from: id */
//    long f81id;
//    byte[] messageData;
//    long presentationTimeDelta;
//    String schemeIdUri;
//    long timescale;
//    String value;
//
//    static {
//        ajc$preClinit();
//    }
//
//    private static /* synthetic */ void ajc$preClinit() {
//        Factory factory = new Factory("EventMessageBox.java", EventMessageBox.class);
//        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSchemeIdUri", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "java.lang.String"), 59);
//        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSchemeIdUri", "com.mp4parser.iso23009.part1.EventMessageBox", "java.lang.String", "schemeIdUri", "", "void"), 63);
//        ajc$tjp_10 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getId", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "long"), 99);
//        ajc$tjp_11 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setId", "com.mp4parser.iso23009.part1.EventMessageBox", "long", DialogFactory.BUNDLE_ID, "", "void"), 103);
//        ajc$tjp_12 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getMessageData", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "[B"), 107);
//        ajc$tjp_13 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setMessageData", "com.mp4parser.iso23009.part1.EventMessageBox", "[B", "messageData", "", "void"), 111);
//        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getValue", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "java.lang.String"), 67);
//        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setValue", "com.mp4parser.iso23009.part1.EventMessageBox", "java.lang.String", "value", "", "void"), 71);
//        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getTimescale", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "long"), 75);
//        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setTimescale", "com.mp4parser.iso23009.part1.EventMessageBox", "long", "timescale", "", "void"), 79);
//        ajc$tjp_6 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getPresentationTimeDelta", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "long"), 83);
//        ajc$tjp_7 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setPresentationTimeDelta", "com.mp4parser.iso23009.part1.EventMessageBox", "long", "presentationTimeDelta", "", "void"), 87);
//        ajc$tjp_8 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getEventDuration", "com.mp4parser.iso23009.part1.EventMessageBox", "", "", "", "long"), 91);
//        ajc$tjp_9 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setEventDuration", "com.mp4parser.iso23009.part1.EventMessageBox", "long", "eventDuration", "", "void"), 95);
//    }
//
//    public EventMessageBox() {
//        super(TYPE);
//    }
//
//    /* access modifiers changed from: protected */
//    public void _parseDetails(ByteBuffer byteBuffer) {
//        parseVersionAndFlags(byteBuffer);
//        this.schemeIdUri = IsoTypeReader.readString(byteBuffer);
//        this.value = IsoTypeReader.readString(byteBuffer);
//        this.timescale = IsoTypeReader.readUInt32(byteBuffer);
//        this.presentationTimeDelta = IsoTypeReader.readUInt32(byteBuffer);
//        this.eventDuration = IsoTypeReader.readUInt32(byteBuffer);
//        this.f81id = IsoTypeReader.readUInt32(byteBuffer);
//        this.messageData = new byte[byteBuffer.remaining()];
//        byteBuffer.get(this.messageData);
//    }
//
//    /* access modifiers changed from: protected */
//    public void getContent(ByteBuffer byteBuffer) {
//        writeVersionAndFlags(byteBuffer);
//        IsoTypeWriter.writeUtf8String(byteBuffer, this.schemeIdUri);
//        IsoTypeWriter.writeUtf8String(byteBuffer, this.value);
//        IsoTypeWriter.writeUInt32(byteBuffer, this.timescale);
//        IsoTypeWriter.writeUInt32(byteBuffer, this.presentationTimeDelta);
//        IsoTypeWriter.writeUInt32(byteBuffer, this.eventDuration);
//        IsoTypeWriter.writeUInt32(byteBuffer, this.f81id);
//        byteBuffer.put(this.messageData);
//    }
//
//    /* access modifiers changed from: protected */
//    public long getContentSize() {
//        return (long) (Utf8.utf8StringLengthInBytes(this.schemeIdUri) + 22 + Utf8.utf8StringLengthInBytes(this.value) + this.messageData.length);
//    }
//
//    public String getSchemeIdUri() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
//        return this.schemeIdUri;
//    }
//
//    public void setSchemeIdUri(String str) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) str));
//        this.schemeIdUri = str;
//    }
//
//    public String getValue() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
//        return this.value;
//    }
//
//    public void setValue(String str) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) str));
//        this.value = str;
//    }
//
//    public long getTimescale() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
//        return this.timescale;
//    }
//
//    public void setTimescale(long j) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, (Object) this, (Object) this, Conversions.longObject(j)));
//        this.timescale = j;
//    }
//
//    public long getPresentationTimeDelta() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
//        return this.presentationTimeDelta;
//    }
//
//    public void setPresentationTimeDelta(long j) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, (Object) this, (Object) this, Conversions.longObject(j)));
//        this.presentationTimeDelta = j;
//    }
//
//    public long getEventDuration() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this));
//        return this.eventDuration;
//    }
//
//    public void setEventDuration(long j) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, (Object) this, (Object) this, Conversions.longObject(j)));
//        this.eventDuration = j;
//    }
//
//    public long getId() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, this, this));
//        return this.f81id;
//    }
//
//    public void setId(long j) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, (Object) this, (Object) this, Conversions.longObject(j)));
//        this.f81id = j;
//    }
//
//    public byte[] getMessageData() {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, this, this));
//        return this.messageData;
//    }
//
//    public void setMessageData(byte[] bArr) {
//        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, (Object) this, (Object) this, (Object) bArr));
//        this.messageData = bArr;
//    }
//}
