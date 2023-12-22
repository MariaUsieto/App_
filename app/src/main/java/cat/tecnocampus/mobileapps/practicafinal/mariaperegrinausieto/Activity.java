package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Activity implements Parcelable {

    private String id;
    private String key;
    //private String text;
    private Boolean done;

    public Activity(String id, String key, Boolean done) {
        this.id = id;
        this.key = key;
        this.done = done;
    }

    public Activity(){}

    protected Activity(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(key);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(done);
        }
    }

    public void readFromParcel(Parcel in){
        id = in.readString();
        key = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            done = in.readBoolean();
        }
    }
}
