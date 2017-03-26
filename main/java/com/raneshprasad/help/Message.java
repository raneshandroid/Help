package com.raneshprasad.help;

/**
 * Created by anubhaprasad on 3/25/17.
 */
import android.net.Uri;

import java.io.Serializable;

public class Message implements Serializable {
    String id, message;
    Uri uri;

    public Message() {
    }

    public Message(String id, String message, String createdAt) {
        this.id = id;
        this.message = message;


    }

    public Message(String id, Uri uri, String createdAt){
        this.uri = uri;
        this.id = id;
        //this.message = message;
    }

    public String getId() {
        return id;
    }
    public Uri getUri(){return uri;}
    public void setId(String id) {
        this.id = id;
    }
    public void setUri(Uri uri){this.uri = uri;}
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}

