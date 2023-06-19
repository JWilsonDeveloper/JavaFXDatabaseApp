package model;

import java.time.ZonedDateTime;

/** This class defines the Appointment object.
 * @author James Wilson
 */
public class Appointment {
    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private ZonedDateTime utcStart;
    private ZonedDateTime utcEnd;
    private int customerId;
    private int userId;

    public Appointment(int appointmentId, String title, String description, String location, String contact, String type, ZonedDateTime utcStart, ZonedDateTime utcEnd, int customerId, int userId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.utcStart = utcStart;
        this.utcEnd = utcEnd;
        this.customerId = customerId;
        this.userId = userId;
    }

    /**
     * @return The appointment's id.
     */
    public int getAppointmentId() {
        return appointmentId;
    }

    /**
     * @param appointmentId The integer with which the appointment's id will be set.
     */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * @return The appointment's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The String with which the appointment's title will be set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The appointment's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The String with which the appointment's description will be set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The appointment's location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location The String with which the appointment's location will be set.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return The appointment's contact.
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact The String with which the appointment's contact will be set.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * @return The appointment's type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The String with which the appointment's type will be set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The appointment's start ZonedDateTime in utc time.
     */
    public ZonedDateTime getUtcStart() {
        return utcStart;
    }

    /**
     * @param utcStart The utc ZonedDateTime with which the appointment's start ZonedDateTime will be set.
     */
    public void setUtcStart(ZonedDateTime utcStart) {
        this.utcStart = utcStart;
    }

    /**
     * @return The appointment's end ZonedDateTime in utc time.
     */
    public ZonedDateTime getUtcEnd() {
        return utcEnd;
    }

    /**
     * @param utcEnd The utc ZonedDateTime with which the appointment's end ZonedDateTime will be set.
     */
    public void setUtcEnd(ZonedDateTime utcEnd) {
        this.utcEnd = utcEnd;
    }

    /**
     * @return The appointment's customer id.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId The integer with which the appointment's customer id will be set.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * @return The appointment's user id.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId The integer with which the appointment's user id will be set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
