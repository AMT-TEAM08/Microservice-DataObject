package org.amt.microservicedataobject.controller;

import org.amt.microservicedataobject.service.aws.AwsDataObjectHelperImpl;
import org.amt.microservicedataobject.service.aws.AwsServiceConfigurator;
import org.amt.microservicedataobject.service.DataObjectHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.time.Duration;

@RestController
public class DataObjectController {

    private final DataObjectHelper dataObjectHelper;

    public DataObjectController() {
        this.dataObjectHelper = new AwsDataObjectHelperImpl(new AwsServiceConfigurator.Builder().withEnvironmentVariables().build());
    }

    @GetMapping("/objects")
    public ResponseEntity<Object> listObjects() {
        try {
            return new ResponseEntity<>(dataObjectHelper.listObjects(), HttpStatus.OK);
        } catch (DataObjectHelper.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DataObjectHelper.DataObjectNotFoundException e) {
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
        } catch (DataObjectHelper.InvalidParamException | NullPointerException e){
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataObjectHelper.AccessDeniedException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DataObjectHelper.DataObjectNotFoundException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/objects/{objectName}")
    public ResponseEntity<String> getObject(@PathVariable String objectName, @RequestParam("duration") int duration) {
        try {
            URL downloadURL = dataObjectHelper.getUrl(objectName, Duration.ofMinutes(duration));
            return ResponseEntity.ok().body(downloadURL.toString());
        } catch (DataObjectHelper.InvalidParamException | NullPointerException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataObjectHelper.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DataObjectHelper.KeyNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/objects/{objectName}")
    public ResponseEntity<Object> deleteObject(@PathVariable String objectName) {
        try {
            dataObjectHelper.delete(objectName);
            return ResponseEntity.noContent().build();
        } catch (DataObjectHelper.InvalidParamException | NullPointerException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataObjectHelper.AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (DataObjectHelper.KeyNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
