FROM clojure
ADD . /consumer
WORKDIR /consumer
RUN lein deps
RUN lein uberjar
CMD ["java", "-jar" , "target/uberjar/com.webtalk.storage-0.1.0-SNAPSHOT-standalone.jar"]