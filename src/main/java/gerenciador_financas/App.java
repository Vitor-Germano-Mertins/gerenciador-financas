package gerenciador_financas;

import java.sql.SQLException;
import java.util.Scanner;

import gerenciador_financas.dao.UsuarioDAO;
import gerenciador_financas.model.Usuario;

public class App {

    static Scanner scanner = new Scanner(System.in);
    static UsuarioDAO usuarioDAO = new UsuarioDAO();

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
            int valor = Integer.parseInt(scanner.nextLine());
            return valor;
        } catch (NumberFormatException e) {
            return -1; // valor inválido, cai no "default" do switch
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
        novoUsuario.setSenhaHash(senha);

        try {
            usuarioDAO.cadastrar(novoUsuario);
            System.out.println("Usuário cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
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

        while (opcao != 2) {
            System.out.println("\n===== Olá, " + usuario.getNome() + " =====");
            System.out.println("1 - Ver transações (em construção)");
            System.out.println("2 - Logout");
            System.out.print("Escolha uma opção: ");

            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    System.out.println("Funcionalidade de transações ainda não implementada.");
                    break;
                case 2:
                    System.out.println("Logout realizado.");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
    }
}