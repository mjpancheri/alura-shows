package br.com.alura.owasp.controller;

import br.com.alura.owasp.dao.UsuarioDao;
import br.com.alura.owasp.model.Role;
import br.com.alura.owasp.model.Usuario;
import br.com.alura.owasp.model.dto.UsuarioDTO;
import br.com.alura.owasp.retrofit.GoogleWebClient;
import br.com.alura.owasp.validation.ImagemValidator;
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
    @Autowired
    private ImagemValidator imagemValidator;

    // alternativa para controlar os atributos de input do form, usando SpringMVC
    /*@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("nome", "email", "senha", "nomeImagem");
    }*/

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
                            @ModelAttribute("usuarioRegistro") UsuarioDTO usuarioDTO,
                            RedirectAttributes redirect, HttpServletRequest request,
                            Model model, HttpSession session) throws IllegalStateException, IOException {

        Usuario usuarioRegistro = usuarioDTO.montaUsuario();
        if (imagemValidator.validarImagem(imagem, usuarioRegistro, request)) {
            usuarioRegistro.getRoles().add(new Role("ROLE_USER"));

            dao.salva(usuarioRegistro);
            session.setAttribute("usuario", usuarioRegistro);
            model.addAttribute("usuario", usuarioRegistro);
            return "usuarioLogado";
        }

        redirect.addFlashAttribute("mensagem", "A imagem passada não é válida!");
        return "redirect:/usuario";
    }

    @RequestMapping(value = "/trocarSenha", method = RequestMethod.POST)
    public String trocarSenha(@ModelAttribute("usuarioSenha") UsuarioDTO usuario,
                              RedirectAttributes redirect, Model model, HttpSession session,
                              HttpServletRequest request) throws IOException {

        String recaptcha = request.getParameter("g-recaptcha-response");
        String novaSenha = request.getParameter("novaSenha");

        if (cliente.verifica(recaptcha)) {
            return atualizarSenha(usuario, novaSenha, redirect, model, session);
        }

        redirect.addFlashAttribute("mensagem", "Por favor, comprove que você é humano!");
        return "redirect:/usuario";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute("usuario") UsuarioDTO usuario,
                        RedirectAttributes redirect, Model model, HttpSession session,
                        HttpServletRequest request) throws IOException {

        String recaptcha = request.getParameter("g-recaptcha-response");

        if (cliente.verifica(recaptcha)) {
            return procurarUsuario(usuario, redirect, model, session);
        }

        redirect.addFlashAttribute("mensagem", "Por favor, comprove que você é humano!");
        return "redirect:/usuario";
    }

    private String procurarUsuario(UsuarioDTO usuario, RedirectAttributes redirect, Model model,
                                   HttpSession session) {
        Usuario usuarioRetornado = dao.procuraUsuario(usuario);
        model.addAttribute("usuario", usuarioRetornado);
        if (usuarioRetornado == null) {
            redirect.addFlashAttribute("mensagem", "Usuário não encontrado");
            return "redirect:/usuario";
        }

        session.setAttribute("usuario", usuarioRetornado);
        return "usuarioLogado";
    }

    private String atualizarSenha(UsuarioDTO usuario, String novaSenha, RedirectAttributes redirect, Model model,
                                  HttpSession session) {
        Usuario usuarioRetornado = dao.procuraUsuario(usuario);
        model.addAttribute("usuario", usuarioRetornado);
        if (usuarioRetornado == null) {
            redirect.addFlashAttribute("mensagem", "Usuário não encontrado");
            return "redirect:/usuario";
        }
        // atualiza senha
        usuarioRetornado.setSenha(novaSenha);
        dao.salva(usuarioRetornado);

        session.setAttribute("usuario", usuarioRetornado);
        return "usuarioLogado";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("usuario");
        return "usuario";
    }

}
