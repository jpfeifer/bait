package org.bait.rest;

import org.bait.model.BankAccountInformation;
import org.bait.service.BaiService;

public class BaitResource {

    private BaiService baiService;

    public void createBaiInfo(BankAccountInformation bankAccountInformation) {
        baiService.createBankAccountInformation(bankAccountInformation);
    }

    public void setBaiService(BaiService baiService) {
        this.baiService = baiService;
    }
}