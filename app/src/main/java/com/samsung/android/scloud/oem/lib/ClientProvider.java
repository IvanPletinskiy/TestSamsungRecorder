package com.samsung.android.scloud.oem.lib;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import com.samsung.android.scloud.oem.lib.backup.IBackupClient;
import com.samsung.android.scloud.oem.lib.backup.ReuseDBHelper;
import com.samsung.android.scloud.oem.lib.backup.file.FileClientManager;
import com.samsung.android.scloud.oem.lib.backup.record.RecordClientManager;
import com.samsung.android.scloud.oem.lib.bnr.BNRClientHelper;
import com.samsung.android.scloud.oem.lib.bnr.ISCloudBNRClient;
import com.samsung.android.scloud.oem.lib.common.IClientHelper;
import com.samsung.android.scloud.oem.lib.qbnr.ISCloudQBNRClient;
import com.samsung.android.scloud.oem.lib.qbnr.QBNRClientHelper;
import com.samsung.android.scloud.oem.lib.sync.file.FileSyncManager;
import com.samsung.android.scloud.oem.lib.sync.file.IFileSyncClient;
import com.samsung.android.scloud.oem.lib.sync.record.IRecordSyncClient;
import com.samsung.android.scloud.oem.lib.sync.record.RecordSyncManager;
import com.sec.android.app.voicenote.common.util.DeviceInfo;
import com.sec.android.app.voicenote.p007ui.dialog.DialogFactory;

import org.mp4parser.boxes.iso14496.part15.SyncSampleEntry;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClientProvider extends ContentProvider {
    /* access modifiers changed from: private */
    public static final Map<String, IClientHelper> CLIENT_MAP = new HashMap();
    private static final Object LOCK = new Object();
    private static final Map<String, Register> REGISTER_MAP = new HashMap();
    /* access modifiers changed from: private */
    public static final String TAG = "ClientProvider";
    private Context context;

    private interface Register {
        void execute(Context context, XmlResourceParser xmlResourceParser);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public boolean onCreate() {
        this.context = getContext();
        init();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void init() {
        REGISTER_MAP.put("ISCloudBNRClient", new Register() {
            public void execute(Context context, XmlResourceParser xmlResourceParser) {
                try {
                    String attributeValue = xmlResourceParser.getAttributeValue((String) null, DialogFactory.BUNDLE_NAME);
                    String attributeValue2 = xmlResourceParser.getAttributeValue((String) null, "contents_id");
                    String attributeValue3 = xmlResourceParser.getAttributeValue((String) null, "client_impl_class");
                    String attributeValue4 = xmlResourceParser.getAttributeValue((String) null, "category");
                    String access$000 = ClientProvider.TAG;
                    LOG.m12d(access$000, "register - xml5 : " + attributeValue + ", " + attributeValue2 + ", " + attributeValue3 + ", " + attributeValue4);
                    try {
                        if (DeviceInfo.STR_TRUE.equals(xmlResourceParser.getAttributeValue((String) null, "quick_backup"))) {
                            String access$0002 = ClientProvider.TAG;
                            LOG.m15i(access$0002, "register - xml6 quick_backup : " + attributeValue + ", " + attributeValue2 + ", " + attributeValue3);
                            ClientProvider.CLIENT_MAP.put(attributeValue, new QBNRClientHelper((ISCloudQBNRClient) Class.forName(attributeValue3).newInstance()));
                            return;
                        }
                        String access$0003 = ClientProvider.TAG;
                        LOG.m15i(access$0003, "register - xml6 : " + attributeValue + ", " + attributeValue2 + ", " + attributeValue3);
                        ClientProvider.CLIENT_MAP.put(attributeValue, new BNRClientHelper((ISCloudBNRClient) Class.forName(attributeValue3).newInstance()));
                    } catch (ClassCastException e) {
                        LOG.m14e(ClientProvider.TAG, "failed cast to BNRClient~!! ", e);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e2) {
                    e2.printStackTrace();
                }
            }
        });
        REGISTER_MAP.put("ISCloudQBNRClient", new Register() {
            public void execute(Context context, XmlResourceParser xmlResourceParser) {
                try {
                    String attributeValue = xmlResourceParser.getAttributeValue((String) null, DialogFactory.BUNDLE_NAME);
                    String attributeValue2 = xmlResourceParser.getAttributeValue((String) null, "contents_id");
                    String attributeValue3 = xmlResourceParser.getAttributeValue((String) null, "client_impl_class");
                    String attributeValue4 = xmlResourceParser.getAttributeValue((String) null, "category");
                    String access$000 = ClientProvider.TAG;
                    LOG.m12d(access$000, "register - xml5 : " + attributeValue + ", " + attributeValue2 + ", " + attributeValue3 + ", " + attributeValue4);
                    try {
                        String access$0002 = ClientProvider.TAG;
                        LOG.m15i(access$0002, "register - xml6 quick_backup : " + attributeValue + ", " + attributeValue2 + ", " + attributeValue3);
                        ClientProvider.CLIENT_MAP.put(attributeValue, new QBNRClientHelper((ISCloudQBNRClient) Class.forName(attributeValue3).newInstance()));
                    } catch (ClassCastException e) {
                        LOG.m14e(ClientProvider.TAG, "failed cast to BNRClient~!! ", e);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e2) {
                    e2.printStackTrace();
                }
            }
        });
        REGISTER_MAP.put("IRecordClient", new Register() {
            public void execute(Context context, XmlResourceParser xmlResourceParser) {
                try {
                    String attributeValue = xmlResourceParser.getAttributeValue((String) null, DialogFactory.BUNDLE_NAME);
                    String attributeValue2 = xmlResourceParser.getAttributeValue((String) null, "client_impl_class");
                    String attributeValue3 = xmlResourceParser.getAttributeValue((String) null, "client_data_directory");
                    String access$000 = ClientProvider.TAG;
                    LOG.m15i(access$000, "register - xml5 : " + attributeValue + ", " + attributeValue2 + ", clientDataDirectory : " + attributeValue3);
                    try {
                        RecordClientManager recordClientManager = new RecordClientManager((IBackupClient) Class.forName(attributeValue2).newInstance());
                        recordClientManager.setDataDirectory(attributeValue3);
                        Map access$100 = ClientProvider.CLIENT_MAP;
                        access$100.put("record_" + attributeValue, recordClientManager);
                    } catch (ClassCastException e) {
                        LOG.m14e(ClientProvider.TAG, "failed cast to BNRClient~!! ", e);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e2) {
                    e2.printStackTrace();
                }
            }
        });
        REGISTER_MAP.put("IFileClient", new Register() {
            public void execute(Context context, XmlResourceParser xmlResourceParser) {
                try {
                    String attributeValue = xmlResourceParser.getAttributeValue((String) null, DialogFactory.BUNDLE_NAME);
                    String attributeValue2 = xmlResourceParser.getAttributeValue((String) null, "client_impl_class");
                    String access$000 = ClientProvider.TAG;
                    LOG.m15i(access$000, "register - xml5 : " + attributeValue + ", " + attributeValue2);
                    try {
                        String access$0002 = ClientProvider.TAG;
                        LOG.m15i(access$0002, "register - xml7 has_file : " + attributeValue + ", " + attributeValue2);
                        FileClientManager fileClientManager = new FileClientManager((IBackupClient) Class.forName(attributeValue2).newInstance());
                        Map access$100 = ClientProvider.CLIENT_MAP;
                        access$100.put("file_" + attributeValue, fileClientManager);
                    } catch (ClassCastException e) {
                        LOG.m14e(ClientProvider.TAG, "failed cast to BNRClient~!! ", e);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e2) {
                    e2.printStackTrace();
                }
            }
        });
        REGISTER_MAP.put("IRecordSyncClient", new Register() {
            public void execute(Context context, XmlResourceParser xmlResourceParser) {
                try {
                    String attributeValue = xmlResourceParser.getAttributeValue((String) null, DialogFactory.BUNDLE_NAME);
                    String attributeValue2 = xmlResourceParser.getAttributeValue((String) null, "client_impl_class");
                    try {
                        String access$000 = ClientProvider.TAG;
                        LOG.m12d(access$000, "register - xml5 : " + attributeValue + ", v :" + attributeValue2);
                        ClientProvider.CLIENT_MAP.put(attributeValue, new RecordSyncManager((IRecordSyncClient) Class.forName(attributeValue2).newInstance()));
                    } catch (ClassCastException e) {
                        LOG.m14e(ClientProvider.TAG, "failed cast to SyncClient ", e);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e2) {
                    e2.printStackTrace();
                }
            }
        });
        REGISTER_MAP.put("IFileSyncClient", new Register() {
            public void execute(Context context, XmlResourceParser xmlResourceParser) {
                try {
                    String attributeValue = xmlResourceParser.getAttributeValue((String) null, DialogFactory.BUNDLE_NAME);
                    String attributeValue2 = xmlResourceParser.getAttributeValue((String) null, "client_impl_class");
                    try {
                        String access$000 = ClientProvider.TAG;
                        LOG.m12d(access$000, "register - xml5 : " + attributeValue + ", v :" + attributeValue2);
                        FileSyncManager fileSyncManager = new FileSyncManager((IFileSyncClient) Class.forName(attributeValue2).newInstance());
                        Map access$100 = ClientProvider.CLIENT_MAP;
                        access$100.put("sync_" + attributeValue, fileSyncManager);
                    } catch (ClassCastException e) {
                        LOG.m14e(ClientProvider.TAG, "failed cast to SyncClient ", e);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String queryParameter = uri.getQueryParameter("command");
        if (TextUtils.isEmpty(queryParameter)) {
            LOG.m15i(TAG, String.format(Locale.US, "query but command is null skip!! [%s]", new Object[]{uri}));
            return null;
        }
        char c = 65535;
        if (queryParameter.hashCode() == 225952122 && queryParameter.equals("checkAndUpdateReuseDB")) {
            c = 0;
        }
        if (c != 0) {
            return null;
        }
        CLIENT_MAP.get(str).handleRequest(this.context, queryParameter, str, (Bundle) null);
        return ReuseDBHelper.getInstance(this.context).query((String[]) null, "sourcekey = ?", new String[]{str}, (String) null, (String) null, (String) null);
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        String str3 = TAG;
        LOG.m15i(str3, "call: version: 2.2.11, method: " + str + ", arg: " + str2);
        try {
            synchronized (LOCK) {
                if (CLIENT_MAP.get(str2) == null) {
                    register(this.context);
                }
            }
            return CLIENT_MAP.get(str2).handleRequest(this.context, str, str2, bundle);
        } catch (Exception e) {
            LOG.m14e(TAG, "Exception err~!!", e);
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("exception", e);
            return bundle2;
        }
    }

    public ParcelFileDescriptor openFile(Uri uri, String str) {
        LOG.m15i(TAG, "openFile: mode: " + str);
        Uri.Builder buildUpon = uri.buildUpon();
        String path = uri.getPath();
        if (!(buildUpon == null || buildUpon.build().getQueryParameter("encode") == null || !buildUpon.build().getQueryParameter("encode").equals(DialogFactory.BUNDLE_PATH))) {
            path = buildUpon.build().getEncodedPath();
        }
        LOG.m12d(TAG, "openFile: uri: " + path);
        String[] split = path.split("/");
        String str2 = split[split.length - 1];
        if (path.lastIndexOf("/") < 1) {
            path = this.context.getFilesDir() + path;
        }
        File file = new File(path);
        if (str == null || !str.equals("restore")) {
            if (TextUtils.isEmpty(str2)) {
                throw new UnsupportedOperationException();
            }
        } else if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        LOG.m12d(TAG, "openFile: real path: " + path + ", fileExist: " + file.exists());
        int i = 939524096;
//        if (str != null) {
//            try {
//                if (str.equals("backup") || str.equals(SyncSampleEntry.TYPE)) {
//                    i = 805306368;
//                }
//            } catch (FileNotFoundException e) {
//                LOG.m14e(TAG, "openFile: Unable to open file: " + path, e);
//                return null;
//            }
//        }
//        return ParcelFileDescriptor.open(new File(path), i);
        if (str != null) {
            try {
                if (str.equals("backup") || str.equals(SyncSampleEntry.TYPE)) {
                    i = 805306368;
                }
                return ParcelFileDescriptor.open(new File(path), i);
            } catch (FileNotFoundException e) {
                LOG.m14e(TAG, "openFile: Unable to open file: " + path, e);
                return null;
            }
        } else {
            return null;
        }
    }

    private void register(Context context2) {
        Context context3 = context2;
        try {
            LOG.m15i(TAG, "register");
            ApplicationInfo applicationInfo = context2.getPackageManager().getApplicationInfo(context2.getPackageName(), 128);
            if (applicationInfo != null) {
                Bundle bundle = applicationInfo.metaData;
                if (bundle != null) {
                    String str = null;
                    int i = 2;
                    if (bundle.containsKey("backup_name")) {
                        if (bundle.containsKey("backup_content_uri")) {
                            XmlResourceParser xml = context2.getResources().getXml(bundle.getInt("backup_name"));
                            try {
                                String str2 = TAG;
                                LOG.m12d(str2, "register - xml1 : " + xml.getName());
                                xml.next();
                                String str3 = TAG;
                                LOG.m12d(str3, "register - xml2 : " + xml.getName());
                                xml.next();
                                String str4 = TAG;
                                LOG.m12d(str4, "register - xml3 : " + xml.getName());
                                if (xml.getName().equals("backup_items")) {
                                    while (true) {
                                        if (xml.next() == 3 && xml.getName().equals("backup_items")) {
                                            break;
                                        }
                                        String str5 = TAG;
                                        LOG.m12d(str5, "register - xml4 : " + xml.getName());
                                        if (xml.getName().equals("backup_item") && xml.getEventType() == i) {
                                            String attributeValue = xml.getAttributeValue(str, "interface");
                                            if (attributeValue == null) {
                                                try {
                                                    REGISTER_MAP.get("ISCloudBNRClient").execute(context3, xml);
                                                } catch (Exception e) {
                                                    String str6 = TAG;
                                                    LOG.m14e(str6, "backup interfaceName is incorrect, " + attributeValue, e);
                                                }
                                            } else {
                                                REGISTER_MAP.get(attributeValue).execute(context3, xml);
                                            }
                                            str = null;
                                            i = 2;
                                        }
                                    }
                                }
                            } catch (IOException | XmlPullParserException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                    if (bundle.containsKey("scloud_support_authority")) {
                        XmlResourceParser openXmlResourceParser = context2.getResources().getAssets().openXmlResourceParser("res/xml/sync_item.xml");
                        String str7 = TAG;
                        LOG.m12d(str7, "register - xml1 : " + openXmlResourceParser.getName());
                        openXmlResourceParser.next();
                        String str8 = TAG;
                        LOG.m12d(str8, "register - xml2 : " + openXmlResourceParser.getName());
                        openXmlResourceParser.next();
                        String str9 = TAG;
                        LOG.m12d(str9, "register - xml3 : " + openXmlResourceParser.getName());
                        if (openXmlResourceParser.getName().equals("sync_items")) {
                            while (true) {
                                if (openXmlResourceParser.next() != 3 || !openXmlResourceParser.getName().equals("sync_items")) {
                                    String str10 = TAG;
                                    LOG.m12d(str10, "register - xml4 : " + openXmlResourceParser.getName());
                                    if (openXmlResourceParser.getName().equals("sync_item") && openXmlResourceParser.getEventType() == 2) {
                                        String attributeValue2 = openXmlResourceParser.getAttributeValue((String) null, "interface");
                                        if (attributeValue2 == null) {
                                            try {
                                                REGISTER_MAP.get("IFileSyncClient").execute(context3, openXmlResourceParser);
                                            } catch (Exception e3) {
                                                String str11 = TAG;
                                                LOG.m14e(str11, "sync interfaceName is incorrect, " + attributeValue2, e3);
                                            }
                                        } else {
                                            REGISTER_MAP.get(attributeValue2).execute(context3, openXmlResourceParser);
                                        }
                                    }
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                } else {
                    LOG.m15i(TAG, "<meta> tag is empty");
                    throw new Exception("failed to get <meta> tag in Manifest.xml");
                }
            } else {
                LOG.m15i(TAG, "failed to get ApplicationInfo with meta-data");
                throw new Exception("failed to get ApplicationInfo with meta-data");
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }
}
