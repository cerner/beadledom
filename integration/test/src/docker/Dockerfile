FROM nginx

RUN apt-get update && \
    apt-get install openssl

RUN echo "[dn]\nCN=localhost\n[req]\ndistinguished_name = dn\n[EXT]\nsubjectAltName=DNS:localhost\nkeyUsage=digitalSignature\nextendedKeyUsage=serverAuth" >> config.txt

RUN openssl req -x509 -out cert.pem -keyout key.pem \
  -newkey rsa:2048 -nodes -sha256 \
  -subj '/CN=localhost' -extensions EXT -config config.txt

RUN openssl pkcs12 -export -passout pass:123456 -out trust.pkcs12 -inkey key.pem -in cert.pem

RUN mkdir -p /etc/keystore
RUN mv trust.pkcs12 /etc/keystore/trust.pkcs12

ENV docker_host beadledom-integration-service
COPY start_nginx.sh /opt/start_nginx.sh
RUN chmod 750 /opt/start_nginx.sh
COPY nginx.conf /etc/nginx/nginx.conf
RUN mv key.pem /etc/nginx/key.pem
RUN mv cert.pem /etc/nginx/cert.pem

CMD ["/opt/start_nginx.sh"]
