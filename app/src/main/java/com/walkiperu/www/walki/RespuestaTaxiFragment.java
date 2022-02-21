package com.walkiperu.www.walki;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import entidades.Driver;
import entidades.ResponseRequest;
import entidades.User;
import entidades.Vehicle;

public class RespuestaTaxiFragment extends Fragment {


    private int mColumnCount = 1;
    private DatabaseReference databaseReference;
    private Pedido request;


    private SolicitudTaxiFragment.OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RespuestaTaxiFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }





    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_respuestataxi_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // GET REQUEST AND WAIT RESPONSE DRIVER...
            Bundle bundle = getArguments();
            request = (Pedido) bundle.getSerializable("request");


            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Country").child(DirectionsUtility.country).child(DirectionsUtility.adminArea).child("delayRT").child(request.getCodigo()).child("responseRequest").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    List<ResponseRequest> listRequest = new ArrayList<>();

                    for (DataSnapshot objeto : dataSnapshot.getChildren()) {


                        //SAVE ONE FOR ONE RESPONSE

                        ResponseRequest responseRequest = objeto.getValue(ResponseRequest.class);
                        listRequest.add(responseRequest);


                    }

                    recyclerView.setAdapter(new RespuestaTaxiRVA(listRequest, getFragmentManager()));


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }



        return view;
    }








}
