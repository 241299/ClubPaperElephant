package ru.bibliowiki.litclubbs.util;

/**
 * @author by pf on 12.12.2016.
 */

import android.app.Activity;

/**
 * Данный класс сохраняет текущее состояние в себе, направляет на кэширование, возвращает Document или Activity при необхолимости, направляет на обновление в определенные временные интервалы.
 * Singleton, ининициализация при запуске.
 * На вход:
 * На выход: готовая Activity
 * Хранит: ссылки к настройкам, к кешированию
 *
 */
public class CurrentState {

    public static CurrentState getInstance(){
        return new CurrentState();
    }

    private CurrentState(){

    }

    public void saveCurrentState(){

    }

    public Activity init(){
        return null;
    }
}
