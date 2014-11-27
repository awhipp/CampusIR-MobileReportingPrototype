package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;


public class NewReport extends Activity {

    ImageView imageView;
    private Uri imageUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        final Button camera_button = (Button) findViewById(R.id.camera_button);

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

        Spinner building_spinner = (Spinner) findViewById(R.id.location_spinner);
        ArrayAdapter<CharSequence> building_adapter = ArrayAdapter.createFromResource(this,
                R.array.buildings_array, android.R.layout.simple_spinner_item);
        building_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_spinner.setAdapter(building_adapter);

        Spinner issue_spinner = (Spinner) findViewById(R.id.issue_spinner);
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
            getContentResolver().notifyChange(selectedImage, null);
            ContentResolver cr = getContentResolver();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);
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

}
