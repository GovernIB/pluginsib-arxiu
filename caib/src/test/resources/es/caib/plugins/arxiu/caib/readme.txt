
Si connectant al servidor amb SSL dona el següent error:

    javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException:
        PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
            unable to find valid certification path to requested target

Cal fer el següent:

  - Anar al servidr amb el navegador i davallar el certificat de servidor.
  - Executar amb JAVA_HOME apuntat al JDK amb que executam el test:

    keytool -import -file D:\Downloads\afirmades-caib-es-chain.pem -alias afirmades-caib -keystore %JAVA_HOME%\jre\lib\security\cacerts