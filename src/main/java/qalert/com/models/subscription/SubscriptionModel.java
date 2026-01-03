package qalert.com.models.subscription;

public class SubscriptionModel {

   Integer subscriptionId;
   double amount;
   int subscriptionMonths;
   String createdDatetime;
   Boolean status;
   
   public Integer getSubscriptionId() {
     return subscriptionId;
   }
   public void setSubscriptionId(Integer subscriptionId) {
     this.subscriptionId = subscriptionId;
   }
   public double getAmount() {
     return amount;
   }
   public void setAmount(double amount) {
     this.amount = amount;
   }
   public int getSubscriptionMonths() {
     return subscriptionMonths;
   }
   public void setSubscriptionMonths(int subscriptionMonths) {
     this.subscriptionMonths = subscriptionMonths;
   }
   public String getCreatedDatetime() {
     return createdDatetime;
   }
   public void setCreatedDatetime(String createdDatetime) {
     this.createdDatetime = createdDatetime;
   }
   public Boolean getStatus() {
     return status;
   }
   public void setStatus(Boolean status) {
     this.status = status;
   }

}
