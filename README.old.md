# BTTN
This Java program mimicks the Twitch live notifications, but with drastically lower delay.

<div align="center">

[![Build-Nightly](https://github.com/jnstockley/BTTN/actions/workflows/build-nightly.yml/badge.svg?branch=nightly)](https://github.com/jnstockley/BTTN/actions/workflows/build-nightly.yml) 
[![Create-Doc-Nightly](https://github.com/jnstockley/BTTN/actions/workflows/create-doc-nightly.yml/badge.svg?branch=nightly)](https://github.com/jnstockley/BTTN/actions/workflows/create-doc-nightly.yml) 
[![Deploy-Nightly](https://github.com/jnstockley/BTTN/actions/workflows/deploy-nightly.yml/badge.svg?branch=nightly)](https://github.com/jnstockley/BTTN/actions/workflows/deploy-nightly.yml) 
[![Create-Docker](https://github.com/jnstockley/BTTN/actions/workflows/create-docker.yml/badge.svg?branch=nightly)](https://github.com/jnstockley/BTTN/actions/workflows/create-docker.yml)

</div>

# How to Install
## Docker (Currently Not Working)
```
docker pull jnstockley/bsn:nightly \
docker run --name BSN jnstockley/bsn:nightly \
docker exec BSN BSN <CLI Command>
```

## Mvn Install (Reccomend for the time being)
Make sure maven is installed on your system!
Make sure Java 17+ is installed on your system!
```
git clone --branch nightly https://github.com/jnstockley/BTTN \
cd BTTN \
mvn install \
mvn package \
mv target/*-jar-with-dependencies.jar BSN.jar
java -jar BSN.jar <CLI Command>
```

## Download Jar (Not Recommended)
```
wget -O BSN.jar https://github.com/jnstockley/BTTN/raw/nightly/out/BSN.jar \
java -jar BSN.jar <CLI Command>
```
