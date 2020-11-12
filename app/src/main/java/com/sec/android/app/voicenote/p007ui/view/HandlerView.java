package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.SimpleActivity;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.MetadataRepository;
import com.sec.android.app.voicenote.service.SimpleMetadataRepositoryManager;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;

/* renamed from: com.sec.android.app.voicenote.ui.view.HandlerView */
public class HandlerView extends LinearLayout {
    private static final String TAG = "HandlerView";
    private String mSession;

    public HandlerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init((Integer) null);
    }

    public HandlerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init((Integer) null);
    }

    public HandlerView(Context context) {
        super(context);
        init((Integer) null);
    }

    public void setBackgroundColor(int i) {
        init(Integer.valueOf(i));
    }

    private void init(Integer num) {
        setOrientation(1);
        removeAllViews();
        View inflate = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0690R.layout.handler_view, (ViewGroup) null);
        View findViewById = inflate.findViewById(C0690R.C0693id.handler_top_part);
        View findViewById2 = inflate.findViewById(C0690R.C0693id.handler_middle_part);
        View findViewById3 = inflate.findViewById(C0690R.C0693id.handler_bottom_part);
        String str = (String) getTag();
        if (num == null) {
            if (str == null || str.isEmpty()) {
                Log.m32w(TAG, "init - tag is empty");
                num = null;
            } else {
                num = Integer.valueOf((int) Long.parseLong(str.substring(1), 16));
            }
        }
        if (num != null) {
            findViewById.setBackground(new ColorDrawable(num.intValue()));
            findViewById2.setBackground(new ColorDrawable(num.intValue()));
            findViewById3.setBackground(new ColorDrawable(num.intValue()));
        }
        int recordMode = getRecordMode();
        if (recordMode == 2) {
            findViewById2.setVisibility(4);
        } else if (recordMode == 4) {
            findViewById2.setVisibility(4);
            findViewById3.setVisibility(4);
        }
        addView(inflate);
    }

    public int getRecordMode() {
        Context context = getContext();
        if (context == null || !(context instanceof SimpleActivity)) {
            return MetadataRepository.getInstance().getRecordMode();
        }
        if (this.mSession == null) {
            this.mSession = VoiceNoteApplication.getSimpleActivitySession();
        }
        return SimpleMetadataRepositoryManager.getInstance().getMetadataRepository(this.mSession).getRecordMode();
    }

    public void setSession(String str) {
        this.mSession = str;
    }

    public void update() {
        View findViewById = findViewById(C0690R.C0693id.handler_top_part);
        View findViewById2 = findViewById(C0690R.C0693id.handler_middle_part);
        View findViewById3 = findViewById(C0690R.C0693id.handler_bottom_part);
        int recordMode = getRecordMode();
        if (recordMode == 2) {
            findViewById.setVisibility(0);
            findViewById2.setVisibility(4);
            findViewById3.setVisibility(0);
        } else if (recordMode != 4) {
            findViewById.setVisibility(0);
            findViewById2.setVisibility(0);
            findViewById3.setVisibility(0);
        } else {
            findViewById.setVisibility(0);
            findViewById2.setVisibility(4);
            findViewById3.setVisibility(4);
        }
    }
}
