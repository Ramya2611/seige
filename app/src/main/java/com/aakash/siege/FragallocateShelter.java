package com.aakash.siege;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;

import static com.aakash.siege.FragShelterList.pname;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragallocateShelter.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragallocateShelter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragallocateShelter extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public String mAddressOutput;

    Geocoder geocoder;
    List<Address> addresses;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected Location mLastLocation;
    Button allocatbtn;
    TextView shelterTxt,sname,slocval,slocdval,adval;
    float min = 999999999;
    Integer qid;
    ImageButton locatebtn;
    private OnFragmentInteractionListener mListener;
    public ApolloClient apolloClient;
    private AddressResultReceiver mResultReceiver;
    public FragallocateShelter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragallocateShelter.
     */
    // TODO: Rename and change types and number of parameters
    public static FragallocateShelter newInstance(String param1, String param2) {
        FragallocateShelter fragment = new FragallocateShelter();
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
    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            Log.d("tag","address: "+mAddressOutput);
            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                Log.d("tag","Error");
            }

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View allocateview = inflater.inflate(R.layout.fragment_fragallocate_shelter, container, false);
        allocatbtn = (Button)allocateview.findViewById(R.id.allbtn);
        shelterTxt = (TextView)allocateview.findViewById(R.id.sheltername);
        sname = (TextView)allocateview.findViewById(R.id.sNamed);
        slocval = (TextView)allocateview.findViewById(R.id.sloc);
        slocdval = (TextView)allocateview.findViewById(R.id.slocd);
        locatebtn = (ImageButton)allocateview.findViewById(R.id.locbtn);
        adval = (TextView)allocateview.findViewById(R.id.addval);

        allocatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double userlat,userlon;
                String totallocation;
                userlat=Double.parseDouble(saveSP.getUserLat(getActivity()));
                userlon=Double.parseDouble(saveSP.getUserLon(getActivity()));
                totallocation = saveSP.getoffLocation(getActivity());
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> locList = gson.fromJson(totallocation, type);
                for(int i=0;i<locList.size();i++){
                    Double latval,lonval;
                    Integer id;
                    String[] locval = locList.get(i).split(",");
                    latval = Double.parseDouble(locval[0]);
                    lonval = Double.parseDouble(locval[1]);
                    id = Integer.parseInt(locval[2]);
                    getDistance(userlat,userlon,latval,lonval,id);
                }

            }
        });
        locatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] spldata = slocdval.getText().toString().split(",");
                if(spldata.length > 1){
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+spldata[0]+","+spldata[1]);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);}else{
                    Toast.makeText(getActivity(), "Please Allocate Shelter First", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return  allocateview;
    }
    public void getDistance(Double ulat,Double ulon,Double slat,Double slon,Integer id){
        Location me   = new Location("");
        Location dest = new Location("");


        me.setLatitude(ulat);
        me.setLongitude(ulon);

        dest.setLatitude(slat);
        dest.setLongitude(slon);
        mLastLocation = dest;

        float dist = me.distanceTo(dest);
        if(min>dist){
            //startIntentService();
            min = dist;
            qid = id;
            if(isOnline())
            callQuery(qid);
            else{
                slocdval.setText(slat.toString()+","+slon.toString());
            }
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    void callQuery(Integer id){
        setUpClient("https://siegegraphql.herokuapp.com/graphql");
        GetshelterQuery getshelterQuery = GetshelterQuery.builder()
                .id(id)
                .build();
        apolloClient.query(getshelterQuery)
                .httpCachePolicy(HttpCachePolicy.NETWORK_FIRST.expireAfter(2, TimeUnit.MINUTES))
                .enqueue(new ApolloCall.Callback<GetshelterQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<GetshelterQuery.Data> fresponse) {

                        final Response<GetshelterQuery.Data> response = fresponse;


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sname.setText(response.data().shelter.get(0).sname);
                                slocdval.setText(response.data().shelter.get(0).slocation);
                                adval.setText(response.data().shelter.get(0).saddress);
                            }
                        });
                        Log.d("tag","msg: "+pname);
                    }
                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        Log.e("Fail", "onFailure: ",e );
                    }
                });
    }

    public void setUpClient(String qlurl){

        File file = new File(getActivity().getCacheDir().toURI());
        //Size in bytes of the cache
        int size = 1024*1024;

        //Create the http response cache store
        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file, size);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl(qlurl)
                .httpCache(new ApolloHttpCache(cacheStore))
                .okHttpClient(okHttpClient)
                .build();
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
