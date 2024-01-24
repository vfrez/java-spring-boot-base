[![Java CI with Maven](https://github.com/vfrez/java-spring-boot-base/actions/workflows/maven.yml/badge.svg)](https://github.com/vfrez/java-spring-boot-base/actions/workflows/maven.yml)

# Testes com Spring-boot

Projeto para alguns testes e práticas com Spring-boot e talvez outras tecnologias com java

Até o momento foi feito:

- Iniciar aplicação com Spring-Boot 3
- Conectar ao MySql 8
- Controle de versão de tabelas com Liquibase
- Criação de APIs com Spring-Web
- Handler de APIs para melhorar visualização de chamadas no console
- APIs para cadastrar/atualizar/apagar uma tabela chamada PESSOA
- APIs para testar desempenho de geração de dados em lote no sistema, usando Java-Faker
  - Cadastro em Single-thread
    - Loops simples
    - Loops com commit no banco usando um lote de itens
  - Cadastro em Multi-thread
    - Usando [ParallelStream](https://www.baeldung.com/java-when-to-use-parallel-stream) ([Onde cada núcleo do processador será uma thread](https://stackoverflow.com/questions/21163108/custom-thread-pool-in-java-8-parallel-stream)) com ForkJoinPool default
    - Usando um [ForkJoinPool novo](https://www.baeldung.com/java-8-parallel-streams-custom-threadpool)


## Requisitos

- Java 17
- Maven
- MySql

## Iniciar sistema

Clone the project

```bash
  git clone https://link-to-project
```

Go to the project directory

```bash
  cd importer
```

Faça o build do projeto

```bash
  mvn clean install -DskipTests
```

Todo teste está sendo feito com IntelliJ, apenas iniciando pela IDE.

Mas pode-se subir o spring também com o comando de terminal a seguir:
```bash
# Na pasta raiz do projeto
mvn spring-boot:run
```

Com isso as APIs ja estarão acessíveis, usando `localhost:8080`


## Lista de APIs criadas

#### Buscar todos os registros da tabela PESSOA

```http
  GET /pessoa
```

#### Buscar quantidade de registros cadastrados na tabela PESSOA

```http
  GET /pessoa/count
```

#### Deleta todos os registros da tabela PESSOA

```http
  DELETE /pessoa/delete-all
```

#### Deleta registro da tabela PESSOA por ID

```http
  DELETE /pessoa/{id}
```

| Parameter    | Type     | Description                       |
|:-------------| :------- | :-------------------------------- |
| PATH: `id`   | `UUID`   | **Required**. Id Gerado na criação do registro PESSOA |

#### Busca registro da tabela PESSOA por ID

```http
  GET /pessoa/{id}
```

| Parameter       | Type     | Description                       |
| :--------       | :------- | :-------------------------------- |
| PATH: `id`      | `UUID`   | **Required**. Id Gerado na criação do registro PESSOA |

#### Cadastra registro na tabela PESSOA

```http
  POST /pessoa
```
Body - application/json
```json
{
	"nome": "Cleiton",
	"sobrenome": "Rasta",
	"dataNascimento": "2000-01-13",
	"observacao": "Vai fazer a galera debochar legal"

}
```

#### Atualizar registro na tabela PESSOA

```http
  PUT /pessoa/{id}
```

| Parameter   | Type     | Description                       |
|:------------| :------- | :-------------------------------- |
| PATH: `id`  | `UUID`   | **Required**. Id Gerado na criação do registro PESSOA |


Body - application/json
```json
{
	"nome": "Cabeça",
	"sobrenome": "de Gelo",
	"dataNascimento": "2001-01-01",
	"observacao": "Dança ao som do Cleiton"

}
```
