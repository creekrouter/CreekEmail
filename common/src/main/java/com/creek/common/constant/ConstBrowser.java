package com.creek.common.constant;

import android.os.Build;


public class ConstBrowser {

    public static final String WebView_User_Agent = "[AWV/v1.0;AD/Android]";


    public static String getCustomUserAgent(String oldUserAgentStr) {
        //在原来UA信息基础上继续追加自定义信息
        String userAgentStr = oldUserAgentStr;
        userAgentStr += "$$Webview;" + getWVCoreVersion(oldUserAgentStr);
        userAgentStr += ";Android;" + Build.VERSION.RELEASE;
        userAgentStr += ";" + Build.BRAND + ";" + Build.MODEL + ";";
        return userAgentStr;
    }

    public static String getWVCoreVersion(String uaStr) {
        //Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; H30-C00 Build/HuaweiH30-C00) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30
        //Mozilla/5.0 (Linux; Android 10; MRX-W29 Build/HUAWEIMRX-W29; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/88.0.4324.93 Mobile Safari/537.36
        String version = "";
        if (uaStr == null || uaStr.length() == 0) {
            return version;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //高于4.4内核是chrome
            if (uaStr.contains("Chrome/")) {
                String[] uaStrs = uaStr.split("Chrome/");
                if (uaStrs.length == 2) {
                    version = uaStrs[1].substring(0, uaStrs[1].indexOf(" "));
                }
            }
        } else {
            if (uaStr.contains("AppleWebKit/")) {
                String[] uaStrs = uaStr.split("AppleWebKit/");
                if (uaStrs.length == 2) {
                    version = uaStrs[1].substring(0, uaStrs[1].indexOf(" "));
                }
            }
        }
        return version;
    }
}
