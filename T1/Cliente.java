import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class Cliente {
    public static void main(String[] args) throws Exception {
        String mensagem = "Mensagem secreta";
        String algoritmo = "AES";
        String modo = "ECB";
        String padding = "PKCS5Padding";
        String chaveString = "chave_secreta123"; 

        // Converter a chave de string para um array de bytes
        byte[] chaveBytes = chaveString.getBytes("UTF-8");

        // Gerar uma chave secreta
        SecretKeySpec chaveSecreta = new SecretKeySpec(chaveBytes, algoritmo);

        // Configurar a cifra
        Cipher cifrador = Cipher.getInstance(algoritmo + "/" + modo + "/" + padding);
        cifrador.init(Cipher.ENCRYPT_MODE, chaveSecreta);

        // Criptografar a mensagem
        byte[] mensagemCifrada = cifrador.doFinal(mensagem.getBytes());

        // Enviar a mensagem cifrada para o destinatário
        try (Socket socket = new Socket("localhost", 12345);
                OutputStream outputStream = socket.getOutputStream()) {
            outputStream.write(mensagemCifrada);
        }
    }
}
