package ie.com.lovetotravel20.models;

import java.io.Serializable;

public class GoogleUser implements Serializable {

    private String name, email, photoUrl, Uid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public GoogleUser(String name, String email, String photoUrl, String uid) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        Uid = uid;
    }

    public GoogleUser() {
    }
}
