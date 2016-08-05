package ru.bibliowiki.litclubbs.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import ru.bibliowiki.litclubbs.MenuActivity;
import ru.bibliowiki.litclubbs.R;

/**
 * @author by pf on 05.07.2016.
 */
public class DownloadTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private int typeOfPage;
    private String separatorUsed;
    Button[] btn;
    Elements e;
    String url;

    public final static int TYPE_DEFAULT = 0;
    public final static int TYPE_PUBLICATIONS = 1;
    public static final int TYPE_ARTICLE = 2;
    public static final int TYPE_BLOG = 3;
    public static final int TYPE_NEWS = 4;
    public static final int TYPE_PAINTER = 5;
    public static final int TYPE_USER = 6;
    public static final int TYPE_RESERVED0 = 7;
    public static final int TYPE_RESERVED1 = 8;
    public static final int TYPE_RESERVED2 = 9;
    public static final int TYPE_RESERVED3 = 10;
    public static final int TYPE_RESERVED4 = 11;
    public static final int TYPE_RESERVED5 = 12;
    public static final int TYPE_RESERVED6 = 13;

    public final static String SEPARATOR_DEFAULT = "item";
    public final static String SEPARATOR_PUBLICATIONS = "content_list_item articles_list_item";
    public final static String SEPARATOR_ARTICLE = "field ft_html f_content";
    public final static String SEPARATOR_DUEL_ARTICLE = "field ft_html f_content2";
    public final static String SEPARATOR_WRITERS = "content_list_item writers_list_item";


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * @param context used to operate with app's context
     * @param url     URL to download
     */
    public DownloadTask(Context context, String url, int typeOfPage, String separatorUsed) {
        this.context = context;
        this.url = url;
        this.typeOfPage = typeOfPage;
        this.separatorUsed = separatorUsed;
    }


//    TODO understand it
//   <div class="field ft_html f_content">
//    <div class="title_top">Текст: </div>
//    <div class="value"><p><strong>        </strong></p>  <p><strong></strong></p>  <p><strong></strong></p>  <p>Пахнет чистая трава,</p>  <p>И дорогу скрыла тьма.</p>  <p>Ты бояться не спеши,</p>  <p>Распахни амбар души!</p>    <p><br></p><p>Всё кузнечики трещат,</p>  <p>Звезды яркие не спят.</p>  <p>Слышишь сердца сильный стук?</p>  <p>Я тебе не просто друг.</p>    <p><br></p><p>Лишь березы видят нас,</p>  <p>Как прохладно в этот час!</p>  <p>Может, будет так всегда?</p>  <p>Ты скажи мне слово «да»!</p></div>
//    </div>


    @Override
    protected Void doInBackground(Void... params) {
        try {
            Document doc = Jsoup.connect(url).get();
            if (doc != null) {
                doc.select("nav").remove();
                e = doc.getElementsByAttributeValue("class", separatorUsed);
//                    e = doc.getElementsByTag("a");
            } else
                RoboErrorReporter.reportError(context, new NullPointerException(context.getString(R.string.documentDownloadFailed)));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (e == null) {
            Toast.makeText(context, context.getString(R.string.documentDownloadFailed), Toast.LENGTH_LONG).show();
            RoboErrorReporter.reportError(context, new NullPointerException(context.getString(R.string.documentDownloadFailed)));
            return;
        }

        //TODO дописать код анимации для кнопки обновления
        ((MenuActivity) context).setCurrentPageLoaded(url, typeOfPage, separatorUsed);

        int tmp = 0;
        btn = new Button[e.size()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout linearLayout = (LinearLayout) ((AppCompatActivity) context).findViewById(R.id.linearLayout_menu);
        if (linearLayout != null) {
            linearLayout.removeAllViews();
        }

        if (typeOfPage != TYPE_ARTICLE && typeOfPage != TYPE_NEWS)
            for (int i = 0; i < e.size(); i++) {
                final Element el = e.get(i);
                if (el != null && el.text().length() > 1) {
                    btn[tmp] = new Button(context);
                    btn[tmp].setLayoutParams(layoutParams);

                    if (linearLayout != null && btn[tmp] != null) {
                        try {
                            ((LinearLayout.LayoutParams) btn[tmp].getLayoutParams()).setMargins(0, 0, 0, 5);
                            btn[tmp].requestLayout();
                            linearLayout.addView(btn[tmp]);
                        } catch (Exception e) {
                            Log.w("Error", e.getMessage());
                            Toast.makeText(context, context.getString(R.string.documentDownloadFailed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    /**
                     * Различный текст в зависимости от выбранного типа страницы
                     */

                    String temp0 = "Smtxt";

                    //TODO ДОДЕЛАТЬ
                    switch (typeOfPage){
                        case TYPE_DEFAULT: temp0 = el.getElementsByAttributeValue("class", "title").text(); break;
                        case TYPE_PUBLICATIONS: temp0 = el.getElementsByAttributeValue("class", "value").text(); break;
                        default: temp0 = el.getElementsByAttributeValue("class", "title").text(); break;
                    }

                    if (!temp0.matches("(\\s+)")) btn[tmp].setText(temp0);

                    Drawable imgTmp = null;
                    if (typeOfPage == TYPE_PUBLICATIONS && el.select("img").first() != null) {
                        imgTmp = ImageDownloadTask.downloadImage(context, el.select("img").first().absUrl("src"), new ImageSize(50, 80));
                    }

                    btn[tmp].setGravity(Gravity.CENTER_HORIZONTAL);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (imgTmp != null)
                            btn[tmp].setCompoundDrawablesWithIntrinsicBounds(imgTmp, null, null, null);
                        btn[tmp].setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ic_bg));
                    } else
                        RoboErrorReporter.reportError(context, new NullPointerException("No image found"));
                    if (((AppCompatActivity) context).findViewById(R.id.scrollView) != null)
                        btn[tmp].setWidth(((AppCompatActivity) context).findViewById(R.id.scrollView).getWidth());
                    else btn[tmp].setWidth(100);





                    btn[tmp].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                /**
                                * Добавление слушателя на кнопку (заодно распознавание типа загружаемой страницы)
                                */

                                final int temp1;
                                String temp0 = el.getElementsByTag("a").first().attr("abs:href");
                                String selectedSeparator = SEPARATOR_ARTICLE;

                                if (temp0.matches("(http://)?litclubbs.bibliowiki.ru/articles/(.*).html")) temp1 = TYPE_ARTICLE;
                                else if (temp0.matches("(http://)?litclubbs.bibliowiki.ru/news/(.*).html")) temp1 = TYPE_NEWS;
                                else if (temp0.matches("(http://)?litclubbs.bibliowiki.ru/posts/(.*).html")) temp1 = TYPE_BLOG;
                                else if (temp0.matches("(http://)?litclubbs.bibliowiki.ru/painter/(.*).html")) temp1 = TYPE_PAINTER;
                                else if (temp0.matches("(http://)?litclubbs.bibliowiki.ru/user/\\d(\\d)?(\\d)")) temp1 = TYPE_USER;
                                else if (temp0.matches("(http://)?litclubbs.bibliowiki.ru/writers/(.*).html")) { temp1 = TYPE_ARTICLE; selectedSeparator = SEPARATOR_DUEL_ARTICLE;}
                                else temp1 = TYPE_DEFAULT; //TODO СРОЧНО ПОПРАВИТЬ!
                                (new DownloadTask(context, el.getElementsByTag("a").first().attr("abs:href"), temp1, selectedSeparator)).execute();
                            } catch (Exception e) {
                                RoboErrorReporter.reportError(context, e);
                            }
                        }
                    });
                    tmp++;
                }
            }
        else {
            TextView text = new TextView(context);
            text.setLinksClickable(true);
            text.setAutoLinkMask(Linkify.ALL);
            text.setText(ConvertHTMLToText.convert(e.get(0).getElementsByAttributeValue("class", "value").toString()));
            text.setLayoutParams(layoutParams);
            if (linearLayout != null) {
                linearLayout.addView(text);
            }
        }
    }
}