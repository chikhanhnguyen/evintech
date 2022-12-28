package com.intech.evintechaccount.services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {

    @Autowired
    private APIContext apiContext;

    public Payment createPayment(Double total,String description, String cancelUrl, String successUrl) 
    		throws PayPalRESTException {
        
    	Amount amount = new Amount();
        amount.setCurrency("EUR");
        
        total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        String totalPrice = String.format("%.2f", total).replace(',', '.');
        amount.setTotal(totalPrice);

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("PAYPAL");

        Payment payment = new Payment();
        payment.setIntent("SALE");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        
        payment.setRedirectUrls(redirectUrls);
        
        System.out.println(apiContext.getAccessToken());
        System.out.println(payment.getTransactions().get(0).getDescription());
        
        return payment.create(apiContext);

    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        
    	Payment payment = new Payment();
        payment.setId(paymentId);
        
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecute);
    }

}
