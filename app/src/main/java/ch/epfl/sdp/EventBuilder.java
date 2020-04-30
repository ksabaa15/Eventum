package ch.epfl.sdp;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.util.Date;

/**
 * Builder to create an Event instance, it handles default values and input checking.
 */
public class EventBuilder {
    private String mTitle;
    private String mDescription;
    private Date mDate;
    private String mAddress = "Lausanne, Switzerland";
    private LatLng mLocation = new LatLng(10, 10);
    private String mImageId;
    private final String DEFAULT_URL = "https://firebasestorage.googleapis.com/v0/b/eventum-6a6b7.appspot.com" +
            "/o/eventDefault.jpg?alt=media&token=a6d345fa-a513-478d-a019-2307ee50022b";

    /**
     * @return the Event instance using the provided attributes
     */
    public Event build() {
        if (mImageId == null) mImageId = DEFAULT_URL;
        ObjectUtils.verifyNotNull(mTitle, mDescription, mDate, mLocation, mImageId);
        if (mTitle.isEmpty()) throw new IllegalArgumentException("No title specified");
        if (mDescription.isEmpty()) throw new IllegalArgumentException("No description specified");
        return new Event(mTitle, mDescription, mDate, mAddress, mLocation, mImageId);
    }

    /**
     * Set the title attribute for the Event to be created.
     *
     * @param title to assign
     * @return caller class with the title attribute assigned
     */
    public EventBuilder setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * Set the description attribute for the Event to be created.
     *
     * @param description to assign
     * @return caller class with the description attribute assigned
     */
    public EventBuilder setDescription(String description) {
        this.mDescription = description;
        return this;
    }

    /**
     * Set the date attribute for the Event to be created using the Date class.
     *
     * @param date to assign
     * @return caller class with the date attribute assigned
     */
    public EventBuilder setDate(Date date) {
        this.mDate = date;
        return this;
    }

    /**
     * Set the date attribute for the Event to be created using human readable date.
     *
     * @param date to assign
     * @return caller class with the date attribute assigned
     */
    public EventBuilder setDate(String date) {
        try {
            this.mDate = Event.parseDate(date);
        } catch (ParseException e) {
            this.mDate = new Date();
        }
        return this;
    }

    /**
     * Set the address attribute for the Event to be created.
     *
     * @param address to assign
     * @return caller class with the address attribute assigned
     */
    public EventBuilder setAddress(String address) {
        this.mAddress = address;
        return this;
    }

    /**
     * Set the location attribute for the Event to be created.
     *
     * @param location to assign
     * @return caller class with the location attribute assigned
     */
    public EventBuilder setLocation(LatLng location) {
        this.mLocation = location;
        return this;
    }

    /**
     * Set the image identifier attribute for the Event to be created.
     *
     * @param imageId to assign
     * @return caller class with the image identifier attribute assigned
     */
    public EventBuilder setImageId(String imageId) {
        this.mImageId = imageId;
        return this;
    }
}
