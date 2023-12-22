package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class User implements Comparable<User>, Parcelable {
    private String email;
    private Boolean active;
    private ArrayList<Activity> activities;

    public User(){
        activities = new ArrayList<>();
        this.active = false;
    }

    public User(String email, String username, ArrayList<Activity> activities, Boolean active) {
        this.email = email;
        this.activities = activities;
        this.active = active;
    }

    protected User(Parcel in) {
        email = in.readString();
        byte tmpActive = in.readByte();
        active = tmpActive == 0 ? null : tmpActive == 1;
        activities = in.createTypedArrayList(Activity.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }

    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    public void deleteActivity(Activity activity) {
        this.activities.remove(activity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(email);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            parcel.writeBoolean(active);
        parcel.writeTypedList(activities);
    }

    @Override
    public int compareTo(User user) {
        int numThis = activities.size();
        int numUser = user.getActivities().size();
        if(numThis == numUser) return 0;
        else if(numThis>numUser) return -1;
        else return 1;
    }
}
