package gerenciador_financas.exception;

public class EmailJaCadastradoException extends Exception {
    public EmailJaCadastradoException(String email) {
        super("O e-mail '" + email + "' já está cadastrado.");
    }
}