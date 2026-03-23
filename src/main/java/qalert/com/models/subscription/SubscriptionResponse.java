package qalert.com.models.subscription;

public class SubscriptionResponse {

   Integer subscriptionId;

   Integer subscriptionMonths;

   float priceWithoutDiscount;

   float priceWithDiscount;

   float discountPercentage;

   public SubscriptionResponse() {
   }

   public Integer getSubscriptionId() {
     return subscriptionId;
   }

   public void setSubscriptionId(Integer subscriptionId) {
     this.subscriptionId = subscriptionId;
   }

   public Integer getSubscriptionMonths() {
     return subscriptionMonths;
   }

   public void setSubscriptionMonths(Integer subscriptionMonths) {
     this.subscriptionMonths = subscriptionMonths;
   }

   public float getPriceWithoutDiscount() {
     return priceWithoutDiscount;
   }

   public void setPriceWithoutDiscount(float priceWithoutDiscount) {
     this.priceWithoutDiscount = priceWithoutDiscount;
   }

   public float getPriceWithDiscount() {
     return priceWithDiscount;
   }

   public void setPriceWithDiscount(float priceWithDiscount) {
     this.priceWithDiscount = priceWithDiscount;
   }

   public float getDiscountPercentage() {
     return discountPercentage;
   }

   public void setDiscountPercentage(float discountPercentage) {
     this.discountPercentage = discountPercentage;
   }
   
}
