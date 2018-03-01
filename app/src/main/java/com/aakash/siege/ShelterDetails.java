package com.aakash.siege;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

/**
 * Created by aakash on 5/2/18.
 */

public class ShelterDetails extends saveSP {

    TextView placename1, Location1, Landmark1, Contact1, Capacity1,mfacility1,wfacilitiy1,ffacility1,lat1,lon1;
    ImageButton callButton,loc3;
    ImageButton MsgButton;


    String placename2, Location2, Landmark2, Contact2, Capacity2,mfacility2,wfacility2,ffacility2,lat2,lon2;
    String MobilePattern = "[0-9]{10}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_shelter);
        placename1 = (TextView) findViewById(R.id.Placenamed);
        Location1 = (TextView) findViewById(R.id.Locationd);
        Landmark1 = (TextView) findViewById(R.id.Landmarkd);
        Contact1 = (TextView) findViewById(R.id.Contactd);
        Capacity1 = (TextView) findViewById(R.id.Shelter_Capacityd);
        mfacility1=(TextView)findViewById(R.id.Medical_Facilityd);
        wfacilitiy1=(TextView)findViewById(R.id.Waterd);
        ffacility1=(TextView)findViewById(R.id.Foodd);
        callButton=(ImageButton)findViewById(R.id.Call);
        loc3=(ImageButton)findViewById(R.id.locationicon);
        MsgButton=(ImageButton)findViewById(R.id.Msg);
        lat1 = (TextView)findViewById(R.id.latd);
        lon1 = (TextView)findViewById(R.id.lond);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = Contact1.getText().toString();
                if(!TextUtils.isEmpty(phoneNo)) {
                    String dial = "tel:" + phoneNo;
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }else {
                    Toast.makeText(ShelterDetails.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        MsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = Contact1.getText().toString();
                if(!TextUtils.isEmpty(phoneNo)) {
                    Intent intent = new Intent("android.intent.action.VIEW");

                    /** creates an sms uri */
                    Uri data = Uri.parse("sms:"+ phoneNo);

                    /** Setting sms uri to the intent */
                    intent.setData(data);

                    /** Initiates the SMS compose screen, because the activity contain ACTION_VIEW and sms uri */
                    startActivity(intent);
                }else {
                    Toast.makeText(ShelterDetails.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:lat1,lon1");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            }
        });





        String Sname = getIntent().getStringExtra("LIST_NAME");
        Log.d("tag","ShelDetail"+Sname);
        callQuery(Sname);


    }
    void callQuery(String name){
        setUpClient("https://siegegraphql.herokuapp.com/graphql");
        SdetailQuery sdetailQuery = SdetailQuery.builder()
                .name(name)
                .build();
        apolloClient.query(sdetailQuery)
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .enqueue(new ApolloCall.Callback<SdetailQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<SdetailQuery.Data> response) {

                SdetailQuery.Data data = response.data();



                placename2 = data.shelter.get(0).sname.toString();
                Location2 = data.shelter.get(0).saddress.toString();
                Landmark2 = data.shelter.get(0).slandmark.toString();
                Contact2 = data.shelter.get(0).scontact.toString();
                Capacity2 = data.shelter.get(0).scapacity.toString();
                ffacility2=data.shelter.get(0).sffacility.toString();
                String[] latlon = data.shelter.get(0).slocation.split(",");
                lat2 = latlon[0];
                lon2 = latlon[1];

                //  Log.d("result", "onResponseresult: "+data.shelter.get(0).sffacility);





                ShelterDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        placename1.setText(placename2);
                        Location1.setText(Location2);
                        Landmark1.setText(Landmark2);
                        Contact1.setText(Contact2);
                        Capacity1.setText(Capacity2);
                        lat1.setText(lat2);
                        lon1.setText(lon2);
                        //mfacility1.setText(mfacility2);
                        if (ffacility2 == "true")
                        {
                            ffacility2 = "Available";
                        }else
                        {
                            ffacility2 ="Not Available";
                        }
                        ffacility1.setText(ffacility2);

                        if (mfacility2 == "true")
                        {
                            mfacility2 = "Available";
                        }else
                        {
                            mfacility2 ="Not Available";
                        }
                        mfacility1.setText(mfacility2);

                        if (wfacility2 == "true")
                        {
                            wfacility2 = "Available";
                        }else
                        {
                            wfacility2 ="Not Available";
                        }
                        wfacilitiy1.setText(wfacility2);







                        // Log.d("result", "onResponseresult: "+placename2);



                    }
                });



                // Log.d("result", "onResponseresult: "+data.shelter.get(0).sname);


            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e("Fail", "onFailure: ",e );
            }
        });
    }

}
