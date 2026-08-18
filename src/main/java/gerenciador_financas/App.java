package gerenciador_financas;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import gerenciador_financas.dao.TransacaoDAO;
import gerenciador_financas.dao.UsuarioDAO;
import gerenciador_financas.exception.EmailJaCadastradoException;
import gerenciador_financas.model.Transacao;
import gerenciador_financas.model.Usuario;

public class App {

    static Scanner scanner = new Scanner(System.in);
    static UsuarioDAO usuarioDAO = new UsuarioDAO();
    static TransacaoDAO transacaoDAO = new TransacaoDAO();

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

    static void cadastrarUsuario() {
        System.out.println("\n--- Cadastro de novo usuário ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);

        try {
            usuarioDAO.cadastrar(novoUsuario, senha);
            System.out.println("Usuário cadastrado com sucesso!");
        } catch (EmailJaCadastradoException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Não foi possível completar o cadastro. Tente novamente mais tarde.");
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

        while (opcao != 5) {
            System.out.println("\n===== Olá, " + usuario.getNome() + " =====");
            System.out.println("1 - Nova transação");
            System.out.println("2 - Ver transações");
            System.out.println("3 - Ver saldo");
            System.out.println("4 - Deletar transação");
            System.out.println("5 - Logout");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    novaTransacao(usuario);
                    break;
                case 2:
                    listarTransacoes(usuario);
                    break;
                case 3:
                    verSaldo(usuario);
                    break;
                case 4:
                    deletarTransacao(usuario);
                    break;
                case 5:
                    System.out.println("Logout realizado.");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    static void novaTransacao(Usuario usuario) {
        System.out.println("\n--- Nova Transação ---");

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        BigDecimal valor;
        while (true) {
            System.out.print("Valor (ex: 150.50): ");
            try {
                valor = new BigDecimal(scanner.nextLine().replace(",", "."));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, tente novamente.");
            }
        }

        String tipo;
        while (true) {
            System.out.print("Tipo (receita/despesa): ");
            tipo = scanner.nextLine().trim().toLowerCase();
            if (tipo.equals("receita") || tipo.equals("despesa")) {
                break;
            }
            System.out.println("Digite 'receita' ou 'despesa'.");
        }

        System.out.print("Categoria (ex: Alimentação, Salário): ");
        String categoria = scanner.nextLine();

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

        Transacao transacao = new Transacao(usuario.getId(), descricao, valor, tipo, categoria, data);

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
                System.out.printf("[%d] %s | %s | R$ %.2f | %s | %s%n",
                        t.getId(),
                        t.getDataTransacao(),
                        t.getTipo().toUpperCase(),
                        t.getValor(),
                        t.getCategoria(),
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
}