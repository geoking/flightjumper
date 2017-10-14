package me.geoking.flightjumper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String location = "Big Ben, London";
        final Geocoder gc = new Geocoder(this);
        List<LatLng> ll = new ArrayList<LatLng>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            location = extras.getString("nameOfPlace");
        }

        if(Geocoder.isPresent()){
            try {
                List<Address> addresses= gc.getFromLocationName(location, 5); // get the found Address Objects

                ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                    if(a.getFeatureName() != null) {
                        location = a.getFeatureName();
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }
        LatLng coord = ll.get(0);
        mMap.addMarker(new MarkerOptions().position(coord).title(location).flat(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(5));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {

                List<String> countries = new ArrayList<String>();
                try {
                    List<Address> addresses = gc.getFromLocation(arg0.latitude, arg0.longitude, 5);
                    countries = new ArrayList<String>(); // A list to save the coordinates if they are available
                    for(Address a : addresses){
                        if(a.getCountryName() != null){
                            countries.add(a.getCountryName());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Context context = getApplicationContext();
                CharSequence text = countries.get(0);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }
        });
    }

    private void onMapClick(LatLng arg0) {


    }
}
