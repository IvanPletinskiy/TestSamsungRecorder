package com.sec.android.app.voicenote.p007ui.pager;

import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.AssistantProvider;

/* renamed from: com.sec.android.app.voicenote.ui.pager.AbsPagerFragment */
public abstract class AbsPagerFragment extends Fragment {
    private static final String TAG = "AbsPagerFragment";
    View mRootViewTmp;

    /* access modifiers changed from: protected */
    public int getWaveHeight(int i, float f, int i2) {
        int i3 = (int) (((float) i) * f);
        return i3 < i2 ? i2 : i3;
    }

    /* access modifiers changed from: package-private */
    public void hideHelpModeGuide() {
    }

    /* access modifiers changed from: package-private */
    public void showHelpModeGuide() {
    }

    public void onPause() {
        super.onPause();
        this.mRootViewTmp = null;
    }

    /* access modifiers changed from: protected */
    public void blockDescendantsForIdleScene(View view, boolean z) {
        AssistantProvider.getInstance().setBlockDescendants(view.findViewById(C0690R.C0693id.main_idle_layout), z);
        AssistantProvider.getInstance().setBlockDescendants(view.findViewById(C0690R.C0693id.main_app_bar_layout), z);
        AssistantProvider.getInstance().setBlockDescendants(view.findViewById(C0690R.C0693id.main_control_view), z);
        AssistantProvider.getInstance().setBlockDescendants(view.findViewById(C0690R.C0693id.main_idle_controlbutton), z);
        AssistantProvider.getInstance().setBlockDescendants(view.findViewById(C0690R.C0693id.main_control_button_layout), z);
    }

    /* access modifiers changed from: protected */
    public void updateViewHeight(View view, int i) {
        if (view != null) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = i;
            view.setLayoutParams(layoutParams);
        }
    }
}
