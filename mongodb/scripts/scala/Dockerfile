FROM hseeberger/scala-sbt:11.0.12_1.5.5_2.13.6

WORKDIR /app

# Copy the Scala script and build.sbt to the container
COPY mongodb-script.scala /app
COPY build.sbt /app

# Fetch dependencies and compile the script
RUN sbt compile

# Run the script
CMD ["sbt", "run"]
