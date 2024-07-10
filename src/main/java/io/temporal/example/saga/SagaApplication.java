package io.temporal.example.saga;

import io.temporal.example.saga.dao.BaseDAO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SagaApplication {

    private static final BaseDAO db = new BaseDAO("jdbc:sqlite:cab_saga.db");

    public static void main(String[] args) {

        SpringApplication.run(SagaApplication.class, args);
        initDB();
    }

    public static void initDB() {
        db.createTables("ride");
        db.createTables("cab_assignment");
        db.createTables("payment");
    }
}
