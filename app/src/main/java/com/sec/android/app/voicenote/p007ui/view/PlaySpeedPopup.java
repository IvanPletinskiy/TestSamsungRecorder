package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import java.util.Locale;

/* renamed from: com.sec.android.app.voicenote.ui.view.PlaySpeedPopup */
public class PlaySpeedPopup implements IPopupView {
    private static final String TAG = "PlaySpeedPopup";
    private static PlaySpeedPopup sPlaySpeedPopup;
    private int mAnchorMargin;
    private View mAnchorView;
    private int[] mLocalParentPos = new int[2];
    private int mMaxStep;
    private TextView mPlaySpeedText;
    private TextView mPlaySpeedTextDefault;
    private PopupWindow mPopupPlaySpeed;
    /* access modifiers changed from: private */
//    public SeslSeekBar mSeekBar;
    /* access modifiers changed from: private */
    public OnPlaySpeedChangeListener mVolumeChangeListener = null;

    /* renamed from: com.sec.android.app.voicenote.ui.view.PlaySpeedPopup$OnPlaySpeedChangeListener */
    public interface OnPlaySpeedChangeListener {
        void onPlaySpeedChange(float f);
    }

    public static synchronized PlaySpeedPopup getInstance(Context context, View view, int i) {
        PlaySpeedPopup playSpeedPopup;
        synchronized (PlaySpeedPopup.class) {
            if (sPlaySpeedPopup == null) {
                sPlaySpeedPopup = new PlaySpeedPopup(context, view, i);
            }
            playSpeedPopup = sPlaySpeedPopup;
        }
        return playSpeedPopup;
    }

    private PlaySpeedPopup(final Context context, View view, int i) {
        Log.m19d(TAG, "create");
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(C0690R.layout.play_speed_popup, (ViewGroup) null);
        this.mPopupPlaySpeed = new PopupWindow(linearLayout, -1, -2) {
            public void dismiss() {
                PlaySpeedPopup.this.dismiss(false);
                super.dismiss();
            }
        };
        this.mPopupPlaySpeed.setWidth(i - context.getResources().getDimensionPixelOffset(C0690R.dimen.play_speed_popup_side_margin));
        this.mPopupPlaySpeed.setContentView(linearLayout);
        this.mPopupPlaySpeed.setOutsideTouchable(true);
        this.mPopupPlaySpeed.setElevation((float) context.getResources().getDimensionPixelOffset(C0690R.dimen.play_speed_popup_bg_elevation));
        this.mAnchorView = view;
        this.mAnchorMargin = context.getResources().getDimensionPixelSize(C0690R.dimen.play_speed_popup_anchor_margin);
        this.mMaxStep = Math.round(15.0f);
        linearLayout.measure(0, 0);
        this.mPopupPlaySpeed.setFocusable(true);
//        this.mSeekBar = (SeslSeekBar) this.mPopupPlaySpeed.getContentView().findViewById(C0690R.C0693id.play_speed_popup_seekbar);
//        this.mSeekBar.setMax(this.mMaxStep);
//        this.mSeekBar.setThumbTintList(colorToColorStateList(context, C0690R.C0691color.play_speed_popup_seekbar_progress_color));
//        this.mSeekBar.setOnSeekBarChangeListener(new SeslSeekBar.OnSeekBarChangeListener(context) {
//            final /* synthetic */ Context val$context;
//
//            public void onStopTrackingTouch(SeslSeekBar seslSeekBar) {
//            }
//
//            {
//                this.val$context = r2;
//            }
//
//            public void onProgressChanged(SeslSeekBar seslSeekBar, int i, boolean z) {
//                Log.m26i(PlaySpeedPopup.TAG, "onProgressChanged progress : " + i + " fromUser : " + z);
//                float access$000 = PlaySpeedPopup.this.convertSpeed(i);
//                SeslSeekBar access$100 = PlaySpeedPopup.this.mSeekBar;
//                access$100.setContentDescription(this.val$context.getResources().getString(C0690R.string.play_speed_controller) + ", " + String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(access$000)}));
//                PlaySpeedPopup.this.setSpeedText(access$000);
//                if (PlaySpeedPopup.this.mVolumeChangeListener != null) {
//                    PlaySpeedPopup.this.mVolumeChangeListener.onPlaySpeedChange(access$000);
//                }
//            }
//
//            public void onStartTrackingTouch(SeslSeekBar seslSeekBar) {
//                SALogProvider.insertSALog(context.getResources().getString(C0690R.string.screen_player_comm), context.getResources().getString(C0690R.string.event_player_speed_updown));
//            }
//        });
        this.mPlaySpeedText = (TextView) linearLayout.findViewById(C0690R.C0693id.play_speed_text);
        this.mPlaySpeedTextDefault = (TextView) linearLayout.findViewById(C0690R.C0693id.play_speed_text_default);
        this.mPlaySpeedTextDefault.setText(String.format(Locale.getDefault(), "%.1f", new Object[]{Double.valueOf(1.0d)}));
    }

    public void show() {
        PopupWindow popupWindow;
        Log.m19d(TAG, "show");
        if (this.mAnchorView == null || (popupWindow = this.mPopupPlaySpeed) == null) {
            throw new IllegalStateException("Play speed panel internal state error occur");
        }
        final View contentView = popupWindow.getContentView();
        int[] iArr = new int[2];
        this.mAnchorView.getLocationInWindow(iArr);
        int measuredHeight = (iArr[1] - contentView.getMeasuredHeight()) - this.mAnchorMargin;
        this.mPopupPlaySpeed.showAtLocation(this.mAnchorView, 8388659, this.mLocalParentPos[0], measuredHeight);
        contentView.postDelayed(new Runnable() {
            private final /* synthetic */ View f$1;

            {
                this.f$1 = contentView;
            }

            public final void run() {
                PlaySpeedPopup.this.lambda$show$0$PlaySpeedPopup(this.f$1);
            }
        }, 0);
    }

    public /* synthetic */ void lambda$show$0$PlaySpeedPopup(View view) {
//        if (this.mSeekBar != null) {
//            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(C0690R.C0693id.play_speed_popup_bar_layout);
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) relativeLayout.getLayoutParams();
//            int measuredWidth = this.mSeekBar.getMeasuredWidth();
//            if (view.getResources().getConfiguration().getLayoutDirection() == 1) {
//                layoutParams.rightMargin = ((measuredWidth / this.mMaxStep) - 2) * 10;
//            } else {
//                layoutParams.leftMargin = (measuredWidth / this.mMaxStep) * 5;
//            }
//            relativeLayout.setLayoutParams(layoutParams);
//            if (relativeLayout.getVisibility() == 8) {
//                relativeLayout.setVisibility(0);
//            }
//        }
    }

    public void dismiss(boolean z) {
        Log.m19d(TAG, "dismiss");
        PopupWindow popupWindow = this.mPopupPlaySpeed;
        if (popupWindow != null) {
            if (z) {
                popupWindow.dismiss();
            }
            this.mPopupPlaySpeed = null;
        }
//        SeslSeekBar seslSeekBar = this.mSeekBar;
//        if (seslSeekBar != null) {
//            seslSeekBar.setOnSeekBarChangeListener((SeslSeekBar.OnSeekBarChangeListener) null);
//            this.mSeekBar = null;
//        }
        this.mVolumeChangeListener = null;
        sPlaySpeedPopup = null;
    }

    public boolean isShowing() {
        PopupWindow popupWindow = this.mPopupPlaySpeed;
        return popupWindow != null && popupWindow.isShowing();
    }

    public void setValue(float f) {
        int round = Math.round((f - 0.5f) * 10.0f);
        if (round < 0) {
            round = 0;
        } else {
            int i = this.mMaxStep;
            if (round > i) {
                round = i;
            }
        }
        Log.m19d(TAG, "setValue value : " + f + " v : " + round);
//        this.mSeekBar.setProgress(round);
        setSpeedText(f);
    }

    /* access modifiers changed from: private */
    public void setSpeedText(float f) {
        this.mPlaySpeedText.setText(String.format(Locale.getDefault(), "%.1f", new Object[]{Float.valueOf(f)}) + "x");
    }

    public void setOnVolumeChangeListener(OnPlaySpeedChangeListener onPlaySpeedChangeListener) {
        this.mVolumeChangeListener = onPlaySpeedChangeListener;
    }

    /* access modifiers changed from: private */
    public float convertSpeed(int i) {
        float f = (float) i;
        if (f < 0.0f) {
            f = 0.0f;
        } else {
            int i2 = this.mMaxStep;
            if (f > ((float) i2)) {
                f = (float) i2;
            }
        }
        float f2 = (0.1f * f) + 0.5f;
        Log.m19d(TAG, "convertSpeed position : " + i + " step : " + f + " speed : " + f2);
        return f2;
    }

    private ColorStateList colorToColorStateList(Context context, int i) {
        return new ColorStateList(new int[][]{new int[0]}, new int[]{context.getColor(i)});
    }

    public void setLocalPositionParent(int[] iArr) {
        this.mLocalParentPos = iArr;
    }
}
