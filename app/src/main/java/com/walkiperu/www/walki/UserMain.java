package com.walkiperu.www.walki;

import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.android.gms.location.places.ui.PlaceAutocomplete;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import entidades.Driver;
import entidades.User;
import entidades.Usuario;
import firebase.connection.FirebaseConn;


public class UserMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ViajesFragment.OnFragmentInteractionListener {


    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private MapsActivity mapsActivity;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private Button buttonMode;


    private User user;
    private String urlPhoto;
    private View vista;

    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_email;
    private ImageView iv_photo;
    private static final int SELECT_ADRESS = 1;
    private static final int MAKE_WALK = 2;
    private static final int MODE_SALIDA = 0;
    private static final int MODE_DESTINO = 1;
    private Pedido pedidoTaxi;
    private Ubicacion ubicacionSalida;
    private Ubicacion ubicacionDestino;

    private EditText et_salidaTaxi;
    private EditText et_destinoTaxi;
    private EditText et_precioTaxi;
    private EditText et_comentTaxi;
    private Switch switch_visa;
    private Switch switch_polarizado;
    private Switch switch_airCondicionado;
    private Switch switch_mas4Pasajeros;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        firebaseUser = FirebaseConn.firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();




        vista = findViewById(R.id.box_pedido);
        vista.setVisibility(View.INVISIBLE);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(vista.getVisibility() == View.VISIBLE){
                    vista.setVisibility(View.INVISIBLE);
                }else {
                    vista.setVisibility(View.VISIBLE);
                    pedidoTaxi = new Pedido();
                    ubicacionSalida = null;
                    ubicacionDestino = null;
                    et_salidaTaxi.setText("");
                    et_destinoTaxi.setText("");
                    et_precioTaxi.setText("");
                    et_comentTaxi.setText("");

                }

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
            }
        });

        et_salidaTaxi =  findViewById(R.id.et_salida_taxi);
        et_destinoTaxi = findViewById(R.id.et_destino_taxi);
        et_precioTaxi = findViewById(R.id.et_precioTaxi);
        et_comentTaxi = findViewById(R.id.et_comentTaxi);
        switch_visa = findViewById(R.id.switch_visa);
        switch_polarizado = findViewById(R.id.switch_polari);
        switch_airCondicionado = findViewById(R.id.switch_aireAcon);
        switch_mas4Pasajeros = findViewById(R.id.switch_mas4);
        buttonMode = findViewById(R.id.button_mode);







        et_salidaTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(),SelectorAdressMap.class);
                intent.putExtra("mensaje","Porfavor seleccione su lugar de salida");
                intent.putExtra("mode",MODE_SALIDA);
                startActivityForResult(intent,SELECT_ADRESS);

            }
        });



        et_destinoTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(),SelectorAdressMap.class);
                intent.putExtra("mensaje","Porfavor seleccione su destino");
                intent.putExtra("mode",MODE_DESTINO);
                startActivityForResult(intent,SELECT_ADRESS);


            }
        });


        buttonMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final ProgressDialog progressDialog = new ProgressDialog(UserMain.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Connecting...");
                progressDialog.show();


                CollectionReference coRef = firebaseFirestore.collection("drivers");
                Query query = coRef.whereEqualTo("uidUser",firebaseUser.getUid());

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.getResult().getDocuments().size()>0){

                                User user = task.getResult().getDocuments().get(0).toObject(User.class);


                                progressDialog.dismiss();
                                Intent intent = new Intent(getBaseContext(),DriverActivity.class);
                                startActivity(intent);


                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(UserMain.this, "No habilitado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });







            }
        });




        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //editamos el header del bavigation para colocar los datos del usuario

        View viewheader = navigationView.getHeaderView(0);



         tv_name  = viewheader.findViewById(R.id.tv_name_um);
         tv_phone = viewheader.findViewById(R.id.tv_phone_um);
         tv_email = viewheader.findViewById(R.id.tv_email_um);
         iv_photo = viewheader.findViewById(R.id.img_photo_um);

         getUser();


         navigationView.setNavigationItemSelectedListener(this);

         android.app.FragmentManager fragmentManager = getFragmentManager();

         mapsActivity = new MapsActivity();

         Bundle b = new Bundle();

         b.putSerializable("MODE",1);

         mapsActivity.setArguments(b);


         fragmentManager.beginTransaction().add(R.id.content_fragment,mapsActivity).commit();


    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        android.app.FragmentManager fragmentManager = getFragmentManager();



        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            Bundle b = new Bundle();

            b.putSerializable("MODE",1);

            mapsActivity.setArguments(b);

            fragmentManager.beginTransaction().replace(R.id.content_fragment, mapsActivity).commit();



            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            ViajesFragment viajesFragment = new ViajesFragment();

            fragmentManager.beginTransaction().replace(R.id.content_fragment,viajesFragment).commit();


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public void createRequest(View view){


        if(checkDate()) {


            if (!et_destinoTaxi.getText().toString().equalsIgnoreCase(ubicacionDestino.getAdresss())) {

                pedidoTaxi.setAdressDestino(et_destinoTaxi.getText().toString());
                pedidoTaxi.setDestino(null);

            }else {

                pedidoTaxi.setAdressDestino(ubicacionDestino.getAdresss());
            }


            pedidoTaxi.setPrecio(Float.parseFloat(et_precioTaxi.getText().toString()));
            pedidoTaxi.setNombre(tv_name.getText().toString());
            pedidoTaxi.setUid(firebaseUser.getUid());
            pedidoTaxi.setScoreUser(4.5);
            pedidoTaxi.setCodigo(databaseReference.push().getKey());
            pedidoTaxi.setUrlFotoUser(urlPhoto);
            pedidoTaxi.setComentario(et_comentTaxi.getText().toString()+"");




            if (switch_visa.isChecked()) {
                pedidoTaxi.setVisa(1);
            } else {
                pedidoTaxi.setVisa(0);
            }

            if (switch_polarizado.isChecked()) {
                pedidoTaxi.setPolarizado(1);
            } else {
                pedidoTaxi.setPolarizado(0);
            }

            if (switch_airCondicionado.isChecked()) {
                pedidoTaxi.setAireAcondicionado(1);
            } else {
                pedidoTaxi.setAireAcondicionado(0);
            }

            if (switch_mas4Pasajeros.isChecked()) {
                pedidoTaxi.setMas4Pasajeros(1);
            } else {
                pedidoTaxi.setMas4Pasajeros(0);
            }


            databaseReference.child("Country").child(DirectionsUtility.country).child(DirectionsUtility.adminArea).child("delayRT").child(pedidoTaxi.getCodigo()).setValue(pedidoTaxi).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserMain.this, "Error de conexion", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Intent intent = new Intent(getBaseContext(), NegociarTaxiActivity.class);
                    intent.putExtra("pedido", pedidoTaxi);
                    startActivityForResult(intent, MAKE_WALK);
                    vista.setVisibility(View.INVISIBLE);

                }
            });




        }
    }



    public void  logOut(){

        Handler handler = new Handler();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("¡ Aviso !");
        progressDialog.setMessage("Cerrando sesion...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                FirebaseConn.logOutFirebase();
                Intent lanzador = new Intent(UserMain.this,Principal.class);
                startActivity(lanzador);
                finish();
            }
        },2000);

    }


    public void getUser() {


        CollectionReference coRef =  firebaseFirestore.collection("users");
        Query query = coRef.whereEqualTo("uid",firebaseUser.getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                User user = task.getResult().getDocuments().get(0).toObject(User.class);
                tv_phone.setText(user.getPhone());
                tv_name.setText(user.getName());
                tv_email.setText(user.getEmail());
                urlPhoto = user.getUrlPhoto();

                Glide
                    .with(getBaseContext())
                    .load(user.getUrlPhoto())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                            Toast.makeText(UserMain.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(iv_photo);

                }

            }
        });





    }




    private boolean checkDate(){


        if(et_destinoTaxi.getText().toString().isEmpty() || et_precioTaxi.getText().toString().isEmpty() || ubicacionSalida == null){
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;

        }else {
            float precio = Float.parseFloat(et_precioTaxi.getText().toString());
            if(precio < 5){
                Toast.makeText(this, "Precio minimo: PEN 5", Toast.LENGTH_SHORT).show();
                return  false;
            }
        }

        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode){

            case SELECT_ADRESS:
                if (resultCode == RESULT_OK) {

                    if (data != null) {

                        int result = (int) data.getSerializableExtra("mode");

                        if (result == MODE_SALIDA) {
                            ubicacionSalida = (Ubicacion) data.getSerializableExtra("dato");

                            pedidoTaxi.setOrigen(ubicacionSalida);
                            pedidoTaxi.setAdressOrigen(ubicacionSalida.getAdresss());
                            et_salidaTaxi.setText(ubicacionSalida.getAdresss());


                        } else if (result == MODE_DESTINO) {
                            ubicacionDestino = (Ubicacion) data.getSerializableExtra("dato");
                            pedidoTaxi.setDestino(ubicacionDestino);
                            et_destinoTaxi.setText(ubicacionDestino.getAdresss());
                        }

                    }

                } else {


                    System.out.println("Errrorr" + resultCode);
                }
                break;

            case MAKE_WALK:

                Toast.makeText(this, "viaje realizado", Toast.LENGTH_SHORT).show();

        }

    }
}
