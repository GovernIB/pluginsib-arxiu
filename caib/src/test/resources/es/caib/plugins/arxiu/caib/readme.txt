
Si connectant al servidor amb SSL dona el següent error:

    javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException:
        PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
            unable to find valid certification path to requested target

Cal fer el següent:

  - Anar al servidr amb el navegador i davallar el certificat de servidor.
  - Executar amb JAVA_HOME apuntat al JDK amb que executam el test:

        - Java 1.6 / 1.7 / 8
        keytool -import -file D:\Downloads\afirmades-caib-es.pem -alias afirmades-caib -keystore %JAVA_HOME%\jre\lib\security\cacerts

        - Java 11
        keytool -import -file D:\Downloads\afirmades-caib-es.pem -alias afirmades-caib -cacerts

    En ambdós casos, demanarà el password de cacerts que per defecte és 'changeit'
