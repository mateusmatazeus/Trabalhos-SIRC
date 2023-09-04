import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.net.*;
import java.util.Base64;

public class Destinatario {
    public static void main(String[] args) throws Exception {
        String algoritmo = "AES";
        String modo = "ECB";
        String padding = "PKCS5Padding";
        String chaveString = "chave_secreta123";

        // Converter a chave de string para um array de bytes
        byte[] chaveBytes = chaveString.getBytes("UTF-8");

        // Configurar o decifrador
        SecretKeySpec chaveSecreta = new SecretKeySpec(chaveBytes, algoritmo);
        Cipher decifrador = Cipher.getInstance(algoritmo + "/" + modo + "/" + padding);

        // Receber a mensagem cifrada do remetente
        try (ServerSocket serverSocket = new ServerSocket(12345);
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream()) {

            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            // Armazenar a mensagem cifrada
            byte[] mensagemCifrada = baos.toByteArray();

            // Decifrar a mensagem
            decifrador.init(Cipher.DECRYPT_MODE, chaveSecreta);
            byte[] mensagemDecifrada = decifrador.doFinal(mensagemCifrada);

            // Exibir a mensagem cifrada e decifrada
            System.out.println("Mensagem Cifrada: " + Base64.getEncoder().encodeToString(mensagemCifrada));
            System.out.println("Mensagem Decifrada: " + new String(mensagemDecifrada));
        }
    }
}
