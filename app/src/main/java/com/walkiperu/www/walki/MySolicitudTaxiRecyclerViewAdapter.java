package com.walkiperu.www.walki;

import android.Manifest;
import android.content.Context;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import entidades.User;

import static android.support.v4.content.ContextCompat.getSystemService;

/**
 * {link RecyclerView.Adapter} that can display a {link DummyItem} and makes a call to the
 * specified {link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySolicitudTaxiRecyclerViewAdapter extends RecyclerView.Adapter<MySolicitudTaxiRecyclerViewAdapter.ViewHolder> {

    private List<Pedido> mValues;
    private List<User> users;
    private FragmentManager fragmentManager;
    private Context context;


    public MySolicitudTaxiRecyclerViewAdapter(List<Pedido> items,FragmentManager fragmentManager) {
        mValues = items;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_solicitudtaxi, parent, false);
        context = parent.getContext();

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {



        holder.getTv_nombre().setText(mValues.get(position).getNombre());
        holder.getScore().setText(String.valueOf(mValues.get(position).getScoreUser()));
        holder.getTv_salida().setText(DirectionsUtility.getSubDirections(mValues.get(position).getAdressOrigen()));
        holder.getTv_destino().setText(DirectionsUtility.getSubDirections(mValues.get(position).getAdressDestino()));
        holder.getTv_coment().setText(mValues.get(position).getComentario());
        //get img user
        Glide
            .with(context)
            .load(mValues.get(position).getUrlFotoUser())
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
            .into(holder.getCircleImageView());

        // OBTENER LA DISTANCIA ENTRE EL PASAJERO Y EL DRIVER Y DAR FORMATO A LA CADENA QUE SE MOSTRARA
        LatLng destino = new LatLng(mValues.get(position).getOrigen().getLatitud(),mValues.get(position).getOrigen().getLogitud());

        double metros = DirectionsUtility.getDistance( obtenerUbicacion(),destino );

        String distancia = "";
        long m;
        float km;
        if(Math.round(metros) > 999){
             km = (float)(Math.round(metros)/1000.0);
             distancia = ( String.format("%.1f",km) ) + " km";
        }else{
            m = Math.round(metros);
            distancia = m +" m";
        }

        holder.getTv_distan().setText(distancia);

        //

        holder.getTv_oferta().setText(" PEN  " + mValues.get(position).getPrecio());

        if(mValues.get(position).getVisa() == 1){
            holder.getOpc_visa().setVisibility(View.VISIBLE);
        }
        if(mValues.get(position).getMas4Pasajeros() == 1){
            holder.getOpc_mas4().setVisibility(View.VISIBLE);
        }
        if(mValues.get(position).getPolarizado() == 1){
            holder.getOpc_polari().setVisibility(View.VISIBLE);
        }

        if(mValues.get(position).getAireAcondicionado() == 1){
            holder.getOpc_aire().setVisibility(View.VISIBLE);
        }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment dialogFragment = new DialogFragment();

                Bundle bundle = new Bundle();
                LatLng destino = new LatLng(mValues.get(position).getOrigen().getLatitud(),mValues.get(position).getOrigen().getLogitud());

                double metros = DirectionsUtility.getDistance( obtenerUbicacion(),destino );

                bundle.putSerializable("pedido", mValues.get(position));
                bundle.putSerializable("distance",metros);

                dialogFragment.setArguments(bundle);



                dialogFragment.show(fragmentManager, "DIALOGO");


            }
        });

    }




    public LatLng obtenerUbicacion() {

        LocationManager locationManager = getSystemService(context,LocationManager.class);

        String provider = locationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location myLocacion = locationManager.getLastKnownLocation(provider);
        return new LatLng( myLocacion.getLatitude(),myLocacion.getLongitude());
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }







    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private CircleImageView circleImageView;
        private TextView tv_nombre;
        private TextView score;
        private TextView tv_salida;
        private TextView tv_destino;
        private TextView tv_coment;
        private TextView tv_distan;


        private ImageView opc_visa,
                            opc_polari,
                                opc_mas4,
                                    opc_aire;


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

        public TextView getTv_distan() {
            return tv_distan;
        }

        public void setTv_distan(TextView tv_distan) {
            this.tv_distan = tv_distan;
        }

        public TextView getTv_oferta() {
            return tv_oferta;
        }

        public void setTv_oferta(TextView tv_oferta) {
            this.tv_oferta = tv_oferta;
        }

        private TextView tv_oferta;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cv_pedido);
            circleImageView = view.findViewById(R.id.imv_photo_respuesta);
            tv_nombre = view.findViewById(R.id.tv_nombre_rv);
            tv_salida = view.findViewById(R.id.tv_salida_rv);
            tv_destino = view.findViewById(R.id.tv_destino_rv);
            score = view.findViewById(R.id.tv_score_rv);
            tv_coment = view.findViewById(R.id.tv_coment_rv);
            tv_distan = view.findViewById(R.id.tv_distancia);
            tv_oferta = view.findViewById(R.id.tv_oferta);

            opc_aire = view.findViewById(R.id.opc_aire);
            opc_mas4 = view.findViewById(R.id.opc_mas4);
            opc_polari = view.findViewById(R.id.opc_polari);
            opc_visa = view.findViewById(R.id.opc_visa);


            cardView.setClickable(true);

        }

        public CardView getCardView() {
            return cardView;
        }

        public void setCardView(CardView cardView) {
            this.cardView = cardView;
        }

        public CircleImageView getCircleImageView() {
            return circleImageView;
        }

        public void setCircleImageView(CircleImageView circleImageView) {
            this.circleImageView = circleImageView;
        }

        public TextView getTv_nombre() {
            return tv_nombre;
        }

        public void setTv_nombre(TextView tv_nombre) {
            this.tv_nombre = tv_nombre;
        }

        public TextView getScore() {
            return score;
        }

        public void setScore(TextView score) {
            this.score = score;
        }

        public TextView getTv_salida() {
            return tv_salida;
        }

        public void setTv_salida(TextView tv_salida) {
            this.tv_salida = tv_salida;
        }

        public TextView getTv_destino() {
            return tv_destino;
        }

        public void setTv_destino(TextView tv_destino) {
            this.tv_destino = tv_destino;
        }

        public TextView getTv_coment() {
            return tv_coment;
        }

        public void setTv_coment(TextView tv_coment) {
            this.tv_coment = tv_coment;
        }


    }
}
