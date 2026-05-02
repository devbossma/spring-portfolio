package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundService;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioService;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionRepository;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionType;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
/*
* This service handles all investment-related operations, including:
* - Adding new investments (with validation against fund's dry powder)
* - Updating current value of investments
* - Exiting investments (marking as exited, recording exit value, and returning proceeds to fund)
* - Writing off investments (marking as written off and recording the loss)
* */
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final PortfolioService portfolioService;
    private final FundService fundService;
    private final InvestmentTransactionRepository investmentTransactionRepository;
    private final InvestmentActivationService investmentActivationService;

    /*
    * Constructor for InvestmentService, which initializes all required dependencies.
    * Params:
    * - investmentRepository: Repository for accessing and managing investment records in the database.
    * - portfolioService: Service for managing portfolios, used to retrieve and update portfolio information related to investments.
    * - fundService: Service for managing funds, used to validate available dry powder and to record fund transactions when investments are exited or written off.
    * - investmentTransactionRepository: Repository for recording all transactions related to investments (buys, sells, write-offs).
    * - investmentActivationService: Service responsible for scheduling the activation of investments after a delay and sending notifications to users.
    * Returns: An instance of InvestmentService with all dependencies injected, ready to handle investment operations.
    * */
    public InvestmentService(InvestmentRepository investmentRepository,
                             PortfolioService portfolioService,
                             FundService fundService,
                             InvestmentTransactionRepository investmentTransactionRepository,
                             InvestmentActivationService investmentActivationService) {
        this.investmentRepository = investmentRepository;
        this.portfolioService = portfolioService;
        this.fundService = fundService;
        this.investmentTransactionRepository = investmentTransactionRepository;
        this.investmentActivationService = investmentActivationService;
    }

    /*
    * Retrieves a list of investments associated with a specific portfolio ID.
    * Params:
    * - portfolioId: The ID of the portfolio for which to retrieve investments.
    * Returns: A list of InvestmentEntity objects that belong to the specified portfolio.
    * */
    public List<InvestmentEntity> listInvestmentsByPortfolioId(Long portfolioId) {
        return investmentRepository.findAll().stream()
                .filter(inv -> inv.getPortfolio().getId().equals(portfolioId))
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }

        /*
    * Adds a new investment to a portfolio, ensuring that the fund has sufficient dry powder to cover the invested amount.
    * If the invested amount is not provided, it calculates it based on price per unit and quantity.
    * It also schedules the activation of the investment after a delay and records the transaction.
    *
    * Params:
    * - investment: An InvestmentEntity object containing the details of the investment to be added (name, price per unit, quantity, etc.).
    * - user: The UserEntity object representing the user making the investment, used for recording transactions and sending notifications.
    * Validation:
    * - The method checks if the invested amount is provided; if not, it calculates it as price per unit multiplied by quantity.
    * - It reloads the portfolio and fund from the database to get the most up-to-date balance and checks if the fund's dry powder is sufficient to cover the invested amount.
    *   If not, it throws an IllegalArgumentException with a message indicating insufficient funds.
    * Returns: None (void). This method performs the operation of adding the investment and does not return a value.
    * */
    @Transactional
    public void addInvestment(InvestmentEntity investment, UserEntity user) {
        if (investment.getInvestedAmount() == null) {
            investment.setInvestedAmount(
                    investment.getPricePerUnit().multiply(BigDecimal.valueOf(investment.getQuantity())));
        }

        // Reloading the portfolio and fund from DB to get accurate balance.
        PortfolioEntity freshPortfolio = portfolioService.getPortfolioById(investment.getPortfolio().getId());
        FundEntity fund = freshPortfolio.getFund();
        BigDecimal dryPowder = fund.getDryPowder();
        if (dryPowder.compareTo(investment.getInvestedAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient funds. Available: $" + dryPowder + ", Required: $" + investment.getInvestedAmount());
        }

        // Set The current value to the initial price per unit if not provided, so it shows up in the portfolio immediately.
        if (investment.getCurrentValue() == null) {
            investment.setCurrentValue(investment.getPricePerUnit());
        }

        investmentRepository.save(investment);

        investmentActivationService.scheduleActivation(
                investment.getId(), user.getId(),
                Instant.now().plusSeconds(InvestmentActivationService.ACTIVATION_DELAY_SECONDS));

        // Record BUY transaction
        investmentTransactionRepository.save(InvestmentTransactionEntity.builder()
                .amount(investment.getInvestedAmount())
                .user(user)
                .type(InvestmentTransactionType.BUY)
                .investment(investment)
                .pricePerUnit(investment.getPricePerUnit())
                .quantity(investment.getQuantity())
                .notes("Investment Purchase")
                .build());

        portfolioService.updatePortfolioTotals(freshPortfolio);
    }

    /*
    * Updates the current value of an active investment and recalculates the portfolio totals accordingly.
    * Params:
    * - id: The ID of the investment to be updated.
    * - currentValue: The new current value of the investment, which will be set in the database.
    * Validation:
    * - The method retrieves the investment by ID and checks if it exists; if not, it throws an exception.
    * - It updates the current value of the investment and saves it to the database.
    * - After updating the investment, it calls the portfolioService to update the portfolio totals,
    *   which will recalculate the total invested amount and deployed capital based on the new current value of the investment.
    * Returns: None (void). This method performs the operation of updating the investment's current value and does not return a value.
    * */
    @Transactional
    public void updateInvestmentCurrentValue(Long id, BigDecimal currentValue) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        investment.setCurrentValue(currentValue);
        investmentRepository.save(investment);
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    /*
    * Exits an active investment by marking it as exited, recording the exit value and time, and returning the proceeds to the fund.
    * It also records a SELL transaction for the investment and a DEPOSIT transaction for the fund.
    * Params:
    * - id: The ID of the investment to be exited.
    * - exitValue: The value at which the investment is being exited, which will be recorded in the database and used to calculate proceeds.
    * - user: The UserEntity object representing the user exiting the investment, used for recording transactions and sending notifications.
    * Validation:
    * - The method retrieves the investment by ID and checks if it exists; if not, it throws an exception.
    * - It updates the investment's status to EXITED, sets the exit value and exit time, and saves it to the database.
    * - It records a SELL transaction for the investment with the exit value and quantity.
    * - It calls the fundService to record a DEPOSIT transaction for the fund with the exit proceeds, indicating that the capital is being returned to the fund.
    * - Finally, it updates the portfolio totals to reflect the changes from exiting the investment.
    * Returns: None (void). This method performs the operation of exiting the investment and does not return a value.
    * */
    @Transactional
    public void exitInvestment(Long id, BigDecimal exitValue, UserEntity user) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        investment.setStatus(InvestmentStatus.EXITED);
        investment.setExitValue(exitValue);
        investment.setExitAt(java.time.LocalDateTime.now());
        investmentRepository.save(investment);

        // Record SELL investment transaction
        investmentTransactionRepository.save(InvestmentTransactionEntity.builder()
                .amount(exitValue)
                .user(user)
                .type(InvestmentTransactionType.SELL)
                .investment(investment)
                .pricePerUnit(exitValue.divide(BigDecimal.valueOf(investment.getQuantity()), 2, java.math.RoundingMode.HALF_UP))
                .quantity(investment.getQuantity())
                .notes("Investment Exit")
                .build());

        // Return exit proceeds to the fund and record a DEPOSIT fund transaction
        FundEntity fund = investment.getPortfolio().getFund();
        fundService.recordFundDeposit(fund, exitValue, user,
                "Exit proceeds from: " + investment.getName());

        // Recalculate deployedCapital.
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    /*
    * Writes off an active investment by marking it as written off, setting the exit value to zero, and recording the loss.
    * It also records a WRITE_OFF transaction for the investment and a DEPOSIT transaction for the fund to reflect the loss.
    * Params:
    * - id: The ID of the investment to be written off.
    * - user: The UserEntity object representing the user writing off the investment, used for recording transactions and sending notifications.
    * Validation:
    * - The method retrieves the investment by ID and checks if it exists; if not, it throws an exception.
    * - It checks if the investment is active; if not, it throws an IllegalStateException indicating that only active investments can be written off.
    * - It updates the investment's status to WRITTEN_OFF, sets the exit value to zero, and sets the exit time to the current time, then saves it to the database.
    * - It records a WRITE_OFF transaction for the investment with the invested amount and quantity, indicating that the investment has been written off.
    * - It calls the fundService to record a DEPOSIT transaction for the fund with the invested amount, indicating that the capital is being returned to the fund due to the write-off.
    * - Finally, it updates the portfolio totals to reflect the changes from writing off the investment.
    * Returns: None (void). This method performs the operation of writing off the investment and does not return a value.
    * */
    @Transactional
    public void writeOffInvestment(Long id, UserEntity user) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        if (!investment.isActive()) {
            throw new IllegalStateException("Only active investments can be written off.");
        }
        investment.setStatus(InvestmentStatus.WRITTEN_OFF);
        investment.setExitValue(BigDecimal.ZERO);
        investment.setExitAt(java.time.LocalDateTime.now());
        investmentRepository.save(investment);

        investmentTransactionRepository.save(InvestmentTransactionEntity.builder()
                .amount(investment.getInvestedAmount())
                .user(user)
                .type(dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionType.WRITE_OFF)
                .investment(investment)
                .pricePerUnit(BigDecimal.ZERO)
                .quantity(investment.getQuantity())
                .notes("Investment Written Off")
                .build());

        FundEntity fund = investment.getPortfolio().getFund();
        fundService.recordFundWriteOff(fund, investment.getInvestedAmount(), user,
                "Write-off: " + investment.getName());

        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    /*
    * Retrieves an investment by its ID.
    * Params:
    * - id: The ID of the investment to be retrieved.
    * Validation:
    * - The method uses the investmentRepository to find the investment by ID. If the investment does not exist, it throws a NoSuchElementException (or a custom exception if you choose to implement one).
    * Returns: An InvestmentEntity object representing the investment with the specified ID if it exists; otherwise, it throws an exception indicating that the investment was not found.
    * */
    public InvestmentEntity getInvestmentById(Long id) {
        return investmentRepository.findById(id).orElseThrow();
    }


    /*
    * Permanently deletes a PENDING investment, cancels its scheduled activation, removes its BUY transaction,
    * and recalculates portfolio totals. Only PENDING investments may be deleted; active or closed investments
    * must be exited or written off instead.
    * */
    @Transactional
    public void deleteInvestment(Long id) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        if (!investment.isPending()) {
            throw new IllegalStateException("Only pending investments can be deleted.");
        }
        investmentActivationService.cancelActivation(id);
        PortfolioEntity portfolio = investment.getPortfolio();
        investmentTransactionRepository.deleteByInvestment(investment);
        investmentRepository.delete(investment);
        portfolioService.updatePortfolioTotals(portfolio);
    }

    /*
    * Updates the details of an existing investment and recalculates the portfolio totals accordingly.
    * Params:
    * - investment: An InvestmentEntity object containing the updated details of the investment, including its ID, which is used to identify which investment to update in the database.
    * Validation:
    * - The method checks if the investment with the given ID exists; if not, it throws an exception.
    * - It updates the investment record in the database with the new details provided in the InvestmentEntity object.
    * - After updating the investment, it calls the portfolioService to update the portfolio totals, which will recalculate the total invested amount and deployed capital based on the updated details of the investment.
    * Returns: None (void). This method performs the operation of updating the investment's details and does not return a value.
    * */
    @Transactional
    public void updateInvestment(InvestmentEntity investment) {
        investmentRepository.save(investment);
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }
}
