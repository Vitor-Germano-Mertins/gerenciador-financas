package gerenciador_financas.dao;

import gerenciador_financas.database.ConexaoBD;
import gerenciador_financas.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public void cadastrar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha_hash) VALUES (?, ?, ?)";

        String hash = BCrypt.hashpw(usuario.getSenhaHash(), BCrypt.gensalt());

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, hash);

            stmt.executeUpdate();
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
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    return usuario;
                }
            }
        }
        return null;
    }
}