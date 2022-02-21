package com.walkiperu.www.walki;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import entidades.Driver;

public class DriverActivity extends AppCompatActivity  implements  SolicitudTaxiFragment.OnListFragmentInteractionListener{


    private DatabaseReference databaseReference;
    private Driver driver;


    private LinearLayout pedidos ;
    private LinearLayout ingresos;
    private LinearLayout credenciales;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        android.app.FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().add(R.id.driver_fragment, new SolicitudTaxiFragment()).commit();

        pedidos = findViewById(R.id.boton_pedidos);
        ingresos = findViewById(R.id.boton_ingresos);
        credenciales = findViewById(R.id.boton_credenciales);




        pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                android.app.FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.driver_fragment, new SolicitudTaxiFragment()).commit();

            }
        });


        ingresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.FragmentManager fragmentManager1 = getFragmentManager();

                fragmentManager1.beginTransaction().replace(R.id.driver_fragment,new IngresosFragment()).commit();

                Toast.makeText(DriverActivity.this, " INGRESOS", Toast.LENGTH_SHORT).show();

            }
        });


        credenciales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DriverActivity.this, " credenciales  ", Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void getDataDriver(){



    }





    @Override
    public void onListFragmentInteraction(List<Pedido> pedidos) {



    }
}
