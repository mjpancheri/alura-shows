package br.com.alura.owasp.model.dto;

import br.com.alura.owasp.model.Usuario;

public class UsuarioDTO {

    private String email;
    private String senha;
    private String nome;
    private String nomeImagem;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeImagem() {
        return nomeImagem;
    }

    public void setNomeImagem(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }

    public Usuario montaUsuario() {
        return new Usuario(email, senha, nome, nomeImagem);
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", nome='" + nome + '\'' +
                ", nomeImagem='" + nomeImagem + '\'' +
                '}';
    }
}
