package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    private Logger log = LoggerFactory.getLogger(FileController.class.getName());


    public FileController () {
        log.info("Create " + getClass().getName());
    }

    @PostMapping("/fileUpload")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("uploadFile called with " + file.getOriginalFilename() + " in path " + new File ("").getAbsolutePath());
        File uploadedFile = new File ("build.gradle.uploaded");
        FileWriter writer = new FileWriter(uploadedFile);

        try {
            IOUtils.copy(file.getInputStream(), writer, Charset.defaultCharset());
            System.out.println ("Uploaded: " + uploadedFile.getAbsolutePath());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @GetMapping("/fileDownload/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        File file = new File(fileName).getAbsoluteFile();
        log.info("downloadFile called with " + file.getAbsolutePath());
        Resource resource = null;
        try {
            URI uri = file.toURI();
            resource = new UrlResource(uri);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String contentType = "application/octet-stream";
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
}