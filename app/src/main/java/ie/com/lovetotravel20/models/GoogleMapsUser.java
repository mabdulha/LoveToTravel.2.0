package ie.com.lovetotravel20.models;

import com.google.android.gms.maps.model.LatLng;

public class GoogleMapsUser {

    public String username;
    public double latitude, longitude;

    public GoogleMapsUser() {

    }

    public GoogleMapsUser(String username, double latitude, double longitude) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
