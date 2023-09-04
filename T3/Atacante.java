import java.io.*;
import java.net.*;
import java.security.SecureRandom;

public class Atacante {
    public static void main(String[] args) throws Exception {
        String serverAddress = "localhost";
        int serverPort = 12345;

        try (Socket socket = new Socket(serverAddress, serverPort);
                OutputStream outputStream = socket.getOutputStream()) {

            // Simule o ataque interceptando e modificando a mensagem cifrada
            byte[] modifiedCipherText = "Sua mensagem modificada aqui".getBytes("UTF-8");

            // Envie a mensagem modificada para Bob (servidor)
            outputStream.write(modifiedCipherText);
            System.out.println("Mensagem modificada enviada para o Destinatario: " + new String(modifiedCipherText, "UTF-8"));
        }
    }
}
