package dev.saberlabs.myspringportfolio.fund;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FundService {

    private final FundRepository fundRepository;

    public FundService(FundRepository fundRepository) {
        this.fundRepository = fundRepository;
    }

    @Transactional
    public void addToFund(Long fundId, BigDecimal amount) {
        FundEntity fund = fundRepository.findById(fundId).orElseThrow();
        fund.setTotalCapital(fund.getTotalCapital().add(amount));
        fundRepository.save(fund);
    }
}
