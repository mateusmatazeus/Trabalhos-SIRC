import java.io.*;
import java.net.*;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cliente {
    public static void main(String[] args) throws Exception {
        String serverAddress = "localhost";
        int serverPort = 12345;

        SecureRandom random = new SecureRandom();
        IvParameterSpec ivSpec = createCtrIvForAES(1, random);
        Key key = createKeyForAES(256, random);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");
        String input = "Voce deve estudar SIRC";

        System.out.println("Mensagem original: " + input);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherText = cipher.doFinal(input.getBytes("UTF-8"));

        try (Socket socket = new Socket(serverAddress, serverPort);
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream()) {

            // Envie a mensagem cifrada para Bob (servidor)
            outputStream.write(cipherText);
            System.out.println("Ciphertext enviado para o destinatario: " + toHex(cipherText));

            // Receba uma possível resposta de Bob (opcional)
            byte[] responseBytes = new byte[1024];
            int bytesRead = inputStream.read(responseBytes);
            if (bytesRead != -1) {
                String response = new String(responseBytes, 0, bytesRead, "UTF-8");
                System.out.println("Resposta do destinatario: " + response);
            }
        }
    }

    // Função para criar IV para AES em modo CTR
    public static IvParameterSpec createCtrIvForAES(int messageNumber, SecureRandom random) {
        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        ivBytes[0] = (byte) (messageNumber >> 24);
        ivBytes[1] = (byte) (messageNumber >> 16);
        ivBytes[2] = (byte) (messageNumber >> 8);
        ivBytes[3] = (byte) (messageNumber);
        return new IvParameterSpec(ivBytes);
    }

    // Função para criar uma chave AES
    public static Key createKeyForAES(int bitLength, SecureRandom random) throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(bitLength, random);
        return generator.generateKey();
    }

    // Função para converter bytes em representação hexadecimal
    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
