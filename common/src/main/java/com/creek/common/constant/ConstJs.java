package com.creek.common.constant;

public class ConstJs {

    private static final String function_resetHtml = "function() {\n" +
            "    document.getElementsByTagName(\"body\")[0].setAttribute(\"style\", \"word-break:break-all;\");\n" +
            "\n" +
            "    let table_arr = document.getElementsByTagName('table');\n" +
            "    for (let i = 0; i < table_arr.length; i++) {\n" +
            "        let table = table_arr[i];\n" +
            "        table.style.maxWidth = '100%';\n" +
            "        table.style.height = 'auto';\n" +
            "    }\n" +
            "\n" +
            "    let img_list = document.getElementsByTagName('img');\n" +
            "    let cid_list = '';\n" +
            "    for (let i = 0; i < img_list.length; i++) {\n" +
            "        let img = img_list[i];\n" +
            "        if (img.src.startsWith('cid:')) {\n" +
            "            img.setAttribute('custom_src', img.src);\n" +
            "            cid_list = cid_list + img.src + ';';\n" +
            "        }\n" +
            "        img.style.maxWidth = '100%';\n" +
            "        img.style.height = 'auto';\n" +
            "        img.onclick = function () {\n" +
            "            window.jsAndroid.openImage(this.src);\n" +
            "        }\n" +
            "    }\n" +
            "    window.jsAndroid.jsBridgeCall(cid_list, document.getElementsByTagName('html')[0].innerHTML, document.getElementsByTagName('body')[0].innerHTML);\n" +
            "}";

    public static final String resetHtml = "javascript:(" + function_resetHtml + ")()";

}
