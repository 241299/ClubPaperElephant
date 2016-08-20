package ru.bibliowiki.litclubbs.lookandfeel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.bibliowiki.litclubbs.util.DownloadTask;
import ru.bibliowiki.litclubbs.util.RoboErrorReporter;

/**
 * @author by pf on 19.08.2016.
 */
public class MenuArrayAdapter extends ArrayAdapter<String>{

    private Context context;
    private Elements elements;
    private int typeOfPage;

    public MenuArrayAdapter(Context context, Elements elements, int typeOfPage){
        super(context, android.R.layout.activity_list_item, android.R.id.icon, new String[elements.size()]);
        this.context = context;
        this.elements = elements;
        this.typeOfPage = typeOfPage;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        String temp0;

        /**
         * Различный текст в зависимости от выбранного типа страницы
         */

        //TODO ДОДЕЛАТЬ
        switch (typeOfPage) {
            case DownloadTask.TYPE_DEFAULT:
                temp0 = elements.get(position).getElementsByAttributeValue("class", "title").text();
                break;
            case DownloadTask.TYPE_PUBLICATIONS:
                temp0 = elements.get(position).getElementsByAttributeValue("class", "value").text();
                break;
            default:
                temp0 = elements.get(position).getElementsByAttributeValue("class", "title").text();
                break;
        }


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(android.R.layout.activity_list_item, parent, false);
        ImageView imageView = (ImageView) row.findViewById(android.R.id.icon);
        TextView textView = (TextView) row.findViewById(android.R.id.text1);
        textView.setText(temp0);

        try {
//                iv = (ImageView) row.findViewById(android.R.id.icon);
            ImageLoader.getInstance().displayImage(elements.get(position).select("img").first().absUrl("src"), imageView);
        } catch (java.lang.NullPointerException e) {
//                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_empty));
            RoboErrorReporter.reportError(context, e.fillInStackTrace());
        }

        return row;
    }

}
