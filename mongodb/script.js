db.xx_mdemo1.insert({
    xx_col1: "String from script",
    xx_col2: 789,
    xx_col3: false,
    xx_col4: new Date(),
    xx_col5: { subfield1: "subdata from script", subfield2: 101112 },
    xx_col6: [6, 7, 8, 9, 10]
});

db.xx_mdemo1.insertMany([
    {
        xx_col1: "String 1 from script",
        xx_col2: 123,
        xx_col3: true,
        xx_col4: new Date(),
        xx_col5: { subfield1: "subdata 1 from script", subfield2: 456 },
        xx_col6: [1, 2, 3, 4, 5]
    },
    {
        xx_col1: "String 2 from script",
        xx_col2: 789,
        xx_col3: false,
        xx_col4: new Date(),
        xx_col5: { subfield1: "subdata 2 from script", subfield2: 101112 },
        xx_col6: [6, 7, 8, 9, 10]
    }
]);