package ru.bibliowiki.litclubbs.util;

/**
 * @author by pf on 04.08.2016.
 */
public class ConvertHTMLToText {
    public static String convert(String html) {
        return html.
                replaceAll("(<p>|<br>)", "\n").
                replaceAll("(</p>|<a href=\"|</a>)", "").
                replaceAll("<(div|/div)(.*)>", "").
                replaceAll("\" target=\"_blank\">", " ")
                .replaceAll("<([a-zA-Z]+)(?!\\s+)>(\\n+|\\s+)?</\\w>", "$2");
    }
}