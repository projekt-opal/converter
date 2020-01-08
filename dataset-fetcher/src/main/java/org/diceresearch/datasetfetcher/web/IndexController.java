package org.diceresearch.datasetfetcher.web;

import net.logstash.logback.argument.StructuredArguments;
import org.diceresearch.datasetfetcher.model.Portal;
import org.diceresearch.datasetfetcher.model.WorkingStatus;
import org.diceresearch.datasetfetcher.repository.PortalRepository;
import org.diceresearch.datasetfetcher.utility.DataSetFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private final PortalRepository portalRepository;
    private final ApplicationContext context;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public IndexController(PortalRepository portalRepository, ApplicationContext context) {
        this.portalRepository = portalRepository;
        this.context = context;
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
            @RequestParam(name = "high", defaultValue = "-1") String high,
            @RequestParam(name = "step", defaultValue = "200") String step
    ) {

        try {
            logger.info("received request for converting {} {} {} {}",
                    StructuredArguments.kv("id", id), StructuredArguments.kv("lnf", lnf),
                    StructuredArguments.kv("high", high), StructuredArguments.kv("step", step));
            if (id != null && !id.isEmpty()) {
                int i_id = Integer.parseInt(id);
                Optional<Portal> optionalPortal = portalRepository.findById(i_id);
                int i_lnf = Integer.parseInt(lnf);
                int i_high = Integer.parseInt(high);
                int i_step = Integer.parseInt(step);
                if (optionalPortal.isPresent()) {
                    Portal portal = optionalPortal.get();
                    portal.setHigh(i_high);
                    portal.setLastNotFetched(i_lnf);
                    portal.setStep(i_step);
                    portalRepository.save(portal);
                    DataSetFetcher fetcher = context.getBean(DataSetFetcher.class);
                    fetcher.initialQueryExecutionFactory(i_id);
                    executorService.submit(fetcher);
                }
            }
        } catch (NumberFormatException e) {
            logger.error("Exception ", e);
        }
        return "redirect:/";
    }

    @GetMapping("/cancel")
    public String convert(@RequestParam(name = "id") String id) {
        try {
            Integer i_id = Integer.parseInt(id);
            Optional<Portal> optionalPortal = portalRepository.findById(i_id);
            optionalPortal.ifPresent(portal -> {
                portal.setWorkingStatus(WorkingStatus.PAUSED);
                portalRepository.save(portal);
            });
        } catch (Exception e) {
            logger.error("Exception in cancel ", e);
        }
        return "redirect:/";
    }


}
