package gerenciador_financas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transacao {
    private int id;
    private int usuarioId;
    private String descricao;
    private BigDecimal valor;
    private String tipo;
    private int categoriaId;
    private LocalDate dataTransacao;

    public Transacao() {
    }

    public Transacao(int usuarioId, String descricao, BigDecimal valor, String tipo, int categoriaId,
            LocalDate dataTransacao) {
        this.usuarioId = usuarioId;
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.categoriaId = categoriaId;
        this.dataTransacao = dataTransacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}