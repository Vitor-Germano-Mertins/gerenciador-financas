# 💰 Gerenciador de Finanças

Sistema de console em Java para controle de finanças pessoais, com autenticação segura, CRUD completo de transações e painel administrativo. Desenvolvido como projeto de portfólio durante o curso de Análise e Desenvolvimento de Sistemas na UNISINOS.

## 📋 Funcionalidades

### Usuário
- Cadastro e login com senha criptografada (hash + salt via BCrypt)
- Registro de transações (receitas e despesas) com categorias fixas
- Listagem, edição e exclusão de transações
- Cálculo de saldo em tempo real
- Exclusão da própria conta, com reautenticação de senha por segurança

### Administrador
- Cadastro de conta administrativa protegido por chave secreta
- Painel para listar todos os usuários do sistema
- Exclusão de qualquer conta de usuário

## 🛠️ Tecnologias

- **Java 17** — linguagem principal
- **Maven** — gerenciamento de dependências e build
- **PostgreSQL** — banco de dados relacional
- **JDBC** — conexão e comunicação com o banco
- **jBCrypt** — hash seguro de senhas

## 🏗️ Arquitetura

O projeto segue uma estrutura em camadas, separando responsabilidades:

```
src/main/java/gerenciador_financas/
├── App.java              # Ponto de entrada e menus (camada de apresentação)
├── model/                # Classes que representam os dados (Usuario, Transacao, Categoria)
├── dao/                  # Acesso ao banco de dados (UsuarioDAO, TransacaoDAO, CategoriaDAO)
├── database/             # Configuração da conexão com o PostgreSQL
└── exception/            # Exceções customizadas (ex: EmailJaCadastradoException)
```

## 🗄️ Modelagem do banco

O banco possui três tabelas principais:

- **usuarios** — dados de login, com o campo `tipo` diferenciando contas `comum` e `admin`
- **categorias** — categorias fixas de receita/despesa (ex: Salário, Alimentação), evitando inconsistência de texto livre
- **transacoes** — cada transação referencia um usuário (`usuario_id`) e uma categoria (`categoria_id`) via chave estrangeira

A exclusão de um usuário remove automaticamente suas transações (`ON DELETE CASCADE`).

## 🔒 Segurança

- Senhas nunca são armazenadas em texto puro — apenas o hash gerado com BCrypt (que já embute salt aleatório)
- Todas as queries usam `PreparedStatement`, prevenindo SQL Injection
- Credenciais do banco e a chave de administrador ficam em `config.properties`, fora do controle de versão
- Exclusão de conta exige reautenticação de senha, mesmo com o usuário já logado

## 🚀 Como rodar

### Pré-requisitos
- Java 17 ou superior
- Maven
- PostgreSQL instalado e rodando

### Passo a passo

1. Clone o repositório:
   ```bash
   git clone https://github.com/Vitor-Germano-Mertins/gerenciador-financas.git
   cd gerenciador-financas
   ```

2. Crie o banco de dados no PostgreSQL:
   ```sql
   CREATE DATABASE financas_db;
   ```

3. Copie o arquivo de configuração de exemplo e preencha com seus dados:
   ```bash
   cp config.properties.example config.properties
   ```
   Edite `config.properties` com sua senha do PostgreSQL e defina uma chave de administrador de sua escolha.

4. Execute os scripts de criação das tabelas (peça o script completo se ainda não tiver salvo um `.sql` no repositório).

5. Compile e execute:
   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass="gerenciador_financas.App"
   ```

## 📌 Próximos passos

- Filtros de relatório por categoria e período
- Dashboard com resumo ao fazer login

## 👤 Autor

**Vitor Germano Mertins**
Estudante de Análise e Desenvolvimento de Sistemas — UNISINOS

[GitHub](https://github.com/Vitor-Germano-Mertins)