import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) throws Exception {
        String mensagem = "Meu nome eh Matazeus";
        String algoritmo = "AES";
        String modo = "ECB";
        String padding = "PKCS5Padding";
        String chaveString = "chave_secreta123";
        String arquivoCifrado = "mensagem_cifrada.txt"; // Nome do arquivo para salvar a mensagem cifrada

        // Converter a chave de string para um array de bytes
        byte[] chaveBytes = chaveString.getBytes("UTF-8");

        // Gerar uma chave secreta
        SecretKeySpec chaveSecreta = new SecretKeySpec(chaveBytes, algoritmo);

        // Configurar a cifra
        Cipher cifrador = Cipher.getInstance(algoritmo + "/" + modo + "/" + padding);
        cifrador.init(Cipher.ENCRYPT_MODE, chaveSecreta);

        // Criptografar a mensagem
        byte[] mensagemCifrada = cifrador.doFinal(mensagem.getBytes());

        // Salvar a mensagem cifrada em um arquivo
        try (FileOutputStream fileOutputStream = new FileOutputStream(arquivoCifrado)) {
            fileOutputStream.write(mensagemCifrada);
        }

        // Enviar o nome do arquivo para o destinatário
        try (Socket socket = new Socket("localhost", 12345);
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true)) {
            printWriter.println(arquivoCifrado);
        }
    }
}
