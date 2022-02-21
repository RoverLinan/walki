package com.walkiperu.www.walki;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NegociarTaxiActivity extends AppCompatActivity {

    private Pedido pedidoTaxi;
    private TextView tv_origen,
                        tv_destino,
                            tv_pen;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negociar_taxi);


        FragmentManager fragmentManager = getFragmentManager();




        pedidoTaxi = (Pedido)getIntent().getSerializableExtra("pedido");
        tv_origen = findViewById(R.id.tv_origen_wait);
        tv_destino = findViewById(R.id.tv_destino_wait);
        tv_pen = findViewById(R.id.tv_pen_wait);

        tv_origen.setText(DirectionsUtility.getSubDirections(pedidoTaxi.getAdressOrigen()));
        tv_destino.setText(DirectionsUtility.getSubDirections(pedidoTaxi.getAdressDestino()));
        tv_pen.setText("PEN " + pedidoTaxi.getPrecio());



        Bundle bundle = new Bundle();

        bundle.putSerializable("request",pedidoTaxi);
        RespuestaTaxiFragment respuestaTaxiFragment = new RespuestaTaxiFragment();
        respuestaTaxiFragment.setArguments(bundle);

        fragmentManager.beginTransaction().add(R.id.fragment_content_respuesta_driver,respuestaTaxiFragment).commit();


    }


    @Override
    protected void onStart() {
        super.onStart();



    }


}
