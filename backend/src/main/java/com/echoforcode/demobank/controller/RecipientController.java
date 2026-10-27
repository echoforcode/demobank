package com.echoforcode.demobank.controller;

import com.echoforcode.demobank.dto.RecipientLookupDto;
import com.echoforcode.demobank.dto.RecipientResponse;
import com.echoforcode.demobank.service.RecipientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class RecipientController {
    private final RecipientService recipients;
    @PostMapping("/find")
    public RecipientResponse find(@Valid @RequestBody RecipientLookupDto dto) {
        return recipients.find(dto);
    }
}