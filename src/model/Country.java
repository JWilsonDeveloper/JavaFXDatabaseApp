package model;

/** This class defines the Country object.
 * @author James Wilson
 */
public class Country {
    private int countryId;
    private int numCustomers;
    private String name;

    public Country(int countryId, int numCustomers, String name) {
        this.countryId = countryId;
        this.numCustomers = numCustomers;
        this.name = name;
    }

    /**
     * @return The country's id.
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * @param countryId The integer with which the country's id will be set.
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     * @return The number of customers in the country.
     */
    public int getNumCustomers() {
        return numCustomers;
    }

    /**
     * @param numCustomers The integer with which the country's number of customers will be set.
     */
    public void setNumCustomers(int numCustomers) {
        this.numCustomers = numCustomers;
    }

    /**
     * @return The country's name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The String with which the country's name will be set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
