# BTTN
This Java program mimicks the Twitch live notifications, but with drastically lower delay.

<div align="center">

[![Build-Nightly](https://github.com/jnstockley/BTTN/actions/workflows/build-nightly.yml/badge.svg?branch=nightly)](https://github.com/jnstockley/BTTN/actions/workflows/build-nightly.yml) 
[![Create-Doc-Nightly](https://github.com/jnstockley/BTTN/actions/workflows/create-doc-nightly.yml/badge.svg?branch=nightly)](https://github.com/jnstockley/BTTN/actions/workflows/create-doc-nightly.yml) 
[![Deploy-Nightly](https://github.com/jnstockley/BTTN/actions/workflows/deploy-nightly.yml/badge.svg?branch=nightly)](https://github.com/jnstockley/BTTN/actions/workflows/deploy-nightly.yml) 
[![Create-Docker](https://github.com/jnstockley/BTTN/actions/workflows/create-docker.yml/badge.svg?branch=nightly)](https://github.com/jnstockley/BTTN/actions/workflows/create-docker.yml)

</div>

# How to Install
## Docker (Recommended)
```
docker pull jnstockley/bsn:nightly \
docker run --name BSN jnstockley/bsn:nightly \
docker exec BSN BSN <CLI Command>
```

## Mvn Install
```
git clone --branch nightly https://github.com/jnstockley/ \
cd BTTN \
mvn install \
mvn package \
java -jar target/bsn-2.0-alpha3-SNAPSHOT-jar-with-dependencies.jar <CLI Command>
```

## Download Jar (Not Recommended)
```
wget -O BSN.jar https://github.com/jnstockley/BTTN/raw/nightly/out/bsn-2.0-alpha3-SNAPSHOT-jar-with-dependencies.jar \
java -jar BSN.jar <CLI Command>
```
