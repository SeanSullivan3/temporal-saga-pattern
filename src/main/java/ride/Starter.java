package ride;

import ride.dao.BaseDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Starter {

    private static final BaseDAO db = new BaseDAO("jdbc:sqlite:cab_saga.db");

    public static void main(String[] args) {

        SpringApplication.run(Starter.class, args);
        initDB();
    }

    public static void initDB() {
        db.createTables("ride");
        db.createTables("cab_assignment");
        db.createTables("payment");
    }
}
