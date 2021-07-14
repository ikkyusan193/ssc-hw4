#!/bin/bash
docker run -p 127.0.0.1:3307:3306 -p 172.17.0.1:3307:3306  --name mariadb -e MARIADB_ROOT_PASSWORD=secret -d mariadb:10

