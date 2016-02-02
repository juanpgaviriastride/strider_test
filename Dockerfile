FROM clojure
ADD . /app
WORKDIR /app
RUN ["lein", "deps"]
CMD ["lein", "do", "migratus,", "run"]