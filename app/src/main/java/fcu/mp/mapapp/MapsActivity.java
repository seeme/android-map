package fcu.mp.mapapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import fcu.mp.mapapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

  private GoogleMap mMap;
  private ActivityMapsBinding binding;

  private LocationManager mLocationManager;

  @Override
  protected void onResume() {
    super.onResume();
    mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
      if (ActivityCompat.checkSelfPermission(this,
          Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        Criteria criteria = new Criteria();
        String bestString =  mLocationManager.getBestProvider(criteria, true);
        Log.v("Sammy", bestString);
        mLocationManager.requestLocationUpdates(bestString, 1000, 1, this);
      } else {
        Toast.makeText(this, "請同意", Toast.LENGTH_LONG).show();
        Log.v("Sammy", "Permision denied");
      }
    } else {
      Toast.makeText(this, "", Toast.LENGTH_LONG).show();
    }
  }



  @Override
  protected void onPause() {
    super.onPause();
    if (ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      mLocationManager.removeUpdates(this);
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT > 23) {
      int hasPermission = ActivityCompat.checkSelfPermission(this,
          Manifest.permission.ACCESS_FINE_LOCATION);
      if( hasPermission != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        return;
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if( requestCode == 1) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        setMyLocation();
      } else {
        Toast.makeText(this,"未取得授權", Toast.LENGTH_LONG).show();
        finish();
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  private void setMyLocation() throws SecurityException {
    mMap.setMyLocationEnabled(true);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityMapsBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    requestPermission();
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

    // Add a marker in Sydney and move the camera
    LatLng taipei101 = new LatLng(25.033611, 121.565);

    CameraPosition cameraPosition = new CameraPosition.Builder().
        target(taipei101).zoom(17).bearing(90).tilt(90).build();

    //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    mMap.getUiSettings().setZoomControlsEnabled(true);
    mMap.getUiSettings().setCompassEnabled(false);
    mMap.getUiSettings().setRotateGesturesEnabled(true);
  }

  @Override
  public void onLocationChanged(@NonNull Location location) {
    String x ="緯="+Double.toString(location.getLatitude());
    String y ="經="+Double.toString(location.getLongitude());
    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 17));
    try {
      mMap.setMyLocationEnabled(true);
    }catch (SecurityException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void onLocationChanged(@NonNull List<Location> locations) {
    LocationListener.super.onLocationChanged(locations);
  }

  @Override
  public void onFlushComplete(int requestCode) {
    LocationListener.super.onFlushComplete(requestCode);
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    LocationListener.super.onStatusChanged(provider, status, extras);
  }

  @Override
  public void onProviderEnabled(@NonNull String provider) {
    LocationListener.super.onProviderEnabled(provider);
  }

  @Override
  public void onProviderDisabled(@NonNull String provider) {
    LocationListener.super.onProviderDisabled(provider);
  }

  @Override
  public void onPointerCaptureChanged(boolean hasCapture) {
    super.onPointerCaptureChanged(hasCapture);
  }
}