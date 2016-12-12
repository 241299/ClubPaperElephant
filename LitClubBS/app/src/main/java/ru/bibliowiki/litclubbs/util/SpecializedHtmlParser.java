package ru.bibliowiki.litclubbs.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ru.bibliowiki.litclubbs.R;

/**
 * @author by pf on 24.10.2016.
 */
public class SpecializedHtmlParser {

    private static SpecializedHtmlParser shp;
    private Context context;
    private String linksArray[];

    public void setArticle(Document document, Elements elements, View view) {

        String preparedText = prepareText(document, elements);

        Spanned spannedText = Html.fromHtml(preparedText, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String s) {
                final Drawable[] image = new Drawable[1];
                try {
                    ImageLoader.getInstance().loadImage(s, new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            image[0] = new BitmapDrawable(context.getResources(), loadedImage);
                        }
                    });
                } catch (Exception e) {
                    image[0] = context.getResources().getDrawable(R.drawable.ic_empty);
                    RoboErrorReporter.reportError(context, new IOException("Can't download an image at ImageDownloadTask"));
                }
                return image[0];
            }
        }, null);

        Spannable reversedText = revertSpanned(spannedText);
        if (view instanceof TextView) ((TextView) view).setText(reversedText);
        executeExWorks();
    }

    private void executeExWorks() {
    }

//  Подготовка текста
    private String prepareText(Document document, Elements elements) {

        String result = document.getElementsByAttributeValue("title", "Автор").select("a").toString() + elements.get(0).toString().replaceAll("<ul>|</ul>|</li>", "").replaceAll("<li>", "\n\n•");

        Elements links = elements.get(0).getElementsByAttributeValue("class", "value").select("a");

        linksArray = new String[links.size() + 1];

//      Ищем ссылки, вытаскиваем их
        linksArray[0] = document.getElementsByAttributeValue("title", "Автор").select("a").attr("abs:href");
        for (int i = 1; i<=links.size(); i++) {
        linksArray[i] = links.get(i-1).attr("abs:href");
        }
        return result;
    }

    private Spannable revertSpanned(Spanned spannedText) {
        Object[] spans = spannedText.getSpans(0, spannedText.length(), Object.class);
        Spannable ret = Spannable.Factory.getInstance().newSpannable(spannedText.toString());
        if (spans != null && spans.length > 0) {
            final int[] j = {linksArray.length};//Счётчик для ArrayList со ссылками
            ClickableSpan clickableSpan;//Работаем только с объектами ClickableSpan
            for (int i = spans.length - 1; i >= 0; --i) {
                if (spans[i] instanceof ClickableSpan) {
                    final String temp = linksArray[--j[0]];
                    clickableSpan = new ClickableSpan(){
                        @Override
                        public void onClick(View view) {
                            int tempType = RecognizeUrl.recognizeUrl(temp);//Узнать тип назначаемой ссылки //TODO:RELEASE Убрать тип как передаваемый аргумент DownloadTask, вынести определение в подметод DownloadTask
                            if (tempType != DownloadTask.TYPE_UNKNOWN) new DownloadTask(context, temp, tempType, RecognizeUrl.matchSeparatorToType(tempType)).execute(); //Назначение действия
                            else
                                Toast.makeText(context, context.getString(R.string.unknownTypeOfTheLink), Toast.LENGTH_SHORT).show();
                        }
                    };
                    ret.setSpan(clickableSpan, spannedText.getSpanStart(spans[i]), spannedText.getSpanEnd(spans[i]), spannedText.getSpanFlags(spans[i]));
                }
                else
                    ret.setSpan(spans[i], spannedText.getSpanStart(spans[i]), spannedText.getSpanEnd(spans[i]), spannedText.getSpanFlags(spans[i]));
            }
        }

        return ret;
    }

    //  Singleton initialization
    private SpecializedHtmlParser() {
    }

    private SpecializedHtmlParser(Context context) {
        this.context = context;
    }

    public static SpecializedHtmlParser getInstance(Context context) {
        if (shp == null) {
            shp = new SpecializedHtmlParser(context);
        }
        return shp;
    }
}