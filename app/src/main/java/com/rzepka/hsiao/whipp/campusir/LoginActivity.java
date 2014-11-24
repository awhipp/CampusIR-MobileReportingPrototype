package com.rzepka.hsiao.whipp.campusir;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity {

    private EditText id;
    private EditText password;
    private CheckBox remember;
    private Context context;
    final private String TAG = "LOGINACTIVITY";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        ActionBar bar = getActionBar();
        bar.setTitle("CampusIR Mobile Reporter -  Please Login");
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a10000")));

        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        remember = (CheckBox) findViewById(R.id.remember_checkbox);
        Button login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(remember.isChecked()){
                    /* Store in SharedPreferences to Skip */
                    Log.d(TAG, "Saved in SharedPreferences");
                }

                if(id.getText().toString().equals("uid") && password.getText().toString().equals("pw")){
                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(context, "Incorrect UID or Password", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

}