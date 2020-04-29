package org.diceresearch.datasetfilefetcher.web;

import org.diceresearch.datasetfilefetcher.utility.FileFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ServiceController {

    private final FileFetcher fileFetcher;
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    @Autowired
    public ServiceController(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    @GetMapping("/convert")
    @ResponseStatus(HttpStatus.OK)
    public void convert(@RequestParam(name = "low") final String low,
                          @RequestParam(name = "folderPath") final String folderPath) {
        Runnable runnable = () -> {
            int low_i = Integer.parseInt(low);
            fileFetcher.fetch(low_i, folderPath);
        };
        service.execute(runnable);
    }

    @GetMapping ("/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancel() {
        fileFetcher.cancel();
    }

    @GetMapping("/refresh")
    public Integer refresh(@RequestParam(name = "low")String low) {
        if(fileFetcher.getLow() <= Integer.parseInt(low)) return -1;
        return fileFetcher.getLow();
    }
}
