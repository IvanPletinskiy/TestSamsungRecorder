package com.mp4parser.iso14496.part30;
//
//import com.coremedia.iso.Utf8;
//import com.coremedia.iso.boxes.SampleDescriptionBox;
//import com.googlecode.mp4parser.DataSource;
//import com.googlecode.mp4parser.authoring.AbstractTrack;
//import com.googlecode.mp4parser.authoring.Sample;
//import com.googlecode.mp4parser.authoring.TrackMetaData;
//import com.googlecode.mp4parser.util.CastUtils;
//
//import org.mp4parser.boxes.iso14496.part30.WebVTTConfigurationBox;
//import org.mp4parser.boxes.iso14496.part30.WebVTTSampleEntry;
//import org.mp4parser.boxes.iso14496.part30.WebVTTSourceLabelBox;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.ArrayList;
//import java.util.List;
//
//public class WebVTTTrack extends AbstractTrack {
//    WebVTTSampleEntry sampleEntry = new WebVTTSampleEntry();
//    List<Sample> samples = new ArrayList();
//    String[] subs;
//
//    public void close() throws IOException {
//    }
//
//    public String getHandler() {
//        return null;
//    }
//
//    public SampleDescriptionBox getSampleDescriptionBox() {
//        return null;
//    }
//
//    public List<Sample> getSamples() {
//        return null;
//    }
//
//    public TrackMetaData getTrackMetaData() {
//        return null;
//    }
//
//    public WebVTTTrack(DataSource dataSource) throws IOException {
//        super(dataSource.toString());
//        this.sampleEntry.addBox(new WebVTTConfigurationBox());
//        this.sampleEntry.addBox(new WebVTTSourceLabelBox());
//        ByteBuffer map = dataSource.map(0, (long) CastUtils.l2i(dataSource.size()));
//        byte[] bArr = new byte[CastUtils.l2i(dataSource.size())];
//        map.get(bArr);
//        this.subs = Utf8.convert(bArr).split("\\r?\\n");
//        String str = "";
//        int i = 0;
//        while (i < this.subs.length) {
//            str = String.valueOf(str) + this.subs[i] + "\n";
//            int i2 = i + 1;
//            if (this.subs[i2].isEmpty() && this.subs[i + 2].isEmpty()) {
//                break;
//            }
//            i = i2;
//        }
//        while (true) {
//            String[] strArr = this.subs;
//            if (i < strArr.length && strArr[i].isEmpty()) {
//                i++;
//            } else {
//                return;
//            }
//        }
//    }
//
//    public long[] getSampleDurations() {
//        return new long[0];
//    }
//}
