## Bship

### How to Setup

1. Download [Docker](https://docs.docker.com/docker-for-mac/) on your machine.
2. Install the Docker Application and make sure you have the latest version installed.

### How to Run

1. Open the terminal
2. Clone the project: ```git clone https://felipehaack@bitbucket.org/felipehaack/bship.git```
3. And go to battleship api folder ```cd bship-api```
4. Before, set the appropriate user id, full name and ip by editing the file ```nano api/conf/application.conf```
5. Run the following command: ```docker-compose up api```
6. Once the docker application has been launched successfully execute: ```cd ../bship-web```
7. Run the following command: ```docker-compose up web```
8. Once the docker application has been launched successfully, open your browser and go to: ```http://localhost:8020```


### How to Test

1. Stop the Battleship instance if you have one running
2. Run the follow command: ```docker-compose run api sbt test```