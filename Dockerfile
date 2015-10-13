FROM clojure
ADD . /app
WORKDIR /app
RUN lein uberjar
CMD java -jar ./target/wt-0.1.0-SNAPSHOT.jar