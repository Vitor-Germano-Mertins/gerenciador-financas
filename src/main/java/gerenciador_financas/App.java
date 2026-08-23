package gerenciador_financas;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import gerenciador_financas.dao.CategoriaDAO;
import gerenciador_financas.dao.TransacaoDAO;
import gerenciador_financas.dao.UsuarioDAO;
import gerenciador_financas.database.ConexaoBD;
import gerenciador_financas.exception.EmailJaCadastradoException;
import gerenciador_financas.model.Categoria;
import gerenciador_financas.model.Transacao;
import gerenciador_financas.model.Usuario;

public class App {

    static Scanner scanner = new Scanner(System.in);
    static UsuarioDAO usuarioDAO = new UsuarioDAO();
    static TransacaoDAO transacaoDAO = new TransacaoDAO();
    static CategoriaDAO categoriaDAO = new CategoriaDAO();

    public static void main(String[] args) {
        menuPrincipal();
    }

    static void menuPrincipal() {
        int opcao = -1;

        while (opcao != 3) {
            System.out.println("\n===== GERENCIADOR DE FINANÇAS =====");
            System.out.println("1 - Cadastrar usuário");
            System.out.println("2 - Login");
            System.out.println("3 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    fazerLogin();
                    break;
                case 3:
                    System.out.println("Saindo... até mais!");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static boolean emailValido(String email) {
        if (email.isEmpty())
            return false;
        return email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    static void cadastrarUsuario() {
        System.out.println("\n--- Cadastro de novo usuário ---");

        String nome;
        while (true) {
            System.out.print("Nome: ");
            nome = scanner.nextLine().trim();
            if (!nome.isEmpty())
                break;
            System.out.println("O nome não pode ficar em branco.");
        }

        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();
            if (emailValido(email))
                break;
            System.out.println("Digite um e-mail válido (ex: nome@dominio.com).");
        }

        String senha;
        while (true) {
            System.out.print("Senha (mínimo 6 caracteres): ");
            senha = scanner.nextLine();
            if (senha.length() >= 6)
                break;
            System.out.println("A senha precisa ter pelo menos 6 caracteres.");
        }

        String tipo = "comum";
        System.out.print("Deseja cadastrar como administrador? (s/n): ");
        String querAdmin = scanner.nextLine().trim().toLowerCase();

        if (querAdmin.equals("s")) {
            System.out.print("Digite a chave de administrador: ");
            String chaveDigitada = scanner.nextLine();

            if (validarChaveAdmin(chaveDigitada)) {
                tipo = "admin";
                System.out.println("Chave correta! Conta será criada como administrador.");
            } else {
                System.out.println("Chave incorreta. A conta será criada como usuário comum.");
            }
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setTipo(tipo);

        try {
            usuarioDAO.cadastrar(novoUsuario, senha);
            System.out.println("Usuário cadastrado com sucesso!");
        } catch (EmailJaCadastradoException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Não foi possível completar o cadastro. Tente novamente mais tarde.");
        }
    }

    static boolean validarChaveAdmin(String chaveDigitada) {
        try {
            Properties config = ConexaoBD.carregarConfig();
            String chaveCorreta = config.getProperty("admin.chave");
            return chaveCorreta != null && chaveCorreta.equals(chaveDigitada);
        } catch (IOException e) {
            System.out.println("Erro ao validar chave de administrador.");
            return false;
        }
    }

    static void fazerLogin() {
        System.out.println("\n--- Login ---");

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            Usuario usuario = usuarioDAO.autenticar(email, senha);

            if (usuario != null) {
                System.out.println("Login realizado! Bem-vindo, " + usuario.getNome());
                menuLogado(usuario);
            } else {
                System.out.println("Email ou senha incorretos.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao fazer login: " + e.getMessage());
        }
    }

    static void menuLogado(Usuario usuario) {
        int opcao = -1;
        int opcaoSair = usuario.isAdmin() ? 8 : 7;

        while (opcao != opcaoSair) {
            System.out.println("\n===== Olá, " + usuario.getNome() + " =====");
            System.out.println("1 - Nova transação");
            System.out.println("2 - Ver transações");
            System.out.println("3 - Ver saldo");
            System.out.println("4 - Deletar transação");
            System.out.println("5 - Editar transação");
            System.out.println("6 - Excluir minha conta");
            if (usuario.isAdmin()) {
                System.out.println("7 - Gerenciar usuários (admin)");
            }
            System.out.println(opcaoSair + " - Logout");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            if (opcao == 1) {
                novaTransacao(usuario);
            } else if (opcao == 2) {
                listarTransacoes(usuario);
            } else if (opcao == 3) {
                verSaldo(usuario);
            } else if (opcao == 4) {
                deletarTransacao(usuario);
            } else if (opcao == 5) {
                editarTransacao(usuario);
            } else if (opcao == 6) {
                boolean contaExcluida = excluirPropriaConta(usuario);
                if (contaExcluida) {
                    return; // encerra o menu logado, volta pro menu principal
                }
            } else if (opcao == 7 && usuario.isAdmin()) {
                gerenciarUsuarios(usuario);
            } else if (opcao == opcaoSair) {
                System.out.println("Logout realizado.");
            } else {
                System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    /**
     * Pede a senha atual do usuário logado e, se confirmar corretamente,
     * exclui a própria conta (e as transações associadas, via ON DELETE CASCADE).
     * Retorna true se a conta foi excluída.
     */
    static boolean excluirPropriaConta(Usuario usuario) {
        System.out.println("\n--- Excluir minha conta ---");
        System.out.println("Essa ação é PERMANENTE e vai apagar sua conta e todas as suas transações.");
        System.out.print("Digite sua senha atual para confirmar: ");
        String senha = scanner.nextLine();

        try {
            boolean senhaCorreta = usuarioDAO.confirmarSenha(usuario.getId(), senha);

            if (!senhaCorreta) {
                System.out.println("Senha incorreta. Exclusão cancelada.");
                return false;
            }

            System.out.print("Tem certeza? Digite CONFIRMAR para excluir definitivamente: ");
            String confirmacao = scanner.nextLine();

            if (!confirmacao.equals("CONFIRMAR")) {
                System.out.println("Exclusão cancelada.");
                return false;
            }

            usuarioDAO.excluir(usuario.getId());
            System.out.println("Conta excluída com sucesso. Até mais!");
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao excluir conta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Painel restrito ao administrador: lista todos os usuários
     * e permite excluir qualquer um deles pelo ID.
     */
    static void gerenciarUsuarios(Usuario admin) {
        int opcao = -1;

        while (opcao != 2) {
            System.out.println("\n--- Gerenciar Usuários (admin) ---");
            System.out.println("1 - Listar e excluir usuário");
            System.out.println("2 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            if (opcao == 1) {
                excluirUsuarioComoAdmin(admin);
            } else if (opcao != 2) {
                System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    static void excluirUsuarioComoAdmin(Usuario admin) {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();

            if (usuarios.isEmpty()) {
                System.out.println("Nenhum usuário cadastrado.");
                return;
            }

            System.out.println("\nUsuários cadastrados:");
            for (Usuario u : usuarios) {
                System.out.printf("[%d] %s | %s | %s%n", u.getId(), u.getNome(), u.getEmail(), u.getTipo());
            }

            System.out.print("\nDigite o ID do usuário que deseja excluir (ou 0 para cancelar): ");
            int id = Integer.parseInt(scanner.nextLine());

            if (id == 0) {
                System.out.println("Operação cancelada.");
                return;
            }

            if (id == admin.getId()) {
                System.out.println(
                        "Você não pode excluir sua própria conta por aqui. Use a opção 'Excluir minha conta' no menu principal.");
                return;
            }

            System.out.print("Tem certeza? Essa ação é permanente. Digite CONFIRMAR: ");
            String confirmacao = scanner.nextLine();

            if (!confirmacao.equals("CONFIRMAR")) {
                System.out.println("Operação cancelada.");
                return;
            }

            usuarioDAO.excluir(id);
            System.out.println("Usuário excluído com sucesso.");

        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        } catch (SQLException e) {
            System.out.println("Erro ao excluir usuário: " + e.getMessage());
        }
    }

    /**
     * Mostra as categorias de um tipo (receita/despesa) numeradas,
     * e devolve o ID da categoria escolhida pela pessoa.
     * Retorna -1 se não houver categorias cadastradas daquele tipo.
     */
    static int escolherCategoria(String tipo) throws SQLException {
        List<Categoria> categorias = categoriaDAO.listarPorTipo(tipo);

        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria de " + tipo + " cadastrada.");
            return -1;
        }

        System.out.println("Categorias de " + tipo + ":");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.println((i + 1) + " - " + categorias.get(i).getNome());
        }

        while (true) {
            System.out.print("Escolha o número da categoria: ");
            try {
                int escolha = Integer.parseInt(scanner.nextLine());
                if (escolha >= 1 && escolha <= categorias.size()) {
                    return categorias.get(escolha - 1).getId();
                }
                System.out.println("Opção inválida.");
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    static void novaTransacao(Usuario usuario) {
        System.out.println("\n--- Nova Transação ---");

        String descricao;
        while (true) {
            System.out.print("Descrição: ");
            descricao = scanner.nextLine().trim();
            if (!descricao.isEmpty())
                break;
            System.out.println("A descrição não pode ficar em branco.");
        }

        BigDecimal valor;
        while (true) {
            System.out.print("Valor (ex: 150.50): ");
            try {
                valor = new BigDecimal(scanner.nextLine().replace(",", "."));
                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("O valor precisa ser maior que zero.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, tente novamente.");
            }
        }

        String tipo;
        while (true) {
            System.out.print("Tipo (receita/despesa): ");
            tipo = scanner.nextLine().trim().toLowerCase();
            if (tipo.equals("receita") || tipo.equals("despesa"))
                break;
            System.out.println("Digite 'receita' ou 'despesa'.");
        }

        int categoriaId;
        try {
            categoriaId = escolherCategoria(tipo);
            if (categoriaId == -1)
                return;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar categorias: " + e.getMessage());
            return;
        }

        LocalDate data;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            System.out.print("Data (dd/mm/aaaa, ou deixe em branco para hoje): ");
            String dataTexto = scanner.nextLine().trim();
            if (dataTexto.isEmpty()) {
                data = LocalDate.now();
                break;
            }
            try {
                data = LocalDate.parse(dataTexto, formatter);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida, use o formato dd/mm/aaaa.");
            }
        }

        Transacao transacao = new Transacao(usuario.getId(), descricao, valor, tipo, categoriaId, data);

        try {
            transacaoDAO.cadastrar(transacao);
            System.out.println("Transação registrada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao registrar transação: " + e.getMessage());
        }
    }

    static void listarTransacoes(Usuario usuario) {
        System.out.println("\n--- Suas Transações ---");

        try {
            List<Transacao> transacoes = transacaoDAO.listarPorUsuario(usuario.getId());

            if (transacoes.isEmpty()) {
                System.out.println("Nenhuma transação registrada ainda.");
                return;
            }

            for (Transacao t : transacoes) {
                String nomeCategoria = "?";
                Categoria categoria = categoriaDAO.buscarPorId(t.getCategoriaId());
                if (categoria != null) {
                    nomeCategoria = categoria.getNome();
                }

                System.out.printf("[%d] %s | %s | R$ %.2f | %s | %s%n",
                        t.getId(),
                        t.getDataTransacao(),
                        t.getTipo().toUpperCase(),
                        t.getValor(),
                        nomeCategoria,
                        t.getDescricao());
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar transações: " + e.getMessage());
        }
    }

    static void verSaldo(Usuario usuario) {
        try {
            BigDecimal saldo = transacaoDAO.calcularSaldo(usuario.getId());
            System.out.printf("%nSaldo atual: R$ %.2f%n", saldo);
        } catch (SQLException e) {
            System.out.println("Erro ao calcular saldo: " + e.getMessage());
        }
    }

    static void deletarTransacao(Usuario usuario) {
        listarTransacoes(usuario);

        System.out.print("\nDigite o ID da transação que deseja deletar: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            transacaoDAO.deletar(id, usuario.getId());
            System.out.println("Transação deletada (se o ID pertencia a você).");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        } catch (SQLException e) {
            System.out.println("Erro ao deletar transação: " + e.getMessage());
        }
    }

    static void editarTransacao(Usuario usuario) {
        listarTransacoes(usuario);

        System.out.print("\nDigite o ID da transação que deseja editar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        Transacao transacao;
        try {
            transacao = transacaoDAO.buscarPorId(id, usuario.getId());
        } catch (SQLException e) {
            System.out.println("Erro ao buscar transação: " + e.getMessage());
            return;
        }

        if (transacao == null) {
            System.out.println("Transação não encontrada (ou não pertence a você).");
            return;
        }

        System.out.println("Deixe em branco pra manter o valor atual.");

        System.out.print("Descrição atual (" + transacao.getDescricao() + "): ");
        String descricao = scanner.nextLine();
        if (!descricao.isBlank()) {
            transacao.setDescricao(descricao);
        }

        System.out.print("Valor atual (" + transacao.getValor() + "): ");
        String valorTexto = scanner.nextLine();
        if (!valorTexto.isBlank()) {
            try {
                transacao.setValor(new BigDecimal(valorTexto.replace(",", ".")));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, mantendo o valor anterior.");
            }
        }

        System.out.print("Trocar categoria? (s/n): ");
        String trocarCategoria = scanner.nextLine().trim().toLowerCase();
        if (trocarCategoria.equals("s")) {
            try {
                int novaCategoriaId = escolherCategoria(transacao.getTipo());
                if (novaCategoriaId != -1) {
                    transacao.setCategoriaId(novaCategoriaId);
                }
            } catch (SQLException e) {
                System.out.println("Erro ao buscar categorias, mantendo a categoria anterior.");
            }
        }

        try {
            transacaoDAO.atualizar(transacao);
            System.out.println("Transação atualizada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar transação: " + e.getMessage());
        }
    }
}