package ru.bibliowiki.litclubbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import ru.bibliowiki.litclubbs.util.CurrentState;

/**
 * @author by pf on 12.12.2016.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_settings);
        Button signInBtn = (Button) findViewById(R.id.settings_sign_in_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "Feature is coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        CurrentState.getInstance().saveCurrentState();
        super.onBackPressed();
    }
}
