import java.io.*;
import java.net.*;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Destinatario {
    public static void main(String[] args) throws Exception {
        int port = 12345;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Aguardando conexão com Alice...");
        Socket socket = serverSocket.accept();
        System.out.println("Conexão estabelecida com Alice.");

        SecureRandom random = new SecureRandom();
        IvParameterSpec ivSpec = createCtrIvForAES(1, random);
        Key key = createKeyForAES(256, random);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");

        try (InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream()) {

            // Recebe a mensagem cifrada de Alice
            byte[] cipherText = readBytes(inputStream);
            System.out.println("Ciphertext recebido: " + toHex(cipherText));

            // Decifra a mensagem
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            byte[] plainText = cipher.doFinal(cipherText);
            System.out.println("Mensagem decifrada por Bob: " + new String(plainText, "UTF-8"));

            // Envie uma resposta para Alice (opcional)
            String response = "Mensagem recebida e decifrada por Bob.";
            byte[] responseBytes = response.getBytes("UTF-8");
            outputStream.write(responseBytes);
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
        Arrays.fill(ivBytes, 4, 16, (byte) 0);
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

    // Função para ler bytes de um InputStream
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[4096];
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }
}
