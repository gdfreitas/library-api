
# Aplicação Java EE

Java EE

JPA 2.1

Bean Validation 1.1

JMS 2.0

EJB 3.2

CDI 1.1

JAX-RS 2.0

Java 8 lambda expressions, Date and Time API, streams, etc

Gson, JUnit, Mockito and Hamcrest.

Arquillian

Wildfly como servidor de aplicação

PostgreSQL

HSQLDB/H2 para testes unitários e de integração

**Maven**
POM - Project Object Model `pom.xml` [docs](https://maven.apache.org/pom.html)
É uma representação de um projeto maven no formato XML. Contém os arquivos de configuração do projeto, 
os desenvolvedores e seus papeis, sistema de rastreamento de defeito (_defect tracking system_), a 
organização responsável pelo projeto, as licenças, a URL do repositório onde o projeto está alocado, as dependências do projeto,
e todas as pequenas peças que por final acabam dando vida ao projeto.


## Configurações

```standalone-full.xml```

### Datasource

```xml
<datasource jndi-name="java:jboss/datasources/library" pool-name="library-pool" enabled="true" use-java-context="true">
   <connection-url>jdbc:postgresql://localhost/library</connection-url>
   <driver>postgres</driver>
   <transaction-isolation>TRANSACTION_READ_COMMITED</transaction-isolation>
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
```

### Drivers

```xml
<driver name="postgres" module="org.postgres">
   <xa-datasource-class>org.postgresql.Driver</xa-datasource-class>
</driver>
```
