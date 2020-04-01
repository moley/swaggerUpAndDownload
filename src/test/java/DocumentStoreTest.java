import client.CommunicationApi;
import invalidPackageName.ApiClient;
import invalidPackageName.ApiException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import org.junit.Test;

public class DocumentStoreTest {

  private ApiClient apiClient = new ApiClient();

  public DocumentStoreTest() {
    apiClient.setBasePath("http://localhost:8080");
  }



  @Test public void testUpload() {
    File readmeFile = new File("readme.md");
    if (!readmeFile.exists())
      throw new IllegalStateException("File " + readmeFile.getAbsolutePath() + " does not exist");

    CommunicationApi communicationApi = new CommunicationApi();
    communicationApi.setApiClient(apiClient);
    try {
      communicationApi.uploadDocument("hans", readmeFile);
    } catch (ApiException e) {
      System.err.println(e.getResponseBody() + "-" + e.getCode());
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }

  @Test public void testDownload() throws IOException {
    CommunicationApi communicationApi = new CommunicationApi();
    communicationApi.setApiClient(apiClient);

    File someFile = null;
    try {
      someFile = communicationApi.downloadDocument("hans");
    } catch (ApiException e) {
      System.err.println(e.getResponseBody() + "-" + e.getCode());
    }
    if (someFile != null) {
      System.out.println("File: " + someFile.getAbsolutePath());
      System.out.println("Content: " + Files.readAllLines(someFile.toPath(), Charset.defaultCharset()));
    }
    else
      throw new IllegalStateException("No file downloaded");

  }
}
