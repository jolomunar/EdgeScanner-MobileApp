package apc.mobprog.myqrcodeandbarcodescanner;

public class PromoData {
    public static String firstname;
    public static String lastname;
    public static String email;
    public static String password;
    public static String locationCode;
    public static String userRole;

    public PromoData(String first, String sur, String emailAdd, String passwd, String locCode,
                     String uRole) {
        this.firstname = first;
        this.lastname = sur;
        this.email = emailAdd;
        this.password = passwd;
        this.locationCode = locCode;
        this.userRole = uRole;
    }
}
