//package ru.bibliowiki.litclubbs.util;
//
//import android.content.Context;
//import android.graphics.Typeface;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.style.ClickableSpan;
//import android.text.style.StyleSpan;
//import android.view.View;
//import android.widget.Toast;
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * @author by pf on 04.08.2016.
// */
//public class ConvertHTMLToText {
//
//    private static int displacement;
//
//    /**
//     * @param html HTML page in string format
//     * @return spanned text:<p>HTML line endings are converted to CRLF</p><p>Text can be customized by <u>underlining</u>, making it <b>bold</b> or <i>italic</i></p>
//     * @see Spannable
//     */
//
//    public static Spannable convert(final Context context, String html, String args) {
//
//        //Избавляемся от ненужного
//        String plainText = html.
//                replaceAll("target=\"(_blank|_self)\"", ""). //target\s*=\s*".*"(>|\s)(.*(?!<))
//                replaceAll("<p\\s+(\"*=*\"*>)|<br>|<p>", "\n").
//                replaceAll("(</p>)", "").
//                replaceAll("<(div|/div)(.*)>", "").
//                replaceAll("<([a-zA-Z]+)(?!\\s+)>(\\n+|\\s+)?</\\w>", "$2").
//                replaceFirst("^\n", args);
//
//        ArrayList<Span> spansList = new ArrayList<>();
//
//        //Смотрим, по каким типам
//        boolean[] types = prepareSearch(plainText);
//        //Массив {Первая встреча, тип тега}
//        int[] temp;
//        StringBuilder stringBuilder = new StringBuilder(plainText);
//
//        //Массивы, хранящие в себе информацию о начальных и конечных точках
////        int indexesOfStartPoint[] = new int[plainText.length()/3];
////        int indexesOfEndPoint[] = indexesOfStartPoint.clone();
//
//        //Пока метод не вернет -2 (элементы кончились)
//        while ((temp = searchForStyleOccurrences(stringBuilder.toString(), types))[0] !=-2){
//            int indexOfStartingTag, indexOfClosingTag;
//
//            switch (temp[1]){
//                case 0:
//                    stringBuilder.delete(temp[0], temp[0] + 8);
//                    stringBuilder.delete((indexOfClosingTag = stringBuilder.substring(temp[0]).indexOf("</strong>")), indexOfClosingTag + 9);
//                    spansList.add(new Span(new StyleSpan(Typeface.BOLD), temp[0], indexOfClosingTag, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
//                    break;
//                case 1:
//                    stringBuilder.delete(temp[0], stringBuilder.substring(temp[0]).indexOf("</em>"));
//                    break;
//                case 2:
//                    stringBuilder.delete(temp[0], stringBuilder.substring(temp[0]).indexOf("</i>"));
//                    break;
//                case 3:
//                    stringBuilder.delete(temp[0], stringBuilder.substring(temp[0]).indexOf("</b>"));
//                    break;
//            }
//
//        }
//
//        plainText = searchFor(plainText, "<strong>", spansList, new StyleSpan(Typeface.BOLD));
//        plainText = searchFor(plainText, "<b>", spansList, new StyleSpan(Typeface.BOLD));
//        plainText = searchFor(plainText, "<i>", spansList, new StyleSpan(Typeface.ITALIC));
//        plainText = searchFor(plainText, "<em>", spansList, new StyleSpan(Typeface.ITALIC));
//        plainText = searchForLinks(context, plainText, spansList);
//
//        Spannable text = new SpannableString(plainText);
//
//        for (Span span : spansList) {
//            text.setSpan(span.o, span.i, span.i1, span.i2);
//
//        }
//
//        return text;
//    }
//
//    private static String searchFor(String string, String subs, ArrayList<Span> spansList, Object spanType) {
//
//        StringBuilder plainTextSB = new StringBuilder(string);
//
//        while (plainTextSB.indexOf(subs) != -1) {
//            int tmp1 = plainTextSB.indexOf(subs);
//            int tmp2 = plainTextSB.indexOf(subs.replace("<", "</")) != -1 ? plainTextSB.indexOf(subs.replace("<", "</")) : plainTextSB.capacity();
//
//            if (tmp2 != plainTextSB.capacity()) plainTextSB.delete(tmp2, tmp2 + subs.length() + 1);
//            plainTextSB.delete(tmp1, tmp1 + subs.length());
//            //This is <b>Some</b> text with a <u>lot of</u> <em>marks</em>
//
//            if (spanType instanceof StyleSpan)
//                spansList.add(new Span(spanType, tmp1, tmp2 - subs.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
//        }
//
//        return plainTextSB.toString();
//    }
//
//    private static boolean[] prepareSearch(String s){
//        return new boolean[]{s.contains("<strong>"), s.contains("<b>"), s.contains("<em>"), s.contains("<i>")};
//    }
//
//    private static int[] searchForStyleOccurrences(String s, boolean[] types){
//        int temp0 = -2, temp1 = -2, temp2 = -2, temp3 = -2;
//        if (types[0]) temp0 = s.indexOf("<strong>");
//        if (types[0]) temp1 = s.indexOf("<em>");
//        if (types[0]) temp2 = s.indexOf("<i>");
//        if (types[0]) temp3 = s.indexOf("<b>");
//
//        int temp4 = Math.min(Math.min(temp0, temp1), Math.min(temp2, temp3));
//
//        if (temp4 == temp0) return new int[]{temp4, 0};
//            else if (temp4 == temp1) return new int[]{temp4, 1};
//            else if (temp4 == temp2) return new int[]{temp4, 2};
//            else if (temp4 == temp3) return new int[]{temp4, 3};
//
//        return new int[]{-2};
//    }
//
//    private static String searchForLinks(final Context context, String string, ArrayList<Span> spansList) {
//
//        StringBuilder plainTextSB = new StringBuilder(string);
//
//        while (plainTextSB.indexOf("<a ") != -1){
//            int tmp1 = plainTextSB.indexOf("<a ");
//            int tmp2 = plainTextSB.indexOf("</a>");
//            int tmp3 = tmp1<tmp2? plainTextSB.substring(tmp1, tmp2).indexOf(">") + tmp1 : plainTextSB.substring(tmp2, tmp1).indexOf(">") + tmp1;
//            int tmp4 = plainTextSB.indexOf("href");
//            final String tmp = tmp4 < tmp3? (tmp4!=-1? plainTextSB.substring(tmp4, tmp3).replaceAll("href\\s*=\\s*\"(.*)\"\\s-?", "$1") : plainTextSB.substring(tmp4+1, tmp3).replaceAll("href\\s*=\\s*\"(.*)\"\\s-?", "$1")) : plainTextSB.substring(tmp3, tmp4).replaceAll("href\\s*=\\s*\"(.*)\"\\s-?", "$1");
//
//            plainTextSB.delete(tmp2, tmp2+4);
//            plainTextSB.delete(tmp1, tmp3+1);
//
//                spansList.add(new Span(new ClickableSpan() {
//                @Override
//                public void onClick(View view) {
//                    int tempType = RecognizeUrl.recognizeUrl(tmp);
//                    String temp = tmp.matches("href\\s*=\\s*\"/users/\\d+\"")? tmp.replaceAll("href\\s*=\\s*\"/users/(\\d+)\"", "http://litclubbs.ru/users/$1") : tmp;
//                    new DownloadTask(context, temp, tempType, RecognizeUrl.matchSeparatorToType(tempType)).execute();
//                }
//            }, tmp1, tmp1+tmp2-tmp3-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE));
//        }
//
//        return plainTextSB.toString();
//    }
//}
//
//class Span {
//
//    Object o;
//    int i, i1, i2;
//
//    Span(Object o, int i, int i1, int i2) {
//        this.o = o;
//        this.i = i;
//        this.i1 = i1;
//        this.i2 = i2;
//    }
//}