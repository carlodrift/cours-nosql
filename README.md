# Cassandra

## Lancement du conteneur Docker

Le fichier `docker-compose.yml` est disponible dans le repo. Une version pour les machines avec peu de RAM est également disponible : `docker-compose-low-ram.yml`.

```bash
docker-compose up -d
```

Il y a trois nœuds Cassandra distincts. Ils font tous partie du même cluster. Le premier nœud, `cr_cassandra1`, agit comme un nœud "seed" pour les deux autres.

Un nœud "seed" est un nœud de référence pour aider les nouveaux nœuds à rejoindre le cluster. Lorsqu'un nœud Cassandra démarre, il utilise les nœuds "seed" pour obtenir une liste des autres nœuds du cluster et comprendre la topologie globale du cluster. Bien que les nœuds "seed" aient un rôle spécial lors de la découverte et de la formation du cluster, ils ne sont pas des "maîtres" ou des nœuds privilégiés en termes de traitement des données. Une fois que le cluster est opérationnel, tous les nœuds, qu'ils soient "seed" ou non, fonctionnent de manière égale et distribuée.

`cr_cassandra1` est défini comme le nœud "seed", `cr_cassandra2` et `cr_cassandra3` le contacteront initialement pour obtenir des informations sur le reste du cluster lorsqu'ils démarreront.

La version low-ram n'a qu'un seul noeud.

## Utilisation de cqlsh

C'est un outil en ligne de commande pour intéragir avec Cassandra.

```bash
docker exec -it r510-cr_cassandra1-1 cqlsh
```

## Keyspaces

Un keyspace est similaire à une base de données dans les systèmes RDBMS (Relational Database Management System). Il contient les tables.

Créons deux keyspaces :
```sql
CREATE KEYSPACE cr_demo1 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};
CREATE KEYSPACE cr_demo2 WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};
```

Si vous utilisez la version low-ram, utilisez un RF de 1.

L'option `WITH replication` définit la stratégie de réplication pour le keyspace. Dans cet exemple, nous utilisons SimpleStrategy avec un replication_factor de 3. Cela signifie que chaque donnée sera répliquée sur trois nœuds différents du cluster, garantissant ainsi une meilleure disponibilité et résilience des données.

SimpleStrategy est généralement recommandé pour les environnements de test ou les clusters à un seul centre de données. Dans un environnement de production, en particulier avec des clusters répartis sur plusieurs centres de données, il est recommandé d'utiliser NetworkTopologyStrategy. Il est conçu pour gérer la réplication de données dans des clusters multi-centres de données. Il permet de définir un facteur de réplication distinct pour chaque centre de données, garantissant ainsi que les données sont correctement répliquées à travers les centres de données, offrant à la fois une haute disponibilité et une résilience en cas de défaillance d'un centre de données entier.

## Tables (Column Families)

```sql
USE cr_demo1;
CREATE TABLE cr_cfdemo1 (
    cr_col1 UUID PRIMARY KEY,
    cr_col2 text,
    cr_col3 int
);

USE cr_demo2;
CREATE TABLE cr_cfdemo2 (
    cr_col1 UUID PRIMARY KEY,
    cr_col2 text,
    cr_col3 int
);
```

## Types de données

```sql
USE cr_demo1;

ALTER TABLE cr_cfdemo1 ADD cr_col4 set<text>;
ALTER TABLE cr_cfdemo1 ADD cr_col5 map<text, int>;
ALTER TABLE cr_cfdemo1 ADD cr_col6 tuple<text, int, float>;
```

```sql
INSERT INTO cr_cfdemo1 (cr_col1, cr_col2, cr_col3) VALUES (uuid(), 'test', 123);
```

## Collections et tuples

Les collections (listes, sets, maps) et les tuples permettent de stocker plusieurs valeurs dans une seule colonne.

Remplacer `<some_uuid>` par l'UUID inséré précédemment (voir avec un SELECT).

```sql
UPDATE cr_cfdemo1 SET cr_col4 = {'value1', 'value2'} WHERE cr_col1 = <some_uuid>;
UPDATE cr_cfdemo1 SET cr_col5 = {'key1': 1, 'key2': 2} WHERE cr_col1 = <some_uuid>;
UPDATE cr_cfdemo1 SET cr_col6 = ('tuple_value', 123, 1.23) WHERE cr_col1 = <some_uuid>;
```

## Types de données personnalisés

Il est possible de créer ses propres types de données dans Cassandra.

```sql
CREATE TYPE cr_custom_type (field1 text, field2 int);
ALTER TABLE cr_cfdemo1 ADD cr_col7 cr_custom_type;
```

## Clés primaires

Identifie de manière unique une ligne dans une table :
- Simple : une seule colonne
- Composite : plusieurs colonnes

La première colonne est la clé de partition, les colonnes suivantes sont les clés de clustering.

```sql
USE cr_demo2;

CREATE TABLE cr_cfdemo3 (
    cr_col1 text,
    cr_col2 int,
    cr_col3 float,
    PRIMARY KEY (cr_col1, cr_col2)
);
```

Ici, `cr_col1` est la clé de partition et `cr_col2` est la clé de clustering, formant une clé primaire composite.

## Clustering order

Le clustering order détermine l'ordre dans lequel les lignes sont stockées pour une clé de partition donnée.

```sql
CREATE TABLE cr_cfdemo4 (
    cr_col1 text,
    cr_col2 int,
    cr_col3 float,
    PRIMARY KEY (cr_col1, cr_col2)
) WITH CLUSTERING ORDER BY (cr_col2 DESC);
```

## Vues matérialisées

Par défaut, cette fonctionnalité est désactivée. Pour l'activer :

```bash
docker exec -it r510-cr_cassandra1-1 bash
sed -i 's/materialized_views_enabled: false/materialized_views_enabled: true/' /etc/cassandra/cassandra.yaml
exit
docker restart r510-cr_cassandra1-1
```

Une vue matérialisée est une table générée à partir d'une table existante et organisée différemment.

```sql
USE cr_demo1;
CREATE MATERIALIZED VIEW cr_mv AS
SELECT cr_col2, cr_col1, cr_col3
FROM cr_cfdemo1
WHERE cr_col2 IS NOT NULL
AND cr_col1 IS NOT NULL
PRIMARY KEY (cr_col2, cr_col1);
```

## Requêtes SELECT

Remplacer `<some_uuid>` par l'UUID inséré précédemment (voir avec un SELECT).

```sql
SELECT * FROM cr_cfdemo1 WHERE cr_col1 = <some_uuid>;
SELECT * FROM cr_cfdemo1 WHERE cr_col2 IN ('value1', 'value2') ALLOW FILTERING;
```

## Fonctions d'agrégation

```sql
SELECT COUNT(*), MAX(cr_col3), AVG(cr_col3), MIN(cr_col3), SUM(cr_col3) FROM cr_cfdemo1;
```

## Limit et ranges

```sql
SELECT * FROM cr_cfdemo1 LIMIT 5;
SELECT * FROM cr_cfdemo1 WHERE cr_col3 > 100 AND cr_col3 < 200 ALLOW FILTERING;
```

## Index

Pour accélérer les requêtes sur des colonnes non primaires.

```sql
CREATE INDEX ON cr_cfdemo1 (cr_col2);
```

## Allow filtering

`ALLOW FILTERING` permet d'exécuter des requêtes qui seraient inefficaces car elles nécessiteraient de scanner une grande partie de la table. C'est utile pour les requêtes sur des colonnes non indexées ou non primaires, mais cela peut être coûteux en termes de performances.

```sql
SELECT * FROM cr_cfdemo1 WHERE cr_col3 = 123 ALLOW FILTERING;
```

## Mise à jour et suppression de données

Remplacer `<some_uuid>` par l'UUID inséré précédemment (voir avec un SELECT).

```sql
UPDATE cr_cfdemo1 SET cr_col4 = cr_col4 + {'value3'} WHERE cr_col1 = <some_uuid>;
DELETE cr_col4['value1'] FROM cr_cfdemo1 WHERE cr_col1 = <some_uuid>;
```

## Index personnalisés et requêtes SASI

Par défaut, cette fonctionnalité est désactivée. Pour l'activer :

```bash
docker exec -it r510-cr_cassandra1-1 bash
sed -i 's/sasi_indexes_enabled: false/sasi_indexes_enabled: true/' /etc/cassandra/cassandra.yaml
exit
docker restart r510-cr_cassandra1-1
```

SASI (SStable Attached Secondary Index) est un type d'index secondaire pour Cassandra. Il offre des capacités de recherche avancées, comme la recherche par préfixe, suffixe et sous-chaîne.

```sql
USE cr_demo1;
CREATE CUSTOM INDEX ON cr_cfdemo1 (cr_col2) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'mode': 'PREFIX', 'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer', 'case_sensitive': 'false'};
CREATE CUSTOM INDEX ON cr_cfdemo1 (cr_col2) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'mode': 'CONTAINS', 'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer', 'case_sensitive': 'false'};
```

- `mode` : Le mode de recherche. CONTAINS est utile pour les recherches par sous-chaîne. PREFIX est utile pour les recherches par préfixe.
- `analyzer_class` : L'analyseur à utiliser. StandardAnalyzer est un analyseur général qui divise le texte en termes basés sur des espaces blancs et des signes de ponctuation.
- `case_sensitive`: Indique si la recherche doit être sensible à la casse ou non.

```sql
INSERT INTO cr_demo1.cr_cfdemo1 (cr_col1, cr_col2, cr_col3) VALUES (uuid(), 'test_prefix_suffix', 123);
INSERT INTO cr_demo1.cr_cfdemo1 (cr_col1, cr_col2, cr_col3) VALUES (uuid(), 'prefix_test_suffix', 456);
INSERT INTO cr_demo1.cr_cfdemo1 (cr_col1, cr_col2, cr_col3) VALUES (uuid(), 'prefix_suffix_test', 789);
```

Recherche par préfixe :

```sql
SELECT * FROM cr_demo1.cr_cfdemo1 WHERE cr_col2 LIKE 'test%';
```

Recherche par suffixe :

```sql
SELECT * FROM cr_demo1.cr_cfdemo1 WHERE cr_col2 LIKE '%test';
```

Recherche par sous-chaîne :

```sql
SELECT * FROM cr_demo1.cr_cfdemo1 WHERE cr_col2 LIKE '%test%';
```

## Nodetool et réparation

`nodetool` est un outil en ligne de commande pour gérer le Cassandra. Réparer une table signifie synchroniser les données entre les nœuds pour s'assurer qu'ils ont tous les mêmes données.

```bash
docker exec -it r510-cr_cassandra1-1 nodetool repair cr_demo1 cr_cfdemo1
```

## Gestion des nœuds et résilience (facultatif)

La suite n'est pas faisable si vous utilisez la version low-ram.

La résilience est la capacité d'un système à fonctionner et à se remettre des pannes. Dans le contexte de Cassandra, cela signifie que le système peut continuer à fonctionner même si certains nœuds tombent en panne.

On peut arrêter certains noeuds pour tester. Arrêtons les deuxième et troisième noeuds :

```bash
docker-compose stop r510-cr_cassandra2-1 r510-cr_cassandra3-1
```

Avec deux nœuds sur trois arrêtés, nous avons maintenant moins que le quorum de nœuds en fonctionnement car nous avons un facteur de réplication de 3.

Le quorum est un concept fondamental dans les systèmes distribués comme Cassandra. Il représente le nombre minimal de nœuds qui doivent confirmer la réception d'une opération pour que celle-ci soit considérée comme réussie. Dans le contexte de Cassandra, un quorum est généralement défini comme la majorité des réplicas pour une entrée spécifique. Ainsi, avec un facteur de réplication (RF) de 3, le quorum serait atteint avec 2 nœuds.

Lorsqu'on effectue des opérations d'insertion ou de sélection, on peut spécifier le niveau de cohérence qu'on souhaite. Le niveau de cohérence détermine combien de nœuds doivent répondre pour qu'une opération soit considérée comme réussie.

Pour comprendre comment les nœuds du cluster communiquent entre eux et comment ils comprennent la topologie du cluster, Cassandra utilise ce qu'on appelle un "snitch". Dans notre configuration, nous utilisons GossipingPropertyFileSnitch, qui détermine comment les nœuds sont répartis géographiquement et comment ils doivent communiquer. Cela influence également la manière dont les données sont répliquées à travers le cluster, ce qui est crucial pour le quorum et la résilience.

Essayons d'insérer une donnée en spécifiant un niveau de cohérence de QUORUM :

```sql
CONSISTENCY QUORUM;
INSERT INTO cr_demo1.cr_cfdemo1 (cr_col1, cr_col2, cr_col3) VALUES (uuid(), 'test_quorum', 456);
```

Et essayons de la récupérer :

```sql
CONSISTENCY QUORUM;
SELECT * FROM cr_demo1.cr_cfdemo1 WHERE cr_col2 = 'test_quorum' ALLOW FILTERING;
```

Avec seulement un nœud en fonctionnement, ces opérations échoueront car nous n'atteignons pas le quorum nécessaire. On aura une erreur indiquant que le niveau de cohérence requis n'a pas été atteint. Pour que ces opérations réussissent, au moins deux nœuds (le quorum pour un RF de 3) doivent être en ligne et fonctionnels.

Redémarrons l'un des noeuds :

```bash
docker-compose start r510-cr_cassandra2-1
```

Avec deux nœuds maintenant en fonctionnement, nous avons atteint le quorum nécessaire pour un RF de 3.

```sql
CONSISTENCY QUORUM;
INSERT INTO cr_demo1.cr_cfdemo1 (cr_col1, cr_col2, cr_col3) VALUES (uuid(), 'test_quorum_recovery', 789);
```

```sql
CONSISTENCY QUORUM;
SELECT * FROM cr_demo1.cr_cfdemo1 WHERE cr_col2 = 'test_quorum_recovery' ALLOW FILTERING;
```

Cette fois, les opérations devraient réussir car deux nœuds (le quorum pour un RF de 3) sont en ligne et fonctionnels. Cela démontre la capacité de Cassandra à se remettre des pannes et à continuer à fonctionner dès que le quorum est rétabli.