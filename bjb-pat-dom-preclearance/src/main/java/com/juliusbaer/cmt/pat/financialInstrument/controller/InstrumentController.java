package com.juliusbaer.cmt.pat.financialInstrument.controller;


import com.juliusbaer.cmt.pat.financialInstrument.dto.FinancialInstrumentDropdownDto;
import com.juliusbaer.cmt.pat.financialInstrument.repository.FinancialInstrumentRepository;
import com.juliusbaer.cmt.pat.financialInstrument.batch.ImportScheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instruments")
@RequiredArgsConstructor
public class InstrumentController {


    private final FinancialInstrumentRepository repo;
    private final ImportScheduler importScheduler;

    @GetMapping("/search")
    //TODO: Short name to add
    public List<FinancialInstrumentDropdownDto> search(@RequestParam(name = "bjbNameLongIssuer", required = false) String bjbNameLongIssuer, @RequestParam(name = "isin", required = false) String isin, @RequestParam(name = "valorNo", required = false) String valorNo, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return repo.searchAny(bjbNameLongIssuer, isin, valorNo, pageable).map(fi -> new FinancialInstrumentDropdownDto(fi.getAssBjbNameLongIssuer(), fi.getIsin(), fi.getValorNo(), fi.getBbgId(), fi.getJbGlobalId())).getContent();
    }

     @GetMapping("/searchEquities")
     //TODO: Short name to add
    public List<FinancialInstrumentDropdownDto> searchEquities(@RequestParam(name = "bjbNameLongIssuer", required = false) String bjbNameLongIssuer, @RequestParam(name = "isin", required = false) String isin, @RequestParam(name = "valorNo", required = false) String valorNo, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return repo.searchEquities(bjbNameLongIssuer, isin, valorNo, pageable).map(fi -> new FinancialInstrumentDropdownDto(fi.getAssBjbNameLongIssuer(), fi.getIsin(), fi.getValorNo(),fi.getBbgId(),fi.getJbGlobalId())).getContent();
    }

    @GetMapping("/searchAllNoOptions")
    //TODO: Short name to add
    public List<FinancialInstrumentDropdownDto> searchAllNoOptions(@RequestParam(name = "bjbNameLongIssuer", required = false) String bjbNameLongIssuer, @RequestParam(name = "isin", required = false) String isin, @RequestParam(name = "valorNo", required = false) String valorNo, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return repo.searchAllNoOptions(bjbNameLongIssuer, isin, valorNo, pageable).map(fi -> new FinancialInstrumentDropdownDto(fi.getAssBjbNameLongIssuer(), fi.getIsin(), fi.getValorNo(),fi.getBbgId(),fi.getJbGlobalId())).getContent();
    }


    @PostMapping("/run")
    public ResponseEntity<String> runImport() {
        try {
            importScheduler.launch();
            return ResponseEntity.accepted().body("Import job started – check logs for completion.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Job failed: " + e.getMessage());
        }
    }
}
