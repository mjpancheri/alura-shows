package br.com.alura.owasp.validation;

import br.com.alura.owasp.model.Usuario;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

@Component
public class ImagemValidator {



    public boolean validarImagem(MultipartFile imagem, Usuario usuario,
                              HttpServletRequest request) throws IllegalStateException, IOException {

        ByteArrayInputStream bytesImagem = new ByteArrayInputStream(imagem.getBytes());
        String mime = URLConnection.guessContentTypeFromStream(bytesImagem);

        if (mime == null) {
            return false;
        }

        usuario.setNomeImagem(imagem.getOriginalFilename());
        File arquivo = new File(request.getServletContext().getRealPath(
                "/image"), usuario.getNomeImagem());
        imagem.transferTo(arquivo);
        return true;
    }
}
