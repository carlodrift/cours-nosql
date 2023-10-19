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

## Opérateurs de comparaison, logiques, élémentaires et de mise à jour

- Comparaison : $eq, $gt, $gte, $lt, $lte, $ne, $in, $nin
- Logiques : $or, $and, $not, $nor
- Élémentaires : $exists, $type
- Mise à jour : $inc, $mul, $rename, $setOnInsert, $set, $unset, $min, $max, $currentDate, $addToSet, $pop, $pullAll, $pull, $pushAll, $push

```js
// cr_col2 est égal à 100
db.cr_mdemo1.find({ cr_col2: { $eq: 100 } })

// cr_col2 est supérieur à 100
db.cr_mdemo1.find({ cr_col2: { $gt: 100 } })

// cr_col2 est supérieur ou égal à 100
db.cr_mdemo1.find({ cr_col2: { $gte: 100 } })

// cr_col2 est inférieur à 200
db.cr_mdemo1.find({ cr_col2: { $lt: 200 } })

// cr_col2 est inférieur ou égal à 200
db.cr_mdemo1.find({ cr_col2: { $lte: 200 } })

// cr_col2 n'est pas égal à 150
db.cr_mdemo1.find({ cr_col2: { $ne: 150 } })

// cr_col2 est dans la liste [100, 150, 200]
db.cr_mdemo1.find({ cr_col2: { $in: [100, 150, 200] } })

// cr_col2 n'est pas dans la liste [100, 150, 200]
db.cr_mdemo1.find({ cr_col2: { $nin: [100, 150, 200] } })

// Soit cr_col2 est égal à 100, soit cr_col3 est égal à 150
db.cr_mdemo1.find({ $or: [{ cr_col2: 100 }, { cr_col3: 150 }] })

// cr_col2 est à la fois supérieur à 100 et inférieur à 200
db.cr_mdemo1.find({ $and: [{ cr_col2: { $gt: 100 } }, { cr_col2: { $lt: 200 } }] })

// cr_col2 n'est pas égal à 150
db.cr_mdemo1.find({ cr_col2: { $not: { $eq: 150 } } })

// Ni cr_col2 est égal à 100, ni cr_col2 est égal à 150
db.cr_mdemo1.find({ $nor: [{ cr_col2: 100 }, { cr_col2: 150 }] })

// Le champ cr_col2 existe
db.cr_mdemo1.find({ cr_col2: { $exists: true } })

// cr_col2 est de type int
db.cr_mdemo1.find({ cr_col2: { $type: "int" } })

// Incrémenter la valeur de cr_col2 de 1 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $inc: { cr_col2: 1 } })

// Multiplier la valeur de cr_col2 par 2 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $mul: { cr_col2: 2 } })

// Renommer le champ cr_col2 en new_col2 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $rename: { "cr_col2": "new_col2" } })

// Définir la valeur de cr_col2 à 150 lors de l'insertion si cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $setOnInsert: { cr_col2: 150 } }, { upsert: true })

// Définir la valeur de cr_col2 à 150 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $set: { cr_col2: 150 } })

// Supprimer le champ cr_col2 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $unset: { cr_col2: "" } })

// Définir la valeur de cr_col2 au minimum entre sa valeur actuelle et 100 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $min: { cr_col2: 100 } })

// Définir la valeur de cr_col2 au maximum entre sa valeur actuelle et 200 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $max: { cr_col2: 200 } })

// Définir la valeur de cr_col5 à la date actuelle pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $currentDate: { cr_col5: true } })

// Ajouter la valeur 6 à cr_col8 (en tant qu'ensemble) pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $addToSet: { cr_col8: 6 } })

// Supprimer le dernier élément du tableau cr_col8 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $pop: { cr_col8: 1 } })

// Supprimer toutes les occurrences des valeurs 2 et 3 du tableau cr_col8 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $pullAll: { cr_col8: [2, 3] } })

// Supprimer toutes les occurrences de la valeur 2 du tableau cr_col8 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $pull: { cr_col8: 2 } })

// Ajouter les valeurs 6, 7 et 8 au tableau cr_col8 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $pushAll: { cr_col8: [6, 7, 8] } })

// Ajouter la valeur 6 au tableau cr_col8 pour les documents où cr_col1 est "String data 1"
db.cr_mdemo1.update({ cr_col1: "String data 1" }, { $push: { cr_col8: 6 } })
```

## Scripts

### [PHP](https://github.com/carlodrift/cours-nosql/tree/main/mongodb/scripts/php)

Se placer dans le dossier `mongodb/scripts/php`.

```bash
docker build -t php-mongodb-script . && docker run --rm --network host php-mongodb-script
```

### [Scala](https://github.com/carlodrift/cours-nosql/tree/main/mongodb/scripts/scala)

Se placer dans le dossier `scala/scripts/php`.

```bash
docker build -t scala-mongodb-script . && docker run --rm --network host scala-mongodb-script
```