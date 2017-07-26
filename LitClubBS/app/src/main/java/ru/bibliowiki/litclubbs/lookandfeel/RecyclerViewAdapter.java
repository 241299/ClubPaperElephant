package ru.bibliowiki.litclubbs.lookandfeel;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.select.Elements;

import ru.bibliowiki.litclubbs.R;
import ru.bibliowiki.litclubbs.util.DownloadTask;
import ru.bibliowiki.litclubbs.util.RecognizeUrl;
import ru.bibliowiki.litclubbs.util.RoboErrorReporter;

/**
 * @author by pf on 19.08.2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private Elements elements;
    private String values[];
    private int numOfElements;
    private int typeOfPage;

    public RecyclerViewAdapter(Context context, Elements elements, int typeOfPage) {
        this.context = context;
        this.elements = elements;
        this.values = new String[elements.size()];
        this.typeOfPage = typeOfPage;

        /**
         * Различный текст в зависимости от выбранного типа страницы
         */

        Display display = ((AppCompatActivity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        int preferredLength = context.getResources().getInteger(R.integer.default_label_length);
        int realWidth = metricsB.widthPixels;
        preferredLength = (int) Math.ceil(preferredLength * realWidth / 720 );

        numOfElements = (typeOfPage == DownloadTask.TYPE_HOME)?
                context.getResources().getInteger(R.integer.homePageNumOfElements)
                : elements.size();

        for (int i = 0; i < numOfElements; i++) {

            String temp0;

            //TODO ДОДЕЛАТЬ
            switch (typeOfPage) {
                case DownloadTask.TYPE_HOME:
                    temp0 = elements.get(i).getElementsByAttributeValue("class", "title").text();
                    if (temp0.length() > preferredLength) temp0 = temp0.substring(0, preferredLength-3).concat("...");
                    break;
                case DownloadTask.TYPE_PUBLICATIONS:
                    temp0 = elements.get(i).getElementsByAttributeValue("class", "value").text();
                    if (temp0.substring(0, temp0.indexOf("Жанр")).length() > preferredLength) temp0 = temp0.substring(0, preferredLength-3).concat("...");
                    temp0 = temp0.replaceFirst("Жанр:", "\nЖанр: ");
                    break;
                case DownloadTask.TYPE_WRITERS:
                    temp0 = elements.get(i).getElementsByAttributeValue("class", "value").text();
                    if (temp0.length() > preferredLength) temp0 = temp0.substring(0, preferredLength-3).concat("...");
                    break;
                default:
                    temp0 = elements.get(i).getElementsByAttributeValue("class", "title").text();
                    if (temp0.length() > preferredLength) temp0 = temp0.substring(0, preferredLength-3).concat("...");
                    break;
            }

            values[i] = temp0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        //Добавить разделители
        viewHolder.textView.setText(values[viewHolder.getAdapterPosition()]);

        try {
                String url = (typeOfPage == DownloadTask.TYPE_HOME)? elements.get(viewHolder.getAdapterPosition()).select("a").first().attr("style").replaceAll("background-image:url\\('(.*)'\\)", "http://litclubbs.ru$1") : elements.get(viewHolder.getAdapterPosition()).select("img").first().absUrl("src");
                ImageLoader.getInstance().displayImage(url, viewHolder.imageView);
            } catch (java.lang.NullPointerException e) {
                viewHolder.imageView.setImageResource(R.drawable.ic_empty);
            }

        /**
         *  Добавление слушателя на элемент
         */
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String temp0 = elements.get(viewHolder.getAdapterPosition()).getElementsByTag("a").first().attr("abs:href");

                    final int temp1 = RecognizeUrl.recognizeUrl(temp0);
                    String selectedSeparator = RecognizeUrl.matchSeparatorToType(temp1);

                    (new DownloadTask(context, elements.get(viewHolder.getAdapterPosition()).getElementsByTag("a").first().attr("abs:href"), temp1, selectedSeparator)).execute();
                } catch (Exception e) {
                    RoboErrorReporter.reportError(context, e);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return numOfElements;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView textView;
        public CardView cardView;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v;
            imageView = (ImageView)cardView.findViewById(R.id.imageView_recycler);
            textView = (TextView)cardView.findViewById(R.id.textView_recycler);
        }
    }
}
