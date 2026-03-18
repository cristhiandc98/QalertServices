package qalert.com.models.subscription;

public class SubscriptionLabelsResponse {

   Integer subscriptionId;

   String subscriptionMonths;

   String price;

   String discountedPrice;

   String discountPercentage;

   public SubscriptionLabelsResponse() {
   }

   public Integer getSubscriptionId() {
     return subscriptionId;
   }

   public void setSubscriptionId(Integer subscriptionId) {
     this.subscriptionId = subscriptionId;
   }

   public String getSubscriptionMonths() {
     return subscriptionMonths;
   }

   public void setSubscriptionMonths(String subscriptionMonths) {
     this.subscriptionMonths = subscriptionMonths;
   }

   public String getPrice() {
     return price;
   }

   public void setPrice(String price) {
     this.price = price;
   }

   public String getDiscountedPrice() {
     return discountedPrice;
   }

   public void setDiscountedPrice(String discountedPrice) {
     this.discountedPrice = discountedPrice;
   }

   public String getDiscountPercentage() {
     return discountPercentage;
   }

   public void setDiscountPercentage(String discountPercentage) {
     this.discountPercentage = discountPercentage;
   }
}
