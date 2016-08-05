package ru.bibliowiki.litclubbs.util;

/**
 * @author by pf on 04.08.2016.
 */
public class ConvertHTMLToText {
    public static String convert(String html) {
        return html.
                replaceAll("<p>", "\n").
                replaceAll("</p>", "").
                replaceAll("<div>", "").
                replaceAll("</div>", "").
                replaceAll("</a>", "").
                replaceAll("<a href=\"", "").
                replaceAll("\" target=\"_blank\">", " ").
                replaceAll("<br>", "\n");
    }
}
