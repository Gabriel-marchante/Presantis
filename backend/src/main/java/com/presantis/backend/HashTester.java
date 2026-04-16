import org.mindrot.jbcrypt.BCrypt;

public class HashTester {
    public static void main(String[] args) {
        String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());
        System.out.println("HASH_IS: " + hash);
        boolean check = BCrypt.checkpw("admin123", "$2a$10$wEtwMvW..Yw69L6vJ6.oH.QZ1TIF5mI7l2Dq1n.U35L09q43Y9HRC");
        System.out.println("CHECK_OLD: " + check);
    }
}
