package org.diceresearch.dataseturlfetcher.web;

import org.diceresearch.dataseturlfetcher.model.Portal;
import org.diceresearch.dataseturlfetcher.repository.PortalRepository;
import org.diceresearch.dataseturlfetcher.utility.DataSetFetcher;
import org.diceresearch.dataseturlfetcher.utility.DataSetFetcherPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    private final PortalRepository portalRepository;
    private final DataSetFetcherPool dataSetFetcherPool;

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
            @RequestParam(name = "portalName", required = false) String portalName,
            @RequestParam(name = "lnf", defaultValue = "0") String lnf,
            @RequestParam(name = "high", defaultValue = "-1") String high) {

        if (portalName != null && !portalName.isEmpty()) {
            DataSetFetcher fetcher = dataSetFetcherPool.getFetcher(portalName);
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
            DataSetFetcher fetcher = dataSetFetcherPool.getFetcher(portalName);
            fetcher.setCanceled(true);
        }
        return "redirect:/";
    }


}
