package com.sec.android.app.voicenote.p007ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateFormat;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.ContextMenuProvider;
import com.sec.android.app.voicenote.provider.StorageProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import java.text.DecimalFormat;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.DetailsDialog */
public class DetailsDialog extends AbsDialogFragment {
    private static final String TAG = "DetailsDialog";
    private AlertDialog mDialog;

    public static DetailsDialog newInstance(Bundle bundle) {
        DetailsDialog detailsDialog = new DetailsDialog();
        detailsDialog.setArguments(bundle);
        return detailsDialog;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00ec, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ee, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00ef, code lost:
        r22 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00f1, code lost:
        r23 = r13;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00f6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00f7, code lost:
        r23 = r13;
        r3 = null;
        r13 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00fc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00fe, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00ff, code lost:
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0124, code lost:
        r15.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0129, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01c9, code lost:
        r15.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x01ce, code lost:
        r3.release();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00fe A[ExcHandler: all (th java.lang.Throwable), Splitter:B:7:0x00a4] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0124  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x012f  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0134  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0139  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01ad  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01b6  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01c9  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01ce  */
    @androidx.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.app.Dialog onCreateDialog(android.os.Bundle r27) {
        /*
            r26 = this;
            r1 = r26
            java.lang.String r2 = "DetailsDialog"
            java.lang.String r0 = "onCreateDialog"
            com.sec.android.app.voicenote.provider.Log.m26i(r2, r0)
            androidx.fragment.app.FragmentActivity r3 = r26.getActivity()
            android.view.LayoutInflater r0 = r3.getLayoutInflater()
            r4 = 0
            r5 = 2131492911(0x7f0c002f, float:1.8609287E38)
            android.view.View r0 = r0.inflate(r5, r4)
            r5 = 2131296452(0x7f0900c4, float:1.8210821E38)
            android.view.View r5 = r0.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r6 = 2131296450(0x7f0900c2, float:1.8210817E38)
            android.view.View r6 = r0.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r7 = 2131296453(0x7f0900c5, float:1.8210823E38)
            android.view.View r7 = r0.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r8 = 2131296451(0x7f0900c3, float:1.821082E38)
            android.view.View r8 = r0.findViewById(r8)
            android.widget.TextView r8 = (android.widget.TextView) r8
            r9 = 2131296455(0x7f0900c7, float:1.8210827E38)
            android.view.View r9 = r0.findViewById(r9)
            android.widget.TextView r9 = (android.widget.TextView) r9
            r10 = 2131296449(0x7f0900c1, float:1.8210815E38)
            android.view.View r10 = r0.findViewById(r10)
            android.widget.TextView r10 = (android.widget.TextView) r10
            r11 = 2131296448(0x7f0900c0, float:1.8210813E38)
            android.view.View r11 = r0.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r12 = 2131296454(0x7f0900c6, float:1.8210825E38)
            android.view.View r12 = r0.findViewById(r12)
            android.widget.TextView r12 = (android.widget.TextView) r12
            android.app.AlertDialog$Builder r13 = new android.app.AlertDialog$Builder
            r13.<init>(r3)
            r14 = 2131755108(0x7f100064, float:1.9141086E38)
            r13.setTitle(r14)
            r13.setView(r0)
            android.os.Bundle r0 = r26.getArguments()
            java.lang.String r14 = "id"
            r15 = r5
            r4 = -1
            long r4 = r0.getLong(r14, r4)
            r16 = 0
            int r0 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r0 < 0) goto L_0x01d2
            com.sec.android.app.voicenote.provider.DBProvider r0 = com.sec.android.app.voicenote.provider.DBProvider.getInstance()
            java.lang.String r0 = r0.getPathById(r4)
            boolean r4 = com.sec.android.app.voicenote.provider.StorageProvider.isExistFile((java.lang.String) r0)
            if (r4 == 0) goto L_0x01d2
            java.io.File r4 = new java.io.File
            r4.<init>(r0)
            java.lang.String r5 = r4.getName()
            java.lang.String r14 = r4.getParent()
            r18 = r15
            android.media.MediaMetadataRetriever r15 = new android.media.MediaMetadataRetriever     // Catch:{ IOException -> 0x010f, all -> 0x010a }
            r15.<init>()     // Catch:{ IOException -> 0x010f, all -> 0x010a }
            r15.setDataSource(r0)     // Catch:{ IOException -> 0x0102, all -> 0x00fe }
            r20 = r3
            r3 = 20
            java.lang.String r3 = r15.extractMetadata(r3)     // Catch:{ IOException -> 0x00fc, all -> 0x00fe }
            r21 = r3
            r3 = 9
            java.lang.String r3 = r15.extractMetadata(r3)     // Catch:{ IOException -> 0x00f6, all -> 0x00fe }
            r15.release()     // Catch:{ IOException -> 0x00ee, all -> 0x00fe }
            r22 = r3
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ IOException -> 0x00ec, all -> 0x00fe }
            r3.<init>()     // Catch:{ IOException -> 0x00ec, all -> 0x00fe }
            r3.setDataSource(r0)     // Catch:{ IOException -> 0x00e8 }
            r23 = r13
            r13 = 0
            android.media.MediaFormat r0 = r3.getTrackFormat(r13)     // Catch:{ IOException -> 0x00e6 }
            java.lang.String r13 = "sample-rate"
            int r13 = r0.getInteger(r13)     // Catch:{ IOException -> 0x00e6 }
            r27 = r13
            java.lang.String r13 = "channel-count"
            int r0 = r0.getInteger(r13)     // Catch:{ IOException -> 0x00e2 }
            r15.release()
            r3.release()
            r13 = r27
            goto L_0x012d
        L_0x00e2:
            r0 = move-exception
            r13 = r27
            goto L_0x011b
        L_0x00e6:
            r0 = move-exception
            goto L_0x00f4
        L_0x00e8:
            r0 = move-exception
            r23 = r13
            goto L_0x00f4
        L_0x00ec:
            r0 = move-exception
            goto L_0x00f1
        L_0x00ee:
            r0 = move-exception
            r22 = r3
        L_0x00f1:
            r23 = r13
            r3 = 0
        L_0x00f4:
            r13 = 0
            goto L_0x011b
        L_0x00f6:
            r0 = move-exception
            r23 = r13
            r3 = 0
            r13 = 0
            goto L_0x0119
        L_0x00fc:
            r0 = move-exception
            goto L_0x0105
        L_0x00fe:
            r0 = move-exception
            r3 = 0
            goto L_0x01c7
        L_0x0102:
            r0 = move-exception
            r20 = r3
        L_0x0105:
            r23 = r13
            r3 = 0
            r13 = 0
            goto L_0x0117
        L_0x010a:
            r0 = move-exception
            r3 = 0
            r15 = 0
            goto L_0x01c7
        L_0x010f:
            r0 = move-exception
            r20 = r3
            r23 = r13
            r3 = 0
            r13 = 0
            r15 = 0
        L_0x0117:
            r21 = 0
        L_0x0119:
            r22 = 0
        L_0x011b:
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01c6 }
            com.sec.android.app.voicenote.provider.Log.m22e(r2, r0)     // Catch:{ all -> 0x01c6 }
            if (r15 == 0) goto L_0x0127
            r15.release()
        L_0x0127:
            if (r3 == 0) goto L_0x012c
            r3.release()
        L_0x012c:
            r0 = 0
        L_0x012d:
            if (r21 == 0) goto L_0x0134
            int r15 = java.lang.Integer.parseInt(r21)
            goto L_0x0135
        L_0x0134:
            r15 = 0
        L_0x0135:
            r2 = 1000(0x3e8, float:1.401E-42)
            if (r15 <= r2) goto L_0x0142
            int r15 = r15 / r2
            java.lang.String r3 = "kbps"
            r2 = 0
            java.lang.String r3 = r1.stringFormatWithRTL(r3, r15, r2)
            goto L_0x0149
        L_0x0142:
            r2 = 0
            java.lang.String r3 = "bps"
            java.lang.String r3 = r1.stringFormatWithRTL(r3, r15, r2)
        L_0x0149:
            r2 = 1000(0x3e8, float:1.401E-42)
            if (r13 <= r2) goto L_0x0161
            int r15 = r13 % 1000
            int r15 = r15 / 100
            int r13 = r13 / r2
            java.lang.String r2 = "kHz"
            if (r15 <= 0) goto L_0x015b
            java.lang.String r2 = r1.stringFormatWithRTL(r2, r13, r15)
            goto L_0x0168
        L_0x015b:
            r15 = 0
            java.lang.String r2 = r1.stringFormatWithRTL(r2, r13, r15)
            goto L_0x0168
        L_0x0161:
            r15 = 0
            java.lang.String r2 = "Hz"
            java.lang.String r2 = r1.stringFormatWithRTL(r2, r13, r15)
        L_0x0168:
            r27 = r12
            long r12 = r4.lastModified()
            r19 = r2
            r15 = r3
            long r2 = r4.length()
            if (r22 == 0) goto L_0x017b
            long r16 = java.lang.Long.parseLong(r22)
        L_0x017b:
            r4 = r18
            r24 = r16
            r16 = r10
            r17 = r11
            r10 = r24
            r4.setText(r5)
            java.lang.String r4 = r1.getDateFormatByFormatSetting(r12)
            r6.setText(r4)
            java.lang.String r4 = r1.getDisplayFormat(r14)
            r7.setText(r4)
            java.lang.String r4 = r1.getDurationFormat(r10)
            r8.setText(r4)
            java.lang.String r2 = r1.getShortSize(r2)
            r9.setText(r2)
            r3 = r15
            r11 = r17
            r11.setText(r3)
            r2 = 1
            if (r0 <= r2) goto L_0x01b6
            r0 = 2131755428(0x7f1001a4, float:1.9141735E38)
            r10 = r16
            r10.setText(r0)
            goto L_0x01be
        L_0x01b6:
            r10 = r16
            r0 = 2131755426(0x7f1001a2, float:1.914173E38)
            r10.setText(r0)
        L_0x01be:
            r12 = r27
            r2 = r19
            r12.setText(r2)
            goto L_0x01d6
        L_0x01c6:
            r0 = move-exception
        L_0x01c7:
            if (r15 == 0) goto L_0x01cc
            r15.release()
        L_0x01cc:
            if (r3 == 0) goto L_0x01d1
            r3.release()
        L_0x01d1:
            throw r0
        L_0x01d2:
            r20 = r3
            r23 = r13
        L_0x01d6:
            r0 = 2131755377(0x7f100171, float:1.9141632E38)
            com.sec.android.app.voicenote.ui.dialog.-$$Lambda$DetailsDialog$aVhBhDpWYR4jrXicC73qJ-rSuvM r2 = com.sec.android.app.voicenote.p007ui.dialog.$$Lambda$DetailsDialog$aVhBhDpWYR4jrXicC73qJrSuvM.INSTANCE
            r3 = r23
            r3.setPositiveButton(r0, r2)
            android.app.AlertDialog r0 = r3.create()
            r1.mDialog = r0
            android.app.AlertDialog r0 = r1.mDialog
            com.sec.android.app.voicenote.ui.dialog.-$$Lambda$DetailsDialog$03-I74OLFoBYjMjdammT1dkfqCg r2 = new com.sec.android.app.voicenote.ui.dialog.-$$Lambda$DetailsDialog$03-I74OLFoBYjMjdammT1dkfqCg
            r3 = r20
            r2.<init>(r3)
            r0.setOnShowListener(r2)
            android.app.AlertDialog r0 = r1.mDialog
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.dialog.DetailsDialog.onCreateDialog(android.os.Bundle):android.app.Dialog");
    }

    public /* synthetic */ void lambda$onCreateDialog$1$DetailsDialog(@SuppressLint({"InflateParams"}) Activity activity, DialogInterface dialogInterface) {
        if (activity != null) {
            this.mDialog.getButton(-1).setTextColor(activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null));
        }
    }

    private String stringFormatWithRTL(String str, int i, int i2) {
        if (i2 > 0) {
            if (getActivity().getResources().getConfiguration().getLayoutDirection() == 1) {
                Locale locale = Locale.getDefault();
                return String.format(locale, str + "%d.%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
            }
            Locale locale2 = Locale.getDefault();
            return String.format(locale2, "%d.%d" + str, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        } else if (getActivity().getResources().getConfiguration().getLayoutDirection() == 1) {
            Locale locale3 = Locale.getDefault();
            return String.format(locale3, str + "%d", new Object[]{Integer.valueOf(i)});
        } else {
            Locale locale4 = Locale.getDefault();
            return String.format(locale4, "%d" + str, new Object[]{Integer.valueOf(i)});
        }
    }

    private String getDateFormatByFormatSetting(long j) {
        FragmentActivity activity = getActivity();
        StringBuilder sb = new StringBuilder(DateFormat.getLongDateFormat(activity).format(Long.valueOf(j)));
        String language = Locale.getDefault().getLanguage();
        if ("ar".equals(language) || "fa".equals(language) || "ur".equals(language) || "iw".equals(language)) {
            sb.append(" ‏‎");
        } else {
            sb.append(' ');
        }
        sb.append(DateFormat.getTimeFormat(activity).format(Long.valueOf(j)));
        return sb.toString();
    }

    private String getShortSize(long j) {
        String str;
        float f;
        DecimalFormat decimalFormat = new DecimalFormat("0.#");
        float f2 = (float) j;
        double d = (double) f2;
        if (d < 838860.8d) {
            f = f2 / 1024.0f;
            str = getResources().getString(C0690R.string.f90kb);
        } else if (d < 8.589934592E8d) {
            f = (f2 / 1024.0f) / 1024.0f;
            str = getResources().getString(C0690R.string.f91mb);
        } else {
            f = ((f2 / 1024.0f) / 1024.0f) / 1024.0f;
            str = getResources().getString(C0690R.string.f89gb);
        }
        return decimalFormat.format((double) f) + ' ' + str;
    }

    private String getDurationFormat(long j) {
        long j2 = j / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf((int) (j2 / 3600)), Integer.valueOf((int) ((j2 / 60) % 60)), Integer.valueOf((int) (j2 % 60))});
    }

    private String getDisplayFormat(String str) {
        String rootPath = StorageProvider.getRootPath(0);
        String rootPath2 = StorageProvider.getRootPath(1);
        if (str.matches(".*" + rootPath + ".*")) {
            return str.replace(rootPath, getResources().getString(C0690R.string.internal_storage_detail_Path));
        }
        StringBuilder sb = new StringBuilder();
        sb.append(".*");
        sb.append(rootPath2);
        sb.append(".*");
        return str.matches(sb.toString()) ? str.replace(rootPath2, getResources().getString(C0690R.string.external_detail_path)) : str;
    }

    public void onResume() {
        int scene = VoiceNoteApplication.getScene();
        if (!(scene == 5 || scene == 10 || scene == 9 || Engine.getInstance().getID() != -1 || ContextMenuProvider.getInstance().getId() != -1)) {
            dismiss();
        }
        super.onResume();
    }
}
