# Aplicação Java EE

Este repositório contém um projeto baseado em um agregado de tecnologias com o objetivo de estudar e praticar os recursos abordados;

O projeto consiste em uma api para gerênciamento de uma biblioteca, com cadastro de autores, livros, categorias de livros, e cadastro de usuários. Possui autenticação para cada recurso disponibilizado conforme os papéis de cada usuário, controle de expiração do tempo de aluguel de cada livro, auditoria de ações realizadas em alguns recursos, etc.

## Tecnologias abordadas

- Java EE
- JPA 2.1
- Bean Validation 1.1
- JMS 2.0
- EJB 3.2
- CDI 1.1
- JAX-RS 2.0
- Recursos do Java 8 (lambda expressions, Date&Time API, streams, etc)
- Gson, JUnit, Mockito and Hamcrest.
- Arquillian
- Wildfly como servidor de aplicação _(Utilizado para Desenvolvimento Wildfly-8.2.0.Final)_
- PostgreSQL
- HSQLDB/H2 para testes unitários e de integração

## Configurações

- Executar o servidor `$JBOSS_HOME/bin/standalone.bat -c=standalone-full.xml`

### Datasource

```xml
<subsystem xmlns="urn:jboss:domain:datasources:2.0">
   <datasources>
      ...
      <datasource jndi-name="java:jboss/datasources/library" pool-name="library-pool" enabled="true" use-java-context="true">
         <connection-url>jdbc:postgresql://localhost/library</connection-url>
         <driver>postgres</driver>
         <transaction-isolation>TRANSACTION_READ_COMMITTED</transaction-isolation>
         <pool>
            <min-pool-size>5</min-pool-size>
            <max-pool-size>30</max-pool-size>
            <prefill>true</prefill>
            <use-strict-min>false</use-strict-min>
            <flush-strategy>FailingConnectionOnly</flush-strategy>
         </pool>
         <security>
            <user-name>postgres</user-name>
            <password>postgres</password>
         </security>
         <statement>
            <prepared-statement-cache-size>32</prepared-statement-cache-size>
         </statement>
      </datasource>
   </datasources>
   ...
</subsystem>
```

### Drivers

- É necessário [baixar o driver do postgres](https://jdbc.postgresql.org/download.html) e adicioná-lo aos drivers do Wildfly.
   1. Criar pasta `postgres` e `postgres.main` em `JBOSS_HOME/modules/system/layers/base/org`;
   2. Adicionar o jar baixado na pasta main;
   3. Criar arquivo `module.xml` conforme abaixo;

module.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
   <module xmlns="urn:jboss:module:1.1" name="org.postgres">
   <resources>
      <resource-root path="postgresql-42.2.2.jar"/>
   </resources>
   <dependencies>
      <module name="javax.api"/>
   </dependencies>
</module>
```

standalone-full.xml

```xml
<subsystem xmlns="urn:jboss:domain:datasources:2.0">
   <datasources>
      ...
      <driver name="postgres" module="org.postgres">
         <xa-datasource-class>org.postgresql.Driver</xa-datasource-class>
      </driver>
   </datasources>
   ...
</subsystem>
```

### Filtro de autenticação

standalone-full.xml

```xml
 <subsystem xmlns="urn:jboss:domain:security:1.2">
   <security-domains>
      ...
      <security-domain name="library" cache-type="default">
         <authentication>
            <login-module code="Database" flag="required">
                  <module-option name="dsJndiName" value="java:jboss/datasources/library"/>
                  <module-option name="principalsQuery" value="select password from lib_user where email=?"/>
                  <module-option name="rolesQuery" value="select role, 'Roles' from lib_user_role ur inner join lib_user u on u.id = ur.user_id where u.email=?"/>
                  <module-option name="hashAlgorithm" value="SHA-256"/>
                  <module-option name="hashEncoding" value="BASE64"/>
                  <module-option name="hashStorePassword" value="false"/>
                  <module-option name="hashUserPassword" value="true"/>
            </login-module>
         </authentication>
      </security-domain>
      ...
   </security-domains>
</subsystem>
```

### Logging
```xml
<subsystem xmlns="urn:jboss:domain:logging:2.0">
    ...
    <console-handler name="CONSOLE">
        <level name="DEBUG"/>
        <formatter>
            <named-formatter name="COLOR-PATTERN"/>
        </formatter>
    </console-handler>
    ...
     <logger category="org.jboss.as.config">
        <level name="INFO"/>
    </logger>
    <logger category="com.library.app">
        <level name="DEBUG"/>
    </logger>
    ...
</subsystem>
```

### JMS
```xml
<jms-destinations>
    ...
    <jms-queue name="Orders">
        <entry name="java:/jms/queue/Orders"/>
    </jms-queue>
    ...
</jms-destinations>
```

## Referências

- [Curso: Build an application from scratch: JEE 7, Java 8 and Wildfly](https://www.udemy.com/build-an-application-from-scratch-jee-7-java-8-and-wildfly)

- [Exposição de Recursos da API @ Postman - Documenter](https://documenter.getpostman.com/view/1862571/RWTkQyRZ)