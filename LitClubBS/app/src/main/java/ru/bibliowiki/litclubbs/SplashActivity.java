package ru.bibliowiki.litclubbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author by pf on 11.12.2016.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
