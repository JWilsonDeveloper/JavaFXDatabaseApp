package utility;

/** When implemented, this interface will be utilized in order to collect the error messages generated when a user tries to save or update an appointment or customer.
 * It is appropriate to use this interface for that purpose. There are numerous potential errors when a user tries to save or update an appointment or customer.
 * Without the use of a lambda expression/functional interface, each of these potential errors would require their own if statements and the resulting code
 * would be large, clunky and difficult to read.
 * @author James Wilson
 */
public interface ErrorMessageInterface {
    String appendMsg(String msg, Object field, String fieldTitle);
}
