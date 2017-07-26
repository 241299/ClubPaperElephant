package ru.bibliowiki.litclubbs.util.parser;

import java.util.ArrayList;

/**
 * @author by pf on 20.03.2017.
 */
public class BasicListParser implements Parser {

    static BasicListParser currentParser = new BasicListParser();
    static ArrayList configuration = new ArrayList();

    public static BasicListParser getInstance(){
        return currentParser;
    }

    private BasicListParser(){}

    @Override
    public void parse() {
        // Arguments???
    }

    @Override
    public void pushConfiguration(){

    }
}
