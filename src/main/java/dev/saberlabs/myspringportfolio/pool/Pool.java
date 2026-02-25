package dev.saberlabs.myspringportfolio.pool;

public record Pool(
        Integer id,
        String status,
        Double totalAmount,
        Double investedAmount,
        Double remainingAmount
) {


    /* This method allows removing an investment amount from the pool. It can be used to update the total amount of the pool when an investment is withdrawn or removed. */
    public void removeInvestment(Double amount) {
        // Logic to remove investment amount from the pool
    }

    /* This method allows updating the status of the pool. It can be used to change the status of the pool based on certain conditions, such as reaching a funding goal or closing the pool. */
    public void updateStatus(String newStatus) {
        // Logic to update the status of the pool
    }

    public static boolean isPoolActive(String status) {
        // Logic to check if the pool is active based on its status
        return "active".equalsIgnoreCase(status);
    }

    public static boolean updatePoolAmount(Pool pool, Double amount) {
        // Logic to update the total amount of the pool based on the investment amount
        return true; // Return true if the update was successful, false otherwise
    }

    public static void withdrawInvestment(Pool pool, Double amount) {
        // Logic to withdraw an investment amount from the pool
    }

    public Double calculateRemainingAmount() {
        // Logic to calculate the remaining amount in the pool based on total and invested amounts
        return totalAmount - investedAmount;
    }

}
