FROM php:8.0-cli

RUN apt-get update && apt-get install -y libssl-dev wget git unzip \
    && pecl install mongodb \
    && docker-php-ext-enable mongodb

RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

WORKDIR /app

COPY . /app

RUN composer require mongodb/mongodb symfony/console

CMD ["./mongodb-script.php", "mongodb:interact"]
