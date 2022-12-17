package org.amt.microservicedataobject;

import org.amt.microservicedataobject.dataobject.AWSDataObjectHelperImpl;
import org.amt.microservicedataobject.dataobject.AwsServiceConfigurator;
import org.amt.microservicedataobject.dataobject.IDataObjectHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
public class DataObjectController {

    private final IDataObjectHelper dataObjectHelper;

    public DataObjectController() {
        this.dataObjectHelper = new AWSDataObjectHelperImpl(new AwsServiceConfigurator.Builder().build());
    }

    @GetMapping("/objects")
    public List<String> listObjects() {
        return dataObjectHelper.listObjects();
    }

    @RequestMapping(value = "/objects", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseEntity<Object> postObject(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("temp", "file");
            file.transferTo(tempFile);
            System.out.println("File uploaded: " + tempFile.getAbsolutePath() + " " + tempFile.length());
            dataObjectHelper.add(file.getName(), tempFile);
            dataObjectHelper.listObjects().forEach(System.out::println);
            return ResponseEntity.ok().build();
        } catch (NullPointerException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
