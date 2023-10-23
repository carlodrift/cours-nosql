# MongoDB

## Lancement du conteneur Docker

Le fichier `docker-compose.yml` est disponible dans le dossier `mongodb` du repo. Il permet de lancer une instance MongoDB.

```bash
docker-compose up -d
```

Ce fichier lance un service MongoDB. Les identifiants par défaut sont `username` pour l'utilisateur et `password` pour le mot de passe. Les données sont persistées dans un volume nommé `mongodb_data`.

## Utilisation de `mongosh`

`mongosh` est l'outil en ligne de commande pour interagir avec MongoDB.

Pour se connecter à l'instance MongoDB lancée par Docker :

```bash
docker exec -it mongodb mongosh -u username -p password
```

## Création d'une base de données

```js
use cr_demo1
```

## Création de collections

```js
db.createCollection("cr_mdemo1")
db.createCollection("cr_mdemo2")
```

## Insertion de données

Insérer une seule ligne :

```js
db.cr_mdemo1.insert({
    cr_col1: "String data",
    cr_col2: 123,
    cr_col3: true,
    cr_col4: new Date(),
    cr_col5: { subfield1: "subdata", subfield2: 456 },
    cr_col6: [1, 2, 3, 4, 5]
})
```

```js
db.cr_mdemo2.insert({
    cr_col1: "String data",               // String : Chaîne de caractères
    cr_col2: 123,                         // Number : Entier (int32 ou int64 selon la plateforme)
    cr_col3: 123.456,                     // Double : Nombre à virgule flottante double précision
    cr_col4: true,                        // Boolean : Valeur booléenne (true ou false)
    cr_col5: new Date(),                  // Date : Date et heure
    cr_col6: new ObjectId(),              // ObjectId : Identifiant unique généré par MongoDB
    cr_col7: { subfield1: "subdata" },    // Embedded document : Document imbriqué
    cr_col8: [1, 2, 3, 4, 5],             // Array : Tableau de valeurs
    cr_col9: BinData(0, "binary data"),   // Binary data : Données binaires
    cr_col10: /pattern/i,                 // Regular expression : Expression régulière
    cr_col11: NumberLong("123456789012"), // 64-bit integer : Entier 64 bits
    cr_col12: NumberInt("123"),           // 32-bit integer : Entier 32 bits
    cr_col13: NumberDecimal("123.456"),   // Decimal128 : Nombre à virgule flottante à précision arbitraire
    cr_col14: Timestamp(0, 0),            // Timestamp : Horodatage pour les opérations internes de MongoDB
    cr_col15: undefined,                  // Undefined : Valeur non définie (généralement déconseillé d'utiliser)
    cr_col16: MinKey,                     // MinKey : Valeur spéciale inférieure à toutes les autres dans MongoDB
    cr_col17: MaxKey                      // MaxKey : Valeur spéciale supérieure à toutes les autres dans MongoDB
})
```

Insérer plusieurs lignes :

```js
db.cr_mdemo1.insertMany([
    {
        cr_col1: "String data 1",
        cr_col2: 123,
        cr_col3: true,
        cr_col4: new Date(),
        cr_col5: { subfield1: "subdata 1", subfield2: 456 },
        cr_col6: [1, 2, 3, 4, 5]
    },
    {
        cr_col1: "String data 2",
        cr_col2: 789,
        cr_col3: false,
        cr_col4: new Date(),
        cr_col5: { subfield1: "subdata 2", subfield2: 101112 },
        cr_col6: [6, 7, 8, 9, 10]
    }
])
```

## Chargement d'un fichier JavaScript

Dans notre configuration docker-compose.yml, nous avons utilisé cette fonctionnalité pour monter le fichier test.js dans le conteneur MongoDB. Pour charger et exécuter le contenu du fichier test.js dans MongoDB, utilisez la commande suivante :

```js
load("/scripts/test.js")
```

## Mise à jour d'un tableau dans une ligne

Pour ajouter un élément à un tableau :

```js
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $push: { cr_col6: 11 } })
```

## Création d'une collection avec un schéma

MongoDB supporte la validation de schéma au niveau de la collection :

```js
db.createCollection("cr_mdemo3", {
   validator: {
      $jsonSchema: {
         bsonType: "object",
         required: ["cr_col1", "cr_col2"],
         properties: {
            cr_col1: {
               bsonType: "string",
               description: "must be a string and is required"
            },
            cr_col2: {
               bsonType: "int",
               minimum: 0,
               maximum: 100,
               description: "must be an integer in [0, 100] and is required"
            }
         }
      }
   }
})
```

## Incrémenter/décrémenter des valeurs dans un tableau

Pour incrémenter une valeur :

```js
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $inc: { "cr_col2": 1 } })
```

Pour décrémenter :

```js
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $inc: { "cr_col2": -1 } })
```

## Suppression de plusieurs lignes avec une REGEX

Supprimer toutes les lignes où `cr_col1` contient "data" :

```js
db.cr_mdemo1.remove({ cr_col1: /data/ })
```

## Création et suppression d'un index

```js
db.cr_mdemo1.createIndex({ cr_col1: 1 })
```

```js
db.cr_mdemo1.dropIndex("cr_col1_1")
```

## Opérateurs

### Opérateurs de comparaison

- `$eq` : Égal à
- `$gt` : Supérieur à
- `$gte` : Supérieur ou égal à
- `$lt` : Inférieur à
- `$lte` : Inférieur ou égal à
- `$ne` : Non égal à
- `$in` : Dans la liste
- `$nin` : Pas dans la liste

Exemples :

```js
db.cr_mdemo1.find({ cr_col2: { $eq: 100 } })
db.cr_mdemo1.find({ cr_col2: { $in: [100, 150, 200] } })
```

### Opérateurs logiques

- `$or` : Ou
- `$and` : Et
- `$not` : Non
- `$nor` : Ni ... ni

Exemples :

```js
db.cr_mdemo1.find({ $or: [{ cr_col2: 100 }, { cr_col3: 150 }] })
db.cr_mdemo1.find({ $and: [{ cr_col2: { $gt: 100 } }, { cr_col2: { $lt: 200 } }] })
```

### Opérateurs élémentaires

- `$exists` : Vérifie l'existence d'un champ
- `$type` : Vérifie le type d'un champ

```js
db.cr_mdemo1.find({ cr_col2: { $exists: true } })
db.cr_mdemo1.find({ cr_col2: { $type: "int" } })
```

### Opérateurs de mise à jour

Opérateurs de mise à jour

- `$inc` : Incrémente une valeur
- `$mul` : Multiplie une valeur
- `$rename` : Renomme un champ
- `$setOnInsert` : Définit une valeur lors de l'insertion
- `$set` : Définit une valeur
- `$unset` : Supprime un champ
- `$min` : Définit une valeur minimale
- `$max` : Définit une valeur maximale
- `$currentDate` : Définit la date actuelle
- `$addToSet` : Ajoute une valeur à un ensemble (sans doublons)
- `$pop` : Supprime le premier ou dernier élément d'un tableau
- `$pullAll` : Supprime toutes les occurrences d'une liste de valeurs d'un tableau
- `$pull` : Supprime toutes les occurrences d'une valeur d'un tableau
- `$push` : Ajoute une valeur à un tableau

Exemples :

```js
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $inc: { cr_col2: 1 } })
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $rename: { "cr_col2": "new_col2" } })
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $push: { cr_col8: 6 } })
```

## Scripts

### [PHP](https://github.com/carlodrift/cours-nosql/tree/main/mongodb/scripts/php)

Se placer dans le dossier `mongodb/scripts/php`.

```bash
docker build -t php-mongodb-script . && docker run --rm --network host php-mongodb-script
```

### [Scala](https://github.com/carlodrift/cours-nosql/tree/main/mongodb/scripts/scala)

Se placer dans le dossier `mongodb/scripts/scala`.

```bash
docker build -t scala-mongodb-script . && docker run --rm --network host scala-mongodb-script
```

## Glossaire

1. **Modèle de données** :
- **Document** : Une unité de stockage de données, similaire à un enregistrement dans les bases de données relationnelles.
- **Collection** : Un ensemble de documents, similaire à une table dans les bases de données relationnelles.
- **BSON** : Format binaire utilisé pour stocker les documents.
2. **Indexation** :
- Permet des recherches rapides.
- Les index peuvent être créés sur n'importe quel champ du document.
3. **Sharding** :
- Technique pour distribuer les données sur plusieurs serveurs.
- Permet à MongoDB de gérer de grandes quantités de données et d'offrir une scalabilité horizontale (ajouter plus de machines ≠ ajouter plus de puissance, scalabilité verticale).
- **Shard** : Chaque serveur de la base de données dans le système sharded est appelé un shard, il détient un sous-ensemble des données de la base de données.
- **Router (mongos)** : Le client parle à un processus router (mongos) qui dirige la requête vers le bon shard.
- **Clé de sharding** : Permet de déterminer comment distribuer les données à travers les shards.
- **Chunk** : Les données sont divisées en blocs de taille similaire appelés chunks. Chaque chunk est associé à une plage de valeurs de la clé de sharding.
4. **Réplication** :
- MongoDB utilise des ensembles de réplicas pour assurer la redondance des données.
- Un ensemble de réplicas est composé d'un nœud primaire et de plusieurs nœuds secondaires.
- Si le nœud primaire devient indisponible, les nœuds secondaires organisent une élection pour choisir un nouveau nœud primaire.
5. **Oplog (Operation Log)** :
- Un journal des opérations qui permet aux nœuds secondaires de rester synchronisés avec le nœud primaire.
6. **Consistance et disponibilité** :
- MongoDB utilise un modèle de consistence éventuelle.
- Les lectures peuvent être configurées pour être effectuées sur le nœud primaire ou sur les nœuds secondaires.
7. **Aggregation Framework** :
- Permet le traitement des données et renvoie des résultats calculés.
- Similaire aux opérations GROUP BY en SQL.
