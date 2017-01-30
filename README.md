## Bship

### How to Setup

1. Download [Docker For Mac](https://docs.docker.com/docker-for-mac/) on your machine.
2. Install the Docker Application and make sure you have the latest version installed.
3. For others OS look at [Docker Install](https://docs.docker.com/compose/install/).


### How to Run

1. Open the terminal
2. Clone the project: ```git clone https://felipehaack@bitbucket.org/felipehaack/bship.git```
3. And go to the application folder ```cd bship```
4. Before, set the appropriate user id, full name, ip and port on api environment by editing the file ```nano docker-compose.yml```
5. Run the following command: ```docker-compose up```
6. Once the docker application has been launched successfully, open your browser and go to: [Web App](http://localhost:8020)

### How to Test

1. Stop the Battleship instance if you have one running
2. Run the follow command: ```docker-compose run api sbt test```