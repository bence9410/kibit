# Kibit test

In the application folder, build the jar with 'mvn clean package', then build the docker image with 'docker build -t bence/kibit .'
Then run the application and the services with 'docker compose up' in the main folder.
To watch the notifications go into the broker with 'docker exec -it -w //opt/kafka/bin kibit-broker-1 sh' and run './kafka-console-consumer.sh --topic notification --from-beginning --bootstrap-server broker:29092'

Open api documentation at 'http://localhost:8080/swagger-ui/index.html#/'
There are 2 account Bence with 10000 money and Erzsébet with 1000 money.
Example request: {"fromName":"Bence","toName":"Erzsébet","amount":1000}

### Requirements

Java 23, Maven and docker

### Architecture to ensure high availability and fault tolerance

I would run the services and the application with multiple replicas in a cluster and I would consider using kubernetes in the cloud. 