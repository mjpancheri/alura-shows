package br.com.alura.owasp.controller;

import br.com.alura.owasp.dao.UsuarioDao;
import br.com.alura.owasp.model.Role;
import br.com.alura.owasp.model.Usuario;
import br.com.alura.owasp.retrofit.GoogleWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Controller
@Transactional
public class UsuarioController {

    @Autowired
    private UsuarioDao dao;
    @Autowired
    private GoogleWebClient cliente;

    private static final String SITE_KEY = System.getenv("RECAPTCHA_SITE");

    @RequestMapping("/usuario")
    public String usuario(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        model.addAttribute("siteKey", SITE_KEY);

        Logger.getAnonymousLogger().info("siteKey: " + SITE_KEY);

        return "usuario";
    }

    @RequestMapping("/usuarioLogado")
    public String usuarioLogado() {
        return "usuarioLogado";
    }

    @RequestMapping(value = "/registrar", method = RequestMethod.POST)
    public String registrar(MultipartFile imagem,
                            @ModelAttribute("usuarioRegistro") Usuario usuarioRegistro,
                            RedirectAttributes redirect, HttpServletRequest request,
                            Model model, HttpSession session) throws IllegalStateException, IOException {

        tratarImagem(imagem, usuarioRegistro, request);
        usuarioRegistro.getRoles().add(new Role("ROLE_USER"));

        dao.salva(usuarioRegistro);
        session.setAttribute("usuario", usuarioRegistro);
        model.addAttribute("usuario", usuarioRegistro);
        return "usuarioLogado";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute("usuario") Usuario usuario,
                        RedirectAttributes redirect, Model model, HttpSession session, HttpServletRequest request) throws IOException {

        String recaptcha = request.getParameter("g-recaptcha-response");

        if (cliente.verifica(recaptcha)) {
            return procurarUsuario(usuario, redirect, model, session);
        }

        redirect.addFlashAttribute("mensagem", "Por favor, comprove que você é humano!");
        return "redirect:/usuario";
    }

    private String procurarUsuario(Usuario usuario, RedirectAttributes redirect, Model model, HttpSession session) {
        Usuario usuarioRetornado = dao.procuraUsuario(usuario);
        model.addAttribute("usuario", usuarioRetornado);
        if (usuarioRetornado == null) {
            redirect.addFlashAttribute("mensagem", "Usuário não encontrado");
            return "redirect:/usuario";
        }

        session.setAttribute("usuario", usuarioRetornado);
        return "usuarioLogado";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("usuario");
        return "usuario";
    }

    private void tratarImagem(MultipartFile imagem, Usuario usuario,
                              HttpServletRequest request) throws IllegalStateException, IOException {
        usuario.setNomeImagem(imagem.getOriginalFilename());
        File arquivo = new File(request.getServletContext().getRealPath(
                "/image"), usuario.getNomeImagem());
        imagem.transferTo(arquivo);

    }
}
