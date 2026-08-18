package gerenciador_financas.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gerenciador_financas.database.ConexaoBD;
import gerenciador_financas.model.Transacao;

public class TransacaoDAO {

    public void cadastrar(Transacao transacao) throws SQLException {
        String sql = "INSERT INTO transacoes (usuario_id, descricao, valor, tipo, categoria, data_transacao) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, transacao.getUsuarioId());
            stmt.setString(2, transacao.getDescricao());
            stmt.setBigDecimal(3, transacao.getValor());
            stmt.setString(4, transacao.getTipo());
            stmt.setString(5, transacao.getCategoria());
            stmt.setDate(6, Date.valueOf(transacao.getDataTransacao()));

            stmt.executeUpdate();
        }
    }

    public void atualizar(Transacao transacao) throws SQLException {
        String sql = "UPDATE transacoes SET descricao = ?, valor = ?, tipo = ?, categoria = ?, data_transacao = ? " +
                "WHERE id = ? AND usuario_id = ?";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, transacao.getDescricao());
            stmt.setBigDecimal(2, transacao.getValor());
            stmt.setString(3, transacao.getTipo());
            stmt.setString(4, transacao.getCategoria());
            stmt.setDate(5, Date.valueOf(transacao.getDataTransacao()));
            stmt.setInt(6, transacao.getId());
            stmt.setInt(7, transacao.getUsuarioId());

            stmt.executeUpdate();
        }
    }

    public Transacao buscarPorId(int id, int usuarioId) throws SQLException {
        String sql = "SELECT * FROM transacoes WHERE id = ? AND usuario_id = ?";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Transacao t = new Transacao();
                t.setId(rs.getInt("id"));
                t.setUsuarioId(rs.getInt("usuario_id"));
                t.setDescricao(rs.getString("descricao"));
                t.setValor(rs.getBigDecimal("valor"));
                t.setTipo(rs.getString("tipo"));
                t.setCategoria(rs.getString("categoria"));
                t.setDataTransacao(rs.getDate("data_transacao").toLocalDate());
                return t;
            }
        }

        return null;
    }

    public List<Transacao> listarPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM transacoes WHERE usuario_id = ? ORDER BY data_transacao DESC";
        List<Transacao> transacoes = new ArrayList<>();

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transacao t = new Transacao();
                t.setId(rs.getInt("id"));
                t.setUsuarioId(rs.getInt("usuario_id"));
                t.setDescricao(rs.getString("descricao"));
                t.setValor(rs.getBigDecimal("valor"));
                t.setTipo(rs.getString("tipo"));
                t.setCategoria(rs.getString("categoria"));
                t.setDataTransacao(rs.getDate("data_transacao").toLocalDate());
                transacoes.add(t);
            }
        }

        return transacoes;
    }

    public BigDecimal calcularSaldo(int usuarioId) throws SQLException {
        String sql = "SELECT " +
                "COALESCE(SUM(CASE WHEN tipo = 'receita' THEN valor ELSE 0 END), 0) - " +
                "COALESCE(SUM(CASE WHEN tipo = 'despesa' THEN valor ELSE 0 END), 0) AS saldo " +
                "FROM transacoes WHERE usuario_id = ?";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("saldo");
            }
        }

        return BigDecimal.ZERO;
    }

    public void deletar(int transacaoId, int usuarioId) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ? AND usuario_id = ?";

        try (Connection conexao = ConexaoBD.conectar();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, transacaoId);
            stmt.setInt(2, usuarioId);

            stmt.executeUpdate();
        }
    }
}