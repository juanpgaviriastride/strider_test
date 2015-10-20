App
===


## Project Resources

  * Trello is used for managing stories:
    * https://
  * Git is used for source control, canonical repo is at https://github.com/MustWin/webtalk-be
  * Strider is used for Continuous Integration, CI server is located at http://162.242.237.24
  * API docs use apiary at http://docs.webtalk.apiary.Slack/
  * io plus hubot is used for project communication

## Staging access

* Staging cluster is composed of
    * 5 Cassandra nodes
    * 5 Elasticsearch nodes
    * 5 ConsumerRexter nodes
    * 1 Big/Fat Rabbitmq boy
    * 1 API node
    ```

## Git practices

  * Use `git pull --rebase` instead of `git --pull wherever possible to avoid meaningless merge commits in the project history.
  * ...

## Setting up development environment

    * Install vbox
    * Install docker
    * `docker-compose up`
    * You may need to use docker-machine on macosx first

## Testing

### Running tests

### Developing tests


### Troubleshoting

    * slack
