package com.pg.paymentgateway.service;

import com.pg.paymentgateway.model.BankAccounts;
import com.pg.paymentgateway.model.BankSettings;
import com.pg.paymentgateway.repository.BankAccountsRepository;
import com.pg.paymentgateway.repository.BankSettingsRepository;
import com.pg.paymentgateway.repository.BankStatementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DailyLimitListener implements FileEventListener{

    private static final Logger logger = LoggerFactory.getLogger(DailyLimitListener.class);

    @Autowired
    BankStatementRepository statementRepository;
    @Autowired
    BankAccountsRepository bankAccountsRepository;
    @Autowired
    BankSettingsRepository bankSettingsRepository;

    private static final String bankActiveVal = "active";

       @Override
    public void handleEvent(FileEvent event) {
        Integer bankId = event.getBankId();
        String accountNum = event.getAccountId();
        Double totalAmount = statementRepository.getTotalAmountByAccountId(accountNum);
        if (totalAmount == null){
            return;
        }
        List<BankAccounts> bankAccounts = bankAccountsRepository.findByAccountNumber(accountNum);
           final AtomicReference<Double>[] remainingAmount = new AtomicReference[]{null};
        bankAccounts.forEach(bankAccount -> {
                    if(remainingAmount[0] == null){
                        remainingAmount[0] = new AtomicReference<>();
                    }
                    remainingAmount[0].set(bankAccount.getTargetDaily() - totalAmount);
                    bankAccount.setPendingDaily(remainingAmount[0].get());
                    bankAccountsRepository.save(bankAccount);
         }
        );
        if(remainingAmount[0] == null){
            return;
        }

        List<BankAccounts> bankAccountsList = bankAccountsRepository.findAll();
        Map<Long, String> accountMap = new HashMap<>();
        bankAccountsList.stream().forEach(bankAccounts1 -> {
                accountMap.put(bankAccounts1.getId(), bankAccounts1.getStatus());
         }
        );

        System.out.println("Total amount is " + totalAmount);
        int priorityOrder = Integer.MAX_VALUE -1;
        if(remainingAmount[0].get() <= 0){

            List<BankSettings> bankSettings = bankSettingsRepository.findByAccountIdAndStatus(bankAccounts.get(0).getId(),"A");
            bankSettings.stream().forEach(bankSetting -> {
                bankSetting.setStatus("I");
               // priorityOrder = bankSetting.getPriority();
                bankSettingsRepository.save(bankSetting);

                List<BankSettings> bankSettingsNext = bankSettingsRepository.findByPriorityGreaterThanAndCategoryOrderByPriority( bankSetting.getPriority() + 1, bankSetting.getCategory());
                for(BankSettings bankSettingNext : bankSettingsNext){
                    if(bankActiveVal.equals(accountMap.get(bankSettingNext.getId()))){
                        bankSettingNext.setStatus("A");
                        bankSettingsRepository.save(bankSettingNext);
                        break;
                    }

                }
            });

        }

    }
}
