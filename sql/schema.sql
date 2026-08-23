-- ============================================================
-- Gerenciador de Finanças - Script de criação do banco de dados
-- ============================================================
-- Execute este script inteiro em um banco PostgreSQL vazio
-- (crie o banco antes com: CREATE DATABASE financas_db;)
-- ============================================================

-- ------------------------------------------------------------
-- Tabela: usuarios
-- Guarda quem pode logar no sistema. O campo "tipo" diferencia
-- contas comuns de contas administrativas.
-- ------------------------------------------------------------
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    tipo VARCHAR(10) NOT NULL DEFAULT 'comum' CHECK (tipo IN ('comum', 'admin')),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- Tabela: categorias
-- Categorias fixas de receita/despesa. A combinação nome+tipo
-- é única (permite, por exemplo, "Outros" existir tanto como
-- receita quanto como despesa).
-- ------------------------------------------------------------
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('receita', 'despesa')),
    UNIQUE (nome, tipo)
);

INSERT INTO categorias (nome, tipo) VALUES
    ('Salário', 'receita'),
    ('Freelance', 'receita'),
    ('Investimentos', 'receita'),
    ('Outros', 'receita'),
    ('Alimentação', 'despesa'),
    ('Transporte', 'despesa'),
    ('Moradia', 'despesa'),
    ('Saúde', 'despesa'),
    ('Lazer', 'despesa'),
    ('Educação', 'despesa'),
    ('Outros', 'despesa');

-- ------------------------------------------------------------
-- Tabela: transacoes
-- Cada transação pertence a um usuário e a uma categoria.
-- Se o usuário for excluído, suas transações são excluídas
-- automaticamente (ON DELETE CASCADE).
-- ------------------------------------------------------------
CREATE TABLE transacoes (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    descricao VARCHAR(200) NOT NULL,
    valor NUMERIC(10, 2) NOT NULL,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('receita', 'despesa')),
    categoria_id INTEGER REFERENCES categorias(id),
    data_transacao DATE NOT NULL DEFAULT CURRENT_DATE
);

-- ============================================================
-- Fim do script. O banco está pronto para uso.
-- ============================================================