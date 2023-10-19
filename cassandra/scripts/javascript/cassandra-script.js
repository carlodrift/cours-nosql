const cassandra = require('cassandra-driver');

const client = new cassandra.Client({
  contactPoints: ['127.0.0.1'],
  localDataCenter: 'dc1'
});

async function main() {
  try {
    await client.connect();

    await client.execute(`DROP TABLE IF EXISTS CR_Keyspace.CR_Table`);

    await client.execute(`CREATE KEYSPACE IF NOT EXISTS CR_Keyspace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}`);

    await client.execute(`CREATE TABLE IF NOT EXISTS CR_Keyspace.CR_Table (id UUID PRIMARY KEY, name TEXT, age INT, address MAP<TEXT, TEXT>)`);

    await client.execute(`TRUNCATE CR_Keyspace.CR_Table`);

    const preparedStatement = 'INSERT INTO CR_Keyspace.CR_Table (id, name, age, address) VALUES (uuid(), ?, ?, ?)';

    await client.execute(preparedStatement, ['William', 56, null], { prepare: true });

    const address = { street: '5 rue de Cassandra', city: 'NoSQL', postal_code: '87000' };
    await client.execute(preparedStatement, ['Bob', 25, address], { prepare: true });

    await client.execute(preparedStatement, ['Charlie', 35, null], { prepare: true });

    const result = await client.execute(`SELECT id, name, age, address FROM CR_Keyspace.CR_Table`);
    result.rows.forEach(row => {
      console.log(`ID: ${row.id}, Name: ${row.name}, Age: ${row.age}, Address: ${JSON.stringify(row.address)}`);
    });

  } catch (err) {
    console.error('Error', err);
  } finally {
    await client.shutdown();
  }
}

main();
