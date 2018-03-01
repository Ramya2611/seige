package com.aakash.siege;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

/**
 * Created by ramya on 31/1/18.
 */

public class login extends saveSP{


    EditText username ,  email, phone, password, confirmPassword;
    TextView textView, address;
    Button register,LocationBtn;
    String username1, address1,  email1, phone1, password1, confirmPassword1, locationlat,locationlon;
    String MobilePattern = "[0-9]{10}";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        username = (EditText) findViewById(R.id.username);
        address = (TextView) findViewById(R.id.address);
        confirmPassword = (EditText)findViewById(R.id.ConfirmPassword);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        textView = (TextView) findViewById(R.id.textview);
        register = (Button) findViewById(R.id.Register);
        LocationBtn = (Button)findViewById(R.id.currentLoc);

        LocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    locationlat = getUserLat(login.this);
                    locationlon = getUserLon(login.this);
                    address.setText(locationlat+","+locationlon);
                }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username1 = username.getText().toString();
                address1 = address.getText().toString();
                confirmPassword1 = confirmPassword.getText().toString();

                email1 = email.getText().toString();
                phone1= phone.getText().toString();
                password1 = password.getText().toString();

                if (username.getText().toString().trim().length()==0){
                    username.setError("Enter Username");
                    username.requestFocus();
                } else
                if(!phone.getText().toString().matches(MobilePattern)) {
                    phone.setError("Enter 10 digit number");
                    phone.requestFocus();


                }else if(!email.getText().toString().matches(emailPattern)){
                    email.setError("Enter valid Email-Id");
                    email.requestFocus();

                }


                else if (password.getText().toString().trim().length()==0){
                    password.setError("Entergit your Password");
                    password.requestFocus();
                } else if (confirmPassword.getText().toString().trim().length()==0){
                    confirmPassword.setError("Enter Confirm Password");
                    confirmPassword.requestFocus();
                }
                else if(!password1.equals(confirmPassword1)){

                    Toast.makeText(getApplicationContext(),"Enter Correct Password",Toast.LENGTH_SHORT).show();

                }

                else {
                    postMutation(username1,phone1,email1,password1,address1);
                    Log.d("bug","address"+address1);
                }

            }
        });



    }


    void callQuery(String mono){
        setUpClient("https://siegegraphql.herokuapp.com/graphql");
        UserQuery userQuery = UserQuery.builder()
                .mobno(mono)
                .build();
        ApolloCall<UserQuery.Data> call = apolloClient.query(userQuery);
        call.enqueue(new ApolloCall.Callback<UserQuery.Data>() {
            @Override
            public void onResponse(@Nonnull Response<UserQuery.Data> response) {

                UserQuery.Data data = response.data();
                Log.d("result", "onResponseresult: "+data.user.get(0).password);
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e("Fail", "onFailure: ",e );
            }
        });
    }
    void postMutation(String uname,String umob,String uemail,String upass,String ulocation){
        setUpClient("https://siegegraphql.herokuapp.com/graphql");
        AddpeopleMutation addpeopleMutation = AddpeopleMutation.builder()
                .name(uname)
                .mobno(umob)
                .email(uemail)
                .pass(upass)
                .location(ulocation)
                .build();
        ApolloCall<AddpeopleMutation.Data> call = apolloClient.mutate(addpeopleMutation);
        call.enqueue(new ApolloCall.Callback<AddpeopleMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<AddpeopleMutation.Data> response) {
                login.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(login.this, MainActivity.class);
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

