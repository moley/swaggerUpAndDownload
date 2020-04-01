import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import server.MultipartUtility;

public class FileUpAndDownloadTester {

  private String apiUrl = "http://localhost:8080";

  @Test
  public void testUpload () throws IOException {
    String uploadUrl = apiUrl + "/fileUpload";
    URL url = new URL(uploadUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    MultipartUtility multipart = new MultipartUtility(uploadUrl, Charset.defaultCharset());

    File uploadFile = new File("readme.md");
    System.out.println ("Uploading " + uploadFile.getAbsolutePath());
    multipart.addFilePart("file", uploadFile);
    multipart.finish();
  }

  @Test
  public void testDownload () throws IOException {

    String downloadUrl = apiUrl + "/fileDownload/readme.md";
    URL url = new URL(downloadUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    File outputFolder = new File ("build/fileUpAndDownloadTester");

    if (!outputFolder.exists())
      outputFolder.mkdirs();

    File downloadedFile = new File(outputFolder, "readme.md.downloaded");

    InputStream inputStream;
    inputStream = con.getInputStream();

    FileOutputStream outputStream = new FileOutputStream(downloadedFile);

    IOUtils.copy(inputStream, outputStream);


    System.out.println ("Downloaded <" + downloadedFile.length() + ">");
    System.out.println ("Downloaded content <" + FileUtils.readFileToString(downloadedFile, Charset.defaultCharset()) + ">");

  }
}
