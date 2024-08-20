package ce.kiran;

import dev.sigstore.KeylessSigner;
import dev.sigstore.KeylessSignerException;
import dev.sigstore.bundle.Bundle;
import dev.sigstore.oidc.client.GithubActionsOidcClient;
import dev.sigstore.oidc.client.OidcClients;
import dev.sigstore.oidc.client.OidcException;
import dev.sigstore.oidc.client.OidcToken;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class Main {
    private static OidcClients retrieveOidcClients() {
        GithubActionsOidcClient client = GithubActionsOidcClient.builder().build();
        return OidcClients.of(client);
    }

    private static Bundle signPayload() throws InvalidAlgorithmParameterException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, KeylessSignerException {
        Path filePath = Paths.get("src/main/java/ce/kiran/hello.txt");
        KeylessSigner signer = new SetDefaults().setOidcClients(retrieveOidcClients());
        return signer.signFile(filePath);
    }
    public static void main(String[] args) throws InvalidAlgorithmParameterException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, KeylessSignerException, OidcException {
        Bundle result = signPayload();
        OidcClients clients = retrieveOidcClients();
//        System.out.println("Oidc info: " + clients);
//        System.out.println("Signature is " + result.getCertPath().getCertificates().getFirst().toString());
        GithubActionsOidcClient client = GithubActionsOidcClient.builder().build();
        Map<String, String> env = System.getenv();
        OidcToken token = client.getIDToken(env);
//        System.out.printf("Oidc token is %s\n", token.getIdToken());
//        System.out.printf("Issuer is %s\n", token.getIssuer());
//        System.out.printf("Subject is %s\n", token.getSubjectAlternativeName());
        System.out.println("Github Actions : " + env.get("GITHUB_ACTIONS"));
        System.out.println("Token : " + env.get("ACTIONS_ID_TOKEN_REQUEST_TOKEN"));
        System.out.println("URL : " + env.get("ACTIONS_ID_TOKEN_REQUEST_URL"));
    }
}