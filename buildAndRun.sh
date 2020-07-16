#!/bin/sh
mvn clean package && docker build -t de.hsos.kbse/GardenDock .
docker rm -f GardenDock || true && docker run -d -p 8080:8080 -p 4848:4848 --name GardenDock de.hsos.kbse/GardenDock 
