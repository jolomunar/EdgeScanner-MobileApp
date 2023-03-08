package apc.mobprog.myqrcodeandbarcodescanner;

public class PromoData {
    public static String firstname;
    public static String lastname;
    public static String email;
    public static String password;
    public String locationCode;
    public String mobNum;

    public PromoData(String first, String sur, String emailAdd, String passwd, String locCode,
                     String mobileNumber) {
        this.firstname = first;
        this.lastname = sur;
        this.email = emailAdd;
        this.password = passwd;
        this.locationCode = locCode;
        this.mobNum = mobileNumber;
    }
}
