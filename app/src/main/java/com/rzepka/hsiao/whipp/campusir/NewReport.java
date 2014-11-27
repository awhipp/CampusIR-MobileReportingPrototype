package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class NewReport extends Activity {

    ImageView imageView;
    private Uri imageUri;
    Spinner building_spinner;
    Spinner issue_spinner;
    Bitmap bitmap;
    Context context;
    SharedPreferences prefs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);
        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);


        final Button camera_button = (Button) findViewById(R.id.camera_button);
        final Button submit_button = (Button) findViewById(R.id.submit_button);
        final EditText area_text = (EditText) findViewById(R.id.area);
        final EditText description_text = (EditText) findViewById(R.id.description);


        camera_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageView = (ImageView) findViewById(R.id.ImageView);
                takePhoto(imageView);
                runOnUiThread(new Runnable() {
                    public void run() { camera_button.setText("Retake Photo");
                    }
                });
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyApplication m = ((MyApplication)getApplicationContext());
                /*if they pass in an image*/
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(area_text.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(description_text.getWindowToken(), 0);

                try {
                    if (bitmap != null) {
                        m.reports_array.add(new Report(
                                building_spinner.getSelectedItem().toString(),
                                area_text.getEditableText().toString(),
                                issue_spinner.getSelectedItem().toString(),
                                description_text.getEditableText().toString(),
                                bitmap
                        ));

                        prefs.edit()
                                .putString("REPORTS", byteToString(serialize(m.reports_array)))
                                .apply();
                    } else {
                        m.reports_array.add(new Report(
                                building_spinner.getSelectedItem().toString(),
                                area_text.getEditableText().toString(),
                                issue_spinner.getSelectedItem().toString(),
                                description_text.getEditableText().toString(),
                                BitmapFactory.decodeResource(context.getResources(),
                                        R.drawable.noimage)
                        ));

                        prefs.edit()
                                .putString("REPORTS", byteToString(serialize(m.reports_array)))
                                .apply();
                    }

                    finish();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });

        building_spinner = (Spinner) findViewById(R.id.location_spinner);
        ArrayAdapter<CharSequence> building_adapter = ArrayAdapter.createFromResource(this,
                R.array.buildings_array, android.R.layout.simple_spinner_item);
        building_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_spinner.setAdapter(building_adapter);

        issue_spinner = (Spinner) findViewById(R.id.issue_spinner);
        ArrayAdapter<CharSequence> issue_adapter = ArrayAdapter.createFromResource(this,
                R.array.issue_array, android.R.layout.simple_spinner_item);
        issue_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issue_spinner.setAdapter(issue_adapter);



    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 &&resultCode == Activity.RESULT_OK) {
            Uri selectedImage = imageUri;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageView = (ImageView)findViewById(R.id.ImageView);

                imageView.setImageBitmap(bitmap);
                imageView.setAdjustViewBounds(true);
//                Toast.makeText(this, selectedImage.toString(),Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }
        }
    }

    protected void onResume(){super.onResume();}
    protected void onStop(){super.onStop();}

    /*
	 * Serializes the Object to a Byte Array for use in the SharedPreferences
	 * */
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

	/*
	 * These are used for conversion to, and from, Byte Arrays
	 * */

    public String byteToString(byte[] data){
        return Base64.encodeToString(data, Base64.DEFAULT);
    }


}
