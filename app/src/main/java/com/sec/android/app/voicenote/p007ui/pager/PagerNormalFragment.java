package com.sec.android.app.voicenote.p007ui.pager;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.common.util.Trace;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.Observable;
import java.util.Observer;

/* renamed from: com.sec.android.app.voicenote.ui.pager.PagerNormalFragment */
public class PagerNormalFragment extends AbsPagerFragment implements Observer {
    private static final String TAG = "PagerNormalFragment";
    private IdleWaveStandard mIdleWave;
    private LinearLayout mLayoutNormal;
    private Space mSpaceView;

    public static void init() {
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        VoiceNoteObservable.getInstance().addObserver(this);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m26i(TAG, "onCreateView");
        Trace.beginSection("StandardFrgm.onCreateView");
        View inflate = layoutInflater.inflate(C0690R.layout.fragment_pager_normal, viewGroup, false);
        this.mLayoutNormal = (LinearLayout) inflate.findViewById(C0690R.C0693id.layout_pager_normal);
        this.mSpaceView = (Space) inflate.findViewById(C0690R.C0693id.space_view);
        this.mIdleWave = (IdleWaveStandard) inflate.findViewById(C0690R.C0693id.idle_wave_normal);
        Trace.endSection();
        return inflate;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        Log.m26i(TAG, "onCreatedView");
        super.onViewCreated(view, bundle);
        updateMultiWindowLayoutView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mLayoutNormal != null) {
            this.mLayoutNormal = null;
        }
        if (this.mIdleWave != null) {
            this.mIdleWave = null;
        }
        VoiceNoteObservable.getInstance().deleteObserver(this);
    }

    public void update(Observable observable, Object obj) {
        int intValue = ((Integer) obj).intValue();
        Log.m26i(TAG, "update - data : " + intValue);
        if (intValue == 21) {
            Log.m26i(TAG, "update - screen size view");
            updateMultiWindowLayoutView();
        }
    }

    private void updateMultiWindowLayoutView() {
        boolean z;
        FragmentActivity activity = getActivity();
        if (activity != null && DisplayManager.isInMultiWindowMode(activity)) {
            Resources resources = activity.getResources();
            int multiWindowCurrentAppHeight = DisplayManager.getMultiWindowCurrentAppHeight(activity);
            int actionBarHeight = DisplayManager.getActionBarHeight(activity);
            int mainTabHeight = ModePager.getInstance().getMainTabHeight();
            int dimensionPixelSize = resources.getDimensionPixelSize(C0690R.dimen.main_tab_list_margin_top);
            int dimensionPixelSize2 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_tab_description_min_height);
            int dimensionPixelSize3 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_wave_min_height);
            int dimensionPixelSize4 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_control_btn_min_height);
            int i = (multiWindowCurrentAppHeight - actionBarHeight) - dimensionPixelSize;
            int i2 = dimensionPixelSize2 + dimensionPixelSize3 + dimensionPixelSize4;
            if (i >= i2) {
                z = true;
            } else {
                i2 -= dimensionPixelSize2 - mainTabHeight;
                z = i >= i2 ? true : true;
            }
            int i3 = 0;
            if (z) {
                this.mLayoutNormal.setVisibility(0);
                this.mIdleWave.setVisibility(0);
                int i4 = (int) (((float) i) * 0.2f);
                int i5 = dimensionPixelSize2 - mainTabHeight;
                int i6 = i4 - mainTabHeight;
                if (i6 < i5) {
                    i4 = dimensionPixelSize2;
                } else {
                    i5 = i6;
                }
                int waveHeight = getWaveHeight(i, 0.35f, dimensionPixelSize3);
                int i7 = (i - i4) - waveHeight;
                if (i7 < dimensionPixelSize4) {
                    int i8 = dimensionPixelSize4 - i7;
                    if (waveHeight > dimensionPixelSize3) {
                        int i9 = waveHeight - dimensionPixelSize3;
                        if (i9 >= i8) {
                            dimensionPixelSize3 = waveHeight - i8;
                        } else {
                            i3 = i8 - i9;
                        }
                        waveHeight = dimensionPixelSize3;
                    } else {
                        i3 = i8;
                    }
                    if (i3 > 0) {
                        i5 -= i3;
                    }
                }
                updateViewHeight(this.mSpaceView, i5);
                updateViewHeight(this.mIdleWave, waveHeight);
            } else if (z) {
                int dimensionPixelSize5 = resources.getDimensionPixelSize(C0690R.dimen.multi_idle_tab_margin_bottom);
                if (i - dimensionPixelSize5 < i2) {
                    this.mLayoutNormal.setVisibility(8);
                    return;
                }
                this.mLayoutNormal.setVisibility(0);
                this.mIdleWave.setVisibility(0);
                int waveHeight2 = getWaveHeight(i, 0.35f, dimensionPixelSize3);
                int i10 = (i - waveHeight2) - mainTabHeight;
                if (i10 < dimensionPixelSize4) {
                    waveHeight2 -= dimensionPixelSize4 - i10;
                }
                updateViewHeight(this.mSpaceView, dimensionPixelSize5);
                updateViewHeight(this.mIdleWave, waveHeight2);
            } else if (z) {
                this.mLayoutNormal.setVisibility(8);
            }
        }
    }
}
