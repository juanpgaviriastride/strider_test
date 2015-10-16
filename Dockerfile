FROM clojure
ADD . /app
WORKDIR /app
RUN lein uberjar
CMD java -jar target/wt.jar