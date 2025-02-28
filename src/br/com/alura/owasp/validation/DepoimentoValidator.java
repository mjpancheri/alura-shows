package br.com.alura.owasp.validation;

import br.com.alura.owasp.model.Depoimento;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DepoimentoValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Depoimento.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Depoimento depoimento = (Depoimento) target;
        String titulo = depoimento.getTitulo();
        String mensagem = depoimento.getMensagem();

        Pattern regex = Pattern.compile("(?i)<script\\b[^>]*>(.*?)</script>");

        Matcher matcher = regex.matcher(titulo);
        if (matcher.find()) {
            errors.rejectValue("titulo", "errors.titulo");
        }

        matcher = regex.matcher(mensagem);
        if (matcher.find()) {
            errors.rejectValue("mensagem", "errors.mensagem");
        }
    }
}
