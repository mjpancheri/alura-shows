package br.com.alura.owasp.dao;

import br.com.alura.owasp.model.Role;
import br.com.alura.owasp.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.logging.Logger;

@Repository
public class UsuarioDaoImpl implements UsuarioDao {

    @PersistenceContext
    private EntityManager manager;

    public void salva(Usuario usuario) {
        encryptUsuarioSenha(usuario);

        Logger.getAnonymousLogger().info("Criando user: " + usuario);
        manager.persist(usuario);
    }

    private void encryptUsuarioSenha(Usuario usuario) {
        String salto = BCrypt.gensalt();
        String senhaHash = BCrypt.hashpw(usuario.getSenha(), salto);
        usuario.setSenha(senhaHash);
    }

    public Usuario procuraUsuario(Usuario usuario) {
        TypedQuery<Usuario> query = manager.createQuery("select u from Usuario u where u.email=:email", Usuario.class);
        query.setParameter("email", usuario.getEmail());

        Usuario usuarioEncontrado = query.getResultList().stream().findFirst().orElse(null);

        if (isSenhaValidaUsuarioEncontrado(usuario, usuarioEncontrado)) {
            return usuarioEncontrado;
        }
        return null;
    }

    private boolean isSenhaValidaUsuarioEncontrado(Usuario usuario, Usuario usuarioEncontrado) {
        if (usuarioEncontrado == null) {
            return false;
        }
        return BCrypt.checkpw(usuario.getSenha(), usuarioEncontrado.getSenha());
    }
}
