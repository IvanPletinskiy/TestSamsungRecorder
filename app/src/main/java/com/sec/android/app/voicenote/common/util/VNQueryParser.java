package com.sec.android.app.voicenote.common.util;

import com.sec.android.app.voicenote.provider.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VNQueryParser {
    private static final String AND = "AND";

    /* renamed from: OR */
    private static final String f103OR = "OR";
    private static final String QUERY_AND = "&";
    private static final String QUERY_OR = "|";
    private static final String REGEX_BLOCK_START_END = "\\[([^\\[]+)\\]";
    private static final String REGEX_START_END = "^\\[|\\]$";
    List<String> resultList = new ArrayList();

    public String[] regexParser(String str) {
        Matcher matcher = Pattern.compile(REGEX_BLOCK_START_END).matcher(str);
        while (matcher.find()) {
            String group = matcher.group();
            Log.m19d("SearchQuery", "regexParser b : " + group);
            String replaceAll = group.replaceAll(REGEX_START_END, "");
            char c = 65535;
            int hashCode = replaceAll.hashCode();
            if (hashCode != 38) {
                if (hashCode == 124 && replaceAll.equals(QUERY_OR)) {
                    c = 1;
                }
            } else if (replaceAll.equals(QUERY_AND)) {
                c = 0;
            }
            if (c == 0) {
                this.resultList.add(AND);
            } else if (c != 1) {
                this.resultList.add(replaceAll);
            } else {
                this.resultList.add(f103OR);
            }
            Log.m19d("SearchQuery", "regexParser : " + replaceAll);
        }
        List<String> list = this.resultList;
        return (String[]) list.toArray(new String[list.size()]);
    }
}
