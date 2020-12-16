export LANG=C.UTF-8

rm -rf TLS
mkdir TLS

serviceName=reactive-api
environment=dev.local

organizationName="AC - Certificado para desenvolvimento local - 127.0.0.1"
organizationUnitName="development local"
localityName="São Paulo"
stateName="São Paulo"
country="BR"

validity=3650 # é válido por 10 anos
keysize=4096  #3072

storepass=changeit
keypass=changeit

san="SAN:c=DNS:reactive-api.dev.loca,DNS:dev.local,DNS:localhost,IP:127.0.0.1"
#san="SAN:c=DNS:localhost,IP:127.0.0.1"
dname="CN=${serviceName}.${environment},O=${organizationName},OU=${organizationUnitName},L=${localityName},S=${stateName},C=${country}"

# server side:
keytool -genkeypair -alias ${serviceName} \
  -keyalg RSA \
  -keysize ${keysize} \
  -keystore TLS/${serviceName}.jks \
  -dname ${dname} \
  -ext BC:c=ca:true \
  -ext KU=keyCertSign \
  -validity ${validity} \
  -keypass ${keypass} \
  -storepass ${storepass}

keytool -list -rfc --keystore TLS/${serviceName}.jks \
  -storepass ${storepass} | openssl x509 -inform pem -pubkey >TLS/${serviceName}.pem

# client side:
openssl x509 -pubkey -noout -in TLS/${serviceName}.pem >TLS/${serviceName}-pubkey.pem
