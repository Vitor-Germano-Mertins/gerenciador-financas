package gerenciador_financas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import gerenciador_financas.database.ConexaoBD;
import gerenciador_financas.exception.EmailJaCadastradoException;
import gerenciador_financas.model.Usuario;

public class UsuarioDAO {

    public void cadastrar(Usuario usuario, String senhaPlana) throws SQLException, EmailJaCadastradoException {
        if (emailExiste(usuario.getEmail())) {
            throw new EmailJaCadastradoException(usuario.getEmail());
        }

        String tipo = usuario.getTipo() != null ? usuario.getTipo() : "comum";
        String sql = "INSERT INTO usuarios (nome, email, senha_hash, tipo) VALUES (?, ?, ?, ?)";
        String hash = BCrypt.hashpw(senhaPlana, BCrypt.gensalt());

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, hash);
            stmt.setString(4, tipo);

            stmt.executeUpdate();
        }
    }

    private boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public Usuario autenticar(String email, String senhaDigitada) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashSalvo = rs.getString("senha_hash");

                if (BCrypt.checkpw(senhaDigitada, hashSalvo)) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    /**
     * Confere se a senha informada bate com a senha atual do usuário.
     * Usado para reautenticação antes de ações sensíveis, como excluir a própria
     * conta.
     */
    public boolean confirmarSenha(int usuarioId, String senhaDigitada) throws SQLException {
        String sql = "SELECT senha_hash FROM usuarios WHERE id = ?";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashSalvo = rs.getString("senha_hash");
                return BCrypt.checkpw(senhaDigitada, hashSalvo);
            }
        }
        return false;
    }

    public void excluir(int usuarioId) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }

        return usuarios;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTipo(rs.getString("tipo"));
        return usuario;
    }
}