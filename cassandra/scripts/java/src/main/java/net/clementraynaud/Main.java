package net.clementraynaud;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.uuid.Uuids;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
                .withLocalDatacenter("dc1")
                .build()) {

            session.execute("CREATE KEYSPACE IF NOT EXISTS CR_Keyspace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}");
            session.execute("USE CR_Keyspace");

            session.execute("DROP TABLE IF EXISTS CR_Table");

            session.execute("CREATE TABLE CR_Table (id UUID PRIMARY KEY, name TEXT, age INT, address MAP<TEXT, TEXT>)");
            session.execute("TRUNCATE CR_Table");

            session.execute("INSERT INTO CR_Table (id, name, age) VALUES (?, ?, ?)", Uuids.timeBased(), "William", 56);

            Map<String, String> address = new HashMap<>();
            address.put("street", "5 rue de Cassandra");
            address.put("city", "NoSQL");
            address.put("postal_code", "87000");
            session.execute("INSERT INTO CR_Table (id, name, age, address) VALUES (?, ?, ?, ?)", Uuids.timeBased(), "Bob", 25, address);

            session.execute("INSERT INTO CR_Table (id, name, age) VALUES (?, ?, ?)", Uuids.timeBased(), "Charlie", 35);

            ResultSet rs = session.execute("SELECT id, name, age, address FROM CR_Table");
            for (Row row : rs) {
                System.out.printf("ID: %s, Name: %s, Age: %d, Address: %s\n", row.getUuid("id"), row.getString("name"), row.getInt("age"), row.getMap("address", String.class, String.class));
            }
        }
    }
}
