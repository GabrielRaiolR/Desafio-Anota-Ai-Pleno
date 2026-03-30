# Desafio Técnico — Backend Analyst

## Sobre o Desafio

O desafio proposto pela **AnotaAI** consiste no desenvolvimento de uma API de gerenciamento de catálogo de produtos para uma aplicação de marketplace. O objetivo central é construir um sistema onde cada alteração no catálogo (criação, atualização ou exclusão de produtos e categorias) dispare automaticamente a geração de um arquivo JSON consolidado do catálogo, armazenado na AWS S3.

### User Stories atendidas

- Cadastro de produto com título, descrição, preço, categoria e ID do dono
- Cadastro de categoria com título, descrição e ID do dono
- Associação de produto a uma categoria (um produto pertence a uma categoria por vez)
- Atualização de dados de produto ou categoria
- Exclusão de produto ou categoria
- Publicação de alterações no catálogo via AWS SNS/SQS
- Consumer que escuta as alterações e gera o JSON do catálogo no S3

---

## Decisão Técnica

O desafio original pedia **Node.js + Express.js**. A solução foi implementada com **Java + Spring Boot**, tecnologia em que o autor(Gabriel Raiol) possui maior domínio, mantendo todos os requisitos funcionais e arquiteturais do enunciado:

- API REST com os mesmos endpoints e comportamentos esperados
- Integração com MongoDB, AWS SNS, AWS SQS e AWS S3
- Separação em camadas (Controller → Service → Repository)
- Variáveis de ambiente para credenciais sensíveis (Não estava previsto no desafio)
- Tratamento global de erros (Não estava previsto no desafio)
- Histórico de commits organizado por contexto

---

## Arquitetura

```
[API Spring Boot]
      │
      │  POST/PUT/DELETE → publica mensagem
      ▼
[AWS SNS — tópico: catalog]
      │
      │  subscription
      ▼
[AWS SQS — fila: catalog-queue]
      │
      │  trigger
      ▼
[AWS Lambda — Node.js]
      │
      │  lê/grava JSON do catálogo
      ▼
[AWS S3 — bucket: anotaai-catalog-marketplace31]
```

Cada operação que altera o catálogo (criar, atualizar ou deletar produto/categoria) publica uma mensagem no **SNS**. A fila **SQS** consome essa mensagem e dispara o **Lambda**, que lê o JSON atual do catálogo no **S3**, aplica a alteração e grava de volta. O endpoint de consulta do catálogo lê diretamente do S3, sem bater no banco de dados.

---

## Tecnologias Utilizadas

| Camada                    | Tecnologia                |
| ------------------------- | ------------------------- |
| Backend API               | Java 17 + Spring Boot 3.5 |
| Banco de dados            | MongoDB                   |
| Mensageria                | AWS SNS + AWS SQS         |
| Armazenamento de catálogo | AWS S3                    |
| Consumer / Lambda         | Node.js 20 (AWS Lambda)   |
| Build                     | Maven                     |
| Credenciais locais        | dotenv-java               |

---

## Estrutura do Projeto

```
src/
└── main/
    └── java/
        └── com/gabriel/desafio_anota_ai/
            ├── config/
            │   ├── aws/
            │   │   ├── AwsSnsConfig.java          # Bean do AmazonSNS
            │   │   └── AwsSnsTopicProperties.java # ARN via @ConfigurationProperties
            │   ├── mongo/
            │   │   └── MongoDBConfig.java
            │   └── GlobalExceptionHandler.java
            ├── controllers/
            │   ├── CategoryController.java
            │   └── ProductController.java
            ├── domain/
            │   ├── category/
            │   │   ├── Category.java
            │   │   ├── CategoryDTO.java
            │   │   └── exceptions/CategoryNotFoundException.java
            │   └── product/
            │       ├── Product.java
            │       ├── ProductDTO.java
            │       └── exceptions/ProductNotFoundException.java
            ├── repositories/
            │   ├── CategoryRepository.java
            │   └── ProductRepository.java
            └── services/
                ├── aws/
                │   ├── AwsSnsService.java
                │   └── MessageDTO.java
                ├── CategoryService.java
                └── ProductService.java
```

---

## Endpoints

### Produtos — `/api/product`

| Método   | Rota                | Descrição               |
| -------- | ------------------- | ----------------------- |
| `POST`   | `/api/product`      | Cadastra um produto     |
| `GET`    | `/api/product`      | Lista todos os produtos |
| `PUT`    | `/api/product/{id}` | Atualiza um produto     |
| `DELETE` | `/api/product/{id}` | Remove um produto       |

**Body (POST/PUT):**

```json
{
  "title": "Nome do produto",
  "description": "Descrição",
  "ownerId": "id-do-dono",
  "price": 100,
  "categoryId": "id-da-categoria"
}
```

### Categorias — `/api/category`

| Método   | Rota                 | Descrição                 |
| -------- | -------------------- | ------------------------- |
| `POST`   | `/api/category`      | Cadastra uma categoria    |
| `GET`    | `/api/category`      | Lista todas as categorias |
| `PUT`    | `/api/category/{id}` | Atualiza uma categoria    |
| `DELETE` | `/api/category/{id}` | Remove uma categoria      |

**Body (POST/PUT):**

```json
{
  "title": "Nome da categoria",
  "description": "Descrição",
  "ownerId": "id-do-dono"
}
```

---

## Como Rodar Localmente

### Pré-requisitos

- Java 17+
- Maven 3.9+
- MongoDB rodando localmente ou URI de conexão
- Conta AWS com SNS Topic criado
- Credenciais AWS com permissão em SNS

### 1. Configurar variáveis de ambiente

Copie o arquivo de exemplo e preencha com suas credenciais:

```bash
cp .env.example .env
```

Edite o `.env`:

```env
AWS_ACCESS_KEY_ID=sua-access-key
AWS_SECRET_ACCESS_KEY=sua-secret-key
AWS_REGION=us-east-1
AWS_SNS_TOPIC_CATALOG_ARN=arn:aws:sns:us-east-1:sua-conta:catalog
```

### 2. Rodar a aplicação

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

---

## Fluxo de Mensagens no Catálogo

Toda vez que um produto ou categoria é **criado**, **atualizado** ou **deletado**, a API publica uma mensagem no **SNS** com os dados alterados e um campo `type` indicando o tipo de operação:

| `type`             | Significado                  |
| ------------------ | ---------------------------- |
| `produto`          | Criar ou atualizar produto   |
| `categoria`        | Criar ou atualizar categoria |
| `delete-produto`   | Deletar produto              |
| `delete-categoria` | Deletar categoria            |

O **Lambda** (Node.js) consome a fila SQS, aplica a operação no JSON do catálogo e salva no S3.

---

## Lambda (Consumer)

O código do Lambda em Node.js está disponível neste repositório e pode ser colado diretamente no console da AWS (runtime **Node.js 20.x**).

O Lambda:

- Lê o JSON atual do catálogo do S3 (`{ownerId}-catalog.json`)
- Aplica a operação (inserir, atualizar ou remover item)
- Grava o JSON atualizado de volta no S3
- Cria o arquivo do zero caso ainda não exista para aquele `ownerId`

---

## Status do Projeto

O projeto foi **desenvolvido, publicado e testado com todos os serviços AWS ativos**:

- API respondendo corretamente a todas as rotas
- Mensagens sendo publicadas no SNS a cada alteração
- Lambda sendo trigado via SQS e atualizando o catálogo no S3
- Arquivo JSON gerado e disponível no bucket para cada `ownerId`

---

## Histórico de Commits

| Commit    | Descrição                                                     |
| --------- | ------------------------------------------------------------- |
| `bcd3bbe` | Projeto Maven/Spring, .env de exemplo e integração AWS SNS    |
| `de9e0d8` | MongoDB, entidades e repositórios                             |
| `616d507` | APIs de categorias e produtos e handler global de exceções    |
| `21ebaa4` | Serialização JSON nas entidades e integração SNS nos serviços |
| `98acd9f` | Publicação SNS ao deletar produto e categoria                 |

---

## Variáveis de Ambiente

| Variável                    | Descrição                     |
| --------------------------- | ----------------------------- |
| `AWS_ACCESS_KEY_ID`         | Access Key do usuário IAM     |
| `AWS_SECRET_ACCESS_KEY`     | Secret Key do usuário IAM     |
| `AWS_REGION`                | Região AWS (ex: `us-east-1`)  |
| `AWS_SNS_TOPIC_CATALOG_ARN` | ARN do tópico SNS do catálogo |

> O arquivo `.env` **não** é versionado.

---

## Autor

Gabriel Raiol Rodrigues
