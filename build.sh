#!/bin/sh
docker build -t belligerator/news-backend-java:prod .
docker push belligerator/news-backend-java:prod
