package server;

import io.swagger.annotations.ApiParam;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DocumentstoreController implements DocumentstoreApi {

  private Logger log = LoggerFactory.getLogger(DocumentstoreController.class.getName());

  public DocumentstoreController () {
    log.info("Create " + getClass().getName());
  }

  @Override
  public ResponseEntity<Resource> downloadDocument(@ApiParam(value = "",required=true) @PathVariable("documentName") String documentName) {
    File file = new File ("").getAbsoluteFile();
    log.info("downloadDocument " + documentName + " in path " + file.getAbsolutePath());

    Resource resource = null;
    try {
      resource = new UrlResource(file.toURI());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    // Try to determine file's content type
    String contentType = null;

    // Fallback to the default content type if type could not be determined
    if(contentType == null) {
      contentType = "application/octet-stream";
    }

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }

  @Override
  public ResponseEntity<Void> uploadDocument(@ApiParam(value = "") @RequestParam(value="securityId", required=false)  String securityId,@ApiParam(value = "file detail") @Valid @RequestPart("file") MultipartFile document) {
    log.info("uploadFile " + document + " with securityId " + securityId);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }
}
