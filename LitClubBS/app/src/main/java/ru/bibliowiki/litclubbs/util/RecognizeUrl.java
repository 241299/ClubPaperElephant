package ru.bibliowiki.litclubbs.util;

/**
 * @author by pf on 17.08.2016.
 */
public class RecognizeUrl {
    /**
     * Распознавание типа загружаемой страницы
     * @param url URL
     * @return type of page
     */
    public static int recognizeUrl(String url) {

        int type;

        if (url.matches("(http://)?litclubbs.ru/articles/(.*).html"))
            type = DownloadTask.TYPE_ARTICLE;
        else if (url.matches("(http://)?litclubbs.ru/news/(.*).html"))
            type = DownloadTask.TYPE_NEWS;
        else if (url.matches("(http://)?litclubbs.ru/posts/(.*).html"))
            type = DownloadTask.TYPE_BLOG;
        else if (url.matches("(http://)?litclubbs.ru/painter/(.*).html"))
            type = DownloadTask.TYPE_PAINTER;
        else if (url.matches("(http://)?litclubbs.ru/user/\\d+"))
            type = DownloadTask.TYPE_USER;
        else if (url.matches("(http://)?litclubbs.ru/writers/(.*).html"))
            type = DownloadTask.TYPE_DUEL_ARTICLE;
        else if (url.matches("((http|https)://)?vk.com.*"))
            type = DownloadTask.TYPE_VK;
        else type = DownloadTask.TYPE_UNKNOWN;

        return type;
    }

    public static String matchSeparatorToType(int type){

        String separator = DownloadTask.SEPARATOR_DEFAULT;
        switch (type){
            case DownloadTask.TYPE_ARTICLE: separator = DownloadTask.SEPARATOR_ARTICLE; break;
            case DownloadTask.TYPE_DUEL_ARTICLE: separator = DownloadTask.SEPARATOR_DUEL_ARTICLE; break;
            case DownloadTask.TYPE_PUBLICATIONS: separator = DownloadTask.SEPARATOR_PUBLICATIONS; break;
            case DownloadTask.TYPE_WRITERS: separator = DownloadTask.SEPARATOR_WRITERS; break;
            case DownloadTask.TYPE_NEWS: separator = DownloadTask.SEPARATOR_NEWS; break;
        }

        return separator;
    }
}
