FROM nginx:alpine

RUN apk update && \
    apk add openssl && \
    openssl req -subj '/CN=localhost' -x509 -newkey rsa:4096 -nodes -keyout key.pem -out cert.pem -days 365

ENV docker_host beadledom-integration-service
COPY nginx.conf /etc/nginx/nginx.conf
RUN mv cert.pem /etc/nginx/cert.pem
RUN mv key.pem /etc/nginx/key.pem

RUN sed -i.bak s/docker_host/$docker_host/g /etc/nginx/nginx.conf
