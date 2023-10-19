db.cr_mdemo1.insert({
    cr_col1: "String from script",
    cr_col2: 789,
    cr_col3: false,
    cr_col4: new Date(),
    cr_col5: { subfield1: "subdata from script", subfield2: 101112 },
    cr_col6: [6, 7, 8, 9, 10]
});

db.cr_mdemo1.insertMany([
    {
        cr_col1: "String 1 from script",
        cr_col2: 123,
        cr_col3: true,
        cr_col4: new Date(),
        cr_col5: { subfield1: "subdata 1 from script", subfield2: 456 },
        cr_col6: [1, 2, 3, 4, 5]
    },
    {
        cr_col1: "String 2 from script",
        cr_col2: 789,
        cr_col3: false,
        cr_col4: new Date(),
        cr_col5: { subfield1: "subdata 2 from script", subfield2: 101112 },
        cr_col6: [6, 7, 8, 9, 10]
    }
]);