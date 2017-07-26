package ru.bibliowiki.litclubbs.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import ru.bibliowiki.litclubbs.MenuActivity;
import ru.bibliowiki.litclubbs.R;
import ru.bibliowiki.litclubbs.lookandfeel.RecyclerViewAdapter;

/**
 * @author by pf on 05.07.2016.
 */


/**
 * Запускает AsyncTask
 * На вход: ссылка URL или HASH для кеша
 * Выход: Document без преобразований
 *
 */
public class DownloadTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private int typeOfPage;
    private String separatorUsed;
    Elements e;
    String url;

    public final static int TYPE_HOME = 0;
    public final static int TYPE_PUBLICATIONS = 1;
    public static final int TYPE_ARTICLE = 2;
    public static final int TYPE_BLOG = 3;
    public static final int TYPE_NEWS = 4;
    public static final int TYPE_PAINTER = 5;
    public static final int TYPE_USER = 6;
    public static final int TYPE_DUEL_ARTICLE = 7;
    public static final int TYPE_WRITERS = 8;
    //public static final int TYPE_RESERVED2 = 9;
    //public static final int TYPE_RESERVED3 = 10;
    //public static final int TYPE_RESERVED4 = 11;
    public static final int TYPE_UNKNOWN = 12;
    public static final int TYPE_VK = 13;

    public final static String SEPARATOR_DEFAULT = "item";
    public final static String SEPARATOR_PUBLICATIONS = "content_list_item articles_list_item";
    public final static String SEPARATOR_ARTICLE = "field ft_html f_content auto_field";
    public final static String SEPARATOR_DUEL_ARTICLE = "field ft_html f_content2 auto_field";
    public final static String SEPARATOR_WRITERS = "content_list_item writers_list_item";
    public static final String SEPARATOR_NEWS = "field ft_html f_content none_field";


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

                e = doc.getElementsByAttributeValue("class", separatorUsed); // TODO Basic List Parser
                if (typeOfPage == TYPE_PUBLICATIONS) e.select("div.field.ft_text.f_teaser").remove();
//                    e = doc.getElementsByTag("a");
            } else
                RoboErrorReporter.reportError(context, new NullPointerException(context.getString(R.string.documentDownloadFailed))); // TODO Change Errors

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
        } //TODO Change Errors


        ((MenuActivity) context).setCurrentPageLoaded(url, typeOfPage, separatorUsed); // TODO Заменить на CurrentState.changeState()

        final LinearLayout linearLayout = (LinearLayout) ((AppCompatActivity) context).findViewById(R.id.linearLayout_menu);

        if (linearLayout != null) {
            linearLayout.removeAllViews();
        }

        if (typeOfPage != TYPE_ARTICLE && typeOfPage != TYPE_NEWS && typeOfPage != TYPE_DUEL_ARTICLE) { // TODO Заменить на отдельный метод

            // TODO Basic List Parser
            RecyclerView recyclerView = new RecyclerView(context);
            RecyclerViewAdapter arrayAdapter = new RecyclerViewAdapter(context, e, typeOfPage);
            recyclerView.setAdapter(arrayAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setNestedScrollingEnabled(true);

            //Добавляем наш recycler view и прослушку для обновления
            if (linearLayout != null) {
                final SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(context);
                recyclerView.setMinimumWidth(linearLayout.getWidth());
                swipeRefreshLayout.addView(recyclerView);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshItems(swipeRefreshLayout);
                    }
                });
                linearLayout.addView(swipeRefreshLayout);
            }

            // END OF TODO Basic List Parser

        } else {
            // TODO Article Parser
            TextView text = new TextView(context);
            text.setLinksClickable(true);
            text.setClickable(true);
            text.setAutoLinkMask(Linkify.ALL);
            SpecializedHtmlParser.getInstance(context).setArticle(doc, e, text);//TODO:RELEASE Сократить обращения к главному документу, навести порядок в классах Download Task и SpecializedHtmlParser
            if (linearLayout != null) {
                ScrollView scrollView = new ScrollView(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    scrollView.setNestedScrollingEnabled(true);
                }
                scrollView.addView(text);

                linearLayout.addView(scrollView);
            }

            Toolbar toolbar = (Toolbar) ((AppCompatActivity) context).findViewById(R.id.toolbar);
            if (toolbar!=null) toolbar.setTitle(doc.title());

            // TODO Article Parser
        }
    }

    // TODO Basic List Parser
    private void refreshItems(SwipeRefreshLayout swiper){
        MenuActivity link = (MenuActivity)context;
        (new DownloadTask(context, link.getCurrentUrlLoaded(), link.getCurrentTypeLoaded(), link.getCurrentSeparatorLoaded())).execute();
        onItemsLoadComplete(swiper);
    }
    private void onItemsLoadComplete(SwipeRefreshLayout swiper){
        swiper.setRefreshing(false);
    }
    // END OF TODO Basic List Parser
}