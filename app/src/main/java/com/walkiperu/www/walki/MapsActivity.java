package com.walkiperu.www.walki;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import entidades.User;
import entidades.Usuario;

import static android.support.v4.content.ContextCompat.getSystemService;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location locacion;
    private int mode;
    private Pedido pedido;

    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;


    private ArrayList<LatLng> points = null;
    private RequestQueue request;
    private final String API_KEY = "AIzaSyAPh64cmLGt9eG0Em3NLHjTSroC_Rkhy1g";
    private JsonObjectRequest jsonObjectRequest;
    private List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.activity_maps, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle b = new Bundle();

        b = getArguments();

        mode = (int) b.getInt("MODE");

        if (mode == 2) {

            pedido = (Pedido) b.getSerializable("pedido");


        }


        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        // ACTIVAR LA UNBICACION EN GOOGLE MAPS
        /* - primero se debe cambiar la version de la Api de android a  API 23 para poder usar el getContex
            ya que no funcionara para versiones menores a esta
         */
        mMap = googleMap;


        switch (mode) {

            case 1:


                obtenerUsuario();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(miUbicacion(), 16.5f);
                mMap.animateCamera(cameraUpdate);

                break;

            case 2:


                LatLng latLng = miUbicacion();

                request = Volley.newRequestQueue(getContext());


                webServiceObtenerRuta(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude),
                        String.valueOf(pedido.getDestino().getLatitud()), String.valueOf(pedido.getDestino().getLogitud()),
                        String.valueOf(pedido.getOrigen().getLatitud()), String.valueOf(pedido.getOrigen().getLogitud()), pedido);
                break;

            case 3:



        }


    }


    private LatLng miUbicacion() {

        LocationManager locationManager = getSystemService(getContext(), LocationManager.class);

        String provider = locationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        Location myLocacion = locationManager.getLastKnownLocation(provider);

        return new LatLng(myLocacion.getLatitude(), myLocacion.getLongitude());


        // locationManager.requestLocationUpdates(provider,5000,0,locationListener);  actualizar ubicacion...

    }


    public void obtenerUsuario() {

         final CircleImageView marker_user = getActivity().findViewById(R.id.marker_user);
         final FrameLayout frameLayout = getActivity().findViewById(R.id.fragm_img_user);
         frameLayout.setVisibility(View.VISIBLE);

        CollectionReference coRef =  firebaseFirestore.collection("users");
        Query query = coRef.whereEqualTo("uid",firebaseUser.getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    User user = task.getResult().getDocuments().get(0).toObject(User.class);

                    Glide
                            .with(getContext())
                            .load(user.getUrlPhoto())
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                                    Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(marker_user);

                }

            }
        });


    }



    public void webServiceObtenerRuta(String latitudInicial, String longitudInicial, String latitudFinal, String longitudFinal,
                                      String waypoLatitud, String waypoLongitud, final Pedido pedido) {

        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                +"&destination="+latitudFinal+","+longitudFinal+"&waypoints=via:"+waypoLatitud+"%2C"+waypoLongitud+"&key="+API_KEY;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {

                    jRoutes = response.getJSONArray("routes");

                    /** Traversing all routes */
                    for(int i=0;i<jRoutes.length();i++){
                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for(int j=0;j<jLegs.length();j++){


                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for(int k=0;k<jSteps.length();k++){
                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for(int l=0;l<list.size();l++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);
                                }
                            }
                            routes.add(path);


                        }
                    }
                    obtenerPuntos();


                    if(points != null) {

                        pintarRuta();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("NO SE PUEDE CONECTAR");
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        request.add(jsonObjectRequest);
    }


    private  List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }



    public void obtenerPuntos() {

        for (int i = 0; i <  routes.size(); i++) {
            points = new ArrayList<LatLng>();


            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }
        }
    }


    public void pintarRuta(){



        PolylineOptions polylineOptions = new PolylineOptions();

        if(points != null) {

            polylineOptions.addAll(points);

            int centerIndex = Math.round(points.size()/2);

            LatLng centerLatLng  =  points.get(centerIndex);
            polylineOptions.width(10);
            polylineOptions.color(Color.BLACK);
            mMap.addPolyline(polylineOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 15));
            MarkerOptions conductor = new MarkerOptions();
            MarkerOptions origen = new MarkerOptions();
            MarkerOptions destino = new MarkerOptions();


            conductor
                    .position(miUbicacion())
                    .icon(bitmapDescriptorFromVector(getContext(),R.drawable.ic_auto_driver1));
            mMap.addMarker(conductor);
            origen
                    .position(new LatLng(pedido.getOrigen().getLatitud(),pedido.getOrigen().getLogitud()))
                    .title(pedido.getAdressOrigen())
                    .icon(bitmapDescriptorFromVector( getContext(),R.drawable.ic_point_salida_svg2));

            Marker markOrigen =  mMap.addMarker(origen);

            destino
                    .position(new LatLng(pedido.getDestino().getLatitud(),pedido.getDestino().getLogitud()))
                    .title(pedido.getAdressDestino())
                    .icon(bitmapDescriptorFromVector( getContext(),R.drawable.ic_point_destino_svg2));
            Marker markerDestino =  mMap.addMarker(destino);


            // mostrar titulo del marcador   marker.showInfoWindows()
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 15));

        }
    }



    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }







}