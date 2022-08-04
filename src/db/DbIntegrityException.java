package db;

public class DbIntegrityException extends RuntimeException {

    //It treats possible integrity exceptions in the database caused by deletion attempts

    private static final long serialVersionUID = 1L;

    public DbIntegrityException(String msg) {
        super(msg);
    }
}
