package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.activity.SimpleActivity;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.provider.Event;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.service.Engine;
import com.sec.android.app.voicenote.uicore.VoiceNoteApplication;
import com.sec.android.app.voicenote.uicore.VoiceNoteObservable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.PermissionDialog */
public class PermissionDialog extends AbsDialogFragment implements DialogInterface.OnClickListener {
    private static final Map<String, String> PLATFORM_PERMISSIONS = new ArrayMap();
    private static final String TAG = "PermissionDialog";
    public static final int UNDEFINED = -1;
    private static final StyleSpan mBoldSpan = new StyleSpan(1);
    private AlertDialog mDialog;
    private VoiceNoteObservable mObservable = VoiceNoteObservable.getInstance();

    static {
        PLATFORM_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", "android.permission-group.STORAGE");
        PLATFORM_PERMISSIONS.put("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission-group.STORAGE");
        PLATFORM_PERMISSIONS.put("android.permission.RECORD_AUDIO", "android.permission-group.MICROPHONE");
        PLATFORM_PERMISSIONS.put("android.permission.READ_PHONE_STATE", "android.permission-group.PHONE");
    }

    public static PermissionDialog newInstance(Bundle bundle) {
        PermissionDialog permissionDialog = new PermissionDialog();
        permissionDialog.setArguments(bundle);
        return permissionDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        final FragmentActivity activity = getActivity();
        int i = getArguments().getInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, -1);
        int i2 = getArguments().getInt(DialogFactory.BUNDLE_NEGATIVE_BTN_ID, -1);
        String string = getArguments().getString(DialogFactory.BUNDLE_WORD, (String) null);
        ArrayList<Integer> integerArrayList = getArguments().getIntegerArrayList(DialogFactory.BUNDLE_PERMISSION_LIST_IDS);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inflate = LayoutInflater.from(getActivity()).inflate(C0690R.layout.dialog_permission, (ViewGroup) null);
        builder.setView(inflate);
        String string2 = activity.getString(C0690R.string.app_name);
        if (integerArrayList != null && !integerArrayList.isEmpty()) {
            Iterator<Integer> it = integerArrayList.iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                if (intValue == C0690R.string.microphone) {
                    TextView textView = (TextView) inflate.findViewById(C0690R.C0693id.permission_phone);
                    textView.setVisibility(0);
                    textView.setText(Resources.getSystem().getIdentifier("permgrouplab_microphone", "string", "android"));
                    textView.setCompoundDrawablesWithIntrinsicBounds(getPermissionIcon("android.permission.RECORD_AUDIO"), (Drawable) null, (Drawable) null, (Drawable) null);
                } else if (intValue == C0690R.string.phone) {
                    TextView textView2 = (TextView) inflate.findViewById(C0690R.C0693id.permission_microphone);
                    textView2.setVisibility(0);
                    textView2.setText(Resources.getSystem().getIdentifier("permgrouplab_phone", "string", "android"));
                    textView2.setCompoundDrawablesWithIntrinsicBounds(getPermissionIcon("android.permission.READ_PHONE_STATE"), (Drawable) null, (Drawable) null, (Drawable) null);
                    string2 = activity.getString(C0690R.string.call_reject_recording);
                } else if (intValue == C0690R.string.storage) {
                    TextView textView3 = (TextView) inflate.findViewById(C0690R.C0693id.permission_storage);
                    textView3.setVisibility(0);
                    textView3.setText(Resources.getSystem().getIdentifier("permgrouplab_storage", "string", "android"));
                    textView3.setCompoundDrawablesWithIntrinsicBounds(getPermissionIcon("android.permission.READ_EXTERNAL_STORAGE"), (Drawable) null, (Drawable) null, (Drawable) null);
                }
            }
        }
        TextView textView4 = (TextView) inflate.findViewById(C0690R.C0693id.permission_alert_text);
        if (string != null) {
            int indexOf = string.indexOf(string2);
            if (indexOf != -1) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                spannableStringBuilder.setSpan(mBoldSpan, indexOf, string2.length() + indexOf, 33);
                textView4.setText(spannableStringBuilder);
            } else {
                textView4.setText(string);
            }
        }
        textView4.setMovementMethod(new ScrollingMovementMethod());
        if (i != -1) {
            builder.setPositiveButton(i, this);
        }
        if (i2 != -1) {
            builder.setNegativeButton(i2, this);
        }
        this.mDialog = builder.create();
        this.mDialog.setCanceledOnTouchOutside(false);
        this.mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private final /* synthetic */ Activity f$1;

            {
                this.f$1 = activity;
            }

            public final void onShow(DialogInterface dialogInterface) {
                PermissionDialog.this.lambda$onCreateDialog$0$PermissionDialog(this.f$1, dialogInterface);
            }
        });
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$PermissionDialog(Activity activity, DialogInterface dialogInterface) {
        ColorStateList colorStateList = activity.getResources().getColorStateList(C0690R.C0691color.dialog_button_color, (Resources.Theme) null);
        this.mDialog.getButton(-1).setTextColor(colorStateList);
        this.mDialog.getButton(-2).setTextColor(colorStateList);
        this.mDialog.getButton(-3).setTextColor(colorStateList);
    }

    public void onResume() {
        super.onResume();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        int i2;
        if (i == -3) {
            i2 = getArguments().getInt(DialogFactory.BUNDLE_NEUTRAL_BTN_EVENT, Event.HIDE_DIALOG);
        } else if (i != -2) {
            i2 = i != -1 ? 998 : getArguments().getInt(DialogFactory.BUNDLE_POSITIVE_BTN_EVENT, Event.HIDE_DIALOG);
        } else {
            i2 = getArguments().getInt(DialogFactory.BUNDLE_NEGATIVE_BTN_EVENT, Event.HIDE_DIALOG);
        }
        Log.m26i("PermissionDialog", "onClick event : " + i2);
        if (i2 == 971) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
            finishActivity();
        } else if (i2 != 972) {
            this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
        } else {
            this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                intent.addFlags(268435456);
                startActivity(intent);
                checkToFinishSimpleActivity();
            } catch (ActivityNotFoundException e) {
                Log.m24e("PermissionDialog", "ActivityNotFoundException !", (Throwable) e);
            }
        }
    }

    private void checkToFinishSimpleActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null && (getActivity() instanceof SimpleActivity) && DisplayManager.isInMultiWindowMode(activity)) {
            activity.finish();
        }
    }

    private void finishActivity() {
        Engine.getInstance().cancelRecord();
        Engine.getInstance().stopPlay(false);
        VoiceNoteApplication.saveEvent(4);
        VoiceNoteApplication.saveScene(0);
        VoiceNoteObservable.getInstance().notifyObservers(4);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.mObservable.notifyObservers(Integer.valueOf(Event.HIDE_DIALOG));
        finishActivity();
    }

    private Drawable getPermissionIcon(String str) {
        Drawable drawable;
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            if (packageManager == null) {
                return null;
            }
            PermissionGroupInfo permissionGroupInfo = getPermissionGroupInfo(packageManager, str);
            if (permissionGroupInfo.icon == 0 || getActivity() == null) {
                drawable = null;
            } else {
                drawable = getActivity().getDrawable(permissionGroupInfo.icon);
                if (drawable != null) {
                    drawable.setTint(getActivity().getResources().getColor(C0690R.C0691color.dialog_permission_item_icon, (Resources.Theme) null));
                }
            }
            return resizeVectorDrawableImage(drawable);
        } catch (Resources.NotFoundException unused) {
            Log.m22e("PermissionDialog", "Resources.NotFoundException :" + str);
            return null;
        }
    }

    private PermissionGroupInfo getPermissionGroupInfo(PackageManager packageManager, String str) {
        try {
            PermissionInfo permissionInfo = packageManager.getPermissionInfo(str, 128);
            String str2 = PLATFORM_PERMISSIONS.get(str);
            if (str2.isEmpty()) {
                str2 = permissionInfo.group;
            }
            PermissionGroupInfo permissionGroupInfo = packageManager.getPermissionGroupInfo(str2, 128);
            if (permissionGroupInfo.icon != 0) {
                return permissionGroupInfo;
            }
            Log.m22e("PermissionDialog", "getPermissionGroupInfo - no Permission Group info for " + str);
            return null;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.m22e("PermissionDialog", "PackageManager.NameNotFoundException :" + str);
            return null;
        }
    }

    private Drawable resizeVectorDrawableImage(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (getActivity() == null || getActivity().getResources() == null) {
            return drawable;
        }
        int dimensionPixelSize = getActivity().getResources().getDimensionPixelSize(C0690R.dimen.permission_icon_size);
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(createBitmap, dimensionPixelSize, dimensionPixelSize, false));
    }
}
