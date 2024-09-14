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
    private static String token;
    private static OidcClients retrieveOidcClients() {
        GithubActionsOidcClient client = GithubActionsOidcClient.builder().build();
        return OidcClients.of(client);
    }

    private static Bundle signPayload() throws InvalidAlgorithmParameterException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, KeylessSignerException {
        Path filePath = Paths.get("src/main/java/ce/kiran/hello.txt");
        CustomOidcClient client = CustomOidcClient.builder().build();
        KeylessSigner signer = new SetDefaults().setOidcClients(OidcClients.of(client));
        return signer.signFile(filePath);
    }
    public static void main(String[] args) throws InvalidAlgorithmParameterException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, KeylessSignerException, OidcException {
        Bundle result = signPayload();
        System.out.println(result.toJson());
    }
}