package com.db.awmd.challenge.dto;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.dto.TransferDTO;
import com.db.awmd.challenge.exception.AccountWouldBeInDeficitException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.challenge.validation.ResourceValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DTOTest {

    ObjectMapper mapper;
    String JSONFileContent;
    String JSONINvalidTranser;
    TransferDTO transferDTO;
    TransferDTO invalidTransferDTO;

    @Autowired
    ResourceValidator resourceValidator;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        try {
            JSONFileContent = readResource("transfer.json", Charsets.UTF_8);
            JSONINvalidTranser = readResource("invalid_transfer.json", Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mapJSONtoDTO() throws Exception {
        assertNotNull(mapper);
        assertNotNull(JSONFileContent);
        transferDTO = mapper.readValue(JSONFileContent, TransferDTO.class);
        invalidTransferDTO = mapper.readValue(JSONINvalidTranser, TransferDTO.class);
        assertNotNull(transferDTO);
        BigDecimal transferAmountToPrecision = transferDTO.getTransferAmoutEuqivalent();
        BigDecimal rounded = transferAmountToPrecision.round(new MathContext(5, RoundingMode.HALF_UP));

        assertNotNull(invalidTransferDTO);
        assertNotNull(resourceValidator);
    }

    @Test
    public void validateInvalidTransfer() throws Exception {
        invalidTransferDTO = mapper.readValue(JSONINvalidTranser, TransferDTO.class);
        assertNotNull(invalidTransferDTO);
        assertNotNull(resourceValidator);
        try {
            resourceValidator.validate(invalidTransferDTO);
            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(true);
        }
    }



    private String readResource(final String fileName, Charset charset) throws IOException {
        return Resources.toString(Resources.getResource(fileName), charset);
    }
}
