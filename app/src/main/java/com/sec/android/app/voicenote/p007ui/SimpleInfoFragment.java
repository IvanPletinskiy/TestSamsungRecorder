package com.sec.android.app.voicenote.p007ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.DesktopModeProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.service.AudioFormat;
import com.sec.android.app.voicenote.service.SimpleEngine;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.SimpleInfoFragment */
public class SimpleInfoFragment extends AbsSimpleFragment implements SimpleEngine.OnSimpleEngineListener {
    private static final String TAG = "SimpleInfoFragment";
    private static final ForegroundColorSpan mTimeTextDimSpan = new ForegroundColorSpan(Color.rgb(112, 112, 112));
    private Handler mEventHandler = null;
    private TextView mMaxDuration = null;
    private LinearLayout mMaxLayout = null;
    private String mMaxLengthText = null;
    private TextView mMaxTextView = null;
    private int mOldTextTimeLength = -1;
    private int mRecordMode;
    private TextView mTimeHmsTextView = null;
    private LinearLayout mTimeLayout = null;
    private TextView mTimeMsTextView = null;

    public void onCreate(Bundle bundle) {
        Log.m26i(TAG, "onCreate");
        super.onCreate(bundle);
        this.mEventHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return SimpleInfoFragment.this.lambda$onCreate$0$SimpleInfoFragment(message);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00d8, code lost:
        if (com.sec.android.app.voicenote.provider.DesktopModeProvider.isDesktopMode() != false) goto L_0x00da;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ boolean lambda$onCreate$0$SimpleInfoFragment(android.os.Message r7) {
        /*
            r6 = this;
            androidx.fragment.app.FragmentActivity r0 = r6.getActivity()
            r1 = 0
            if (r0 == 0) goto L_0x0116
            boolean r0 = r6.isAdded()
            if (r0 == 0) goto L_0x0116
            boolean r0 = r6.isRemoving()
            if (r0 != 0) goto L_0x0116
            boolean r0 = r6.isResumed()
            if (r0 != 0) goto L_0x0023
            com.sec.android.app.voicenote.service.SimpleEngine r0 = r6.mSimpleEngine
            boolean r0 = r0.getScreenOff()
            if (r0 == 0) goto L_0x0023
            goto L_0x0116
        L_0x0023:
            int r0 = r7.what
            r2 = 101(0x65, float:1.42E-43)
            if (r0 == r2) goto L_0x0111
            r2 = 2010(0x7da, float:2.817E-42)
            r3 = 3
            r4 = 4
            java.lang.String r5 = "SimpleInfoFragment"
            if (r0 == r2) goto L_0x00e0
            r2 = 2012(0x7dc, float:2.82E-42)
            if (r0 == r2) goto L_0x00cb
            r2 = 3010(0xbc2, float:4.218E-42)
            if (r0 == r2) goto L_0x00ac
            r2 = 1010(0x3f2, float:1.415E-42)
            if (r0 == r2) goto L_0x006b
            r2 = 1011(0x3f3, float:1.417E-42)
            if (r0 == r2) goto L_0x00cb
            r7 = 1022(0x3fe, float:1.432E-42)
            if (r0 == r7) goto L_0x005b
            r7 = 1023(0x3ff, float:1.434E-42)
            if (r0 == r7) goto L_0x004b
            goto L_0x0116
        L_0x004b:
            androidx.fragment.app.FragmentActivity r7 = r6.getActivity()
            r0 = 2131755373(0x7f10016d, float:1.9141623E38)
            android.widget.Toast r7 = android.widget.Toast.makeText(r7, r0, r1)
            r7.show()
            goto L_0x0116
        L_0x005b:
            androidx.fragment.app.FragmentActivity r7 = r6.getActivity()
            androidx.fragment.app.FragmentManager r7 = r7.getSupportFragmentManager()
            r0 = 0
            java.lang.String r2 = "StorageFullDialog"
            com.sec.android.app.voicenote.p007ui.dialog.DialogFactory.show(r7, r2, r0)
            goto L_0x0116
        L_0x006b:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "INFO_RECORDER_STATE - state : "
            r0.append(r2)
            int r2 = r7.arg1
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r5, r0)
            int r7 = r7.arg1
            if (r7 == r3) goto L_0x00a2
            if (r7 == r4) goto L_0x0089
            goto L_0x0116
        L_0x0089:
            com.sec.android.app.voicenote.service.SimpleEngine r7 = r6.mSimpleEngine
            boolean r7 = r7.isSimpleRecorderMode()
            if (r7 == 0) goto L_0x0116
            com.sec.android.app.voicenote.service.SimpleEngine r7 = r6.mSimpleEngine
            long r2 = r7.getSimpleModeItem()
            r4 = -1
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x0116
            r6.showDuration()
            goto L_0x0116
        L_0x00a2:
            com.sec.android.app.voicenote.service.SimpleEngine r7 = r6.mSimpleEngine
            int r7 = r7.getCurrentTime()
            r6.updateCurrentTime(r7)
            goto L_0x0116
        L_0x00ac:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "INFO_EDITOR_STATE - state : "
            r0.append(r2)
            int r2 = r7.arg1
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r5, r0)
            int r7 = r7.arg1
            if (r7 == r4) goto L_0x00c7
            goto L_0x0116
        L_0x00c7:
            r6.showDuration()
            goto L_0x0116
        L_0x00cb:
            boolean r0 = r6.isInMultiWindow()
            if (r0 != 0) goto L_0x00da
            com.sec.android.app.voicenote.provider.DesktopModeProvider.getInstance()
            boolean r0 = com.sec.android.app.voicenote.provider.DesktopModeProvider.isDesktopMode()
            if (r0 == 0) goto L_0x0116
        L_0x00da:
            int r7 = r7.arg1
            r6.updateCurrentTime(r7)
            goto L_0x0116
        L_0x00e0:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "INFO_PLAYER_STATE - state : "
            r0.append(r2)
            int r2 = r7.arg1
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            com.sec.android.app.voicenote.provider.Log.m26i(r5, r0)
            int r7 = r7.arg1
            r0 = 2
            if (r7 == r0) goto L_0x010d
            if (r7 == r3) goto L_0x0100
            if (r7 == r4) goto L_0x0100
            goto L_0x0116
        L_0x0100:
            com.sec.android.app.voicenote.service.SimpleEngine r7 = r6.mSimpleEngine
            int r7 = r7.getCurrentTime()
            r6.updateCurrentTime(r7)
            r6.showDuration()
            goto L_0x0116
        L_0x010d:
            r6.showDuration()
            goto L_0x0116
        L_0x0111:
            int r7 = r7.arg1
            r6.updateCurrentTime(r7)
        L_0x0116:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.SimpleInfoFragment.lambda$onCreate$0$SimpleInfoFragment(android.os.Message):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x00f3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View onCreateView(android.view.LayoutInflater r6, android.view.ViewGroup r7, android.os.Bundle r8) {
        /*
            r5 = this;
            java.lang.String r8 = "SimpleInfoFragment"
            java.lang.String r0 = "onCreateView"
            com.sec.android.app.voicenote.provider.Log.m26i(r8, r0)
            boolean r8 = r5.isInMultiWindow()
            r0 = 0
            if (r8 != 0) goto L_0x0020
            com.sec.android.app.voicenote.provider.DesktopModeProvider.getInstance()
            boolean r8 = com.sec.android.app.voicenote.provider.DesktopModeProvider.isDesktopMode()
            if (r8 == 0) goto L_0x0018
            goto L_0x0020
        L_0x0018:
            r8 = 2131492934(0x7f0c0046, float:1.8609334E38)
            android.view.View r6 = r6.inflate(r8, r7, r0)
            goto L_0x0027
        L_0x0020:
            r8 = 2131492963(0x7f0c0063, float:1.8609393E38)
            android.view.View r6 = r6.inflate(r8, r7, r0)
        L_0x0027:
            r7 = 0
            r6.setOnClickListener(r7)
            r7 = -1
            r5.mOldTextTimeLength = r7
            r7 = 2131296530(0x7f090112, float:1.821098E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.LinearLayout r7 = (android.widget.LinearLayout) r7
            r5.mTimeLayout = r7
            r7 = 2131296529(0x7f090111, float:1.8210977E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r5.mTimeHmsTextView = r7
            r7 = 2131296532(0x7f090114, float:1.8210983E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r5.mTimeMsTextView = r7
            com.sec.android.app.voicenote.service.SimpleEngine r7 = r5.mSimpleEngine
            int r7 = r7.getCurrentTime()
            com.sec.android.app.voicenote.service.SimpleMetadataRepository r8 = r5.mSimpleMetadata
            int r8 = r8.getRecordMode()
            r5.mRecordMode = r8
            r8 = 1
            java.lang.String r8 = r5.getStringByDuration(r7, r8)
            r5.setTextTimeView(r8, r7)
            android.widget.LinearLayout r7 = r5.mTimeLayout
            com.sec.android.app.voicenote.provider.AssistantProvider r1 = com.sec.android.app.voicenote.provider.AssistantProvider.getInstance()
            java.lang.String r8 = r1.stringForReadTime(r8)
            r7.setContentDescription(r8)
            r7 = 2131296526(0x7f09010e, float:1.8210971E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.LinearLayout r7 = (android.widget.LinearLayout) r7
            r5.mMaxLayout = r7
            r7 = 2131296527(0x7f09010f, float:1.8210973E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r5.mMaxTextView = r7
            r7 = 2131296525(0x7f09010d, float:1.821097E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            r5.mMaxDuration = r7
            android.widget.TextView r7 = r5.mMaxDuration
            r8 = 8
            r7.setVisibility(r8)
            int r7 = r5.mRecordMode
            r1 = 4
            if (r7 == r1) goto L_0x00a5
            r1 = 5
            if (r7 == r1) goto L_0x00a5
            r1 = 6
            if (r7 != r1) goto L_0x00fd
        L_0x00a5:
            int r7 = r5.getMaxDuration()
            java.lang.String r7 = r5.getStringByDuration(r7, r0)
            android.widget.TextView r1 = r5.mMaxDuration
            r1.setText(r7)
            android.widget.LinearLayout r1 = r5.mMaxLayout
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            androidx.fragment.app.FragmentActivity r3 = r5.getActivity()
            r4 = 2131755320(0x7f100138, float:1.9141516E38)
            java.lang.String r3 = r3.getString(r4)
            r2.append(r3)
            r3 = 32
            r2.append(r3)
            com.sec.android.app.voicenote.provider.AssistantProvider r3 = com.sec.android.app.voicenote.provider.AssistantProvider.getInstance()
            java.lang.String r7 = r3.stringForReadTime(r7)
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            r1.setContentDescription(r7)
            androidx.fragment.app.FragmentActivity r7 = r5.getActivity()
            boolean r7 = com.sec.android.app.voicenote.provider.HWKeyboardProvider.isHWKeyboard(r7)
            if (r7 == 0) goto L_0x00f3
            android.widget.TextView r7 = r5.mMaxTextView
            r7.setVisibility(r8)
            android.widget.TextView r7 = r5.mMaxDuration
            r7.setVisibility(r8)
            goto L_0x00fd
        L_0x00f3:
            android.widget.TextView r7 = r5.mMaxTextView
            r7.setVisibility(r0)
            android.widget.TextView r7 = r5.mMaxDuration
            r7.setVisibility(r0)
        L_0x00fd:
            boolean r7 = r5.isInMultiWindow()
            if (r7 == 0) goto L_0x010e
            com.sec.android.app.voicenote.activity.SimpleActivity$LaunchMode r7 = r5.mLaunchMode
            com.sec.android.app.voicenote.activity.SimpleActivity$LaunchMode r0 = com.sec.android.app.voicenote.activity.SimpleActivity.LaunchMode.SPEECHTOTEXT
            if (r7 != r0) goto L_0x010e
            android.widget.LinearLayout r7 = r5.mMaxLayout
            r7.setVisibility(r8)
        L_0x010e:
            boolean r7 = r5.isInMultiWindow()
            if (r7 != 0) goto L_0x0126
            boolean r7 = com.sec.android.app.voicenote.provider.DesktopModeProvider.isDesktopMode()
            if (r7 != 0) goto L_0x0126
            r7 = 2131296533(0x7f090115, float:1.8210985E38)
            android.view.View r7 = r6.findViewById(r7)
            android.widget.ImageView r7 = (android.widget.ImageView) r7
            r7.setVisibility(r8)
        L_0x0126:
            int r7 = r5.mStartingEvent
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r5.onUpdate(r7)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.SimpleInfoFragment.onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle):android.view.View");
    }

    public void onViewCreated(View view, Bundle bundle) {
        Log.m26i(TAG, "onViewCreated");
        super.onViewCreated(view, bundle);
        this.mSimpleEngine.registerListener(this);
    }

    public void onDestroyView() {
        Log.m26i(TAG, "onDestroyView");
        this.mSimpleEngine.unregisterListener(this);
        super.onDestroyView();
    }

    public void onDestroy() {
        Log.m26i(TAG, "onDestroy");
        this.mEventHandler = null;
        super.onDestroy();
    }

    public void onUpdate(Object obj) {
        Log.m19d(TAG, "onUpdate : " + obj);
        int intValue = ((Integer) obj).intValue();
        if (intValue == 4) {
            updateCurrentTime(0);
        } else if (intValue != 975) {
            if (intValue != 5001) {
                if (intValue != 50003) {
                    if (intValue != 1001) {
                        if (intValue == 1002) {
                            updateCurrentTime(this.mSimpleEngine.getCurrentTime());
                            return;
                        } else if (intValue == 1006) {
                            this.mMaxDuration.setVisibility(8);
                            return;
                        } else if (!(intValue == 1007 || intValue == 2005 || intValue == 2006)) {
                            if (intValue != 5004) {
                                if (intValue != 5005) {
                                    switch (intValue) {
                                        case Event.PLAY_START:
                                        case Event.PLAY_PAUSE:
                                        case Event.PLAY_RESUME:
                                            break;
                                        default:
                                            return;
                                    }
                                } else {
                                    showDuration();
                                    return;
                                }
                            }
                        }
                    }
                    showMaxTime(this.mRecordMode);
                    return;
                }
                updateCurrentTime(0);
                return;
            }
            showDuration();
            updateCurrentTime(this.mSimpleEngine.getCurrentTime());
        } else {
            if (this.mSimpleEngine.getPlayerState() == 4) {
                Handler handler = this.mEventHandler;
                handler.sendMessage(handler.obtainMessage(2012, this.mSimpleEngine.getCurrentTime(), -1));
            }
            showDuration();
        }
    }

    private void updateCurrentTime(int i) {
        String stringByDuration = getStringByDuration(i, true);
        setTextTimeView(stringByDuration, i);
        this.mTimeLayout.setContentDescription(AssistantProvider.getInstance().stringForReadTime(stringByDuration));
    }

    private void showMaxTime(int i) {
        Log.m26i(TAG, "showMaxTime - recordMode : " + i);
        if (i == 4 || i == 6 || i == 5) {
            int maxDuration = this.mSimpleEngine.getAudioFormat().getMaxDuration();
            this.mMaxTextView.setVisibility(0);
            this.mMaxDuration.setVisibility(0);
            String stringByDuration = getStringByDuration(maxDuration, false);
            this.mMaxDuration.setText(stringByDuration);
            LinearLayout linearLayout = this.mMaxLayout;
            linearLayout.setContentDescription(getActivity().getString(C0690R.string.max) + ' ' + AssistantProvider.getInstance().stringForReadTime(stringByDuration));
            return;
        }
        this.mMaxTextView.setVisibility(8);
        this.mMaxDuration.setVisibility(8);
    }

    private void showDuration() {
        int i;
        Log.m26i(TAG, "showDuration");
        if (this.mSimpleEngine.isSimpleRecorderMode()) {
            i = (int) DBProvider.getInstance().getFileDuration(this.mSimpleEngine.getSimpleModeItem());
        } else {
            i = this.mSimpleEngine.getDuration();
        }
        this.mMaxDuration.setVisibility(0);
        this.mMaxTextView.setVisibility(8);
        String stringByDuration = getStringByDuration(i, true);
        this.mMaxDuration.setText(stringByDuration);
        LinearLayout linearLayout = this.mMaxLayout;
        linearLayout.setContentDescription(getActivity().getString(C0690R.string.max) + ' ' + AssistantProvider.getInstance().stringForReadTime(stringByDuration));
    }

    public void onEngineUpdate(int i, int i2, int i3) {
        Handler handler = this.mEventHandler;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(i, i2, i3));
        }
    }

    private int getMaxDuration() {
        int i = this.mRecordMode;
        if (i == 4) {
            return 600000;
        }
        if (i == 5) {
            return getDurationBySize(getActivity().getIntent().getStringExtra("mime_type"), getActivity().getIntent().getLongExtra("android.provider.MediaStore.extra.MAX_BYTES", 10247680));
        }
        if (i != 6) {
            return 36000999;
        }
        return getMsFromSize(Settings.getMmsMaxSize());
    }

    private int getMsFromSize(long j) {
        return (int) ((((float) ((j / 1024) * 8)) / 12.51f) * 1000.0f);
    }

    public int getDurationBySize(String str, long j) {
        return (int) ((((((double) j) / 1024.0d) * 8.0d) / ((double) (!AudioFormat.MimeType.AMR.equals(str) ? 128.0f : 12.51f))) * 1000.0d);
    }

    private String getStringByDuration(int i, boolean z) {
        int i2 = i / 1000;
        int i3 = (i / 10) - (i2 * 100);
        int i4 = i2 / 3600;
        int i5 = (i2 / 60) % 60;
        int i6 = i2 % 60;
        if (z) {
            if (i4 > 0) {
                return String.format(Locale.getDefault(), "%d:%02d:%02d.%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i3)});
            }
            return String.format(Locale.getDefault(), "%02d:%02d.%02d", new Object[]{Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i3)});
        } else if (i4 > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6)});
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i5), Integer.valueOf(i6)});
        }
    }

    private void setTextTimeView(String str, int i) {
        int i2;
        int i3;
        String[] split = str.split("\\.");
        int i4 = i / 1000;
        int i5 = (i4 / 60) % 60;
        int i6 = (i4 / 3600 != 0 || i5 >= 10) ? 0 : i5 >= 1 ? 1 : (i4 < 10 || i4 > 59) ? (i4 < 1 || i4 > 9) ? 5 : 4 : 3;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.insert(0, split[0]);
        spannableStringBuilder.setSpan(mTimeTextDimSpan, 0, i6, 17);
        if (!isInMultiWindow()) {
            DesktopModeProvider.getInstance();
            if (!DesktopModeProvider.isDesktopMode()) {
                if (this.mOldTextTimeLength != str.length()) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mTimeHmsTextView.getLayoutParams();
                    if (str.length() > 10) {
                        i3 = getMaxWidthTextInfo(this.mTimeHmsTextView.getPaint(), 8);
                    } else if (str.length() > 8) {
                        i3 = getMaxWidthTextInfo(this.mTimeHmsTextView.getPaint(), 7);
                    } else {
                        i3 = getMaxWidthTextInfo(this.mTimeHmsTextView.getPaint(), 5);
                    }
                    layoutParams.width = i3;
                    this.mTimeHmsTextView.setLayoutParams(layoutParams);
                    int maxWidthTextInfo = getMaxWidthTextInfo(this.mTimeMsTextView.getPaint(), 2);
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mTimeMsTextView.getLayoutParams();
                    layoutParams2.width = maxWidthTextInfo;
                    this.mTimeMsTextView.setLayoutParams(layoutParams2);
                    this.mOldTextTimeLength = str.length();
                }
                this.mTimeHmsTextView.setText(spannableStringBuilder);
                this.mTimeMsTextView.setText(split[1]);
            }
        }
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mTimeHmsTextView.getLayoutParams();
        if (str.length() > 10) {
            i2 = getResources().getDimensionPixelOffset(C0690R.dimen.multi_info_recording_time_hhms_width) + (getResources().getDimensionPixelOffset(C0690R.dimen.multi_info_recording_time_ms_width) / 2);
        } else if (str.length() > 8) {
            i2 = getResources().getDimensionPixelOffset(C0690R.dimen.multi_info_recording_time_hhms_width);
        } else {
            i2 = getResources().getDimensionPixelOffset(C0690R.dimen.multi_info_recording_time_hms_width);
        }
        if (i2 != layoutParams3.width) {
            layoutParams3.width = i2;
            this.mTimeHmsTextView.setLayoutParams(layoutParams3);
        }
        this.mTimeHmsTextView.setText(spannableStringBuilder);
        this.mTimeMsTextView.setText(split[1]);
    }

    private boolean isInMultiWindow() {
        return !getActivity().isDestroyed() && getActivity().isInMultiWindowMode();
    }

    private int getMaxWidthTextInfo(Paint paint, int i) {
        if (this.mMaxLengthText == null) {
            int i2 = -1;
            for (int i3 = 0; i3 <= 9; i3++) {
                String format = String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(i3)});
                int measureText = ((int) paint.measureText(format)) + 1;
                if (measureText > i2) {
                    this.mMaxLengthText = format;
                    i2 = measureText;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (i == 2) {
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
        } else if (i == 5) {
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
        } else if (i == 7) {
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
        } else if (i == 8) {
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
            sb.append(":");
            sb.append(this.mMaxLengthText);
            sb.append(this.mMaxLengthText);
        }
        return ((int) paint.measureText(sb.toString())) + i + 1;
    }
}
