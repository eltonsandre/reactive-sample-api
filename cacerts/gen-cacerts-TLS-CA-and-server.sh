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

san="SAN:c=DNS:reactive-api.dev.local,DNS:dev.local,DNS:localhost,IP:127.0.0.1"
#san="SAN:c=DNS:localhost,IP:127.0.0.1"
dname="CN=${serviceName}.${environment},O=${organizationName},OU=${organizationUnitName},L=${localityName},S=${stateName},C=${country}"

echo "-----
Certificado para CA raiz
 Antes de começar, crie primeiro os seguintes subdiretórios:

 root-ca ->  (você armazenará todos os artefatos necessários para configurar uma autoridade de certificação aqui)
 ${serviceName} -> (você armazenará todos os artefatos necessários para o seu certificado de servidor assinado aqui)

 Na primeira etapa, você precisa gerar as chaves privadas / públicas e o certificado correspondente para a CA raiz. Posteriormente, você usará
 este certificado raiz na seção para assinar o certificado do servidor .

 "
keytool -genkeypair -keyalg RSA -keysize ${keysize} \
  -alias root-ca \
  -dname "${dname}" \
  -ext BC:c=ca:true \
  -ext KU=keyCertSign \
  -validity ${validity} \
  -keystore ./TLS/ca.jks \
  -storepass ${storepass} \
  -keypass ${keypass}

echo "-----
Este comando cria um novo java keystore ca.jks na pasta root-ca contendo as chaves privada e pública. O certificado usa o algoritmo RSA com um
 comprimento de bit de 3072 e é válido por 10 anos.
 Isso inclui também o nome distinto  CN=Meu CA, OU=Desenvolvimento, O=Minha Organização, C=BR .
 Agora você exporta o certificado para o arquivo ca.pem no subdiretório  root-ca usando este comando :

 "
keytool -exportcert -keystore ./TLS/ca.jks \
  -storepass ${storepass} \
  -alias root-ca \
  -rfc -file ./TLS/ca.pem

echo "-----
 Certificado de Servidor Assinado

 Na próxima etapa, você criará outro novo arquivo de armazenamento de chaves java contendo as chaves privadas/públicas para o certificado do
 servidor.
 A chave privada é necessária para gerar a solicitação de assinatura de certificado. A CA usa a chave pública para validar a solicitação de
 assinatura de certificado.

 "
keytool -genkeypair -keyalg RSA -keysize ${keysize} \
  -alias ${serviceName} \
  -dname "${dname}" \
  -ext BC:c=ca:false \
  -ext EKU:c=serverAuth \
  -ext ${san} \
  -validity ${validity} \
  -keystore ./TLS/${serviceName}.jks \
  -storepass ${storepass} \
  -keypass ${keypass}

echo "-----
 Você pode encontrar o novo servidor de armazenamento de chaves  java.jks no subdiretório server. Novamente usamos o algoritmo RSA com um
 comprimento de bit de 3072 e o definimos como válido por 10 anos.
 Agora você continuará com a geração da solicitação de assinatura para o certificado do servidor. Isso cria o arquivo server.csr no subdiretório
 server.

 "
keytool -certreq -keystore ./TLS/${serviceName}.jks \
  -storepass ${storepass} -alias ${serviceName} \
  -keypass ${keypass} -file ./TLS/${serviceName}.csr

echo "-----
Com o próximo comando, você agora assinará e exportará o certificado do servidor usando o arquivo server.csr da etapa anterior.

"
keytool -gencert \
  -keystore ./TLS/ca.jks \
  -storepass ${storepass} \
  -infile ./TLS/${serviceName}.csr \
  -alias root-ca -keypass ${keypass} \
  -ext BC:c=ca:false -ext EKU:c=serverAuth -ext ${san} \
  -validity ${validity} -rfc \
  -outfile ./TLS/${serviceName}.pem

echo "-----
Para obter a cadeia de confiança válida necessária entre o ca raiz e o certificado do servidor assinado, você deve executar a última etapa a
seguir.

"
keytool -importcert -noprompt -keystore ./TLS/${serviceName}.jks \
  -storepass ${storepass} -alias root-ca \
  -keypass ${keypass} \
  -file ./TLS/ca.pem

keytool -importcert -noprompt -keystore ./TLS/${serviceName}.jks \
  -storepass ${storepass} -alias ${serviceName} \
  -keypass ${keypass} \
  -file ./TLS/${serviceName}.pem

echo "Isso importa o certificado para o ca raiz e atualiza o certificado do servidor existente (não assinado) com o certificado assinado.
 Finalmente, temos um armazenamento de chaves java contendo a cadeia completa de certificados prontos para serem usados em nosso aplicativo de
 inicialização do Spring.

Usar o Keytool para executar manualmente todas as etapas para criar os certificados é bom para o aprendizado. Mas se você deseja automatizar
 essas coisas para usos subsequentes, o mkcert é uma ótima ferramenta para isso."
