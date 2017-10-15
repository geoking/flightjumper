package me.geoking.flightjumper;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String originCountry;
    private String destCountry;
    private String originCode;
    private String destCode;
    private boolean slideAppear = false;

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
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
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
                        originCountry = a.getCountryName();
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }
        LatLng coord = ll.get(0);
        mMap.addMarker(new MarkerOptions().position(coord).title(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coord));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(5));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {

                if (slideAppear == true) {
                    SlidingUpPanelLayout slide = (SlidingUpPanelLayout) findViewById(R.id.slidePanel);
                    slide.setPanelHeight(0);
                    slideAppear = false;
                }
                else {

                    List<String> countries = new ArrayList<String>();
                    try {
                        List<Address> addresses = gc.getFromLocation(arg0.latitude, arg0.longitude, 5);
                        countries = new ArrayList<String>(); // A list to save the coordinates if they are available
                        for (Address a : addresses) {
                            if (a.getCountryName() != null || !a.getCountryName().equals("")) {
                                countries.add(a.getCountryName());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    destCountry = countries.get(0).toString();

                    String url = "http://partners.api.skyscanner.net/apiservices/autosuggest/v1.0/UK/GBP/en-GB/?query=" + originCountry.replace(" ", "%20") + "&apiKey=ha906464854775459164611892547937";

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        // Convert String to json object
                                        JSONObject json = new JSONObject(response);

                                        // get LL json object
                                        JSONArray json_Place = json.getJSONArray("Places");

                                        // get value from LL Json Object
                                        originCode = json_Place.getJSONObject(0).getString("CountryId");

                                        String url = "http://partners.api.skyscanner.net/apiservices/autosuggest/v1.0/UK/GBP/en-GB/?query=" + destCountry.replace(" ", "%20") + "&apiKey=ha906464854775459164611892547937";

                                        // Request a string response from the provided URL.
                                        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {

                                                        try {
                                                            // Convert String to json object
                                                            JSONObject json = new JSONObject(response);

                                                            // get LL json object
                                                            JSONArray json_Place = json.getJSONArray("Places");

                                                            // get value from LL Json Object
                                                            destCode = json_Place.getJSONObject(0).getString("CountryId");

                                                            String url = "http://partners.api.skyscanner.net/apiservices/browseroutes/v1.0/GB/gbp/en-GB/" + originCode + "/" + destCode + "/2017-10/2017-10?apikey=ha906464854775459164611892547937";

                                                            // Request a string response from the provided URL.
                                                            StringRequest stringRequest3 = new StringRequest(Request.Method.GET, url,
                                                                    new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {

                                                                            try {
                                                                                JSONObject json = new JSONObject(response);
                                                                                JSONArray json_Quotes = json.getJSONArray("Quotes");
                                                                                JSONArray json_Places = json.getJSONArray("Places");

                                                                                int cheapestPrice = 999999;
                                                                                int originId = 0;
                                                                                String originPlace = "";
                                                                                int destinationId = 0;
                                                                                String destinationPlace = "";
                                                                                int price = 999999;

                                                                                for (int i = 0; i < json_Quotes.length(); i++) {
                                                                                    if (json_Quotes.getJSONObject(i).has("MinPrice")) {
                                                                                        price = json_Quotes.getJSONObject(i).getInt("MinPrice");
                                                                                    }
                                                                                    if (price < cheapestPrice) {
                                                                                        cheapestPrice = price;
                                                                                        originId = json_Quotes.getJSONObject(i).getJSONObject("OutboundLeg").getInt("OriginId");
                                                                                        destinationId = json_Quotes.getJSONObject(i).getJSONObject("OutboundLeg").getInt("DestinationId");
                                                                                    }
                                                                                }

                                                                                for (int i = 0; i < json_Places.length(); i++) {
                                                                                    int placeID = json_Places.getJSONObject(i).getInt("PlaceId");

                                                                                    if (originId == placeID && json_Places.getJSONObject(i).has("CityName")) {
                                                                                        originPlace = json_Places.getJSONObject(i).getString("CityName");
                                                                                    }
                                                                                    if (destinationId == placeID && json_Places.getJSONObject(i).has("CityName")) {
                                                                                        destinationPlace = json_Places.getJSONObject(i).getString("CityName");
                                                                                    }
                                                                                }

                                                                                changeScrollLabel(originPlace + " - " + destinationPlace + ": £" + cheapestPrice);

                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                            SlidingUpPanelLayout slide = (SlidingUpPanelLayout) findViewById(R.id.slidePanel);
                                                                            slide.setPanelHeight(200);

                                                                        }
                                                                    }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {

                                                                }
                                                            });

                                                            queue.add(stringRequest3);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });

                                        queue.add(stringRequest2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                    slideAppear = true;
                }

            }
        });
    }

    private void changeScrollLabel(String str) {

        TextView slideText = (TextView) findViewById(R.id.textView3);

        slideText.setText(str);

    }
}
