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

import static net.logstash.logback.argument.StructuredArguments.kv;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private final PortalRepository portalRepository;
    private final DataSetUrlFetcherPool dataSetUrlFetcherPool;

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
            @RequestParam(name = "portalName", required = false) String portalName,
            @RequestParam(name = "lnf", defaultValue = "0") String lnf,
            @RequestParam(name = "high", defaultValue = "-1") String high) {

        logger.info("received request for converting {}", kv("portalName: ", portalName));

        if (portalName != null && !portalName.isEmpty()) {
            DataSetUrlFetcher fetcher = dataSetUrlFetcherPool.getFetcher(portalName);
            fetcher.setCanceled(false);
            int x = Integer.parseInt(lnf);
            int y = Integer.parseInt(high);
            Portal portal = portalRepository.findByName(portalName);
            portal.setLastNotFetched(x);
            portal.setHigh(y);
            portalRepository.save(portal);

            new Thread(fetcher).start();
        }
        return "redirect:/";
    }

    @GetMapping("/cancel")
    public String convert(@RequestParam(name = "portalName", required = false) String portalName) {
        if (portalName != null && !portalName.isEmpty()) {
            DataSetUrlFetcher fetcher = dataSetUrlFetcherPool.getFetcher(portalName);
            fetcher.setCanceled(true);
        }
        return "redirect:/";
    }


}
