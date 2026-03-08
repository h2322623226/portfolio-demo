package com.arauta.portfolio.util;

import org.springframework.util.StringUtils;

/**
 * HTML 工具方法
 * 使用方式：在 Model 或 Service 呼叫 HtmlUtils.nl2br(text)
 * 再透過 th:utext 輸出（已有 th:utext 就直接套用）
 */
public class HtmlUtils {

    private HtmlUtils() {}

    /**
     * 將換行符號（\n、\r\n、\r）轉成 HTML <br> 標籤
     * 同時對原始文字做 HTML escape，防止 XSS
     *
     * 範例：
     *   輸入："第一行\n第二行"
     *   輸出："第一行<br>第二行"
     */
    public static String nl2br(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        // 先 escape HTML 特殊字元，再把換行轉成 <br>
        String escaped = text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

        // 處理 \r\n（Windows）、\r（舊 Mac）、\n（Unix）
        return escaped
            .replace("\r\n", "<br>")
            .replace("\r", "<br>")
            .replace("\n", "<br>");
    }
}
