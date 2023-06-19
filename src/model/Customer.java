package model;

/** This class defines the Customer object.
 * @author James Wilson
 */
public class Customer {
    private int customerId;
    private String name;
    private String phone;
    private String address;
    private String postalCode;
    private String fld;
    private String country;

    public Customer(int customerId, String name, String phone, String address, String postalCode, String fld, String country) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.postalCode = postalCode;
        this.fld = fld;
        this.country = country;
    }

    /**
     * @return The customer's id.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId The integer with which the customer's id will be set.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * @return The customer's name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The String with which the customer's name will be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The customer's phone.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The String with which the customer's phone will be set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The customer's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address The String with which the customer's address will be set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return The customer's address.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode The String with which the customer's postal code will be set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return The customer's first level division.
     */
    public String getFld() {
        return fld;
    }

    /**
     * @param fld The String with which the customer's first level division will be set.
     */
    public void setFld(String fld) {
        this.fld = fld;
    }

    /**
     * @return The customer's country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The String with which the customer's country will be set.
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
