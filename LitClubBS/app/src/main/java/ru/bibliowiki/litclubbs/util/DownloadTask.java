package ru.bibliowiki.litclubbs.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

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
    public static final int TYPE_DUEL_ARTICLE = 7;
    public static final int TYPE_WRITERS = 8;
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
    private Document doc;


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
            doc = Jsoup.connect(url).get();
            if (doc != null) {
                doc.select("nav").remove(); //Убрать навигацию
                //TODO Вернуть их и активность переделанными позже
                doc.select("div[class*=widget_comments_list").remove(); //Убрать комментарии (Для главной)
                doc.select("div[class*=widget_activity_list").remove(); //Убрать активность
                doc.select("div[class*=widget_profiles_list list").remove(); //Убрать "Авторы приглашают"
                doc.select("ul[class*=menu").remove(); //Убрать пустые ссылки в конце страницы

                e = doc.getElementsByAttributeValue("class", separatorUsed);
//                    e = doc.getElementsByTag("a");
            } else
                RoboErrorReporter.reportError(context, new NullPointerException(context.getString(R.string.documentDownloadFailed)));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //TODO Разделение тасков в разные классы одного интерфейса в non-proto версии
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (e == null) {
            Toast.makeText(context, context.getString(R.string.documentDownloadFailed), Toast.LENGTH_LONG).show();
            RoboErrorReporter.reportError(context, new NullPointerException(context.getString(R.string.documentDownloadFailed)));
            return;
        }

        ((MenuActivity) context).setCurrentPageLoaded(url, typeOfPage, separatorUsed);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        final GridLayout gridLayout = (GridLayout) ((AppCompatActivity) context).findViewById(R.id.gridLayout_menu);

        if (gridLayout != null) {
            gridLayout.removeAllViews();
        }

        if (typeOfPage != TYPE_ARTICLE && typeOfPage != TYPE_NEWS && typeOfPage != TYPE_DUEL_ARTICLE)
            for (int i = 0; i < e.size(); i++) {
                final Element el = e.get(i);

                /**
                 * Различный текст в зависимости от выбранного типа страницы
                 */

                String temp0;

                //TODO ДОДЕЛАТЬ
                switch (typeOfPage) {
                    case TYPE_DEFAULT:
                        temp0 = el.getElementsByAttributeValue("class", "title").text();
                        break;
                    case TYPE_PUBLICATIONS:
                        temp0 = el.getElementsByAttributeValue("class", "value").text();
                        break;
                    default:
                        temp0 = el.getElementsByAttributeValue("class", "title").text();
                        break;
                }

//                    Drawable imgTmp = null;
//                    if (typeOfPage == TYPE_PUBLICATIONS && el.select("img").first() != null) {
//                        imgTmp = ImageDownloadTask.downloadImage(context, el.select("img").first().absUrl("src"), new ImageSize(50, 80));
//                    }

                TextView tv = new TextView(context);
                LinearLayout linearLayout = new LinearLayout(context);

                if (gridLayout != null) {

                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    if (typeOfPage == TYPE_PUBLICATIONS) {
                        ImageView iv = new ImageView(context);
                        iv.setMaxWidth(50);
                        iv.setMaxHeight(50);
                        try {
                            ImageLoader.getInstance().displayImage(el.select("img").first().absUrl("src"), iv);
                        } catch (java.lang.NullPointerException e){
                            iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_empty));
                        }
                        linearLayout.addView(iv);
                    }

                    tv.setText(temp0);
                    linearLayout.addView(tv);
                    linearLayout.setPadding(0, 0, 0, 10);

                    gridLayout.addView(linearLayout);
                }

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            /**
                             * Добавление слушателя на текст
                             */

                            String temp0 = el.getElementsByTag("a").first().attr("abs:href");
                            String selectedSeparator = SEPARATOR_ARTICLE;

                            final int temp1 = RecognizeUrl.recognizeUrl(temp0);
                            if (temp1 == TYPE_DUEL_ARTICLE) selectedSeparator = SEPARATOR_DUEL_ARTICLE;
                            (new DownloadTask(context, el.getElementsByTag("a").first().attr("abs:href"), temp1, selectedSeparator)).execute();
                        } catch (Exception e) {
                            RoboErrorReporter.reportError(context, e);
                        }
                    }
                });
            }
        else {
            TextView text = new TextView(context);
            text.setLinksClickable(true);
            text.setClickable(true);
            text.setAutoLinkMask(Linkify.ALL);
            text.setText(ConvertHTMLToText.convert(context, e.get(0).getElementsByAttributeValue("class", "value").toString(), doc.getElementsByAttributeValue("title", "Автор").select("a").toString()));
            text.setLayoutParams(layoutParams);
            if (gridLayout != null) {
                gridLayout.addView(text);
            }
        }
    }
}