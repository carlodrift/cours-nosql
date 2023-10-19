from cassandra.cluster import Cluster

def main():
    cluster = Cluster(['127.0.0.1'], port=9042)
    session = cluster.connect()

    session.execute("""
    CREATE KEYSPACE IF NOT EXISTS CR_Keyspace 
    WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}
    """)

    session.execute("DROP TABLE IF EXISTS CR_Keyspace.CR_Table")

    session.execute("""
    CREATE TABLE CR_Keyspace.CR_Table (
        id UUID PRIMARY KEY,
        name TEXT,
        age INT,
        address MAP<TEXT, TEXT>
    )
    """)

    insert_scalar_stmt = session.prepare("INSERT INTO CR_Keyspace.CR_Table (id, name, age) VALUES (uuid(), ?, ?)")
    session.execute(insert_scalar_stmt, ['William', 56])

    address = {'street': '5 rue de Cassandra', 'city': 'NoSQL', 'postal_code': '87000'}
    insert_structured_stmt = session.prepare("INSERT INTO CR_Keyspace.CR_Table (id, name, age, address) VALUES (uuid(), ?, ?, ?)")
    session.execute(insert_structured_stmt, ['Bob', 25, address])

    session.execute(insert_scalar_stmt, ['Charlie', 35])

    rows = session.execute("SELECT id, name, age, address FROM CR_Keyspace.CR_Table")
    for row in rows:
        print(f"ID: {row.id}, Name: {row.name}, Age: {row.age}, Address: {row.address}")

    cluster.shutdown()

if __name__ == "__main__":
    main()
