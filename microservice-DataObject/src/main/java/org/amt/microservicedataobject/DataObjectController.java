package org.amt.microservicedataobject;

import org.amt.microservicedataobject.dataobject.AWSDataObjectHelperImpl;
import org.amt.microservicedataobject.dataobject.AwsServiceConfigurator;
import org.amt.microservicedataobject.dataobject.IDataObjectHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DataObjectController {

    private final IDataObjectHelper dataObjectHelper;

    public DataObjectController() {
        this.dataObjectHelper = new AWSDataObjectHelperImpl(new AwsServiceConfigurator.Builder().build());
    }

    @GetMapping("/listObjects")
    public List<String> listObjects() {
        return dataObjectHelper.listObjects();
    }
}
