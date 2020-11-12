package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.WebTosActivity;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.RecognizerDBProvider;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;

import androidx.annotation.NonNull;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.PrivacyPolicyDialog */
public class PrivacyPolicyDialog extends AbsDialogFragment {
    /* access modifiers changed from: private */
    public static final String TAG = "PrivacyPolicyDialog";
    private final int ACCESSIBILITY_ON = 1;
    private boolean mIsGdpr;

    /* renamed from: tv */
    private TextView f108tv = null;

    public static PrivacyPolicyDialog newInstance(Bundle bundle) {
        PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog();
        privacyPolicyDialog.setArguments(bundle);
        return privacyPolicyDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        Log.m26i(TAG, "onCreateDialog");
        this.mIsGdpr = getArguments().getBoolean(DialogFactory.BUNDLE_GDPR_COUNTRY);
        View inflate = getActivity().getLayoutInflater().inflate(C0690R.layout.dialog_privacy_policy, (ViewGroup) null);
        this.f108tv = (TextView) inflate.findViewById(C0690R.C0693id.privacy_policy_content);
        isClickableInAccessibilityMode();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0690R.string.privacy_policy_title_dialog);
        builder.setView(inflate);
        final int i = this.mIsGdpr ? C0690R.string.ok_button : C0690R.string.agree;
        builder.setPositiveButton(i, new DialogInterface.OnClickListener() {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = i;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacyPolicyDialog.this.lambda$onCreateDialog$0$PrivacyPolicyDialog(this.f$1, dialogInterface, i);
            }
        });
        return builder.create();
    }

    public /* synthetic */ void lambda$onCreateDialog$0$PrivacyPolicyDialog(int i, DialogInterface dialogInterface, int i2) {
        Log.m26i(TAG, getString(i));
        RecognizerDBProvider.setTOSAccepted(1);
        if (VoiceNoteApplication.getScene() == 1) {
            VoiceNoteObservable.getInstance().notifyObservers(1009);
        } else if (VoiceNoteApplication.getScene() == 12) {
            VoiceNoteObservable.getInstance().notifyObservers(Integer.valueOf(Event.TRANSLATION_FROM_GDPR_DIALOG));
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
    }

    public void onResume() {
        super.onResume();
        Log.m19d(TAG, "onResume");
        isClickableInAccessibilityMode();
    }

    private void isClickableInAccessibilityMode() {
        String str;
        if (this.f108tv != null) {
            if (this.mIsGdpr) {
                str = getString(C0690R.string.gdpr_content_dialog, "#", "#");
            } else {
                str = getString(C0690R.string.non_gdpr_content_dialog, "#", "#");
            }
            int indexOf = str.indexOf("#");
            int indexOf2 = str.indexOf("#", indexOf + 1) - 1;
            SpannableString spannableString = new SpannableString(str.replaceAll("#", ""));
            if (!(indexOf == -1 || indexOf2 == -1)) {
                spannableString.setSpan(new ClickableSpan() {
                    public void onClick(View view) {
                        Intent intent = new Intent(PrivacyPolicyDialog.this.getContext(), WebTosActivity.class);
                        intent.putExtra("from_button", true);
                        try {
                            PrivacyPolicyDialog.this.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.m24e(PrivacyPolicyDialog.TAG, "ActivityNotFoundException", (Throwable) e);
                        }
                    }

                    public void updateDrawState(TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                    }
                }, indexOf, indexOf2, 0);
            }
            try {
                if (Settings.Secure.getInt(getActivity().getContentResolver(), "accessibility_enabled") == 1) {
                    this.f108tv.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            PrivacyPolicyDialog.this.lambda$isClickableInAccessibilityMode$1$PrivacyPolicyDialog(view);
                        }
                    });
                } else {
                    Log.m19d(TAG, "off!");
                    this.f108tv.setOnClickListener((View.OnClickListener) null);
                }
            } catch (Settings.SettingNotFoundException e) {
                Log.m22e(TAG, e.toString());
            }
            this.f108tv.setMovementMethod(LinkMovementMethod.getInstance());
            this.f108tv.setText(spannableString, TextView.BufferType.SPANNABLE);
        }
    }

    public /* synthetic */ void lambda$isClickableInAccessibilityMode$1$PrivacyPolicyDialog(View view) {
        Intent intent = new Intent(getContext(), WebTosActivity.class);
        intent.putExtra("from_button", true);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "ActivityNotFoundException", (Throwable) e);
        }
    }
}
