package ru.bibliowiki.litclubbs.lookandfeel;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, Elements elements, int typeOfPage) {
//        super(context, android.R.layout.activity_list_item, android.R.id.icon, new String[elements.size()]);
        this.context = context;
        this.elements = elements;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.values = new String[elements.size()];

        /**
         * Различный текст в зависимости от выбранного типа страницы
         */

        for (int i = 0; i < elements.size(); i++) {

            String temp0;

            //TODO ДОДЕЛАТЬ
            switch (typeOfPage) {
                case DownloadTask.TYPE_DEFAULT:
                    temp0 = elements.get(i).getElementsByAttributeValue("class", "title").text();
                    break;
                case DownloadTask.TYPE_PUBLICATIONS:
                    temp0 = elements.get(i).getElementsByAttributeValue("class", "value").text();
                    break;
                default:
                    temp0 = elements.get(i).getElementsByAttributeValue("class", "title").text();
                    break;
            }
            values[i] = temp0;
        }
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_layout, parent, false);
            viewHolder = new ViewHolder(convertView);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView_recycler);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView_recycler);
            viewHolder.textView.setText(values[position]);

            if (viewHolder.imageView.getDrawable() == null)
                try {
                  ImageLoader.getInstance().displayImage(elements.get(position).select("img").first().absUrl("src"), viewHolder.imageView);
                } catch (java.lang.NullPointerException e) {
                    viewHolder.imageView.setImageResource(R.drawable.ic_empty);
                    RoboErrorReporter.reportError(context, e.fillInStackTrace());
                }

            /**
             *  Добавление слушателя на элемент
             */
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        String temp0 = elements.get(position).getElementsByTag("a").first().attr("abs:href");
                        String selectedSeparator = DownloadTask.SEPARATOR_ARTICLE;

                        final int temp1 = RecognizeUrl.recognizeUrl(temp0);
                        if (temp1 == DownloadTask.TYPE_DUEL_ARTICLE)
                            selectedSeparator = DownloadTask.SEPARATOR_DUEL_ARTICLE;
                        (new DownloadTask(context, elements.get(position).getElementsByTag("a").first().attr("abs:href"), temp1, selectedSeparator)).execute();
                    } catch (Exception e) {
                        RoboErrorReporter.reportError(context, e);
                    }
                }
            });
            convertView.setTag(viewHolder);
        } else viewHolder = (ViewHolder) convertView.getTag();
        return convertView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        viewHolder.textView.setText(values[position]);

        try {
                ImageLoader.getInstance().displayImage(elements.get(position).select("img").first().absUrl("src"), viewHolder.imageView);
            } catch (java.lang.NullPointerException e) {
                viewHolder.imageView.setImageResource(R.drawable.ic_empty);
                RoboErrorReporter.reportError(context, e.fillInStackTrace());
            }

        /**
         *  Добавление слушателя на элемент
         */
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String temp0 = elements.get(position).getElementsByTag("a").first().attr("abs:href");
                    String selectedSeparator = DownloadTask.SEPARATOR_ARTICLE;

                    final int temp1 = RecognizeUrl.recognizeUrl(temp0);
                    if (temp1 == DownloadTask.TYPE_DUEL_ARTICLE)
                        selectedSeparator = DownloadTask.SEPARATOR_DUEL_ARTICLE;
                    (new DownloadTask(context, elements.get(position).getElementsByTag("a").first().attr("abs:href"), temp1, selectedSeparator)).execute();
                } catch (Exception e) {
                    RoboErrorReporter.reportError(context, e);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return elements.size();
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
