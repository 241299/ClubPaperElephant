package ru.bibliowiki.litclubbs.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * @author by pf on 04.08.2016.
 */
public class ConvertHTMLToText {

    /**
     * @param html HTML page in string format
     * @return spanned text:<p>HTML line endings are converted to CRLF</p><p>Text can be customized by <u>underlining</u>, making it <b>bold</b> or <i>italic</i></p>
     * @see Spannable
     */

    public static Spannable convert(final Context context, String html, String args) {

        //Избавляемся от ненужного
        String plainText = html.
                replaceAll("target=\"(_blank|_self)\"", ""). //target\s*=\s*".*"(>|\s)(.*(?!<))
                replaceAll("(<p\\s+.*>(?!.*<))|<br>|<p>", "\n").
                replaceAll("(</p>)", "").
                replaceAll("<(div|/div)(.*)>", "").
                replaceAll("<([a-zA-Z]+)(?!\\s+)>(\\n+|\\s+)?</\\w>", "$2").
                replaceFirst("^\n", args);

        ArrayList<Span> spansList = new ArrayList<>();

        plainText = searchFor(plainText, "<strong>", spansList, new StyleSpan(Typeface.BOLD));
        plainText = searchFor(plainText, "<b>", spansList, new StyleSpan(Typeface.BOLD));
        plainText = searchFor(plainText, "<i>", spansList, new StyleSpan(Typeface.ITALIC));
        plainText = searchFor(plainText, "<em>", spansList, new StyleSpan(Typeface.ITALIC));
        plainText = searchForLinks(context, plainText, spansList);

        Spannable text = new SpannableString(plainText);

        for (Span span : spansList) {
            text.setSpan(span.o, span.i, span.i1, span.i2);

        }

        return text;
    }

    private static String searchFor(String string, String subs, ArrayList<Span> spansList, Object spanType) {

        StringBuilder plainTextSB = new StringBuilder(string);

        while (plainTextSB.indexOf(subs) != -1) {
            int tmp1 = plainTextSB.indexOf(subs);
            int tmp2 = plainTextSB.indexOf(subs.replace("<", "</")) != -1 ? plainTextSB.indexOf(subs.replace("<", "</")) : plainTextSB.capacity();

            if (tmp2 != plainTextSB.capacity()) plainTextSB.delete(tmp2, tmp2 + subs.length() + 1);
            plainTextSB.delete(tmp1, tmp1 + subs.length());
            //This is <b>Some</b> text with a <u>lot of</u> <em>marks</em>

            if (spanType instanceof StyleSpan)
                spansList.add(new Span(spanType, tmp1, tmp2 - subs.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        }

        return plainTextSB.toString();
    }


    private static String searchForLinks(final Context context, String string, ArrayList<Span> spansList) {

        StringBuilder plainTextSB = new StringBuilder(string);

        while (plainTextSB.indexOf("<a ") != -1){
            int tmp1 = plainTextSB.indexOf("<a ");
            int tmp2 = plainTextSB.indexOf("</a>");
            int tmp3 = tmp1<tmp2? plainTextSB.substring(tmp1, tmp2).indexOf(">") + tmp1 : plainTextSB.substring(tmp2, tmp1).indexOf(">") + tmp1;
            int tmp4 = plainTextSB.indexOf("href");
            final String tmp = tmp4 < tmp3? (tmp4!=-1? plainTextSB.substring(tmp4, tmp3).replaceAll("href\\s*=\\s*\"(.*)\"\\s-?", "$1") : plainTextSB.substring(tmp4+1, tmp3).replaceAll("href\\s*=\\s*\"(.*)\"\\s-?", "$1")) : plainTextSB.substring(tmp3, tmp4).replaceAll("href\\s*=\\s*\"(.*)\"\\s-?", "$1");

            plainTextSB.delete(tmp2, tmp2+4);
            plainTextSB.delete(tmp1, tmp3+1);

            spansList.add(new Span(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Does it work?\n" + tmp, Toast.LENGTH_SHORT).show();
                    int tmptype = RecognizeUrl.recognizeUrl(tmp);
                    String temp = tmp.matches("href\\s*=\\s*\"/users/\\d+\"")? tmp.replaceAll("href\\s*=\\s*\"/users/(\\d+)\"", "http://litclubbs.bibliowiki.ru/users/$1") : tmp;
                    new DownloadTask(context, temp, tmptype, RecognizeUrl.matchSeparatorToType(tmptype)).execute();
                }
            }, tmp1, tmp1+tmp2-tmp3-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
        }

        return plainTextSB.toString();
    }
}

class Span {

    Object o;
    int i, i1, i2;

    Span(Object o, int i, int i1, int i2) {
        this.o = o;
        this.i = i;
        this.i1 = i1;
        this.i2 = i2;
    }
}