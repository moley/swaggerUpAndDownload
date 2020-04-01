package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 *
 */
public class MultipartUtility {
  private final String boundary;
  private static final String LINE_FEED = "\r\n";
  private HttpURLConnection httpConn;
  private Charset charset;
  private OutputStream outputStream;
  private PrintWriter writer;

  /**
   * This constructor initializes a new HTTP POST request with content type
   * is set to multipart/form-data
   * @param requestURL        the url to upload
   * @param charset           the charset to use
   * @throws IOException      if error occurs on download
   */
  public MultipartUtility(String requestURL, Charset charset)
      throws IOException {
    this.charset = charset;

    // creates a unique boundary based on time stamp
    boundary = "===" + System.currentTimeMillis() + "===";

    URL url = new URL(requestURL);
    httpConn = (HttpURLConnection) url.openConnection();
    httpConn.setUseCaches(false);
    httpConn.setDoOutput(true); // indicates POST method
    httpConn.setDoInput(true);
    httpConn.setRequestProperty("Content-Type",
        "multipart/form-data; boundary=" + boundary);
    httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
    httpConn.setRequestProperty("Test", "Bonjour");
    outputStream = httpConn.getOutputStream();
    writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
        true);
  }

  /**
   * Adds a upload file section to the request
   * @param fieldName name attribute in file
   * @param uploadFile a File to be uploaded
   * @throws IOException if error occurrs on access
   */
  public void addFilePart(String fieldName, File uploadFile)
      throws IOException {
    String fileName = uploadFile.getName();
    writer.append("--" + boundary).append(LINE_FEED);
    writer.append(
        "Content-Disposition: form-data; name=\"" + fieldName
            + "\"; filename=\"" + fileName + "\"")
        .append(LINE_FEED);
    writer.append(
        "Content-Type: "
            + URLConnection.guessContentTypeFromName(fileName))
        .append(LINE_FEED);
    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
    writer.append(LINE_FEED);
    writer.flush();



    FileInputStream inputStream = new FileInputStream(uploadFile);
    IOUtils.copy(inputStream, outputStream);
    outputStream.flush();
    inputStream.close();
    writer.flush();
  }

  /**
   * Completes the request and receives response from the server.
   * @return a list of Strings as response in case the server returned status OK, otherwise an exception is thrown.
   * @throws IOException if error occurs on access
   */
  public List<String> finish() throws IOException {
    List<String> response = new ArrayList<String>();

    writer.append(LINE_FEED).flush();
    writer.append("--" + boundary + "--").append(LINE_FEED);
    writer.close();

    // checks server's status code first
    int status = httpConn.getResponseCode();
    if (status == HttpURLConnection.HTTP_OK) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          httpConn.getInputStream()));
      String line = null;
      while ((line = reader.readLine()) != null) {
        response.add(line);
      }
      reader.close();
      httpConn.disconnect();
    } else {
      throw new IOException("Server returned non-OK status: " + status);
    }

    return response;
  }
}