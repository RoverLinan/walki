package com.walkiperu.www.walki;

import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import entidades.Driver;
import entidades.ResponseRequest;
import entidades.User;
import entidades.Vehicle;

public class RespuestaTaxiRVA extends RecyclerView.Adapter<RespuestaTaxiRVA.ViewHolder> {

    private List<ResponseRequest> mValues;
    private FragmentManager fragmentManager;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private Context context;


    public RespuestaTaxiRVA(List<ResponseRequest> items, FragmentManager fragmentManager){


        mValues = items;
        this.fragmentManager = fragmentManager;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RespuestaTaxiRVA.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_oferta_driver, parent, false);
        context = parent.getContext();

        return new RespuestaTaxiRVA.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


        final CollectionReference coRef = firebaseFirestore.collection("drivers");
        Query query = coRef.whereEqualTo("uidUser",mValues.get(i).getIdDriver());


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.getResult().getDocuments().size() > 0) {
                    Driver driver = task.getResult().getDocuments().get(0).toObject(Driver.class);
                    CollectionReference coRef1 = firebaseFirestore.collection("vehicles");
                    Query query1 = coRef1.whereEqualTo("uidVehicle", driver.getUidVehicle());

                    query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            // get vehicle

                            Vehicle vehicle = task.getResult().getDocuments().get(0).toObject(Vehicle.class);
                            viewHolder.getTv_nameAuto().setText(vehicle.getMaker() + " - " + vehicle.getModel());
                            viewHolder.tv_placaAuto.setText(vehicle.getPlaca());
                        }
                    });


                    CollectionReference coRef2 = firebaseFirestore.collection("users");
                    Query query2 = coRef2.whereEqualTo("uid", driver.getUidUser());

                    query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            // get info user

                            User user = task.getResult().getDocuments().get(0).toObject(User.class);
                            viewHolder.getTv_name().setText(user.getName());
                            viewHolder.getCv_oferta().setVisibility(View.VISIBLE);

                            Glide
                                    .with(context)
                                    .load(user.getUrlPhoto())
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                                            Toast.makeText(context, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            return false;
                                        }
                                    })
                                    .into(viewHolder.getPhoto());


                        }
                    });
                }
            }
        });


        viewHolder.getTv_cash().setText("PEN " + mValues.get(i).getPrecio());
        viewHolder.getTv_time().setText(mValues.get(i).getTime() + " min");
        viewHolder.getTv_distance().setText(mValues.get(i).getDistance());


        viewHolder.getCv_oferta().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("Country").child(DirectionsUtility.country).child(DirectionsUtility.adminArea).child("delayRT").child(mValues.get(i).getIdRequest())
                        .child("responseRequest").child(mValues.get(i).getIdDriver()).child("accept").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //ACCEPT DRIVER




                    }
                });


            }
        });




    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cv_oferta;
        private CircleImageView photo;
        private TextView tv_name,
                            tv_score,
                                tv_nameAuto,
                                    tv_placaAuto,
                                        tv_distance,
                                            tv_time,
                                                tv_cash;


        private ImageView opc_visa,
                            opc_polari,
                               opc_mas4,
                                 opc_aire;



        public ViewHolder(View view) {
            super(view);
            cv_oferta = view.findViewById(R.id.cv_oferta_driver);
            photo = view.findViewById(R.id.imv_photo_respuesta);
            tv_name = view.findViewById(R.id.tv_nombredriver_respuesta);
            tv_score = view.findViewById(R.id.tv_scoredriver_respuesta);
            tv_nameAuto = view.findViewById(R.id.tv_name_auto_respuesta);
            tv_placaAuto = view.findViewById(R.id.tv_placa_auto_respuesta);
            tv_time = view.findViewById(R.id.tv_min_respuesta);
            tv_distance = view.findViewById(R.id.tv_dista_respuesta);
            tv_cash = view.findViewById(R.id.tv_cash_respuesta);



            cv_oferta.setClickable(true);
            cv_oferta.setVisibility(View.INVISIBLE);

        }


        public CardView getCv_oferta() {
            return cv_oferta;
        }

        public CircleImageView getPhoto() {
            return photo;
        }

        public TextView getTv_name() {
            return tv_name;
        }

        public TextView getTv_score() {
            return tv_score;
        }

        public TextView getTv_nameAuto() {
            return tv_nameAuto;
        }

        public TextView getTv_placaAuto() {
            return tv_placaAuto;
        }

        public TextView getTv_distance() {
            return tv_distance;
        }

        public TextView getTv_time() {
            return tv_time;
        }

        public TextView getTv_cash() {
            return tv_cash;
        }

        public ImageView getOpc_visa() {
            return opc_visa;
        }

        public ImageView getOpc_polari() {
            return opc_polari;
        }

        public ImageView getOpc_mas4() {
            return opc_mas4;
        }

        public ImageView getOpc_aire() {
            return opc_aire;
        }
    }


}
