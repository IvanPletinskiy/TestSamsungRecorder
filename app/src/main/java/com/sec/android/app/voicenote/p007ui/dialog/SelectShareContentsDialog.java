package com.sec.android.app.voicenote.p007ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.sec.android.app.voicenote.C0690R;
import com.sec.android.app.voicenote.common.util.TextData;
import com.sec.android.app.voicenote.common.util.VNMediaScanner;
import com.sec.android.app.voicenote.p007ui.AbsDialogFragment;
import com.sec.android.app.voicenote.p007ui.dialog.SelectShareContentsDialog;
import com.sec.android.app.voicenote.provider.DBProvider;
import com.sec.android.app.voicenote.provider.Log;
import com.sec.android.app.voicenote.provider.QuickConnectProvider;
import com.sec.android.app.voicenote.provider.SALogProvider;
import com.sec.android.app.voicenote.provider.SurveyLogProvider;
import com.sec.android.app.voicenote.provider.ViewProvider;
import com.sec.android.app.voicenote.receiver.ShareTaskReceiver;
import com.sec.android.app.voicenote.service.codec.M4aConsts;
import com.sec.android.app.voicenote.service.codec.M4aInfo;
import com.sec.android.app.voicenote.service.codec.M4aReader;
import com.sec.android.app.voicenote.service.helper.SttHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/* renamed from: com.sec.android.app.voicenote.ui.dialog.SelectShareContentsDialog */
public class SelectShareContentsDialog extends AbsDialogFragment {
    private static final String EASY_SHARE_MORE_QUICK_CONNECT = "more_actions_quick_connect";
    public static final int MULTIPLE_MEMO = 2;
    public static final String SHARE_MEMO_FILE = "memo_list";
    private static final int SHARE_ONLY_TEXT_FILE = 100;
    private static final int SHARE_VOICE_AND_TEXT_FILE = 101;
    public static final String SHARE_VOICE_FILE = "voice_list";
    public static final int SINGLE_MEMO = 1;
    private static final String TAG = "SelectShareContentsDialog";
    /* access modifiers changed from: private */
    public FragmentActivity mActivity;
    /* access modifiers changed from: private */
    public AlertDialog mDialog;
    private long mID;
    private int mMode;
    /* access modifiers changed from: private */
    public int mScene;
    /* access modifiers changed from: private */
    public Intent mShareIntent;
    /* access modifiers changed from: private */
    public ArrayList<Parcelable> mUriList;
    /* access modifiers changed from: private */
    public ArrayList<Parcelable> mUriMemoList;
    /* access modifiers changed from: private */
    public ArrayList<Parcelable> mUriShareList = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Parcelable> mUriTextList;
    /* access modifiers changed from: private */
    public TextUriTask textUriTask;

    public static SelectShareContentsDialog newInstance(Bundle bundle) {
        SelectShareContentsDialog selectShareContentsDialog = new SelectShareContentsDialog();
        selectShareContentsDialog.setArguments(bundle);
        return selectShareContentsDialog;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle bundle) {
        String[] strArr;
        this.mActivity = getActivity();
        this.mMode = getArguments().getInt(DialogFactory.BUNDLE_SHARE_VOICE_MEMO);
        this.mID = getArguments().getLong(DialogFactory.BUNDLE_ID);
        this.mUriList = getArguments().getParcelableArrayList(SHARE_VOICE_FILE);
        this.mUriTextList = new ArrayList<>();
        this.mUriMemoList = getArguments().getParcelableArrayList(SHARE_MEMO_FILE);
        this.mScene = getArguments().getInt(DialogFactory.BUNDLE_SCENE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (this.mUriList.size() == 0 && this.mUriMemoList.size() == 0) {
            Log.m22e(TAG, "item size is zero ");
            return builder.create();
        }
        Log.m19d(TAG, "mMode = " + this.mMode);
        int i = this.mMode;
        if (i != 1) {
            strArr = i != 2 ? null : new String[]{getString(C0690R.string.voice_only), getString(C0690R.string.text_only_file), getString(C0690R.string.voice_text)};
        } else {
            strArr = new String[]{getString(C0690R.string.voice_only), getString(C0690R.string.text_only_file), getString(C0690R.string.text), getString(C0690R.string.voice_text)};
        }
        builder.setTitle(getString(C0690R.string.share_as));
        View inflate = View.inflate(getActivity(), C0690R.layout.select_share_dialog_layout, (ViewGroup) null);
        ListView listView = (ListView) inflate.findViewById(C0690R.C0693id.option_share_list_view);
        listView.setDivider((Drawable) null);
        listView.setAdapter(new ArrayAdapter(getActivity(), C0690R.layout.select_share_dialog_item, strArr));
        ViewProvider.setBackgroundListView(getActivity(), listView);
        listView.setSelector(ContextCompat.getDrawable(getActivity(), C0690R.C0692drawable.voice_ripple_rectangle));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                SelectShareContentsDialog.this.lambda$onCreateDialog$0$SelectShareContentsDialog(adapterView, view, i, j);
            }
        });
        builder.setView(inflate);
        this.mDialog = builder.create();
        return this.mDialog;
    }

    public /* synthetic */ void lambda$onCreateDialog$0$SelectShareContentsDialog(AdapterView adapterView, View view, int i, long j) {
        this.mShareIntent = new Intent();
        int i2 = this.mMode;
        if (i2 != 1) {
            if (i2 == 2) {
                if (i == 0) {
                    shareVoiceMemo();
                } else if (i == 1) {
                    shareTextMemo(2);
                } else if (i == 2) {
                    shareVoiceAndTextMemo();
                }
            }
        } else if (i == 1) {
            shareTextMemo(1);
        } else if (i == 2) {
            shareStringTextMemo();
        } else if (i != 3) {
            shareVoiceMemo();
        } else {
            shareVoiceAndTextMemo();
        }
    }

    private void shareStringTextMemo() {
        this.mUriShareList.addAll(this.mUriList);
        if (this.mUriShareList.size() > 0) {
            this.mShareIntent.setAction("android.intent.action.SEND_MULTIPLE");
            this.mShareIntent.setType("text/plain, audio/*");
            this.mShareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", this.mUriShareList);
        } else {
            this.mShareIntent.setAction("android.intent.action.SEND");
            this.mShareIntent.setType("text/plain");
        }
        String checkSTT = checkSTT(this.mID);
        this.mShareIntent.putExtra("android.intent.extra.TEXT", checkSTT != null ? readFile(new File(checkSTT)) : "");
        startSharingFile();
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_share_memo), getResources().getString(C0690R.string.event_share_popup_text));
    }

    private void shareVoiceAndTextMemo() {
        this.textUriTask = new TextUriTask();
        this.textUriTask.execute(new Integer[]{101});
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_share_memo), getResources().getString(C0690R.string.event_share_popup_voice_text));
    }

    private void shareTextMemo(int i) {
        Uri uri = null;
        if (i == 1) {
            this.mUriShareList.addAll(this.mUriList);
            String checkSTT = checkSTT(Long.parseLong((String) Objects.requireNonNull(Uri.parse(String.valueOf(this.mUriMemoList.get(0))).getLastPathSegment())));
            if (checkSTT != null) {
                if (DBProvider.getInstance().getContentExistCheckFromFiles(checkSTT) == -1) {
                    new VNMediaScanner(getContext()).startScan(checkSTT);
                } else {
                    uri = DBProvider.getInstance().getContentURIFromFiles(checkSTT);
                }
                this.mUriTextList.add(uri);
            }
            this.mUriShareList.addAll(this.mUriTextList);
            this.mShareIntent.setAction("android.intent.action.SEND_MULTIPLE");
            if (this.mUriList.size() > 0) {
                this.mShareIntent.setType("application/txt, audio/*");
            } else {
                this.mShareIntent.setType("application/txt");
            }
            this.mShareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", this.mUriShareList);
            startSharingFile();
        } else {
            this.textUriTask = new TextUriTask();
            this.textUriTask.execute(new Integer[]{100});
        }
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_share_memo), getResources().getString(C0690R.string.event_share_popup_text_file));
    }

    private void shareVoiceMemo() {
        this.mUriShareList.addAll(this.mUriList);
        this.mUriShareList.addAll(this.mUriMemoList);
        if (this.mUriShareList.size() == 1) {
            this.mShareIntent.setAction("android.intent.action.SEND");
            this.mShareIntent.setType("audio/*");
            this.mShareIntent.putExtra("android.intent.extra.STREAM", (Uri) this.mUriShareList.get(0));
        } else {
            this.mShareIntent.setAction("android.intent.action.SEND_MULTIPLE");
            this.mShareIntent.setType("audio/*");
            this.mShareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", this.mUriShareList);
        }
        SALogProvider.insertSALog(getResources().getString(C0690R.string.screen_popup_share_memo), getResources().getString(C0690R.string.event_share_popup_text_file));
        startSharingFile();
    }

    /* access modifiers changed from: private */
    public static void setShareSurveyLog(int i) {
        if (i == 3 || i == 4) {
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_PLAYER_SHARE, -1);
        } else {
            SurveyLogProvider.insertFeatureLog(SurveyLogProvider.SURVEY_LIST_SHARE, -1);
        }
    }

    public static String checkSTT(long j) {
        String pathById = DBProvider.getInstance().getPathById(j);
        String fileName = DBProvider.getInstance().getFileName(j);
        if (pathById == null) {
            return null;
        }
        String parent = new File(pathById).getParent();
        Log.m19d(TAG, "checkSTT pathOnly : " + parent + " name : " + fileName);
        if (!M4aInfo.isM4A(pathById)) {
            return null;
        }
        synchronized (M4aConsts.FILE_LOCK) {
            M4aInfo readFile = new M4aReader(pathById).readFile();
            if (readFile == null) {
                Log.m19d(TAG, "M4A info is null");
                return null;
            } else if (readFile.hasCustomAtom.get(M4aConsts.STTD).booleanValue()) {
                String str = parent + '/' + fileName + "_memo.txt";
                if (DBProvider.getInstance().getContentExistCheckFromFiles(str) != -1) {
                    return str;
                }
                File file = new File(str);
                ArrayList<TextData> read = new SttHelper(readFile).read();
                if (read == null || !read.isEmpty()) {
                    makeSTTTextFile(file, read);
                    return str;
                }
                Log.m19d(TAG, "STT data is null");
                return null;
            } else {
                Log.m19d(TAG, "File doesn't have STTD");
                return null;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x0080 A[SYNTHETIC, Splitter:B:39:0x0080] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0093 A[SYNTHETIC, Splitter:B:48:0x0093] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00ad A[SYNTHETIC, Splitter:B:54:0x00ad] */
    /* JADX WARNING: Removed duplicated region for block: B:63:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:64:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void makeSTTTextFile(java.io.File r7, java.util.ArrayList<com.sec.android.app.voicenote.common.util.TextData> r8) {
        /*
            java.lang.String r0 = "Exception : "
            java.lang.String r1 = "SelectShareContentsDialog"
            if (r8 == 0) goto L_0x00c5
            boolean r2 = r8.isEmpty()
            if (r2 == 0) goto L_0x000e
            goto L_0x00c5
        L_0x000e:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
            boolean r4 = r7.exists()     // Catch:{ FileNotFoundException -> 0x008b, IOException -> 0x0078 }
            if (r4 == 0) goto L_0x0030
            boolean r4 = r7.delete()     // Catch:{ FileNotFoundException -> 0x008b, IOException -> 0x0078 }
            if (r4 != 0) goto L_0x0025
            java.lang.String r4 = "delete failed"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r4)     // Catch:{ FileNotFoundException -> 0x008b, IOException -> 0x0078 }
        L_0x0025:
            boolean r4 = r7.createNewFile()     // Catch:{ FileNotFoundException -> 0x008b, IOException -> 0x0078 }
            if (r4 != 0) goto L_0x0030
            java.lang.String r4 = "createNewFile failed"
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r4)     // Catch:{ FileNotFoundException -> 0x008b, IOException -> 0x0078 }
        L_0x0030:
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x008b, IOException -> 0x0078 }
            r4.<init>(r7)     // Catch:{ FileNotFoundException -> 0x008b, IOException -> 0x0078 }
            int r7 = r8.size()     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            r3 = 0
            r5 = r3
        L_0x003b:
            if (r5 >= r7) goto L_0x0057
            java.lang.Object r6 = r8.get(r5)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            com.sec.android.app.voicenote.common.util.TextData r6 = (com.sec.android.app.voicenote.common.util.TextData) r6     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            int r6 = r6.dataType     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            if (r6 != 0) goto L_0x0054
            java.lang.Object r6 = r8.get(r5)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            com.sec.android.app.voicenote.common.util.TextData r6 = (com.sec.android.app.voicenote.common.util.TextData) r6     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            java.lang.String[] r6 = r6.mText     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            r6 = r6[r3]     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            r2.append(r6)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
        L_0x0054:
            int r5 = r5 + 1
            goto L_0x003b
        L_0x0057:
            java.lang.String r7 = r2.toString()     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            byte[] r7 = r7.getBytes()     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            r4.write(r7)     // Catch:{ FileNotFoundException -> 0x0073, IOException -> 0x0070, all -> 0x006d }
            r4.close()     // Catch:{ Exception -> 0x0066 }
            goto L_0x00aa
        L_0x0066:
            r7 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            goto L_0x009d
        L_0x006d:
            r7 = move-exception
            r3 = r4
            goto L_0x00ab
        L_0x0070:
            r7 = move-exception
            r3 = r4
            goto L_0x0079
        L_0x0073:
            r7 = move-exception
            r3 = r4
            goto L_0x008c
        L_0x0076:
            r7 = move-exception
            goto L_0x00ab
        L_0x0078:
            r7 = move-exception
        L_0x0079:
            java.lang.String r8 = "IOException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r8, (java.lang.Throwable) r7)     // Catch:{ all -> 0x0076 }
            if (r3 == 0) goto L_0x00aa
            r3.close()     // Catch:{ Exception -> 0x0084 }
            goto L_0x00aa
        L_0x0084:
            r7 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            goto L_0x009d
        L_0x008b:
            r7 = move-exception
        L_0x008c:
            java.lang.String r8 = "FileNotFoundException"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r1, (java.lang.String) r8, (java.lang.Throwable) r7)     // Catch:{ all -> 0x0076 }
            if (r3 == 0) goto L_0x00aa
            r3.close()     // Catch:{ Exception -> 0x0097 }
            goto L_0x00aa
        L_0x0097:
            r7 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
        L_0x009d:
            r8.append(r0)
            r8.append(r7)
            java.lang.String r7 = r8.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r7)
        L_0x00aa:
            return
        L_0x00ab:
            if (r3 == 0) goto L_0x00c4
            r3.close()     // Catch:{ Exception -> 0x00b1 }
            goto L_0x00c4
        L_0x00b1:
            r8 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            r2.append(r8)
            java.lang.String r8 = r2.toString()
            com.sec.android.app.voicenote.provider.Log.m22e(r1, r8)
        L_0x00c4:
            throw r7
        L_0x00c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.dialog.SelectShareContentsDialog.makeSTTTextFile(java.io.File, java.util.ArrayList):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0036 A[SYNTHETIC, Splitter:B:18:0x0036] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String readFile(java.io.File r5) {
        /*
            java.lang.String r0 = "SelectShareContentsDialog"
            r1 = 0
            if (r5 == 0) goto L_0x003e
            boolean r2 = r5.exists()
            if (r2 == 0) goto L_0x003e
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ Exception -> 0x002d }
            r2.<init>(r5)     // Catch:{ Exception -> 0x002d }
            long r3 = r5.length()     // Catch:{ Exception -> 0x002b }
            int r5 = (int) r3     // Catch:{ Exception -> 0x002b }
            byte[] r3 = new byte[r5]     // Catch:{ Exception -> 0x002b }
            int r4 = r2.read(r3)     // Catch:{ Exception -> 0x002b }
            if (r4 == r5) goto L_0x0022
            java.lang.String r5 = "readFile size error"
            com.sec.android.app.voicenote.provider.Log.m19d(r0, r5)     // Catch:{ Exception -> 0x002b }
        L_0x0022:
            r2.close()     // Catch:{ Exception -> 0x002b }
            java.lang.String r5 = new java.lang.String
            r5.<init>(r3)
            return r5
        L_0x002b:
            r5 = move-exception
            goto L_0x002f
        L_0x002d:
            r5 = move-exception
            r2 = r1
        L_0x002f:
            java.lang.String r3 = "Exception"
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r0, (java.lang.String) r3, (java.lang.Throwable) r5)
            if (r2 == 0) goto L_0x003e
            r2.close()     // Catch:{ Exception -> 0x003a }
            goto L_0x003e
        L_0x003a:
            r5 = move-exception
            com.sec.android.app.voicenote.provider.Log.m24e((java.lang.String) r0, (java.lang.String) r3, (java.lang.Throwable) r5)
        L_0x003e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.sec.android.app.voicenote.p007ui.dialog.SelectShareContentsDialog.readFile(java.io.File):java.lang.String");
    }

    private void startSharingFile() {
        if (QuickConnectProvider.getInstance().isInstalledQuickConnect(getActivity())) {
            this.mShareIntent.putExtra(EASY_SHARE_MORE_QUICK_CONNECT, 1);
        }
        this.mShareIntent.addFlags(64);
        try {
            Intent createChooser = Intent.createChooser(this.mShareIntent, this.mActivity.getString(C0690R.string.sharevia), PendingIntent.getBroadcast(this.mActivity, 0, new Intent(this.mActivity, ShareTaskReceiver.class), 134217728).getIntentSender());
            setShareSurveyLog(this.mScene);
            startActivity(createChooser);
        } catch (ActivityNotFoundException e) {
            Log.m24e(TAG, "multipleSend() - activity not found!", (Throwable) e);
        }
        clearData();
        dismiss();
    }

    private void clearData() {
        this.mUriShareList.clear();
        this.mUriList.clear();
        this.mUriTextList.clear();
        this.mUriMemoList.clear();
    }

    private String getTitle(String str) {
        String substring = str.substring(str.lastIndexOf(47) + 1);
        return substring.substring(0, substring.lastIndexOf(46));
    }

    /* renamed from: com.sec.android.app.voicenote.ui.dialog.SelectShareContentsDialog$TextUriTask */
    private class TextUriTask extends AsyncTask<Integer, Integer, Void> {
        private ProgressDialog mProgressDialog;

        private TextUriTask() {
            this.mProgressDialog = null;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            if (SelectShareContentsDialog.this.getActivity() != null) {
                this.mProgressDialog = new ProgressDialog(SelectShareContentsDialog.this.getActivity());
                this.mProgressDialog.setMessage(SelectShareContentsDialog.this.getActivity().getString(C0690R.string.please_wait));
                this.mProgressDialog.setProgressStyle(0);
                this.mProgressDialog.setCancelable(false);
                this.mProgressDialog.getWindow().addFlags(128);
                this.mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public final void onCancel(DialogInterface dialogInterface) {
                        SelectShareContentsDialog.TextUriTask.this.lambda$onPreExecute$0$SelectShareContentsDialog$TextUriTask(dialogInterface);
                    }
                });
                if (SelectShareContentsDialog.this.mUriMemoList.size() >= 15) {
                    this.mProgressDialog.show();
                }
                super.onPreExecute();
            }
        }

        public /* synthetic */ void lambda$onPreExecute$0$SelectShareContentsDialog$TextUriTask(DialogInterface dialogInterface) {
            Log.m29v(SelectShareContentsDialog.TAG, "shareTask : Cancel interrupt!!!");
            this.mProgressDialog.dismiss();
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Integer... numArr) {
            SelectShareContentsDialog.this.mUriShareList.addAll(SelectShareContentsDialog.this.mUriList);
            if (numArr[0].intValue() == 101) {
                SelectShareContentsDialog.this.mUriShareList.addAll(SelectShareContentsDialog.this.mUriMemoList);
            }
            for (int i = 0; i < SelectShareContentsDialog.this.mUriMemoList.size(); i++) {
                String checkSTT = SelectShareContentsDialog.checkSTT(Long.parseLong((String) Objects.requireNonNull(Uri.parse(String.valueOf(SelectShareContentsDialog.this.mUriMemoList.get(i))).getLastPathSegment())));
                if (checkSTT != null) {
                    if (DBProvider.getInstance().getContentExistCheckFromFiles(checkSTT) == -1) {
                        new VNMediaScanner(SelectShareContentsDialog.this.mActivity).startScan(checkSTT);
                    }
                    Uri contentURIFromFiles = DBProvider.getInstance().getContentURIFromFiles(checkSTT);
                    if (contentURIFromFiles == null) {
                        contentURIFromFiles = Uri.fromFile(new File(checkSTT));
                    }
                    SelectShareContentsDialog.this.mUriTextList.add(contentURIFromFiles);
                }
            }
            SelectShareContentsDialog.this.mUriShareList.addAll(SelectShareContentsDialog.this.mUriTextList);
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            if (!SelectShareContentsDialog.this.mActivity.isDestroyed()) {
                ProgressDialog progressDialog = this.mProgressDialog;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    this.mProgressDialog = null;
                }
                SelectShareContentsDialog.this.mShareIntent.setAction("android.intent.action.SEND_MULTIPLE");
                SelectShareContentsDialog.this.mShareIntent.setType("application/txt, audio/*");
                SelectShareContentsDialog.this.mShareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", SelectShareContentsDialog.this.mUriShareList);
                if (QuickConnectProvider.getInstance().isInstalledQuickConnect(SelectShareContentsDialog.this.getActivity())) {
                    SelectShareContentsDialog.this.mShareIntent.putExtra(SelectShareContentsDialog.EASY_SHARE_MORE_QUICK_CONNECT, 1);
                }
                SelectShareContentsDialog.this.mShareIntent.addFlags(64);
                try {
                    Intent createChooser = Intent.createChooser(SelectShareContentsDialog.this.mShareIntent, SelectShareContentsDialog.this.mActivity.getString(C0690R.string.sharevia), PendingIntent.getBroadcast(SelectShareContentsDialog.this.mActivity, 0, new Intent(SelectShareContentsDialog.this.mActivity, ShareTaskReceiver.class), 134217728).getIntentSender());
                    SelectShareContentsDialog.setShareSurveyLog(SelectShareContentsDialog.this.mScene);
                    SelectShareContentsDialog.this.startActivity(createChooser);
                } catch (ActivityNotFoundException e) {
                    Log.m24e(SelectShareContentsDialog.TAG, "multipleSend() - activity not found!", (Throwable) e);
                }
                TextUriTask unused = SelectShareContentsDialog.this.textUriTask = null;
                SelectShareContentsDialog.this.mDialog.dismiss();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        TextUriTask textUriTask2 = this.textUriTask;
        if (textUriTask2 != null) {
            textUriTask2.cancel(true);
            this.textUriTask = null;
        }
        this.mDialog = null;
    }
}
