package com.sec.android.app.voicenote.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.DeviceInfo;
import com.sec.android.app.voicenote.common.util.DisplayManager;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.NFCProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.Settings;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.VoiceNoteFeature;
import java.nio.charset.Charset;
import java.util.Locale;

public class NFCWritingActivity extends BaseToolbarActivity {
    public static final String FROM_LAUNCH_INFO = "from_launch_info";
    private static final String TAG = "NFCWritingActivity";
    public static final String TAG_LABEL_INFO = "label_info";
    private IntentFilter[] mIntentFilter = null;
    private String mLabelInfo = null;
    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent = null;
    private String[][] techListsArray = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        IntentFilter intentFilter;
        Log.m26i(TAG, "onCreate " + bundle);
        super.onCreate(bundle);
        setContentView((int) C0690R.layout.activity_nfc);
        ImageView imageView = (ImageView) findViewById(C0690R.C0693id.nfc_image);
        if (VoiceNoteFeature.FLAG_IS_FOLDER_PHONE(this)) {
            imageView.setImageResource(C0690R.C0692drawable.voice_recorder_write_to_voice_label_for_folder_phone);
        } else {
            imageView.setImageResource(C0690R.C0692drawable.voice_recorder_ic_write_to_voice_label);
        }
        if (getResources().getConfiguration().orientation == 2 && DisplayManager.getMultiwindowMode() != 2) {
            getWindow().setFlags(1024, 1024);
        }
        setDisplayShowHomeEnabled();
        setTitleActivity(getTitle().toString());
        setOverwriteBackgroundToolbar(C0690R.C0691color.actionbar_color_bg);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, C0690R.C0691color.actionbar_color_bg));
        Intent intent = getIntent();
        if (intent != null) {
            this.mLabelInfo = intent.getStringExtra(TAG_LABEL_INFO);
            Log.m26i(TAG, "onCreate  mLabelInfo : " + this.mLabelInfo);
            resolveIntent(intent);
        }
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (this.mNfcAdapter == null) {
            Log.m26i(TAG, "mNfcAdapter is null");
        }
        this.mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, NFCWritingActivity.class).addFlags(536870912), 0);
        try {
            intentFilter = new IntentFilter("android.nfc.action.TAG_DISCOVERED");
            try {
                intentFilter.addCategory("android.intent.category.DEFAULT");
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            intentFilter = null;
            finish();
            this.mIntentFilter = new IntentFilter[]{intentFilter};
            this.techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
            DeviceInfo.logDeviceInfo(this, getResources().getConfiguration());
        }
        this.mIntentFilter = new IntentFilter[]{intentFilter};
        this.techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
        DeviceInfo.logDeviceInfo(this, getResources().getConfiguration());
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.m26i(TAG, "onResume");
        super.onResume();
        if (this.mNfcAdapter != null) {
            Log.m26i(TAG, "NFC enableForegroundDispatch");
            this.mNfcAdapter.enableForegroundDispatch(this, this.mPendingIntent, this.mIntentFilter, this.techListsArray);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        Log.m26i(TAG, "onPause");
        super.onPause();
        NfcAdapter nfcAdapter = this.mNfcAdapter;
        if (nfcAdapter != null) {
            try {
                nfcAdapter.disableForegroundDispatch(this);
            } catch (IllegalStateException unused) {
                Log.m32w(TAG, "catch the exception while disableForegroundDispatch the NfcAdapter");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        Log.m26i(TAG, "onNewIntent");
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        boolean z;
        Log.m26i(TAG, "resolveIntent : " + intent.getAction());
        if ("android.nfc.action.TAG_DISCOVERED".equals(intent.getAction()) || "android.nfc.action.TECH_DISCOVERED".equals(intent.getAction())) {
            Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
            if (parcelableArrayExtra == null || parcelableArrayExtra.length <= 0) {
                z = true;
            } else {
                NdefMessage[] ndefMessageArr = new NdefMessage[parcelableArrayExtra.length];
                z = true;
                for (int i = 0; i < parcelableArrayExtra.length; i++) {
                    try {
                        ndefMessageArr[i] = (NdefMessage) parcelableArrayExtra[i];
                        byte[] payload = ndefMessageArr[i].getRecords()[0].getPayload();
                        if (payload != null) {
                            if (payload.length > 0) {
                                String str = (payload[0] & 128) == 0 ? "UTF-8" : "UTF-16";
                                byte b = (byte) (payload[0] & 63);
                                z = NFCProvider.updateTaggedInfo(getApplicationContext(), new String(payload, b + 1, (payload.length - b) - 1, str), false);
                            }
                        }
                        Log.m26i(TAG, "resolveIntent : There is no payload.");
                    } catch (Exception e) {
                        Log.m22e(TAG, "resolveIntent " + e);
                    }
                }
            }
            if (!z) {
                Log.m26i(TAG, "writeTag fail : different device");
                Bundle bundle = new Bundle();
                bundle.putInt(DialogFactory.BUNDLE_TITLE_ID, C0690R.string.unable_to_write_voice_label);
                bundle.putString(DialogFactory.BUNDLE_WORD, getString(C0690R.string.voice_label_error_msg1) + ' ' + getString(C0690R.string.voice_label_error_msg2));
                bundle.putInt(DialogFactory.BUNDLE_POSITIVE_BTN_ID, 17039370);
                DialogFactory.show(getSupportFragmentManager(), DialogFactory.UNABLE_NFC_TAG_DIALOG, bundle);
                return;
            }
            if (this.mLabelInfo == null) {
                Log.m19d(TAG, "mFilepath is null. Gets filename from settings.");
                this.mLabelInfo = Settings.getStringSettings(Settings.KEY_NFC_LABEL_INFO);
            }
            Tag tag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
            NdefRecord createTextRecord = createTextRecord(this.mLabelInfo, Locale.getDefault(), true);
            Ndef ndef = null;
            NdefMessage ndefMessage = createTextRecord != null ? new NdefMessage(new NdefRecord[]{createTextRecord}) : null;
            if (writeTag(tag, ndefMessage)) {
                NFCProvider.updateTaggedInfo(getApplicationContext(), this.mLabelInfo, true);
                Toast.makeText(getApplicationContext(), C0690R.string.Tag_has_been_written_successfully, 0).show();
                SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_NFC_WRITE, -1);
                SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_voice_label), getResources().getString(C0690R.string.event_write_voice_label));
                finish();
                return;
            }
            try {
                ndef = Ndef.get(tag);
            } catch (Exception unused) {
            }
            if (ndefMessage == null || ndef == null || ndefMessage.getByteArrayLength() <= ndef.getMaxSize()) {
                Toast.makeText(getApplicationContext(), C0690R.string.Failed_to_write_tag, 0).show();
                Log.m26i(TAG, "writeTag fail");
                finish();
                return;
            }
            Toast.makeText(getApplicationContext(), C0690R.string.nfc_filename_too_long, 0).show();
            Log.m26i(TAG, "writeTag fail : file name is too long size : " + ndefMessage.getByteArrayLength() + " tag size : " + ndef.getMaxSize());
            finish();
        }
    }

    public static NdefRecord createTextRecord(String str, Locale locale, boolean z) {
        if (str == null) {
            Log.m22e(TAG, "payload is null");
            return null;
        }
        byte[] bytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        byte[] bytes2 = str.getBytes(Charset.forName(z ? "UTF-8" : "UTF-16"));
        int i = z ? 0 : 128;
        byte[] bArr = new byte[(bytes.length + 1 + bytes2.length)];
        bArr[0] = (byte) ((char) (i + bytes.length));
        System.arraycopy(bytes, 0, bArr, 1, bytes.length);
        System.arraycopy(bytes2, 0, bArr, bytes.length + 1, bytes2.length);
        return NdefRecord.createMime("voice/path", bArr);
    }

    /* access modifiers changed from: package-private */
    public boolean writeTag(Tag tag, NdefMessage ndefMessage) {
        if (tag == null || ndefMessage == null) {
            Log.m22e(TAG, "writeTag null parameters");
            return false;
        }
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                try {
                    ndef.connect();
                    if (!ndef.isWritable()) {
                        Log.m22e(TAG, "IDS_NFC_BODY_TAG_IS_READ_ONLY");
                        ndef.close();
                        return false;
                    } else if (ndef.getMaxSize() < 0) {
                        Log.m22e(TAG, "IDS_NFC_BODY_TAG_CAPACITY_IS_PD_BYTES_CONTENT_IS_PD_BYTES");
                        ndef.close();
                        return false;
                    } else {
                        try {
                            ndef.writeNdefMessage(ndefMessage);
                            Log.m26i(TAG, "IDS_NFC_BODY_MESSAGE_SAVED_TO_PRE_FORMATTED_TAG");
                            ndef.close();
                            return true;
                        } catch (Exception e) {
                            Log.m24e(TAG, "Failed to writeNdefMessage", (Throwable) e);
                            ndef.close();
                            return false;
                        }
                    }
                } catch (Exception e2) {
                    Log.m24e(TAG, "Failed to connect", (Throwable) e2);
                    ndef.close();
                    return false;
                }
            } else {
                NdefFormatable ndefFormatable = NdefFormatable.get(tag);
                if (ndefFormatable != null) {
                    try {
                        ndefFormatable.connect();
                        ndefFormatable.format(ndefMessage);
                        Log.m26i(TAG, "IDS_NFC_BODY_TAG_FORMATTED_AND_MESSAGE_SAVED");
                        ndefFormatable.close();
                        return true;
                    } catch (Exception unused) {
                        Log.m22e(TAG, "IDS_NFC_BODY_FAILED_TO_FORMAT_TAG");
                        ndefFormatable.close();
                        return false;
                    }
                } else {
                    Log.m22e(TAG, "IDS_NFC_BODY_NDEF_NOT_SUPPORTED_BY_TAG");
                    return false;
                }
            }
        } catch (Exception e3) {
            Log.m24e(TAG, "Failed to write tag", (Throwable) e3);
            Log.m22e(TAG, "IDS_NFC_BODY_FAILED_TO_WRITE_TO_TAG");
            return false;
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
