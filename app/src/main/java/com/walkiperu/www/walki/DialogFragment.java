package com.walkiperu.www.walki;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.IpSecManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Sleeper;

import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.nio.BufferUnderflowException;
import java.util.EventListener;
import java.util.List;
import java.util.TimerTask;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;
import entidades.ResponseRequest;
import entidades.User;

public class DialogFragment extends android.app.DialogFragment implements ViajesFragment.OnFragmentInteractionListener{



    private DatabaseReference dbRef;
    private ValueEventListener mSendEventListner;


    private Context context;
    private CircleImageView dialog_foto;
    private TextView dialog_name;
    private TextView dialog_score;
    private TextView dialog_salida;
    private TextView dialog_destino;
    private TextView dialog_coment;
    private TextView dialog_oferta;

    private ImageView dialog_opc_visa,
                        dialog_opc_polari,
                            dialog_opc_aire,
                                dialog_opc_mas4;



    private Button botonAceptar;
    private Button btn_oferta;
    private Button btn_masOferta;
    private Button btn_menosOferta;


    private Pedido pedido;
    private User user;
    private double precioOferta;
    private  double distanceRequest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_solicitud_taxi, container, false);




        context = getContext();

        Bundle bundle = getArguments();

        pedido = (Pedido) bundle.getSerializable("pedido");
        distanceRequest = (double) bundle.getSerializable("distance");

        FragmentManager fragmentManager = getChildFragmentManager();
        MapsActivity mapsActivity = new MapsActivity();
        Bundle b = new Bundle();

        b.putSerializable("MODE", 2);
        b.putSerializable("pedido", pedido);

        mapsActivity.setArguments(b);

        fragmentManager.beginTransaction().add(R.id.dialog_frag_container, mapsActivity).commit();



        precioOferta = pedido.getPrecio();

        btn_oferta = view.findViewById(R.id.btn_dialog_ofertar);
        btn_masOferta = view.findViewById(R.id.btn_masOferta);
        btn_menosOferta = view.findViewById(R.id.btn_menosOferta);

        dialog_foto = view.findViewById(R.id.dialog_img_user);
        dialog_name = view.findViewById(R.id.dialog_name_user);
        dialog_score = view.findViewById(R.id.dialog_score);
        dialog_salida = view.findViewById(R.id.dialog_salida);
        dialog_destino = view.findViewById(R.id.dialog_destino);
        dialog_coment = view.findViewById(R.id.dialog_coment);
        dialog_oferta = view.findViewById(R.id.dialog_oferta);

        dialog_opc_aire = view.findViewById(R.id.dialog_opc_aire);
        dialog_opc_mas4 = view.findViewById(R.id.dialog_opc_mas4);
        dialog_opc_polari = view.findViewById(R.id.dialog_opc_polari);
        dialog_opc_visa = view.findViewById(R.id.dialog_opc_visa);

        btn_oferta.setText("PEN " +precioOferta);
        btn_oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ofertar();

            }
        });


        btn_masOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(precioOferta < pedido.getPrecio()*2){

                    precioOferta+=0.50;
                    btn_oferta.setText("PEN " +precioOferta);


                }
            }
        });

        btn_menosOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(precioOferta > pedido.getPrecio()){

                    precioOferta-=0.50;
                    btn_oferta.setText("PEN " +precioOferta);


                }
            }
        });


        obtenerDatos();



        return view;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if(dialog != null){

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width,height);


        }


    }



    public void obtenerDatos(){

        dialog_name.setText(pedido.getNombre());
        dialog_salida.setText(DirectionsUtility.getSubDirections(pedido.getAdressOrigen()));
        dialog_destino.setText(DirectionsUtility.getSubDirections(pedido.getAdressDestino()));
        dialog_coment.setText(pedido.getComentario());
        dialog_oferta.setText("PEN  " +(pedido.getPrecio()));

       if(pedido.getVisa() == 1){
           dialog_opc_visa.setVisibility(View.VISIBLE);
       }

       if(pedido.getAireAcondicionado() == 1){
           dialog_opc_aire.setVisibility(View.VISIBLE);
       }

       if(pedido.getPolarizado() == 1){
           dialog_opc_polari.setVisibility(View.VISIBLE);
       }
       if(pedido.getMas4Pasajeros() == 1){
           dialog_opc_mas4.setVisibility(View.VISIBLE);
       }



    }


    public void ofertar(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final ResponseRequest responseRequest = new ResponseRequest();

        responseRequest.setIdResponse(databaseReference.push().getKey());
        responseRequest.setIdRequest(pedido.getCodigo());
        responseRequest.setIdDriver(FirebaseAuth.getInstance().getCurrentUser().getUid());
        responseRequest.setStatus(false);
        responseRequest.setPrecio(precioOferta);


        String distancia = "";
        long m;
        float km;
        if(Math.round(distanceRequest) > 999){
            km = (float)(Math.round(distanceRequest)/1000.0);
            distancia = ( String.format("%.1f",km) ) + " km";
        }else{
            m = Math.round(distanceRequest);
            distancia = m +" m";

        }

        responseRequest.setDistance(distancia);

        if(distanceRequest < 1500.0){
            responseRequest.setTime(3);
        }

        if(distanceRequest >=  1500.0 && distanceRequest < 2500.0){
            responseRequest.setTime(5);
        }

        if(distanceRequest >= 2500.0 && distanceRequest < 3500.0){
            responseRequest.setTime(10);
        }

        if(distanceRequest >= 3500.0 ){
            responseRequest.setTime(20);
        }




        databaseReference.child("Country").child(DirectionsUtility.country).child(DirectionsUtility.adminArea).
                child("delayRT").child(pedido.getCodigo()).child("responseRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(responseRequest).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(), "Error de conexion", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                View view1 = getLayoutInflater().inflate(R.layout.dialog_aceptar_pedido, null);
                alertDialog.setView(view1);
                final AlertDialog dialog = alertDialog.create();

                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                final Thread counter = new Thread(){
                    private  int counter = 0;
                    @Override
                    public void run() {
                        super.run();


                        for (int i = 0; i < 20; i++) {
                            try {
                                counter++;
                                System.out.println("----------------------" + counter);
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }



                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference2.child("Country").child(DirectionsUtility.country).child(DirectionsUtility.adminArea).
                                child("delayRT").child(pedido.getCodigo()).child("responseRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("idRequest").equalTo(pedido.getCodigo());


                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                                ResponseRequest responseRequest1 = dataSnapshot.getValue(ResponseRequest.class);

                                if (!responseRequest.isAccept()) {

                                    dialog.dismiss();
                                    dismiss();

                                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference();
                                    databaseReference3.child("Country").child(DirectionsUtility.country).child(DirectionsUtility.adminArea).
                                            child("delayRT").child(pedido.getCodigo()).child("responseRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue(false);


                                }

                            }




                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    public int getCounter(){
                        return  counter;
                    }
                };


                counter.start();


                DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference();
                 mSendEventListner =   databaseReference4.child("Country").child(DirectionsUtility.country).child(DirectionsUtility.adminArea).
                        child("delayRT").child(pedido.getCodigo()).child("responseRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("accept").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        boolean accept =  dataSnapshot.getValue(boolean.class);
                        if(accept){

                            Intent intent = new Intent(context,RequestAcceptActivity.class);
                            intent.putExtra("request",pedido);

                            context.startActivity(intent);
                            dialog.dismiss();
                            dismiss();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                dbRef = databaseReference4;



            }
        });





    }






    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("destruidoooooooo");

        if(mSendEventListner != null){
            System.out.println("ELIMINANDOOOOOOOOOOOOOOO");


            if(mSendEventListner instanceof ChildEventListener){
                System.out.println("CHILD");
            }else{
                if(mSendEventListner instanceof  ValueEventListener){
                    // REMOVE LISTENER OF ACCEPT REQUEST
                    dbRef.child("Country").child(DirectionsUtility.country).child(DirectionsUtility.adminArea).
                            child("delayRT").child(pedido.getCodigo()).child("responseRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("accept").removeEventListener(mSendEventListner);
                    mSendEventListner = null;
                }
            }

        }


    }
}
