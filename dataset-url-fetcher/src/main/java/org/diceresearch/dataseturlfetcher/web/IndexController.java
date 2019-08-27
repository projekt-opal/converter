package org.diceresearch.dataseturlfetcher.web;

import org.diceresearch.dataseturlfetcher.model.Portal;
import org.diceresearch.dataseturlfetcher.repository.PortalRepository;
import org.diceresearch.dataseturlfetcher.utility.DataSetUrlFetcher;
import org.diceresearch.dataseturlfetcher.utility.DataSetUrlFetcherPool;
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
    private final DataSetUrlFetcherPool dataSetUrlFetcherPool;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public IndexController(PortalRepository portalRepository, DataSetUrlFetcherPool dataSetUrlFetcherPool) {
        this.portalRepository = portalRepository;
        this.dataSetUrlFetcherPool = dataSetUrlFetcherPool;
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
            if (id != null && !id.isEmpty()) {
                Optional<Portal> optional = portalRepository.findById(Integer.parseInt(id));
                int x = Integer.parseInt(lnf);
                int y = Integer.parseInt(high);
                if (optional.isPresent()) {
                    Portal portal = optional.get();
                    DataSetUrlFetcher fetcher = dataSetUrlFetcherPool.getFetcher(portal.getId());
                    logger.info("received request for converting {}", kv("portalName: ", portal.getName()));
                    portal.setLastNotFetched(x);
                    portal.setHigh(y);
                    portalRepository.save(portal);
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
                DataSetUrlFetcher fetcher = dataSetUrlFetcherPool.getFetcher(Integer.parseInt(id));
                fetcher.setCanceled(true);
            }
        } catch (NumberFormatException e) {
            logger.error("Exception in cancel ", e);
        }
        return "redirect:/";
    }


}
