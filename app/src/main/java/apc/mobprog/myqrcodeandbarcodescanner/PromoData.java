package apc.mobprog.myqrcodeandbarcodescanner;

public class PromoData {
    public static String firstname;
    public String lastname;
    public String email;
    public static String password;
    public String locationCode;
    public String mobNum;
    public String empAge;
    public String empGen;

    public PromoData(String first, String sur, String emailAdd, String passwd, String locCode,
                     String mobileNumber, String age, String gender) {
        this.firstname = first;
        this.lastname = sur;
        this.email = emailAdd;
        this.password = passwd;
        this.locationCode = locCode;
        this.mobNum = mobileNumber;
        this.empAge = age;
        this.empGen= gender;
    }
}
