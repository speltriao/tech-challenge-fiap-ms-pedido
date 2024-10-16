# Tech Challenge FIAP 2024 - Galega Burger
Este projeto é uma aplicação Java 21 usando Gradle para automação de builds e Flyway para migração de banco de dados. Ele é configurado para rodar em um ambiente Docker, utilizando `docker compose` para orquestrar os contêineres do banco de dados PostgreSQL e da aplicação.

Video Fase 2: https://youtu.be/3O_Ujf5kH8w

| Nome:                              | Matrícula: | E-mail:                   |
|------------------------------------|------------|---------------------------|
| Alexandre Casella Speltri          | RM354896   | alexandreporks@gmail.com  |
| Gabriel Henrique da Silva Gava     | RM355695   | nero.gava@gmail.com       |
| Gabriela Oliveira De Freitas Gomes | RM353369   | gabriella_gomes@ymail.com |

## Indices
1. [Pré-requisitos](#1--pré-requisitos)
2. [Estrutura Geral](#2--estrutura-do-projeto)
3. [Executando o Projeto com Docker](#3--configuração-e-execução-do-projeto-com-docker)
4. [Executando com Kubernetes](#4--kubernetes-k8s)
5. [Configurações adicionais](#5--configurações)
6. [Limpeza e parada dos serviços](#6--limpeza-e-parada-dos-serviços)
7. [Swagger e Debug](#7--swagger-e-debug)

## 1. 📃 Pré-requisitos

- Docker instalado
- Docker Compose instalado
- Minikube e Kubectl (Opcional)

## 2. 📦 Estrutura do Projeto

- `build.gradle` - Configuração do Gradle.
- `settings.gradle` - Configuração dos projetos Gradle.
- `src/main/java` - Código fonte da aplicação.
- `src/main/resources` - Recursos da aplicação, incluindo scripts de migração Flyway.
- `Dockerfile` - Instruções para criar a imagem Docker da aplicação.
- `docker-compose.yml` - Configuração para iniciar os serviços Docker.

## 3. 🐋 Configuração e Execução do Projeto com Docker

### Passo 1: Construir imagens e inciar serviços
Antes de executar a aplicação, você precisa construir as imagens e contêineres Docker. 
Com o Docker Desktop aberto (Windows) ou com serviço do Docker rodando (macOS/Linux), navegue até o diretório do projeto e execute o seguinte comando:
```sh
docker compose up -d
```
_O "-d" significa "detached mode". Assim o docker compose inicia os contêineres em segundo plano._

Este comando criará e iniciará dois contêineres:

- `postgres`: um contêiner rodando PostgreSQL.
- `tech-challenge`: um contêiner rodando a aplicação Java.

### Passo 2: Verificar a Aplicação

Após iniciar os serviços, você pode verificar se a aplicação está funcionando corretamente acessando a rota de HealthCheck em `http://localhost:8080/healthcheck`. A resposta esperada é:

```plaintext
API is up and running
```

Você também pode verificar os logs para garantir que a aplicação e o banco de dados iniciaram corretamente:

```sh
docker compose logs -f
```
Ou utilize o Docker Desktop.

## 4. ☸️ Kubernetes (K8s)
Para executar o projeto dentro de um cluster Kubernetes (K8s), é necessário, primeiramente, 
criar seu cluster usando Minikube ou Docker Desktop. Após a criação do cluster, 
siga os passos abaixo utilizando o terminal na pasta de deployment (src/main/resources/deployment):

1. `kubectl apply -f postgres-secrets.yaml`
2. `kubectl apply -f postgres-deployment.yaml`
3. `kubectl apply -f app-deploy.yaml`

O Kubernetes será responsável por criar toda a estrutura de deployments, load balancer, services e pods.
Para visualizar todos os pods em execução, bem como seus IDs e portas, use o comando:
`kubectl get pods`  

Para acessar um pod a partir de sua máquina, é necessário fazer um "port forward" com o seguinte comando:  
`kubectl port-forward spring-rest-api-${id} 8080:8080`  

Onde `${id}` é o identificar do POD. 

## 5. ⚙️ Configurações

### Banco de Dados

O banco de dados PostgreSQL está configurado com as seguintes credenciais (definidas no `docker-compose.yml`):

- **Nome do Banco de Dados**: `galega_burguer`
- **Usuário**: `postgres`
- **Senha**: `postgres`

### Variáveis de Ambiente

As variáveis de ambiente para a configuração da fonte de dados do Spring Boot estão definidas no `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/galega_burguer
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: postgres
```

### Flyway

Os scripts de migração do Flyway devem ser colocados no diretório `src/main/resources/db/migration`. O Flyway irá automaticamente detectar esses scripts e aplicá-los ao banco de dados ao iniciar a aplicação.

## 6. 🧹 Limpeza e Parada dos Serviços

Para parar os serviços e remover os contêineres, volumes e redes criados pelo `docker compose`, execute:

```sh
docker compose down -v
```

Ou utilize o Docker Desktop.

## 7. 🪰 Swagger e Debug

### Swagger
Para acessar nossa documentação de API REST no padrão Swagger, basta acessar a seguinte URL no seu navegador:
> [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Debug
Tanto o `Dockerfile` quanto o `docker-compose.yml` estão configurados para expor a porta **5005** para debug. Basta conectar o JVM debug de sua IDE em:
>  **localhost:5005**
