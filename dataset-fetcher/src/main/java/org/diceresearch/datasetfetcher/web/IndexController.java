package org.diceresearch.datasetfetcher.web;

import org.diceresearch.datasetfetcher.model.Portal;
import org.diceresearch.datasetfetcher.repository.PortalRepository;
import org.diceresearch.datasetfetcher.utility.DataSetFetcher;
import org.diceresearch.datasetfetcher.utility.DataSetFetcherPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private final PortalRepository portalRepository;
    private final DataSetFetcherPool dataSetFetcherPool;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public IndexController(PortalRepository portalRepository, DataSetFetcherPool dataSetFetcherPool) {
        this.portalRepository = portalRepository;
        this.dataSetFetcherPool = dataSetFetcherPool;
    }

    @GetMapping("/")
    public String index(Model model) {
        Iterable<Portal> portals = portalRepository.findAll();
        model.addAttribute("portals", portals);
        return "index";
    }

    @GetMapping("/convert")
    public String convert(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "lnf", defaultValue = "0") String lnf,
            @RequestParam(name = "high", defaultValue = "-1") String high) {

        try {
            logger.info("received request for converting {} {} {}", kv("id", id), kv("lnf", lnf), kv("high", high));
            if (id != null && !id.isEmpty()) {
                int i_id = Integer.parseInt(id);
                Optional<Portal> optional = portalRepository.findById(i_id);
                int i_lnf = Integer.parseInt(lnf);
                int i_high = Integer.parseInt(high);
                if (optional.isPresent()) {
                    Portal portal = optional.get();
                    portal.setHigh(i_high);
                    portal.setLastNotFetched(i_lnf);
                    portalRepository.save(portal);
                    DataSetFetcher fetcher = dataSetFetcherPool.getFetcher(portal.getId());
                    fetcher.initialQueryExecutionFactory(i_id);
                    executorService.submit(fetcher);
                }
            }
        } catch (NumberFormatException e) {
            logger.error("Exception in convert ", e);
        }
        return "redirect:/";
    }

    @GetMapping("/cancel")
    public String convert(@RequestParam(name = "id") String id) {
        try {
            if (id != null && !id.isEmpty()) {
                DataSetFetcher fetcher = dataSetFetcherPool.getFetcher(Integer.parseInt(id));
                fetcher.setCanceled(true);
            }
        } catch (NumberFormatException e) {
            logger.error("Exception in cancel ", e);
        }
        return "redirect:/";
    }


}
