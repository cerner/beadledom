FROM nginx

RUN apt-get update && \
    apt-get install openssl

# Create a config.txt file with the ssl config in it.
RUN echo "[dn]\nCN=localhost\n[req]\ndistinguished_name = dn\n[EXT]\nsubjectAltName=DNS:localhost\nkeyUsage=digitalSignature\nextendedKeyUsage=serverAuth" >> config.txt

# Create a ssl key and cert file
RUN openssl req -x509 -out cert.pem -keyout key.pem \
  -newkey rsa:2048 -nodes -sha256 \
  -subj '/CN=localhost' -extensions EXT -config config.txt

# Use the key and cert file to create a pkcs12 file that we can use in the JVM to get it to trust
# the self-signed cert we made previously
RUN openssl pkcs12 -export -passout pass:123456 -out trust.pkcs12 -inkey key.pem -in cert.pem

# Move the pkcs file somewhere we know so we can grab it later
RUN mkdir -p /etc/keystore
RUN mv trust.pkcs12 /etc/keystore/trust.pkcs12

# Move and copy the nginx files to the correct spots.
COPY nginx.conf /etc/nginx/nginx.conf
RUN mv key.pem /etc/nginx/key.pem
RUN mv cert.pem /etc/nginx/cert.pem
