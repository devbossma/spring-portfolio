package dev.saberlabs.myspringportfolio.portfolio;

import dev.saberlabs.myspringportfolio.investment.Investment;

public record Portfolio(Integer id, String owner, Investment[] investments) {

     /* This method allows adding a new investment to the portfolio. It can be used to track and manage the investments within the portfolio. */
     public void addInvestment(Investment investment) {
         // Logic to add a new investment to the portfolio
     }

     /* This method allows removing an investment from the portfolio. It can be used to manage and update the investments within the portfolio. */
     public void removeInvestment(Integer investmentId) {
         // Logic to remove an investment from the portfolio based on its ID
     }

     /* This method allows calculating the total value of the portfolio based on the investments it contains. It can be used to track the overall performance of the portfolio. */
     public Double calculateTotalValue() {
         // Logic to calculate the total value of the portfolio based on its investments
         return 0.0; // Return the calculated total value of the portfolio
     }
}
