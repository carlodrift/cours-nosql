package main

import (
	"fmt"
	"log"

	"github.com/gocql/gocql"
)

func main() {
	cluster := gocql.NewCluster("127.0.0.1")
	cluster.Keyspace = "system"
	cluster.Consistency = gocql.One
	session, err := cluster.CreateSession()
	if err != nil {
		log.Fatalf("%v", err)
	}
	defer session.Close()

	err = session.Query(`CREATE KEYSPACE IF NOT EXISTS CR_Keyspace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}`).Exec()
	if err != nil {
		log.Fatalf("%v", err)
	}

	session.Query(`DROP TABLE IF EXISTS CR_Keyspace.CR_Table`).Exec()

	err = session.Query(`CREATE TABLE CR_Keyspace.CR_Table (id UUID PRIMARY KEY, name TEXT, age INT, address MAP<TEXT, TEXT>)`).Exec()
	if err != nil {
		log.Fatalf("%v", err)
	}

	err = session.Query(`TRUNCATE CR_Keyspace.CR_Table`).Exec()
	if err != nil {
		log.Fatalf("%v", err)
	}

	err = session.Query(`INSERT INTO CR_Keyspace.CR_Table (id, name, age) VALUES (uuid(), 'William', 56)`).Exec()
	if err != nil {
		log.Fatalf("%v", err)
	}

	address := map[string]string{"street": "5 rue de Cassandra", "city": "NoSQL", "postal_code": "87000"}
	err = session.Query(`INSERT INTO CR_Keyspace.CR_Table (id, name, age, address) VALUES (uuid(), 'Bob', 25, ?)`, address).Exec()
	if err != nil {
		log.Fatalf("%v", err)
	}

	err = session.Query(`INSERT INTO CR_Keyspace.CR_Table (id, name, age) VALUES (uuid(), 'Charlie', 35)`).Exec()
	if err != nil {
		log.Fatalf("%v", err)
	}

	iter := session.Query(`SELECT id, name, age, address FROM CR_Keyspace.CR_Table`).Iter()
	var id gocql.UUID
	var name string
	var age int
	var addressMap map[string]string
	for iter.Scan(&id, &name, &age, &addressMap) {
		fmt.Printf("ID : %s, Nom : %s, Âge : %d, Adresse : %v\n", id, name, age, addressMap)
	}
	if err := iter.Close(); err != nil {
		log.Fatalf("%v", err)
	}
}
