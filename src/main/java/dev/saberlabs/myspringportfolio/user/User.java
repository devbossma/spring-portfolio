package dev.saberlabs.myspringportfolio.user;

import dev.saberlabs.myspringportfolio.investment.Investment;
import dev.saberlabs.myspringportfolio.pool.Pool;

public record User(Integer id, String username, String email, String password, Pool pool, Investment[] investments) {

        /* This method allows updating the user's email address. It can be used to change the email associated with the user's account. */
        public void updateEmail(String newEmail) {
            // Logic to update the user's email address
        }

        /* This method allows updating the user's password. It can be used to change the password associated with the user's account. */
        public void updatePassword(String newPassword) {
            // Logic to update the user's password
        }

        /* This method allows adding a new investment to the user's portfolio. It can be used to track and manage the user's investments. */
        public void addInvestment(Investment investment) {
            // Logic to add a new investment to the user's portfolio
        }

        /* This method allows removing an investment from the user's portfolio. It can be used to manage and update the user's investments. */
        public void removeInvestment(Integer investmentId) {
            // Logic to remove an investment from the user's portfolio based on its ID
        }

        /* This method allows updating the user's pool information. It can be used to manage and track the pool associated with the user. */
        public void updatePool(Pool newPool) {
            // Logic to update the user's pool information
        }

}
