package com.aakash.siege;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.exception.ApolloException;

import java.io.File;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;

import static com.aakash.siege.saveSP.getUserLat;
import static com.aakash.siege.saveSP.getUserLon;
import static com.aakash.siege.saveSP.getUserName;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragaddShelter.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragaddShelter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragaddShelter extends Fragment {
    public ApolloClient apolloClient;
    EditText place_name1,Location1,Landmark1,Contact1,Capacity1,Address1;
    Button Register1;
    ImageView getLocbtn;
    String place_name2,Location2,Landmark2,Contact2,loclat,loclon,Address2;

    int id,Capacity2;
    String MobilePattern = "[0-9]{10}";
    String CPattern = "[0-9]";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragaddShelter.OnFragmentInteractionListener mListener;

    public FragaddShelter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragaddShelter.
     */
    // TODO: Rename and change types and number of parameters
    public static FragaddShelter newInstance(String param1, String param2) {
        FragaddShelter fragment = new FragaddShelter();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_fragadd_shelter, container, false);
        place_name1 = (EditText) rootview.findViewById(R.id.Place_Name);
        Location1 = (EditText) rootview.findViewById(R.id.Location);
        Address1 = (EditText)rootview.findViewById(R.id.Address);
        Landmark1 = (EditText) rootview.findViewById(R.id.Landmark);
        Contact1 = (EditText) rootview.findViewById(R.id.Contact);
        Capacity1 = (EditText) rootview.findViewById(R.id.Capacity);
        Register1 = (Button) rootview.findViewById(R.id.Registerbut);
        getLocbtn = (ImageView) rootview.findViewById(R.id.locationicon1);

        getLocbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loclat = getUserLat(getActivity());
                loclon = getUserLon(getActivity());
                Location1.setText(loclat+","+loclon);
            }
        });

        Register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(place_name1.getText().toString().trim().length()==0) {
                    place_name1.setError("Enter place_name");
                    place_name1.requestFocus();
                }else if (Address1.getText().toString().trim().length()==0) {
                    Address1.setError("Enter Address");
                    Address1.requestFocus();
                }else if (Location1.getText().toString().trim().length()==0) {
                    Location1.setError("Enter Location");
                    Location1.requestFocus();
                }else if (Landmark1.getText().toString().trim().length()==0) {
                    Landmark1.setError("Enter Landmark");
                    Landmark1.requestFocus();
                }else if(Contact1.getText().toString().trim().length()==0) {
                    Contact1.setError("Enter 10 digit number");
                    Contact1.requestFocus();
                }else if(!Contact1.getText().toString().matches(MobilePattern)) {
                    Contact1.setError("Enter 10 digit number");
                    Contact1.requestFocus();
                } else if(Capacity1.getText().toString().trim().length()==0) {
                    Capacity1.setError("Enter number");
                    Capacity1.requestFocus();
                }
                else if(Capacity1.getText().toString().matches(CPattern)) {
                    Capacity1.setError("Enter valid number");
                    Capacity1.requestFocus();
                }else {

                    place_name2 = place_name1.getText().toString();
                    Location2 = Location1.getText().toString();
                    Landmark2 = Landmark1.getText().toString();
                    Contact2 = Contact1.getText().toString();
                    Address2 = Address1.getText().toString();
                    Capacity2 = Integer.parseInt(Capacity1.getText().toString());
                    id = Integer.parseInt(getUserName(getActivity()));
                    postMutation(id, place_name2, Location2, Address2, Landmark2, Capacity2, Contact2);
                }

            }
        });
        return rootview;
    }

    void postMutation(int id,String uname,String location,String address,String landmark,int capacity,String contact){
        File file = new File(getActivity().getCacheDir().toURI());
        //Size in bytes of the cache
        int size = 1024*1024;

        //Create the http response cache store
        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file, size);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl("https://siegegraphql.herokuapp.com/graphql")
                .httpCache(new ApolloHttpCache(cacheStore))
                .okHttpClient(okHttpClient)
                .build();
        AddshelterMutation addshelterMutation = AddshelterMutation.builder()
                .id(id)
                .name(uname)
                .location(location)
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
                             getActivity().runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     Toast.makeText(getActivity(), "Shelter registered Successfully", Toast.LENGTH_SHORT).show();
                                     Intent intent = new Intent(getActivity(), NavActivity.class);
                                     getActivity().finish();
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


            // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
