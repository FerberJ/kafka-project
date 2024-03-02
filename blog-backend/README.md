docker network create blog-nw

docker run -d --name=redpanda-1 -p 9092:9092 --network blog-nw -d docker.redpanda.com/vectorized/redpanda:v23.3.5 redpanda start --advertise-kafka-addr redpanda-1:9092

docker run --name blog-mysql -p 3306:3306 --network blog-nw -e MYSQL_ROOT_PASSWORD=vs4tw -e MYSQL_USER=dbuser -e MYSQL_PASSWORD=dbuser -e MYSQL_DATABASE=blogdb -d mysql:8.0

./mvnw verify 

docker run --network blog-nw -i --rm -p 8080:8080 ghcr.io/ferberj/kafka-blog-backend

docker run --network blog-nw -i --rm -p 8085:8085 ghcr.io/ferberj/kafka-validator

docker push ghcr.io/ferberj/kafka-blog-backend
docker push ghcr.io/ferberj/kafka-validator

    environment:
      QUARKUS_DATASOURCE_JDBC_URL: jdbc:mysql://mysql:3306/blogdb
      QUARKUS_DATASOURCE_USERNAME: user
      QUARKUS_DATASOURCE_PASSWORD: user
      QUARKUS_KAFKA_BOOTSTRAP_SERVERS: redpanda-0:9644