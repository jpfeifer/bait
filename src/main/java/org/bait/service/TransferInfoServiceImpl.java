package org.bait.service;

import org.bait.db.TransferInfoRepository;
import org.bait.db.model.TransferInfoDbImpl;
import org.bait.model.Bai;
import org.bait.model.TransferInfo;
import org.bait.service.api.TransferInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferInfoServiceImpl implements TransferInfoService {

    private TransferInfoRepository transferInfoRepository;

    @Override
    @Transactional
    public TransferInfo createTransferInformation(final TransferInfo transferInfo) {
        TransferInfoDbImpl transferInfoTransient = createTransient(transferInfo);
        Bai baiTransient = BaiServiceImpl.createTransient();
        baiTransient.setBaiId(transferInfo.getBaiId());
        transferInfoTransient.setBai(baiTransient);
        return transferInfoRepository.save(transferInfoTransient);
    }

    public static TransferInfoDbImpl createTransient(final TransferInfo transferInfo) {
        TransferInfoDbImpl transferInfoTransient = new TransferInfoDbImpl();
        transferInfoTransient.setSubject(transferInfo.getSubject());
        transferInfoTransient.setAmount(transferInfo.getAmount());
        return transferInfoTransient;
    }

    @Override
    public TransferInfoDbImpl findTransferInfo(final String transferInfoId) {
        return transferInfoRepository.findOne(transferInfoId);
    }

    @Override
    @Transactional
    public void deleteTransferInformation(final String transferId) {
        transferInfoRepository.deleteByTransferId(transferId);
    }

    @Autowired
    @Required
    public void setTransferInfoRepository(final TransferInfoRepository transferInfoRepository) {
        this.transferInfoRepository = transferInfoRepository;
    }

}
