import javax.crypto.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;

public class servidor {
    public static void main(String[] args) {
        // Inicializa a variável para facilitar a alteração do número da porta
        // posteriormente, se necessário
        int porta = 11111;

        // Abre um socket na porta
        try (ServerSocket socketServidor = new ServerSocket(porta)) {
            // Anuncia em qual porta o servidor está ouvindo
            System.out.println("Servidor ouvindo na porta " + porta);

            // Gera um par de chaves RSA para o servidor
            KeyPair parChavesServidor = RSA.gerarParChaves();
            PublicKey chavePublicaServidor = parChavesServidor.getPublic();
            PrivateKey chavePrivadaServidor = parChavesServidor.getPrivate();

            while (true) {
                // Abre um socket de conexão no servidor
                try (Socket socketCliente = socketServidor.accept()) {
                    System.out.println("Conexão de: " + socketCliente.getInetAddress());

                    // Envia a chave pública do servidor para o cliente
                    BufferedWriter escritor = new BufferedWriter(
                            new OutputStreamWriter(socketCliente.getOutputStream()));
                    byte[] chavePublicaServidorBytes = chavePublicaServidor.getEncoded();
                    String chavePublicaServidorCodificada = Base64.getEncoder()
                            .encodeToString(chavePublicaServidorBytes);
                    escritor.write(chavePublicaServidorCodificada);
                    escritor.newLine();
                    escritor.flush();

                    // Recebe a chave pública do cliente
                    BufferedReader leitor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                    String chavePublicaClienteCodificada = leitor.readLine();
                    byte[] chavePublicaClienteBytes = Base64.getDecoder().decode(chavePublicaClienteCodificada);
                    PublicKey chavePublicaCliente = KeyFactory.getInstance("RSA")
                            .generatePublic(new X509EncodedKeySpec(chavePublicaClienteBytes));

                    while (true) {
                        // Lê a mensagem criptografada do cliente
                        String dadosArquivoCriptografados = leitor.readLine();
                        byte[] dadosCriptografados = Base64.getDecoder().decode(dadosArquivoCriptografados);

                        // Descriptografa a mensagem com a chave privada do servidor
                        byte[] dadosArquivoDescriptografados = RSA.descriptografar(dadosCriptografados,
                                chavePrivadaServidor);

                        // Imprime a mensagem descriptografada do cliente
                        String mensagemDescriptografada = new String(dadosArquivoDescriptografados);
                        System.out
                                .println("Mensagem descriptografada recebida do cliente: " + mensagemDescriptografada);

                        // Lê a resposta do servidor do arquivo ServerResponse
                        String respostaServidor = new String(Files.readAllBytes(Paths.get("ServerResponse.txt")));

                        // Criptografa a resposta do servidor com a chave pública do cliente
                        byte[] respostaServidorCriptografada = RSA.criptografar(respostaServidor.getBytes(),
                                chavePublicaCliente);
                        System.out.print("Resposta do servidor criptografada:"
                                + Base64.getEncoder().encodeToString(respostaServidorCriptografada) + "\n");

                        // Envia a resposta criptografada do servidor para o cliente
                        escritor.write(Base64.getEncoder().encodeToString(respostaServidorCriptografada));
                        escritor.newLine();
                        escritor.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
