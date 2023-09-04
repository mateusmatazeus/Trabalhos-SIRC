import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.net.*;

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

        // Receber o nome do arquivo da mensagem cifrada do remetente
        try (ServerSocket serverSocket = new ServerSocket(12345);
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String arquivoCifrado = bufferedReader.readLine();

            // Ler a mensagem cifrada do arquivo
            try (FileInputStream fileInputStream = new FileInputStream(arquivoCifrado);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fileInputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                byte[] mensagemCifrada = baos.toByteArray();

                // Decifrar a mensagem
                decifrador.init(Cipher.DECRYPT_MODE, chaveSecreta);
                byte[] mensagemDecifrada = decifrador.doFinal(mensagemCifrada);

                // Exibir a mensagem cifrada e decifrada
                System.out.println("Mensagem Cifrada: " + new String(mensagemCifrada));
                System.out.println("Mensagem Decifrada: " + new String(mensagemDecifrada));
            }
        }
    }
}
