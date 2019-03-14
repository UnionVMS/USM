#! /bin/bash
 
# ==================================================================
#
# Script for adding the cert for midway for the directory service
#
# ==================================================================

#function to trust a cert if needed
trust_cert() {
    # $1 url:port
    # $2 alias
    echo trying to trust cert for $1 under alias $2
    if [[ $($JAVA_HOME/bin/keytool -list -storepass changeit -alias $2 -keystore $JAVA_HOME/jre/lib/security/cacerts | grep 'fingerprint') ]]; then
        echo "cert is trusted"
    else
        # get the cert in pem format
        echo "getting cert"
        echo -n | openssl s_client -showcerts -connect $1 2>/dev/null  | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > cert.pem
        # convert it to der
        openssl x509 -outform der -in cert.pem -out cert.der
        echo "installing cert"
        # install it into the trusstroe
        $JAVA_HOME/bin/keytool -importcert -noprompt -storepass changeit -alias $2 -file cert.der -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts
        echo "cert installed"
    fi

}

trust_cert svm-midway.athens.intrasoft-intl.private:10636 svm-midway.athens.intrasoft-intl.private
trust_cert cygnus-test.athens.intrasoft-intl.private:28443 cygnus-test

