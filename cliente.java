import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 11111;

        try (Socket socket = new Socket(host, port)) {
            // Gera uma chave AES de 256 bits
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();

            // Envia a chave AES (Ks1) criptografada com a chave pública de B
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            // Gera um par de chaves RSA para o cliente
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey clientPublicKey = keyPair.getPublic();
            PrivateKey clientPrivateKey = keyPair.getPrivate();

            // Envia a chave pública RSA do cliente para o servidor
            out.writeObject(clientPublicKey);
            out.flush();

            // Recebe a chave pública RSA do servidor
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            PublicKey serverPublicKey = (PublicKey) in.readObject();

            // Cria um objeto Cipher para criptografar a chave AES com a chave pública de B
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);

            // Criptografa a chave AES (Ks1)
            byte[] encryptedSecretKey = cipher.doFinal(secretKey.getEncoded());

            // Envia a chave AES criptografada
            out.writeObject(encryptedSecretKey);
            out.flush();

            // Prompt para o nome do arquivo
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Informe o nome do ficheiro: ");
            String userInput = userInputReader.readLine();

            while (!userInput.equalsIgnoreCase("Sair")) {
                File file = new File(userInput);

                if (file.exists() && file.isFile()) {
                    // Le o arquivo
                    byte[] fileData = Files.readAllBytes(file.toPath());

                    // Cria um objeto Cipher para criptografar o arquivo com AES
                    Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                    aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);

                    // Criptografa o arquivo
                    byte[] encryptedFileData = aesCipher.doFinal(fileData);

                    // Envia o arquivo criptografado
                    out.writeObject(encryptedFileData);
                    out.flush();

                    System.out.println("Ficheiro enviado com sucesso.");
                } else {
                    System.out.println("Ficheiro nao encontrado");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
