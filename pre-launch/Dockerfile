FROM clojure
ADD . /pre-launch
WORKDIR /pre-launch
RUN ["lein", "deps"]
CMD ["lein", "run"]
