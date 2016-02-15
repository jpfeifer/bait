package org.bait.service;

import org.bait.model.TransferInfo;

public interface TransferInfoService {
    TransferInfo createTransferInformation(TransferInfo transferInfo);

    TransferInfo findTransferInfo(String transferInfoId);

}
