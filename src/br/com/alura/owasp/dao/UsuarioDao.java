package br.com.alura.owasp.dao;

import br.com.alura.owasp.model.Usuario;
import br.com.alura.owasp.model.dto.UsuarioDTO;

public interface UsuarioDao {

    void salva(Usuario usuario);

    Usuario procuraUsuario(UsuarioDTO usuario);
}
