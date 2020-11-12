package com.sec.android.app.voicenote.p007ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.provider.AssistantProvider;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

/* renamed from: com.sec.android.app.voicenote.ui.view.TrimPopup */
public class TrimPopup implements IPopupView {
    private static final String TAG = "TrimPopup";
    private static TrimPopup sTrimPopup;
    private View mAnchorView;
    private Context mContext;
    private PopupWindow mPopupTrim;

    public static synchronized TrimPopup getInstance(Context context, View view) {
        TrimPopup trimPopup;
        synchronized (TrimPopup.class) {
            if (sTrimPopup == null) {
                sTrimPopup = new TrimPopup(context, view);
            }
            trimPopup = sTrimPopup;
        }
        return trimPopup;
    }

    private TrimPopup(Context context, View view) {
        View inflate = LayoutInflater.from(context).inflate(C0690R.layout.trim_popup_window, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(C0690R.C0693id.delete_dimmed_area);
        TextView textView2 = (TextView) inflate.findViewById(C0690R.C0693id.delete_selected_area);
        textView.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(textView.getText().toString()));
        textView2.setContentDescription(AssistantProvider.getInstance().getContentDesToButton(textView2.getText().toString()));
        this.mAnchorView = view;
        this.mContext = context;
        textView2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TrimPopup.this.lambda$new$0$TrimPopup(view);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TrimPopup.this.lambda$new$1$TrimPopup(view);
            }
        });
        if (Engine.getInstance().isDeleteEnable()) {
            textView2.setEnabled(true);
            textView2.setFocusable(true);
            textView2.setTextColor(this.mContext.getResources().getColor(C0690R.C0691color.trim_popup_text_color_enable, (Resources.Theme) null));
        } else {
            textView2.setEnabled(false);
            textView2.setTextColor(this.mContext.getResources().getColor(C0690R.C0691color.trim_popup_text_color_disable, (Resources.Theme) null));
        }
        if (Engine.getInstance().isTrimEnable()) {
            textView.setEnabled(true);
            textView.setFocusable(true);
            textView.setTextColor(this.mContext.getResources().getColor(C0690R.C0691color.trim_popup_text_color_enable, (Resources.Theme) null));
        } else {
            textView.setEnabled(false);
            textView.setTextColor(this.mContext.getResources().getColor(C0690R.C0691color.trim_popup_text_color_disable, (Resources.Theme) null));
        }
        this.mPopupTrim = new PopupWindow(inflate, -2, -2);
        this.mPopupTrim.setContentView(inflate);
        this.mPopupTrim.setOutsideTouchable(true);
    }

    public /* synthetic */ void lambda$new$0$TrimPopup(View view) {
        Log.m19d(TAG, "start delete ");
        if (Engine.getInstance().startDelete() == -119) {
            Toast.makeText(this.mContext, C0690R.string.please_wait, 0).show();
            Log.m22e(TAG, "Engine BUSY !!!!");
        }
        SALogProvider.insertSALog(this.mContext.getResources().getString(C0690R.string.screen_edit_comm), this.mContext.getResources().getString(C0690R.string.event_del_selected_area));
        dismiss(true);
    }

    public /* synthetic */ void lambda$new$1$TrimPopup(View view) {
        Log.m19d(TAG, "start trim ");
        if (Engine.getInstance().startTrim() == -119) {
            Toast.makeText(this.mContext, C0690R.string.please_wait, 0).show();
            Log.m22e(TAG, "Engine BUSY !!!!");
        }
        SALogProvider.insertSALog(this.mContext.getResources().getString(C0690R.string.screen_edit_comm), this.mContext.getResources().getString(C0690R.string.event_del_dimmed_area));
        dismiss(true);
    }

    public void show() {
        Log.m19d(TAG, "show");
        PopupWindow popupWindow = this.mPopupTrim;
        if (popupWindow != null) {
            View contentView = popupWindow.getContentView();
            contentView.measure(0, 0);
            int[] iArr = new int[2];
            this.mAnchorView.getLocationInWindow(iArr);
            int i = iArr[0];
            int measuredHeight = iArr[1] - contentView.getMeasuredHeight();
            this.mPopupTrim.setFocusable(true);
            this.mPopupTrim.showAtLocation(this.mAnchorView, 8388659, i, measuredHeight);
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.EDIT_SHOW_TRIM_POPUP));
        }
    }

    public void dismiss(boolean z) {
        Log.m19d(TAG, "dismiss");
        PopupWindow popupWindow = this.mPopupTrim;
        if (popupWindow != null) {
            if (z) {
                popupWindow.dismiss();
            }
            this.mPopupTrim = null;
        }
        this.mAnchorView = null;
        this.mContext = null;
        sTrimPopup = null;
    }

    public void dismiss() {
        PopupWindow popupWindow = this.mPopupTrim;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public boolean isShowing() {
        PopupWindow popupWindow = this.mPopupTrim;
        return popupWindow != null && popupWindow.isShowing();
    }

    public boolean performClick(int i) {
        View findViewById;
        Log.m19d(TAG, "performClick id : " + i);
        PopupWindow popupWindow = this.mPopupTrim;
        if (popupWindow == null || (findViewById = popupWindow.getContentView().findViewById(i)) == null) {
            return true;
        }
        findViewById.performClick();
        return true;
    }
}
