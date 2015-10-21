FROM clojure
ADD . /app
WORKDIR /app
CMD java -jar target/wt.jar