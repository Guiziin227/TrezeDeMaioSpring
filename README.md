# Sistema de Gerenciamento de Acervo do Museu Treze de Maio

## Descrição do Projeto

O Sistema de Gerenciamento de Acervo do Museu Treze de Maio é uma aplicação web desenvolvida com o objetivo de auxiliar na organização, consulta e administração do acervo bibliográfico da instituição.

---

# Problema

O gerenciamento manual do acervo dificultava a organização e a localização das obras, tornando o processo de administração mais lento e suscetível a inconsistências.

Além disso, a ausência de um sistema centralizado comprometia o controle das informações dos itens, das editoras e da disponibilidade do acervo.

---

# Solução Proposta

Foi desenvolvido um sistema web baseado na arquitetura MVC (Model-View-Controller) utilizando o ecossistema Spring.

A solução centraliza todas as informações do acervo em um único sistema, permitindo que bibliotecários e administradores realizem o gerenciamento completo dos materiais de forma simples, organizada e segura.

Entre as principais funcionalidades estão:

* Cadastro de livros, jornais e revistas;
* Consulta e pesquisa do acervo;
* Edição de registros;
* Exclusão de itens;
* Upload de imagens;
* Gerenciamento de editoras;
* Controle de autenticação e autorização de usuários;
* Paginação e filtros de pesquisa.

---

# Tecnologias Utilizadas

* Java 21
* Spring Boot
* Spring MVC
* Spring Security
* Spring Data JPA
* Hibernate
* MySQL
* Thymeleaf
* HTML5
* CSS3
* JavaScript
* Maven
* Git
* GitHub

---

# Arquitetura

O projeto foi desenvolvido seguindo o padrão arquitetural **MVC (Model-View-Controller)**.

A arquitetura está organizada nas seguintes camadas:

* **Model:** representa as entidades do domínio, como Item, Livro, Jornal, Revista e Editora.
* **DTO:** responsável pela transferência de dados entre Controller e Service.
* **View:** desenvolvida utilizando Thymeleaf.
* **Controller:** recebe as requisições HTTP e coordena o fluxo da aplicação.
* **Service:** concentra as regras de negócio.
* **Repository:** realiza o acesso ao banco de dados utilizando Spring Data JPA.

---

# Instalação e Execução

## Pré-requisitos

* Java JDK 21 ou superior
* Maven 3.9 ou superior
* MySQL 8.0 ou superior
* Git

## 1. Clone o repositório

```bash
git clone https://github.com/Guiziin227/TrezeDeMaioSpring.git
cd TrezeDeMaioSpring
```

## 2. Configure o banco de dados

Crie um banco de dados chamado:

```sql
CREATE DATABASE treze_maio;
```

Em seguida, configure o arquivo `src/main/resources/application.properties` com as credenciais do seu MySQL.

Exemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/treze_maio?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

## 3. Execute o projeto

Pelo Maven:

```bash
mvn spring-boot:run
```

Ou execute a classe principal `TrezeDeMaioApplication` diretamente pela IDE.

Após iniciar a aplicação, acesse:

```
http://localhost:8080
```
---

# Equipe de Desenvolvimento

* Eduardo Fontoura
* Guilherme Weber
* Guilherme Scher
* José Barros
* José Otávio Baggio
---

# Demonstração

Vídeo de demonstração do sistema:
https://www.youtube.com/watch?v=GnGZ4wLCbYo
Vídeo devagar:
https://youtu.be/AQb-9veTx5o
