#!/usr/bin/env bash

sed -i.bak s/docker_host/$docker_host/g /etc/nginx/nginx.conf && nginx -g "daemon off;"
