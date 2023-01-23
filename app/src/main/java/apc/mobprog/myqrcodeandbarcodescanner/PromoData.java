package apc.mobprog.myqrcodeandbarcodescanner;

public class PromoData {
    public String firstname, lastname, email, password, locationCode;

    public PromoData(String first, String sur, String emailAdd, String passwd, String locCode) {
        this.firstname = first;
        this.lastname = sur;
        this.email = emailAdd;
        this.password = passwd;
        this.locationCode = locCode;
    }
}
