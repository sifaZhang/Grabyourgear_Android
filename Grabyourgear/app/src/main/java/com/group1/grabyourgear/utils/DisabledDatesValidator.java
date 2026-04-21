package com.group1.grabyourgear.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.ArrayList;
import java.util.List;

public class DisabledDatesValidator implements CalendarConstraints.DateValidator, Parcelable {

    private final List<Long> disabledDates;

    public DisabledDatesValidator(List<Long> disabledDates) {
        this.disabledDates = disabledDates;
    }

    @Override
    public boolean isValid(long date) {
        return !disabledDates.contains(date);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(disabledDates);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DisabledDatesValidator> CREATOR =
            new Creator<DisabledDatesValidator>() {
                @Override
                public DisabledDatesValidator createFromParcel(Parcel in) {
                    List<Long> list = new ArrayList<>();
                    in.readList(list, Long.class.getClassLoader());
                    return new DisabledDatesValidator(list);
                }

                @Override
                public DisabledDatesValidator[] newArray(int size) {
                    return new DisabledDatesValidator[size];
                }
            };
}
