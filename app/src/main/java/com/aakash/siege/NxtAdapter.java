package com.aakash.siege;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by aakash on 8/2/18.
 */

public class NxtAdapter extends RecyclerView.Adapter<NxtAdapter.ViewHolder>{
    private List<String> mDataset,maddress,mlandmark,mcapacity,mcontact;
    private static Context mcon;

    public void NxtAdapter(Context con){
        mcon = con;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sName.setText(mDataset.get(position));
        holder.sAddress.setText(maddress.get(position));
        holder.sLandmark.setText(mlandmark.get(position));
        holder.sCapacity.setText(mcapacity.get(position));
        holder.sContact.setText(mcontact.get(position));
        mcon = holder.sName.getContext();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView sName,sAddress,sLandmark,sCapacity,sremaining,sContact;
        public ViewHolder(View v) {
            super(v);
            sName = v.findViewById(R.id.Name);
            sAddress = v.findViewById(R.id.Address);
            sLandmark = v.findViewById(R.id.Landmark);
            sCapacity = v.findViewById(R.id.Capacity_remianingd);
            sremaining = v.findViewById(R.id.remainingCapacity);
            sContact = v.findViewById(R.id.Capacity_Contactd);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("tag","Clicked"+v.getId());

            String dname = ((TextView)v.findViewById(R.id.Name)).getText().toString();
            Intent intent = new Intent(mcon, ShelterDetails.class);
            intent.putExtra("LIST_NAME", dname);
            mcon.startActivity(intent);
        }
    }

    public NxtAdapter(List<String> myDataset,List<String> myAddress,List<String> myLandmark,List<String> myCapacity,List<String> myContact) {
        mDataset = myDataset;
        maddress = myAddress;
        mlandmark = myLandmark;
        mcapacity = myCapacity;
        mcontact = myContact;
    }

    @Override
    public NxtAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listcard,parent,false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }




    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
