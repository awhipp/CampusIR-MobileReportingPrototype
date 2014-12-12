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

                if(issue_spinner.getSelectedItemPosition() == issue_adapter.getPosition("Other") &&
                        description_text.getEditableText().toString().replaceAll(" ","").replaceAll("\n","").equals("")){
                    Toast.makeText(NewReport.this, "Other Selected. Please complete \"More Information.\"", Toast.LENGTH_LONG).show();
                }else if (!area_text.getEditableText().toString().replaceAll(" ","").equals("") &&
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
        communities.put("Bel Air Hall", new Point(38.992797, -76.942644));
        communities.put("Cambridge Hall", new Point(38.991727, -76.943052));
        communities.put("Centreville Hall", new Point(38.992235, -76.942076));
        communities.put("Chestertown Hall", new Point(38.992836, -76.943481));
        communities.put("Cumberland Hall", new Point(38.992314, -76.943958));
        communities.put("Denton Hall", new Point(38.992235, -76.949995));
        communities.put("Easton Hall", new Point(38.992997, -76.950271));
        communities.put("Elkton Hall", new Point(38.992437, -76.948925));
        communities.put("Oakland Hall", new Point(38.993847, -76.949394));
        communities.put("Ellicott Hall", new Point(38.991823, -76.946685));
        communities.put("Hagerstown Hall", new Point(38.992429, -76.947441));
        communities.put("La Plata Hall", new Point(38.992438, -76.945858));
        communities.put("Old Leonardtown", new Point(38.982876, -76.932612));
        communities.put("New Leonardtown", new Point(38.984325, -76.933352));
        communities.put("Anne Arundel Hall", new Point(38.985974, -76.946747));
        communities.put("Caroline Hall", new Point(38.983743, -76.945855));
        communities.put("Carroll Hall", new Point(38.983997, -76.945641));
        communities.put("Dorchester Hall", new Point(38.986708, -76.946146));
        communities.put("Prince Frederick Hall", new Point(38.983136, -76.945629));
        communities.put("Queen Anne's Hall", new Point(38.985284, -76.946194));
        communities.put("St. Mary's Hall", new Point(38.986960, -76.945615));
        communities.put("Somerset Hall", new Point(38.985061, -76.945566));
        communities.put("Wicomico Hall", new Point(38.983743, -76.945855));
        communities.put("Worcester Hall", new Point(38.984675, -76.945035));
        communities.put("Allegany Hall", new Point(38.981592, -76.941432));
        communities.put("Baltimore Hall", new Point(38.982257, -76.942205));
        communities.put("Calvert Hall", new Point(38.982923, -76.942333));
        communities.put("Cecil Hall", new Point(38.982933, -76.941652));
        communities.put("Charles Hall", new Point(38.981590, -76.940525));
        communities.put("Frederick Hall", new Point(38.982047, -76.940740));
        communities.put("Garrett Hall", new Point(38.983266, -76.942703));
        communities.put("Harford Hall", new Point(38.982529, -76.940869));
        communities.put("Howard Hall", new Point(38.981946, -76.941969));
        communities.put("Kent Hall", new Point(38.983275, -76.941834));
        communities.put("Montgomery Hall", new Point(38.981900, -76.939324));
        communities.put("Prince George's Hall", new Point(38.982601, -76.941840));
        communities.put("Talbot Hall", new Point(38.983377, -76.942279));
        communities.put("Washington Hall", new Point(38.981823, -76.941357));
        communities.put("South Campus Commons", new Point(38.982087, -76.942925));

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
