package vfediuchko.pins.test.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmResults;
import vfediuchko.pins.test.PreferenceStorage;
import vfediuchko.pins.test.R;
import vfediuchko.pins.test.Utils;
import vfediuchko.pins.test.db.IRealmResultCallback;
import vfediuchko.pins.test.db.PinRepository;
import vfediuchko.pins.test.db.model.Pin;

public class FragmentMap extends Fragment {
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String MARKER_TITLE = "title";
    public static final int PERMISSIONS_REQUEST_FIND_LOCATION = 1;

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isLocationInited;
    private boolean focusOnMarker;
    private double markerLon;
    private double markerLat;
    private String userId;
    private Bundle bundle;
    private MapView mMapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = PreferenceStorage.getActiveUserId();
        bundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

       View mainView = inflater.inflate(R.layout.fragment_map, container, false);

        MapsInitializer.initialize(getActivity());

        mMapView = (MapView) mainView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        setUpMap(mainView);

        return mainView;
    }

    private void setUpMap(View inflatedView) {
        if (googleMap == null) {
            ((MapView) inflatedView.findViewById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    FragmentMap.this.googleMap = googleMap;
                    initMapDetails();
                    addUserMarkers();
                    if (null != bundle) {
                        isLocationInited = true;
                        focusOnMarker = true;
                        markerLon = bundle.getDouble(LONGITUDE);
                        markerLat = bundle.getDouble(LATITUDE);
                    }
                    if (focusOnMarker)
                        focusOnLocation(markerLat, markerLon);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    private void addMarker(String title, double lat, double lng) {

        if (null != googleMap) {
            googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(title)
                            .draggable(true)
            );
        }
    }

    void initMapDetails() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FIND_LOCATION);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setPadding(0, Utils.dpToPx(getContext(), 48), 0, 0);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final EditText edittext = new EditText(getActivity());
                alert.setMessage(getString(R.string.pin_place_title));
                alert.setTitle(getString(R.string.pin_place_description));

                alert.setView(edittext);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = edittext.getText().toString();

                        addMarker(title, latLng.latitude, latLng.longitude);
                        PinRepository pinRepository = new PinRepository();

                        Realm realm = Realm.getInstance(getActivity());
                        realm.beginTransaction();
                        Pin pin = realm.createObject(Pin.class);
                        pin.setId(pinRepository.getNextKey());
                        pin.setUserId(userId);
                        pin.setLatitude(latLng.latitude);
                        pin.setLongitude(latLng.longitude);
                        pin.setTitle(title);
                        realm.commitTransaction();
                    }
                });
                alert.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
            }
        });
        goToUserLocation();
    }

    private void goToUserLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                if (!isLocationInited) {
                    focusOnLocation(location.getLatitude(), location.getLongitude());
                    isLocationInited = true;
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FIND_LOCATION);
            return;
        }
        locationManager.removeUpdates(locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FIND_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.removeUpdates(locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
                break;
            }

        }
    }

    private void focusOnLocation(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 11f);
        googleMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onDestroyView() {
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null)
            fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
        fm.executePendingTransactions();
        super.onDestroyView();
    }

    private void addUserMarkers() {
        PinRepository pinRepository = new PinRepository();
        pinRepository.getAllUserPins(getActivity(), userId, new IRealmResultCallback() {
            @Override
            public void onSuccess(RealmResults<Pin> realmResults) {
                for (Pin pin : realmResults) {
                    addMarker(pin.getTitle(), pin.getLatitude(), pin.getLongitude());
                }
            }
        });
    }
}
