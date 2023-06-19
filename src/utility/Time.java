package utility;

import DAO.DBAppointments;
import controller.UpdateAppointmentController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Customer;
import java.sql.SQLException;
import java.time.*;

/** This class is a collection of methods relating to the conversion of times across timezones.
 * @author James Wilson
 */
public class Time {

    /** This method converts a time in utc to local time, based on the user's system's zoneId
     * @param utcZDT A utc ZonedDateTime.
     * @return The time ZonedDateTime in local time.
     */
    public static ZonedDateTime utcToLocal(ZonedDateTime utcZDT) {
        return ZonedDateTime.ofInstant(utcZDT.toInstant(), ZoneId.systemDefault());
    }

    /** This method converts a local time to utc.
     * @param localZDT A ZonedDateTime to be converted to utc.
     * @return The utc ZonedDateTime.
     */
    public static ZonedDateTime localToUtc(ZonedDateTime localZDT){
        return ZonedDateTime.ofInstant(localZDT.toInstant(), ZoneId.of("UTC"));
    }

    /** This method returns the times of a specified date for which a specified customer can be scheduled an appointment.
     * It begins by creating a list of each hour in utc time during which the company is open for business.
     * Then it converts those hours into the user's local time.
     * Then it removes any hours during which the customer already has an appointment scheduled.
     * @param selectedCustomer The selected customer.
     * @param startDate The date for which the selected customer may be scheduled an appointment.
     * @throws SQLException
     * @return A list of times for which the customer can be scheduled an appointment.
     */
    public static ObservableList<LocalTime> getAvailableTimes(Customer selectedCustomer, LocalDate startDate, Boolean updating) throws SQLException {
        ObservableList<LocalTime> availableTimesUtc = FXCollections.observableArrayList();
        ObservableList<LocalTime> availableTimesLocal = FXCollections.observableArrayList();

        // Create a list of each hour in utc time during which the company is open for business.
        for (int h = 12; h < 24; ++h){
            availableTimesUtc.add(LocalTime.of(h, 00));
        }
        for (int h = 0; h < 2; ++h){
            availableTimesUtc.add(LocalTime.of(h, 00));
        }

        // Convert the business hours list into local time.
        for(LocalTime hourUtc: availableTimesUtc) {
            ZonedDateTime utcZdt = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(), hourUtc), ZoneId.of("UTC"));
            ZonedDateTime localZdt = Time.utcToLocal(utcZdt);
            availableTimesLocal.add(localZdt.toLocalTime());
        }

        // Get a list of the customer's appointments.
        ObservableList<Appointment> customerAppointments = FXCollections.observableArrayList();
        for(Appointment a: DBAppointments.getAllAppointments()){
            if (a.getCustomerId() == selectedCustomer.getCustomerId()){
                customerAppointments.add(a);
            }
        }

        // For each of the customer's existing appointments, if the appointment's start date/time matches an available hour,
        // that hour is removed from the available times UNLESS that hour is the previously scheduled hour of an appointment currently being updated,
        // in which case the hour will remain an option.
        for(Appointment a: customerAppointments) {
            ZonedDateTime localStartZdt = Time.utcToLocal(a.getUtcStart());
            if (localStartZdt.toLocalDate().equals(startDate)) {
                for(int i=0; i<availableTimesLocal.size(); ++i){
                    if(updating == true) {
                        if (availableTimesLocal.get(i).equals(localStartZdt.toLocalTime()) && !availableTimesLocal.get(i).equals(UpdateAppointmentController.previousStartTime)) {
                            availableTimesLocal.remove(availableTimesLocal.get(i));
                            --i;
                        }
                    }
                    else if (availableTimesLocal.get(i).equals(localStartZdt.toLocalTime())) {
                        availableTimesLocal.remove(availableTimesLocal.get(i));
                        --i;
                    }
                }
            }
        }
        return availableTimesLocal;
    }

    public static ObservableList<Month> getMonths() {
        ObservableList<Month> months = FXCollections.observableArrayList();
        for(int i =1; i < 13; ++i) {
            months.add(Month.of(i));
        }
        return months;
    }
}
