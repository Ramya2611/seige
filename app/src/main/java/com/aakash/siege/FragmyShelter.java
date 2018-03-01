package com.aakash.siege;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.exception.ApolloException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmyShelter.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmyShelter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmyShelter extends Fragment  {
    private RecyclerView nRecyclerView;
    private RecyclerView.Adapter nAdapter;
    private RecyclerView.LayoutManager nLayoutManager;
    public int Listsize = 0;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    final static List<String> pname = new ArrayList<>();
    final static List<String> paddress = new ArrayList<>();
    final static List<String> plandmark = new ArrayList<>();
    final static List<String> pcapacity = new ArrayList<>();
    final static List<String> pcontact = new ArrayList<>();
    public final static List<String> plocation = new ArrayList<>();
    public ApolloClient apolloClient;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmyShelter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmyShelter.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmyShelter newInstance(String param1, String param2) {
        FragmyShelter fragment = new FragmyShelter();
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

        callQuery(Integer.parseInt(saveSP.getUserName(getActivity())));
        while(pname.size() == 0){

        }
        Log.d("tag","oncreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_fragmy_shelter, container, false);
        nRecyclerView = (RecyclerView)rootview.findViewById(R.id.my_recycler_view);
        nRecyclerView.setHasFixedSize(true);

        nLayoutManager = new LinearLayoutManager(this.getActivity());
        nRecyclerView.setLayoutManager(nLayoutManager);

        nAdapter = new NxtAdapter(pname,paddress,plandmark,pcapacity,pcontact);
        nRecyclerView.setAdapter(nAdapter);
        Log.d("tag","oncreateView");
        return rootview;
    }

    void callQuery(int id){
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
                        Listsize = response.data().shelter().size();
                        if(pname.size()== 0)
                            for (int i=0;i<Listsize;i++){
                                pname.add(response.data().shelter().get(i).sname);
                                paddress.add(response.data().shelter().get(i).saddress);
                                plandmark.add(response.data().shelter().get(i).slandmark);
                                pcapacity.add(response.data().shelter().get(i).scapacity.toString());
                                pcontact.add(response.data().shelter().get(i).scontact);
                                plocation.add(response.data().shelter().get(i).slocation+","+response.data().shelter().get(i).id);
                            }
                        if(getActivity() != null)
                        saveSP.setoffLocation(getActivity(),plocation);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


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
}
