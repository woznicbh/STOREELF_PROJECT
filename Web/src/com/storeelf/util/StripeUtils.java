package com.storeelf.util;

import java.util.HashMap;
import java.util.Map;

import com.storeelf.report.web.StoreElfConstants;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;

public class StripeUtils {
	
	public static Customer createStripeCustomer(String token, String email) throws AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {

		Stripe.apiKey = StoreElfConstants.STRIPE_TEST_KEY;

		Map<String, Object> custParams = new HashMap<String, Object>();
		custParams.put("email", email);
		custParams.put("source", token);
		
		return Customer.create(custParams);
	}
	
	public static Card addAndReturnCC(String token, Customer customer) throws AuthenticationException,
			InvalidRequestException, APIConnectionException, CardException, APIException {
		
		Stripe.apiKey = StoreElfConstants.STRIPE_TEST_KEY;
		
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("source", token);

		return (Card) customer.getSources().create(cardParams);
	}
	
	public static void addSubscription(String stripeId) throws AuthenticationException, InvalidRequestException,
			APIConnectionException, CardException, APIException {

		Map<String, Object> subParams = new HashMap<String, Object>();
		subParams.put("customer", stripeId);
		subParams.put("plan", "StoreElf_Monthly");
		subParams.put("tax_percent", 5.05);
		subParams.put("trial_period_days", 30);

		Subscription.create(subParams);

	}
	
}
