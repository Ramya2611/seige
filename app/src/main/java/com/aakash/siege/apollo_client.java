package com.aakash.siege;

import android.support.v7.app.AppCompatActivity;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;

import java.io.File;

import okhttp3.OkHttpClient;

/**
 * Created by aakash on 31/1/18.
 */

public class apollo_client extends AppCompatActivity {

    public ApolloClient apolloClient;


    //Directory where cached responses will be stored


    public void setUpClient(String qlurl){

        File file = new File(this.getCacheDir().toURI());
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
}
