package com.arauta.portfolio.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlUtilsTest {

    // ──────────────────────────────────────────
    // null / blank 輸入
    // ──────────────────────────────────────────

    @Test
    void nl2br_nullInput_returnsEmpty() {
        assertThat(HtmlUtils.nl2br(null)).isEqualTo("");
    }

    @Test
    void nl2br_emptyString_returnsEmpty() {
        assertThat(HtmlUtils.nl2br("")).isEqualTo("");
    }

    @Test
    void nl2br_blankString_returnsEmpty() {
        // StringUtils.hasText(" ") == false → 返回 ""
        assertThat(HtmlUtils.nl2br("   ")).isEqualTo("");
    }

    // ──────────────────────────────────────────
    // 換行轉換
    // ──────────────────────────────────────────

    @Test
    void nl2br_unixNewline_replacedWithBrTag() {
        assertThat(HtmlUtils.nl2br("line1\nline2")).isEqualTo("line1<br>line2");
    }

    @Test
    void nl2br_windowsNewline_replacedWithSingleBrTag() {
        // \r\n 必須整體替換為單一 <br>，不能變成兩個 <br>
        assertThat(HtmlUtils.nl2br("line1\r\nline2")).isEqualTo("line1<br>line2");
    }

    @Test
    void nl2br_oldMacNewline_replacedWithBrTag() {
        assertThat(HtmlUtils.nl2br("line1\rline2")).isEqualTo("line1<br>line2");
    }

    @Test
    void nl2br_multipleNewlines_allReplaced() {
        assertThat(HtmlUtils.nl2br("a\nb\nc")).isEqualTo("a<br>b<br>c");
    }

    @Test
    void nl2br_noNewlines_stringReturnedUnchanged() {
        assertThat(HtmlUtils.nl2br("plain text")).isEqualTo("plain text");
    }

    // ──────────────────────────────────────────
    // HTML Escape（XSS 防護）
    // ──────────────────────────────────────────

    @Test
    void nl2br_ampersand_isEscaped() {
        assertThat(HtmlUtils.nl2br("a & b")).isEqualTo("a &amp; b");
    }

    @Test
    void nl2br_lessThanSign_isEscaped() {
        assertThat(HtmlUtils.nl2br("a < b")).isEqualTo("a &lt; b");
    }

    @Test
    void nl2br_greaterThanSign_isEscaped() {
        assertThat(HtmlUtils.nl2br("a > b")).isEqualTo("a &gt; b");
    }

    @Test
    void nl2br_doubleQuote_isEscaped() {
        assertThat(HtmlUtils.nl2br("say \"hello\"")).isEqualTo("say &quot;hello&quot;");
    }

    @Test
    void nl2br_singleQuote_isEscaped() {
        assertThat(HtmlUtils.nl2br("it's")).isEqualTo("it&#39;s");
    }

    @Test
    void nl2br_scriptTagXssAttempt_isFullyEscaped() {
        String input = "<script>alert('xss')</script>";
        String output = HtmlUtils.nl2br(input);
        assertThat(output).doesNotContain("<script>");
        assertThat(output).doesNotContain("</script>");
        assertThat(output).contains("&lt;script&gt;");
    }

    @Test
    void nl2br_xssWithNewline_escapedAndNewlineConverted() {
        String input = "<b>bold</b>\nnext line";
        String output = HtmlUtils.nl2br(input);
        assertThat(output).isEqualTo("&lt;b&gt;bold&lt;/b&gt;<br>next line");
    }

    @Test
    void nl2br_escapeHappensBeforeNewlineReplacement() {
        // 先 escape 再換行 → & 不會被 <br> 的 < 影響
        String output = HtmlUtils.nl2br("a&b\nc");
        assertThat(output).isEqualTo("a&amp;b<br>c");
    }
}
