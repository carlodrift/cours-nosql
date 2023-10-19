#!/usr/bin/env php
<?php

require 'vendor/autoload.php';

use MongoDB\Client;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Console\Application;

class MongoDBCommand extends Command
{
    protected static $defaultName = 'mongodb:interact';

    protected function execute(InputInterface $input, OutputInterface $output)
    {
        $client = new Client('mongodb://username:password@localhost:27017');

        $database = $client->CR_Database;

        $collection = $database->CR_Table;

        $collection->drop();

        $collection->insertOne(['name' => 'William', 'age' => 56]);
        $collection->insertOne(['name' => 'Bob', 'age' => 25]);

        $collection->insertOne([
            'name' => 'Charlie',
            'age' => 35,
            'address' => [
                'street' => '5 rue de Cassandra',
                'city' => 'NoSQL',
                'postal_code' => '87000'
            ]
        ]);

        $output->writeln("Data sorted ascending by age:");
        $cursor = $collection->find([], ['sort' => ['age' => 1]]);
        foreach ($cursor as $document) {
            $output->writeln(json_encode($document));
        }

        $output->writeln("Data sorted descending by age:");
        $cursor = $collection->find([], ['sort' => ['age' => -1]]);
        foreach ($cursor as $document) {
            $output->writeln(json_encode($document));
        }

        return Command::SUCCESS;
    }
}

$application = new Application();
$application->add(new MongoDBCommand());
$application->run();
