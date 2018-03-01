package com.aakash.siege;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

/**
 * Created by aakash on 2/2/18.
 */

public class sregister extends saveSP {
    EditText place_name1,Location1,Landmark1,Contact1,Capacity1;
    Button Register1;
    String place_name2,Location2,Landmark2,Contact2;
    int id,Capacity2;
    String MobilePattern = "[0-9]{10}";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shelter_register);
        place_name1 = (EditText) findViewById(R.id.Place_Name);
        Location1 = (EditText) findViewById(R.id.Location);
        Landmark1 = (EditText) findViewById(R.id.Landmark);
        Contact1 = (EditText) findViewById(R.id.Contact);
        Capacity1 = (EditText) findViewById(R.id.Capacity);
        Register1 = (Button) findViewById(R.id.Registerbut);

        Register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                place_name2 = place_name1.getText().toString();
                Location2 = Location1.getText().toString();
                Landmark2 = Landmark1.getText().toString();
                Contact2 = Contact1.getText().toString();
                Capacity2   = Integer.parseInt(Capacity1.getText().toString());
                id = Integer.parseInt(getUserName(sregister.this));
                postMutation(id,place_name2,Location2,Landmark2,Capacity2,Contact2);

            }
        });
    }
    void postMutation(int id,String uname,String address,String landmark,int capacity,String contact){
        setUpClient("https://siegegraphql.herokuapp.com/graphql");
        AddshelterMutation addshelterMutation = AddshelterMutation.builder()
                .id(id)
                .name(uname)
                .address(address)
                .landmark(landmark)
                .capacity(capacity)
                .contact(contact)
                .build();
        ApolloCall<AddshelterMutation.Data> call = apolloClient.mutate(addshelterMutation);
        call.enqueue(new ApolloCall.Callback<AddshelterMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<AddshelterMutation.Data> response) {
                AddshelterMutation.Data res = response.data();
                sregister.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(sregister.this, "Shelter registered Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(sregister.this, FragShelterList.class);
                        finish();
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e("Fail", "onFailure: ",e );
            }
        });
    }
}
