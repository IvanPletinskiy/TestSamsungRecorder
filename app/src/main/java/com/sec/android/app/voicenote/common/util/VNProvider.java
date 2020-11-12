package com.sec.android.app.voicenote.common.util;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.sec.android.app.voicenote.provider.Log;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;

public class VNProvider extends ContentProvider {
    private static final String AUTHORITY = "com.sec.android.app.voicenote.common.util.VNProvider";
    private static final int ColumnCount = 8;
    private static final String FILED_RECORD_MODE = "recording_mode";
    private static final int GLOBAL_SEARCH = 1;
    private static final String QUERY_ORDER_BY = "datetaken DESC";
//    private static final int QUERY_PARSER_VERSION = SemFloatingFeature.getInstance().getInt("SEC_FLOATING_FEATURE_SFINDER_CONFIG_QUERY_PARSER_VERSION", 1);
    private static final int QUERY_PARSER_VERSION = 1;
    private static final int REGEX_SEARCH = 3;
    private static final String[] SUGGEST_COLUMNS = {CategoryRepository.LabelColumn.f102ID, "suggest_text_1", "suggest_text_2", "suggest_text_3", "suggest_uri", "suggest_mime_type", "suggest_intent_extra_data", "suggest_icon_1"};
    private static final String[] SearchTarget = {"title"};
    private static final String TAG = "VNProvider";
    private static final UriMatcher URI_MATCHER = new UriMatcher(-1);
    private String mEndTime = null;
    private String mLimit = null;
    private String mStartTime = null;

    public int delete(@NonNull Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(@NonNull Uri uri) {
        return null;
    }

    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return false;
    }

    public int update(@NonNull Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    static {
        URI_MATCHER.addURI(AUTHORITY, "search_suggest_query", 1);
        URI_MATCHER.addURI(AUTHORITY, "search_suggest_query/*", 1);
        URI_MATCHER.addURI(AUTHORITY, "search_suggest_regex_query", 3);
    }

    private String getLimit() {
        String str = this.mLimit;
        if (str == null || str.isEmpty()) {
            return "";
        }
        return " limit " + this.mLimit;
    }

    private Cursor getResultCursor(Cursor cursor) {
        Log.m26i(TAG, "getResultCursor");
        MatrixCursor matrixCursor = new MatrixCursor(SUGGEST_COLUMNS);
        if (cursor == null) {
            matrixCursor.close();
            return null;
        }
        int columnIndex = cursor.getColumnIndex(CategoryRepository.LabelColumn.f102ID);
        int columnIndex2 = cursor.getColumnIndex("title");
        int columnIndex3 = cursor.getColumnIndex("datetaken");
        int columnIndex4 = cursor.getColumnIndex("date_modified");
        int columnIndex5 = cursor.getColumnIndex("duration");
        int columnIndex6 = cursor.getColumnIndex("mime_type");
        if (columnIndex < 0 || columnIndex2 < 0 || columnIndex4 < 0 || columnIndex5 < 0) {
            Log.m22e(TAG, "getResultCursor - return null");
            matrixCursor.close();
            cursor.close();
            return null;
        }
        ArrayList arrayList = new ArrayList(8);
        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            arrayList.clear();
            arrayList.add(Long.valueOf(cursor.getLong(columnIndex)));
            arrayList.add(cursor.getString(columnIndex2));
            long j = columnIndex3 > 0 ? cursor.getLong(columnIndex3) : 0;
            if (j <= 0) {
                j = 1000 * cursor.getLong(columnIndex4);
            }
            arrayList.add(Long.valueOf(j));
            arrayList.add(stringForTime(cursor.getLong(columnIndex5)));
            arrayList.add(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(columnIndex)));
            arrayList.add(cursor.getString(columnIndex6));
            arrayList.add(Long.valueOf(cursor.getLong(columnIndex)));
            arrayList.add(Long.valueOf(cursor.getLong(columnIndex)));
            matrixCursor.addRow(arrayList);
            cursor.moveToNext();
        }
        cursor.close();
        matrixCursor.moveToFirst();
        return matrixCursor;
    }

    private Cursor getGlobalSearchCursor(String str) {
        String str2 = str;
        if (getContext() == null) {
            Log.m22e(TAG, "getGlobalSearchCursor getContext() return null !!");
            return null;
        }
        StringBuilder sb = new StringBuilder("( ");
        int i = 0;
        while (true) {
            String[] strArr = SearchTarget;
            if (i >= strArr.length) {
                break;
            }
            sb.append(strArr[i]);
            sb.append(" Like ?");
            if (i < SearchTarget.length - 1) {
                sb.append(" ) OR ( ");
            }
            i++;
        }
        sb.append(" )");
        Cursor query = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{CategoryRepository.LabelColumn.f102ID, "title", "datetaken", "date_modified", "duration", "mime_type", FILED_RECORD_MODE}, sb.toString(), new String[]{'%' + str2 + '%', '%' + str2 + '%', '%' + str2 + '%'}, QUERY_ORDER_BY);
        if (query == null) {
            return null;
        }
        query.moveToFirst();
        return getResultCursor(query);
    }

    private Cursor getRexCursor(String str) {
        String[] strArr;
        String[] strArr2;
        String str2;
        String str3 = str;
        if (getContext() == null) {
            Log.m22e(TAG, "getRexCursor getContext() return null !!");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (str3 == null || str.isEmpty()) {
            sb.append(getListQuery());
            strArr = null;
        } else {
            sb.append("( ");
            sb.append(getListQuery());
            sb.append(") AND ( ( ");
            if (QUERY_PARSER_VERSION != 1) {
                strArr2 = str3.split("\n");
            } else if (str3.contains("[")) {
                strArr2 = new VNQueryParser().regexParser(str3);
            } else {
                String[] split = str3.split(" ");
                int length = (split.length * 2) - 1;
                String[] strArr3 = new String[length];
                for (int i = 0; i < length; i++) {
                    if (i % 2 != 0) {
                        strArr3[i] = "AND";
                    } else if (i == 0) {
                        strArr3[0] = split[0];
                    } else {
                        strArr3[i] = split[i / 2];
                    }
                }
                strArr2 = strArr3;
            }
            String[] strArr4 = new String[(SearchTarget.length * ((strArr2.length + 1) / 2))];
            int i2 = 0;
            int i3 = 0;
            while (i2 < SearchTarget.length) {
                int i4 = i3;
                for (int i5 = 0; i5 < strArr2.length; i5++) {
                    if (i5 % 2 == 0) {
                        sb.append(SearchTarget[i2]);
                        sb.append(" Like ?");
                        strArr4[i4] = '%' + strArr2[i5] + '%';
                        i4++;
                    } else {
                        sb.append(' ');
                        if ("&".equals(strArr2[i5])) {
                            str2 = "AND";
                        } else {
                            str2 = strArr2[i5];
                        }
                        sb.append(str2);
                        if (i5 != strArr2.length - 1) {
                            sb.append(' ');
                        }
                    }
                }
                if (i2 < SearchTarget.length - 1) {
                    sb.append(" ) OR ( ");
                }
                i2++;
                i3 = i4;
            }
            sb.append(" ) )");
            strArr = strArr4;
        }
        String str4 = this.mStartTime;
        if (!(str4 == null || this.mEndTime == null)) {
            this.mStartTime = Long.toString(Long.parseLong(str4) / 1000);
            this.mEndTime = Long.toString(Long.parseLong(this.mEndTime) / 1000);
            sb.append(" AND ( ( ");
            sb.append("date_modified");
            sb.append(" >= ");
            sb.append(this.mStartTime);
            sb.append(" )");
            sb.append(" AND ( ");
            sb.append("date_modified");
            sb.append(" <= ");
            sb.append(this.mEndTime);
            sb.append(" ) )");
        }
        Cursor query = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{CategoryRepository.LabelColumn.f102ID, "title", "datetaken", "date_modified", "duration", "mime_type", FILED_RECORD_MODE}, sb.toString(), strArr, QUERY_ORDER_BY + getLimit());
        if (query == null) {
            return null;
        }
        query.moveToFirst();
        return getResultCursor(query);
    }

    public Cursor query(@NonNull Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Log.m29v(TAG, "query uri : " + uri);
        String queryParameter = uri.getQueryParameter("location");
        String queryParameter2 = uri.getQueryParameter("startid");
        this.mStartTime = uri.getQueryParameter("stime");
        this.mEndTime = uri.getQueryParameter("etime");
        this.mLimit = uri.getQueryParameter("limit");
        int match = URI_MATCHER.match(uri);
        if (match == 1) {
            return getGlobalSearchCursor(strArr2[0]);
        }
        if (match == 3) {
            if (strArr2 != null) {
                return getRexCursor(strArr2[0]);
            }
            if (queryParameter != null && !queryParameter.isEmpty()) {
                Log.m29v(TAG, "tag query Location : " + queryParameter);
                return getRexCursor(queryParameter);
            } else if (queryParameter2 == null || queryParameter2.isEmpty()) {
                String str3 = this.mStartTime;
                if (str3 != null && this.mEndTime != null && !str3.isEmpty() && !this.mEndTime.isEmpty()) {
                    Log.m29v(TAG, "startTime : " + this.mStartTime + " endTime : " + this.mEndTime);
                    return getRexCursor((String) null);
                }
            } else {
                Log.m29v(TAG, "tag query id2 : " + queryParameter2);
                return getRexCursor(queryParameter2);
            }
        }
        return null;
    }

    private String stringForTime(long j) {
        long j2 = j / 1000;
        int i = (int) (j2 % 60);
        int i2 = (int) ((j2 / 60) % 60);
        int i3 = (int) (j2 / 3600);
        if (i3 > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i2), Integer.valueOf(i)});
        }
        return String.format(Locale.getDefault(), "%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i)});
    }

    private StringBuilder getListQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("((_data LIKE '%.3ga' and is_music == '0') or ");
        sb.append("_data LIKE '%.amr' or (_data LIKE '%.m4a' and recordingtype == '1'))");
        sb.append(" and (_data NOT LIKE '%/.voice.3ga' and _data NOT LIKE '%/.voice.amr' and _data NOT LIKE '%/.voice.m4a')");
        sb.append(" and (mime_type LIKE 'audio/3gpp' or mime_type LIKE 'audio/amr' or mime_type LIKE 'audio/mp4' or mime_type LIKE 'audio/mpeg')");
        sb.append(" and (_size != '0')");
        return sb;
    }
}
