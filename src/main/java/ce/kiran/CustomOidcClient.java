package ce.kiran;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.util.Key;
import dev.sigstore.http.HttpClients;
import dev.sigstore.http.HttpParams;
import dev.sigstore.http.ImmutableHttpParams;
import dev.sigstore.oidc.client.*;
import io.grpc.Internal;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class CustomOidcClient implements OidcClient {
    private static final Logger log = Logger.getLogger(GithubActionsOidcClient.class.getName());
    static final String GITHUB_ACTIONS_KEY = "GITHUB_ACTIONS";
    static final String REQUEST_TOKEN_KEY = "ACTIONS_ID_TOKEN_REQUEST_TOKEN";
    static final String REQUEST_URL_KEY = "ACTIONS_ID_TOKEN_REQUEST_URL";
    private static final String DEFAULT_AUDIENCE = "sigstore";
    private final String audience;
    private final HttpParams httpParams;
    private String id_token;

    public static Builder builder() {
        return new CustomOidcClient.Builder();
    }

    private CustomOidcClient(HttpParams httpParams, String audience) {
        this.audience = audience;
        this.httpParams = httpParams;
    }

    public boolean isEnabled(Map<String, String> env) {
        String githubActions = (String)env.get("GITHUB_ACTIONS");
        if (githubActions != null && !githubActions.isEmpty()) {
            String bearer = (String)env.get("ACTIONS_ID_TOKEN_REQUEST_TOKEN");
            String urlBase = (String)env.get("ACTIONS_ID_TOKEN_REQUEST_URL");
            if (bearer != null && !bearer.isEmpty() && urlBase != null && !urlBase.isEmpty()) {
                return true;
            } else {
                log.info("Github env detected, but github idtoken not found: skipping github actions oidc");
                return false;
            }
        } else {
            log.fine("Github env not detected: skipping github actions oidc");
            return false;
        }
    }

    public OidcToken getIDToken(Map<String, String> env) throws OidcException {
        String bearer = (String)env.get("ACTIONS_ID_TOKEN_REQUEST_TOKEN");
        String urlBase = (String)env.get("ACTIONS_ID_TOKEN_REQUEST_URL");
        if (bearer == null) {
            throw new OidcException("Could not get github actions environment variable 'ACTIONS_ID_TOKEN_REQUEST_TOKEN'");
        } else if (urlBase == null) {
            throw new OidcException("Could not get github actions environment variable 'ACTIONS_ID_TOKEN_REQUEST_URL'");
        } else {
            GenericUrl url = new GenericUrl(urlBase + "&audience=" + this.audience);

            try {
               HttpRequest req = HttpClients.newRequestFactory(this.httpParams).buildGetRequest(url);
               req.setParser(new GsonFactory().createJsonObjectParser());
               req.getHeaders().setAuthorization("Bearer " + bearer);
               req.getHeaders().setAccept("application/json; api-version=2.0");
               req.getHeaders().setContentType("application/json");
               CustomOidcJsonResponse resp = req.execute().parseAs(CustomOidcJsonResponse.class);
                String idToken = resp.getValue();
                this.id_token = idToken;
                JsonWebSignature jws = JsonWebSignature.parse(new GsonFactory(), idToken);
                return ImmutableOidcToken.builder().idToken(idToken).issuer(jws.getPayload().getIssuer()).subjectAlternativeName(jws.getPayload().getSubject()).build();
            } catch (IOException var9) {
                IOException e = var9;
                throw new OidcException("Could not obtain github actions oidc token", e);
            }
        }
    }

    public String getIdToken() {
        return this.id_token;
    }

    @Internal
    public static class CustomOidcJsonResponse extends GenericJson {
        @Key("value")
        private String value;

        public CustomOidcJsonResponse() {
        }

        String getValue() {
            return this.value;
        }
    }

    public static class Builder {
        private HttpParams httpParams = ImmutableHttpParams.builder().build();
        private String audience = "sigstore";

        private Builder() {
        }

        public CustomOidcClient.Builder audience(String audience) {
            this.audience = audience;
            return this;
        }

        public CustomOidcClient.Builder httpParams(HttpParams httpParams) {
            this.httpParams = httpParams;
            return this;
        }

        public CustomOidcClient build() {
            return new CustomOidcClient(this.httpParams, this.audience);
        }
    }
}
