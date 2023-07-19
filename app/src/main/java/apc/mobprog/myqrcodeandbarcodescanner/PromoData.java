package apc.mobprog.myqrcodeandbarcodescanner;

import java.util.ArrayList;

public class PromoData {
    public static String firstname;
    public static String lastname;
    public static String email;
    public static String password;
    public static String locationCode;
    public static String userRole;
    public static ArrayList userInfo = new ArrayList<String>();
    public static String userName;

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
