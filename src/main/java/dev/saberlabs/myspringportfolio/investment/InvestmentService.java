package dev.saberlabs.myspringportfolio.investment;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;

    public InvestmentService(InvestmentRepository investmentRepository) {
        this.investmentRepository = investmentRepository;
    }

    public Iterable<InvestmentEntity> getAllInvestments() {
        return investmentRepository.findAll();
    }

    public InvestmentEntity getInvestmentById(Long id) {
        return investmentRepository.findById(id).orElse(null);
    }

    public InvestmentEntity createInvestment(InvestmentEntity investment) {
        return investmentRepository.save(investment);
    }

    public void deleteInvestment(Long id) {
        investmentRepository.deleteById(id);
    }

    public Double CalculateProfitLoss(InvestmentEntity investment) {
        return (investment.getCurrentValue() - investment.getPurchasePrice()) ;
    }

    @Transactional
    public boolean UpdateInvestment(InvestmentEntity investment) {
        if (investmentRepository.existsById(investment.getId())) {
            investmentRepository.save(investment);
            return true;
        }
        return false;
    }
}
