FROM clojure
ADD . /consumer
WORKDIR /consumer
ENV CASS_KEYSPACE develop
ENV CASS_REPLICA_FACTOR 1
CMD java -jar target/uberjar/com.webtalk.storage-0.1.0-SNAPSHOT-standalone.jar