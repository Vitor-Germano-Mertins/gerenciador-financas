package gerenciador_financas.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoBD {

    public static Properties carregarConfig() throws IOException {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        }
        return props;
    }

    public static Connection conectar() throws SQLException {
        try {
            Properties config = carregarConfig();

            String url = config.getProperty("db.url");
            String usuario = config.getProperty("db.usuario");
            String senha = config.getProperty("db.senha");

            return DriverManager.getConnection(url, usuario, senha);
        } catch (IOException e) {
            throw new SQLException("Erro ao ler arquivo de configuração: " + e.getMessage());
        }
    }
}