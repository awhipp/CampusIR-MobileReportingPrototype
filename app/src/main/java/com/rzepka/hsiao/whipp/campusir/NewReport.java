package com.rzepka.hsiao.whipp.campusir;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;


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

        if(getActionBar()!= null) {
            getActionBar().setTitle("New Incident");
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final Button camera_button = (Button) findViewById(R.id.camera_button);
        final Button submit_button = (Button) findViewById(R.id.submit_button);
        final EditText area_text = (EditText) findViewById(R.id.area);
        final EditText description_text = (EditText) findViewById(R.id.description);
        final ImageButton gps_button = (ImageButton) findViewById(R.id.gpsButton);

        building_spinner = (Spinner) findViewById(R.id.location_spinner);
        final ArrayAdapter<CharSequence> building_adapter = ArrayAdapter.createFromResource(this,
                R.array.buildings_array, android.R.layout.simple_spinner_item);
        building_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_spinner.setAdapter(building_adapter);

        issue_spinner = (Spinner) findViewById(R.id.issue_spinner);
        final ArrayAdapter<CharSequence> issue_adapter = ArrayAdapter.createFromResource(this,
                R.array.issue_array, android.R.layout.simple_spinner_item);
        issue_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issue_spinner.setAdapter(issue_adapter);

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

                if (!area_text.getEditableText().toString().replaceAll(" ","").equals("") &&
                        issue_spinner.getSelectedItemPosition() != issue_adapter.getPosition("[Select One]") &&
                        building_spinner.getSelectedItemPosition() != building_adapter.getPosition("[Select One]")) {
                    new AlertDialog.Builder(NewReport.this)
                            .setTitle("Submit Incident")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyApplication m = ((MyApplication) getApplicationContext());
                                    InputMethodManager imm = (InputMethodManager) getSystemService(
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
                                                    description_text.getEditableText().toString()
                                            ));
                                            prefs.edit()
                                                    .putString("REPORTS", byteToString(serialize(m.reports_array)))
                                                    .apply();
                                        }
                                        Toast.makeText(NewReport.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                                        finish();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();


                }else{
                    Toast.makeText(NewReport.this, "Please Complete Required Fields (*)", Toast.LENGTH_LONG).show();
                }
            }
        });

        gps_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nearest_community = nearestCommunity();
                if(nearest_community != null)
                    building_spinner.setSelection(building_adapter.getPosition(nearest_community));
            }
        });
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
            } catch (Exception e) {
                Toast.makeText(this, "Image Capture Failed. Try again.", Toast.LENGTH_LONG).show();
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String nearestCommunity(){
        HashMap<String, Point> communities = new HashMap<>();
        communities.put("Cambridge", new Point(38.991911, -76.943051));
        communities.put("Denton", new Point(38.992728, -76.949655));
        communities.put("Ellicott", new Point(38.991965, -76.946661));
        communities.put("Leonardtown", new Point(38.983683, -76.933412));
        communities.put("North Hill", new Point(38.983740, -76.944880));
        communities.put("South Hill", new Point(38.982254, -76.941378));
        communities.put("Commons", new Point(38.982087, -76.942925));

        String nearest = "";
        double shortest_distance = Double.MAX_VALUE;
        GPSTracker gps = new GPSTracker(NewReport.this);
        Point myLocation = new Point(gps.getLatitude(), gps.getLongitude());

        if(gps.canGetLocation()) {
            for (String s : communities.keySet()) {
                if (nearest.equals("")){
                    nearest = s;
                    shortest_distance = communities.get(s).distance(myLocation);
                }else {
                    if(communities.get(s).distance(myLocation) < shortest_distance){
                        nearest = s;
                        shortest_distance = communities.get(s).distance(myLocation);
                    }
                }
            }
            gps.stopUsingGPS();
            return nearest;
        }else{
            Toast.makeText(NewReport.this, "GPS Disabled", Toast.LENGTH_LONG).show();
            gps.stopUsingGPS();
            return null;
        }

    }



}
