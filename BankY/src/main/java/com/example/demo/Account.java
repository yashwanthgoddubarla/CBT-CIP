package com.example.demo;

import java.io.Serializable;

public class Account implements Serializable {
   private static final long serialVersionUID = 1L;
   private String AccountNumber;
   private String AccountHolder;
   private double AccountBalance;
   private int SecurityPin;

   public Account(String var1, String var2, int var3) {
      this.AccountNumber = var1;
      this.AccountHolder = var2;
      this.SecurityPin = var3;
      this.AccountBalance = 0.0D;
   }

   public String getAccountNumber() {
      return this.AccountNumber;
   }

   public String getAccountHolder() {
      return this.AccountHolder;
   }

   public double getAccountBalance() {
      return this.AccountBalance;
   }

   public int getSecurityPin() {
      return this.SecurityPin;
   }
   

   public void deposit(double var1, int var3) {
      if (this.validatePin(var3)) {
         if (var1 > 0.0D) {
            this.AccountBalance += var1;
            System.out.println("Deposited: " + var1);
            this.printStars();
         } else {
            System.out.println("Invalid deposit amount.");
         }
      } else {
         System.out.println("Invalid security pin.");
      }

   }

   public void withdraw(double var1, int var3) {
      if (this.validatePin(var3)) {
         if (var1 > 0.0D && this.AccountBalance >= var1) {
            this.AccountBalance -= var1;
            System.out.println("Withdraw: " + var1);
            this.printStars();
         } else {
            System.out.println("Invalid withdrawal amount or insufficient funds.");
         }
      } else {
         System.out.println("Invalid security pin.");
      }

   }

   public void transfer(Account var1, double var2, int var4) {
      if (this.validatePin(var4)) {
         if (var2 > 0.0D && this.AccountBalance >= var2) {
            this.AccountBalance -= var2;
            var1.AccountBalance += var2;
            System.out.println("Transferred: " + var2 + " to " + var1.getAccountHolder());
            this.printStars();
         } else {
            System.out.println("Invalid transfer amount or insufficient funds.");
         }
      } else {
         System.out.println("Invalid security pin.");
      }

   }

   private boolean validatePin(int var1) {
      return this.SecurityPin == var1;
   }
   private void printStars() {
	      System.out.println("****************************************");
	   }

   
}