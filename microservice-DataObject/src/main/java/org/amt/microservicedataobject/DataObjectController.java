package org.amt.microservicedataobject;

import org.amt.microservicedataobject.dataobject.AWSDataObjectHelperImpl;
import org.amt.microservicedataobject.dataobject.AwsServiceConfigurator;
import org.amt.microservicedataobject.dataobject.IDataObjectHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;

@RestController
public class DataObjectController {

    private final IDataObjectHelper dataObjectHelper;

    public DataObjectController() {
        this.dataObjectHelper = new AWSDataObjectHelperImpl(new AwsServiceConfigurator.Builder().build());
    }

    @GetMapping("/objects")
    public ResponseEntity<Object> listObjects() {
        try {
            return new ResponseEntity<>(dataObjectHelper.listObjects(), HttpStatus.OK);
        } catch (IDataObjectHelper.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IDataObjectHelper.DataObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/objects", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<Object> postObject(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("temp", "file");
            file.transferTo(tempFile);
            System.out.println("File uploaded: " + tempFile.getAbsolutePath() + " " + tempFile.length());
            dataObjectHelper.add(file.getOriginalFilename(), tempFile);
            return ResponseEntity.ok().build();
        } catch (NullPointerException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IDataObjectHelper.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IDataObjectHelper.DataObjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/objects/{objectName}")
    public ResponseEntity<String> getObject(@PathVariable String objectName) {
        try {
            URL downloadURL = dataObjectHelper.getUrl(objectName);
            return ResponseEntity.ok().body(downloadURL.toString());
        } catch (NullPointerException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IDataObjectHelper.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IDataObjectHelper.KeyNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
