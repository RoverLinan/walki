package com.walkiperu.www.walki;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class RequestAcceptActivity extends AppCompatActivity {

    private Pedido request;
    private MapsActivity map;


    private Button  b_cancel,
                        b_finalice,
                            b_arrived,
                                b_rout,
                                    b_call,
                                        b_sos;
    private TextView    tv_name,
                            tv_score,
                                tv_locationA,
                                    tv_locationB,
                                        tv_coment,
                                            tv_ofert,
                                                tv_cancel,
                                                    tv_time;






    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_request_accept);

        request = (Pedido)getIntent().getSerializableExtra("request");

        initializationView();
        setTextViews();





        FragmentManager fragmentManager = getFragmentManager();
        MapsActivity mapsActivity = new MapsActivity();
        Bundle b = new Bundle();
        b.putSerializable("MODE", 3);
        mapsActivity.setArguments(b);

        fragmentManager.beginTransaction().add(R.id.ara_frag_container, mapsActivity).commit();





    }




    public void initializationView(){

        tv_cancel = findViewById(R.id.ara_b_cancel);
        tv_name = findViewById(R.id.ara_name_user);
        tv_coment = findViewById(R.id.ara_coment);
        tv_locationA = findViewById(R.id.ara_locationA);
        tv_locationB = findViewById(R.id.ara_locationB);
        tv_ofert = findViewById(R.id.ara_ofert);
        tv_score = findViewById(R.id.ara_score);
        tv_time = findViewById(R.id.ara_tv_time);












    }


    public void setTextViews(){

        tv_name.setText(request.getNombre());
        tv_locationA.setText(request.getAdressOrigen());
        tv_locationB.setText(request.getAdressDestino());
        tv_coment.setText(request.getComentario());
        tv_ofert.setText("PEN " +request.getPrecio());
    }

}
