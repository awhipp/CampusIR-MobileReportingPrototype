package com.rzepka.hsiao.whipp.campusir;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;


public class LoginActivity extends Activity {

    private EditText id;
    private EditText password;
    private CheckBox remember;
    private Context context;
    SharedPreferences prefs;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().apply();

        new DeserializePreferences().execute(context);

        if(prefs.getBoolean("REMEMBER", false)){
            Intent intent = new Intent(context, MenuActivity.class);
            startActivity(intent);
            finish();
        }

        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        remember = (CheckBox) findViewById(R.id.remember_checkbox);
        Button login = (Button) findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(remember.isChecked()){
                    prefs.edit().putBoolean("REMEMBER", true).apply();
                }

                /* Blank UID and PW for sake of ease of testing */
                if(id.getText().toString().equals("") && password.getText().toString().equals("")){
                    Intent intent = new Intent(context, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(context, "Incorrect UID or Password", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private class DeserializePreferences extends AsyncTask<Context, Void, Boolean> {
        /*
         * Creates a worker thread which downloads the information in the background while the
         * main display shows the SpanishDict logo, and an "animated" loading text
         * */
        protected Boolean doInBackground(Context... context) {
            try{
                if(!prefs.getString("REPORTS","NONE").equals("NONE")) {
                    MyApplication m = ((MyApplication) getApplicationContext());
                    m.reports_array =  deserialize(stringToByte(prefs.getString("REPORTS","NONE")));
                }
            }catch( Exception e ){
                e.printStackTrace();
            }
            return true;
        }

    }

    /*
     * Deserializes the Object from a Byte Array to a WOTD Array for use in the application
     * */
    @SuppressWarnings("unchecked")
    public ArrayList<Report> deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (ArrayList<Report>) is.readObject();
    }

    public byte[] stringToByte(String data){
        return Base64.decode(data, Base64.DEFAULT);
    }

}
