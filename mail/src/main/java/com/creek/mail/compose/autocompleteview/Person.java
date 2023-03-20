package com.creek.mail.compose.autocompleteview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Simple container object for contact data
 * <p>
 * Created by mgod on 9/12/13.
 *
 * @author mgod
 */
public class Person implements Parcelable {
    private String name;
    private String email;

    public Person(String n, String e) {
        name = n;
        email = e;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
    }

    private Person(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}