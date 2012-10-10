package org.mifosng.platform.api.infrastructure;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.mifosng.platform.api.commands.AdjustLoanTransactionCommand;
import org.mifosng.platform.api.commands.BranchMoneyTransferCommand;
import org.mifosng.platform.api.commands.BulkLoanReassignmentCommand;
import org.mifosng.platform.api.commands.ChargeCommand;
import org.mifosng.platform.api.commands.ClientCommand;
import org.mifosng.platform.api.commands.ClientIdentifierCommand;
import org.mifosng.platform.api.commands.CodeCommand;
import org.mifosng.platform.api.commands.DepositAccountCommand;
import org.mifosng.platform.api.commands.DepositAccountWithdrawInterestCommand;
import org.mifosng.platform.api.commands.DepositAccountWithdrawalCommand;
import org.mifosng.platform.api.commands.DepositProductCommand;
import org.mifosng.platform.api.commands.DepositStateTransitionApprovalCommand;
import org.mifosng.platform.api.commands.DepositStateTransitionCommand;
import org.mifosng.platform.api.commands.FundCommand;
import org.mifosng.platform.api.commands.GroupCommand;
import org.mifosng.platform.api.commands.LoanApplicationCommand;
import org.mifosng.platform.api.commands.LoanChargeCommand;
import org.mifosng.platform.api.commands.LoanProductCommand;
import org.mifosng.platform.api.commands.LoanStateTransitionCommand;
import org.mifosng.platform.api.commands.LoanTransactionCommand;
import org.mifosng.platform.api.commands.NoteCommand;
import org.mifosng.platform.api.commands.OfficeCommand;
import org.mifosng.platform.api.commands.OrganisationCurrencyCommand;
import org.mifosng.platform.api.commands.RoleCommand;
import org.mifosng.platform.api.commands.SavingAccountCommand;
import org.mifosng.platform.api.commands.SavingProductCommand;
import org.mifosng.platform.api.commands.StaffCommand;
import org.mifosng.platform.api.commands.UserCommand;
import org.mifosng.platform.api.data.ApiParameterError;
import org.mifosng.platform.api.errorhandling.InvalidJsonException;
import org.mifosng.platform.api.errorhandling.UnsupportedParameterException;
import org.mifosng.platform.exceptions.PlatformApiDataValidationException;
import org.springframework.format.number.NumberFormatter;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

@Service
public class ApiDataConversionServiceImpl implements ApiDataConversionService {

	private final Gson gsonConverter;
	
	public ApiDataConversionServiceImpl() {
		gsonConverter = new Gson();
	}
	
    @Override
    public ChargeCommand convertJsonToChargeCommand(final Long resourceIdentifier, final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
        final Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        final Set<String> supportedParams = new HashSet<String>(
        		Arrays.asList("name", "amount", "locale", "currencyCode",
            "currencyOptions", "chargeAppliesTo", "chargeTimeType",
            "chargeCalculationType", "chargeCalculationTypeOptions", "penalty", "active"
        ));

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        final String name = extractStringParameter("name", requestMap, modifiedParameters);
        final BigDecimal amount = extractBigDecimalParameter("amount", requestMap, modifiedParameters);
        final String currencyCode = extractStringParameter("currencyCode", requestMap, modifiedParameters);

        final Integer chargeTimeType = extractIntegerParameter("chargeTimeType", requestMap, modifiedParameters);
        final Integer chargeAppliesTo = extractIntegerParameter("chargeAppliesTo", requestMap, modifiedParameters);
        final Integer chargeCalculationType = extractIntegerParameter("chargeCalculationType", requestMap, modifiedParameters);

        final boolean penalty = extractBooleanParameter("penalty", requestMap, modifiedParameters);
        final boolean active = extractBooleanParameter("active", requestMap, modifiedParameters);

        return new ChargeCommand(modifiedParameters, resourceIdentifier, name, amount,
                currencyCode, chargeTimeType, chargeAppliesTo, chargeCalculationType, penalty, active);
    }

    @Override
	public FundCommand convertJsonToFundCommand(final Long resourceIdentifier, final String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("name", "externalId")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    String name = extractStringParameter("name", requestMap, modifiedParameters);
	    String externalId = extractStringParameter("externalId", requestMap, modifiedParameters);
	    
		return new FundCommand(modifiedParameters, resourceIdentifier, name, externalId);
	}
    
	@Override
	public StaffCommand convertJsonToStaffCommand(Long resourceIdentifier,
			String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("firstname", "lastname","officeId", "loanOfficerFlag")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    String firstname = extractStringParameter("firstname", requestMap, modifiedParameters);
	    String lastname = extractStringParameter("lastname", requestMap, modifiedParameters);
	    Boolean isLoanOfficer = extractBooleanParameter("loanOfficerFlag", requestMap, modifiedParameters);
	    Long officeId = extractLongParameter("officeId", requestMap, modifiedParameters);
	    
		return new StaffCommand(modifiedParameters, resourceIdentifier, officeId,firstname, lastname, isLoanOfficer);
	}

    @Override
    public BulkLoanReassignmentCommand convertJsonToBulkLoanReassignmentCommand(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        Set<String> supportedParams = new HashSet<String>(
                Arrays.asList("fromLoanOfficerId", "toLoanOfficerId","loans")
        );

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        Long fromLoanOfficerId = extractLongParameter("fromLoanOfficerId", requestMap, modifiedParameters);
        Long toLoanOfficerId = extractLongParameter("toLoanOfficerId", requestMap, modifiedParameters);

        // check array
        JsonParser parser = new JsonParser();

        String[] loans = null;
        JsonElement element = parser.parse(json);
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("loans")) {
                modifiedParameters.add("loans");
                JsonArray array = object.get("loans").getAsJsonArray();
                loans = new String[array.size()];
                for (int i=0; i<array.size(); i++) {
                    loans[i] = array.get(i).getAsString();
                }
            }
        }
        //

        return new BulkLoanReassignmentCommand(fromLoanOfficerId, toLoanOfficerId, loans);
	}

	@Override
	public OfficeCommand convertJsonToOfficeCommand(final Long resourceIdentifier, final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("name", "externalId", "parentId", "openingDate", "locale", "dateFormat")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    String name = extractStringParameter("name", requestMap, modifiedParameters);
	    String externalId = extractStringParameter("externalId", requestMap, modifiedParameters);
	    Long parentId = extractLongParameter("parentId", requestMap, modifiedParameters);
	    LocalDate openingLocalDate = extractLocalDateParameter("openingDate", requestMap, modifiedParameters);
	    
	    return new OfficeCommand(modifiedParameters, resourceIdentifier, name, externalId, parentId, openingLocalDate);
	}
	
	@Override
	public RoleCommand convertJsonToRoleCommand(final Long resourceIdentifier, final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("name", "description", "permissions")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    // check array
	    JsonParser parser = new JsonParser();
		
		String[] permissionIds = null;
		JsonElement element = parser.parse(json);
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			if (object.has("permissions")) {
				modifiedParameters.add("permissions");
				JsonArray array = object.get("permissions").getAsJsonArray();
				permissionIds = new String[array.size()];
				for (int i=0; i<array.size(); i++) {
					permissionIds[i] = array.get(i).getAsString();
				}
			}
		}
	    //
	    
	    String name = extractStringParameter("name", requestMap, modifiedParameters);
	    String description = extractStringParameter("description", requestMap, modifiedParameters);
	    
	    return new RoleCommand(modifiedParameters, resourceIdentifier, name, description, permissionIds);
	}
	
	@Override
	public UserCommand convertJsonToUserCommand(final Long resourceIdentifier, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("username", "firstname", "lastname", "password", "repeatPassword", "email", "officeId", "notSelectedRoles", "roles")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    String username = extractStringParameter("username", requestMap, modifiedParameters);
	    String firstname = extractStringParameter("firstname", requestMap, modifiedParameters);
	    String lastname = extractStringParameter("lastname", requestMap, modifiedParameters);
	    String password = extractStringParameter("password", requestMap, modifiedParameters);
	    String repeatPassword = extractStringParameter("repeatPassword", requestMap, modifiedParameters);
	    String email = extractStringParameter("email", requestMap, modifiedParameters);
	    Long officeId = extractLongParameter("officeId", requestMap, modifiedParameters);
	    
	    // check array
	    JsonParser parser = new JsonParser();
		
		String[] notSelectedRoles = null;
		String[] roles = null;
		JsonElement element = parser.parse(json);
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			if (object.has("notSelectedRoles")) {
				modifiedParameters.add("notSelectedRoles");
				JsonArray array = object.get("notSelectedRoles").getAsJsonArray();
				notSelectedRoles = new String[array.size()];
				for (int i=0; i<array.size(); i++) {
					notSelectedRoles[i] = array.get(i).getAsString();
				}
			}
			
			if (object.has("roles")) {
				modifiedParameters.add("roles");
				JsonArray array = object.get("roles").getAsJsonArray();
				roles = new String[array.size()];
				for (int i=0; i<array.size(); i++) {
					roles[i] = array.get(i).getAsString();
				}
			}
		}
	    //
	    
		return new UserCommand(modifiedParameters, resourceIdentifier, username, firstname, lastname, password, repeatPassword, email, officeId, notSelectedRoles, roles);
	}
	
	@Override
	public BranchMoneyTransferCommand convertJsonToBranchMoneyTransferCommand(final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("fromOfficeId", "toOfficeId", "transactionDate", "currencyCode", "transactionAmount", "description", "locale", "dateFormat")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	   
	    Long fromOfficeId = extractLongParameter("fromOfficeId", requestMap, modifiedParameters);
	    Long toOfficeId = extractLongParameter("toOfficeId", requestMap, modifiedParameters);
	    LocalDate transactionLocalDate = extractLocalDateParameter("transactionDate", requestMap, modifiedParameters);
	    String currencyCode = extractStringParameter("currencyCode", requestMap, modifiedParameters);
	    BigDecimal transactionAmountValue = extractBigDecimalParameter("transactionAmount", requestMap, modifiedParameters);
	    String description = extractStringParameter("description", requestMap, modifiedParameters);
	    
	    return new BranchMoneyTransferCommand(modifiedParameters, fromOfficeId, toOfficeId, transactionLocalDate, currencyCode, transactionAmountValue, description);
	}
	
	@Override
	public ClientCommand convertJsonToClientCommand(final Long resourceIdentifier, final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("externalId", "firstname", "lastname", "clientOrBusinessName", "officeId", "joiningDate", "locale", "dateFormat")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	   
	    String externalId = extractStringParameter("externalId", requestMap, modifiedParameters);
	    Long officeId = extractLongParameter("officeId", requestMap, modifiedParameters);
	    LocalDate joiningDate = extractLocalDateParameter("joiningDate", requestMap, modifiedParameters);
	    String firstname = extractStringParameter("firstname", requestMap, modifiedParameters);
	    String lastname = extractStringParameter("lastname", requestMap, modifiedParameters);
	    String clientOrBusinessName = extractStringParameter("clientOrBusinessName", requestMap, modifiedParameters);
	    
	    return new ClientCommand(modifiedParameters, resourceIdentifier, externalId, firstname, lastname, clientOrBusinessName, officeId, joiningDate);
	}
	
	@Override
    public GroupCommand convertJsonToGroupCommand(final Long resourceIdentifier, final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
        final Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        final Set<String> supportedParams = new HashSet<String>(
                Arrays.asList("name", "officeId", "externalId", "clientMembers")
        );
        
        checkForUnsupportedParameters(requestMap, supportedParams);

        final Set<String> modifiedParameters = new HashSet<String>();

        final Long officeId = extractLongParameter("officeId", requestMap, modifiedParameters);
        final String externalId = extractStringParameter("externalId", requestMap, modifiedParameters);
        final String name = extractStringParameter("name", requestMap, modifiedParameters);

        // check array
        final JsonParser parser = new JsonParser();
        
        String[] clientMembers = null;
        JsonElement element = parser.parse(json);
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("clientMembers")) {
                modifiedParameters.add("clientMembers");
                JsonArray array = object.get("clientMembers").getAsJsonArray();
                clientMembers = new String[array.size()];
                for (int i=0; i<array.size(); i++) {
                    clientMembers[i] = array.get(i).getAsString();
                }
            }
        }
        //
        
        return new GroupCommand(modifiedParameters, resourceIdentifier, externalId, name, officeId, clientMembers);
    }

    @Override
	public LoanProductCommand convertJsonToLoanProductCommand(final Long resourceIdentifier, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    final Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
// FIXME - A - LoanProduct - KW - Do we add support for "overpaidTolerance" parameter?
	    final Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("name", "description", "fundId", "transactionProcessingStrategyId", 
	    				"currencyCode", "digitsAfterDecimal", 
	    				"principal", "inArrearsTolerance", 
	    				"interestRatePerPeriod", "repaymentEvery", "numberOfRepayments", 
	    				"repaymentFrequencyType", "interestRateFrequencyType", 
	    				"amortizationType", "interestType", "interestCalculationPeriodType",
                        "charges", "locale")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    final JsonParser parser = new JsonParser();
	    final JsonElement element = parser.parse(json);
	    final JsonParserHelper helper = new JsonParserHelper();
	    
	    final Set<String> modifiedParameters = new HashSet<String>();
	    final String name = helper.extractStringNamed("name", element, modifiedParameters);
	    final String description = helper.extractStringNamed("description", element, modifiedParameters);
	    final Long fundId = helper.extractLongNamed("fundId", element, modifiedParameters);
	    
		final Long transactionProcessingStrategyId = helper.extractLongNamed("transactionProcessingStrategyId", element, modifiedParameters);
		
		final String currencyCode = helper.extractStringNamed("currencyCode", element, modifiedParameters);
		final Integer digitsAfterDecimal = helper.extractIntegerNamed("digitsAfterDecimal", element, modifiedParameters);
		
	    final BigDecimal principal = helper.extractBigDecimalNamed("principal", element, modifiedParameters);
	    final BigDecimal inArrearsTolerance = helper.extractBigDecimalNamed("inArrearsTolerance", element, modifiedParameters);
	    final BigDecimal interestRatePerPeriod = helper.extractBigDecimalNamed("interestRatePerPeriod", element, modifiedParameters);
	    final Integer repaymentEvery = helper.extractIntegerNamed("repaymentEvery", element, modifiedParameters);
	    final Integer numberOfRepayments = helper.extractIntegerNamed("numberOfRepayments", element, modifiedParameters);
	    final Integer repaymentFrequencyType = helper.extractIntegerNamed("repaymentFrequencyType", element, modifiedParameters);
	    
// FIXME - A - LoanProduct - KW - should loan term fields be part of product also as are basic part of 'loan terms' 
//	    final Integer loanTermFrequency = helper.extractIntegerNamed("loanTermFrequency", element, modifiedParameters);
//	    final Integer loanTermFrequencyType = helper.extractIntegerNamed("loanTermFrequencyType", element, modifiedParameters);
	    final Integer interestRateFrequencyType = helper.extractIntegerNamed("interestRateFrequencyType", element, modifiedParameters);
	    final Integer amortizationType = helper.extractIntegerNamed("amortizationType", element, modifiedParameters);
	    final Integer interestType = helper.extractIntegerNamed("interestType", element, modifiedParameters);
	    final Integer interestCalculationPeriodType = helper.extractIntegerNamed("interestCalculationPeriodType", element, modifiedParameters);
	    
	    // FIXME - A - LoanProduct - KW - product charges at present dont allow for adding 'charge' to loan product and setting its 'amount', might this be needed?
        // loan product charges 
        String[] charges = null;
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("charges")) {
                modifiedParameters.add("charges");
                JsonArray array = object.get("charges").getAsJsonArray();
                charges = new String[array.size()];
                for (int i=0; i<array.size(); i++) {
                    charges[i] = array.get(i).getAsString();
                }
            }
        }

		return new LoanProductCommand(modifiedParameters, resourceIdentifier, name, description, fundId, transactionProcessingStrategyId, 
				currencyCode, digitsAfterDecimal, principal, inArrearsTolerance, numberOfRepayments, repaymentEvery, interestRatePerPeriod,
				repaymentFrequencyType, interestRateFrequencyType, amortizationType, interestType, interestCalculationPeriodType, charges);
	}
	
	@Override
	public LoanApplicationCommand convertJsonToLoanApplicationCommand(final Long resourceIdentifier, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
		final Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
		final Set<String> supportedParams = new HashSet<String>(
    		Arrays.asList("clientId", "groupId", "productId", "externalId", "fundId", "transactionProcessingStrategyId",
    				"principal", "inArrearsTolerance", "interestRatePerPeriod", "repaymentEvery", "numberOfRepayments", 
    				"loanTermFrequency", "loanTermFrequencyType", "charges",
    				"repaymentFrequencyType", "interestRateFrequencyType", "amortizationType", "interestType", "interestCalculationPeriodType",
    				"expectedDisbursementDate", "repaymentsStartingFromDate", "interestChargedFromDate", "submittedOnDate", "submittedOnNote",
    				"locale", "dateFormat", "loanOfficerId", "id")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    final Set<String> parametersPassedInCommand = new HashSet<String>();
	    
	    final JsonParser parser = new JsonParser();
	    final JsonElement element = parser.parse(json);
	    final JsonParserHelper helper = new JsonParserHelper();
	    
	    final Long clientId = helper.extractLongNamed("clientId", element, parametersPassedInCommand);
		final Long groupId = helper.extractLongNamed("groupId", element, parametersPassedInCommand);
	    final Long productId = helper.extractLongNamed("productId", element, parametersPassedInCommand);
	    final Long fundId = helper.extractLongNamed("fundId", element, parametersPassedInCommand);
	    final Long loanOfficerId = helper.extractLongNamed("loanOfficerId", element, parametersPassedInCommand);
	    final Long transactionProcessingStrategyId = helper.extractLongNamed("transactionProcessingStrategyId", element, parametersPassedInCommand);
	    final String externalId = helper.extractStringNamed("externalId", element, parametersPassedInCommand);
	    final BigDecimal principal = helper.extractBigDecimalNamed("principal", element, parametersPassedInCommand);
	    final BigDecimal inArrearsToleranceValue = helper.extractBigDecimalNamed("inArrearsTolerance", element, parametersPassedInCommand);
	    final BigDecimal interestRatePerPeriod = helper.extractBigDecimalNamed("interestRatePerPeriod", element, parametersPassedInCommand);
	    
	    final Integer repaymentEvery = helper.extractIntegerNamed("repaymentEvery", element, parametersPassedInCommand);
	    final Integer numberOfRepayments = helper.extractIntegerNamed("numberOfRepayments", element, parametersPassedInCommand);
	    final Integer repaymentFrequencyType = helper.extractIntegerNamed("repaymentFrequencyType", element, parametersPassedInCommand);
	    final Integer loanTermFrequency = helper.extractIntegerNamed("loanTermFrequency", element, parametersPassedInCommand);
	    final Integer loanTermFrequencyType = helper.extractIntegerNamed("loanTermFrequencyType", element, parametersPassedInCommand);
	    final Integer interestRateFrequencyType = helper.extractIntegerNamed("interestRateFrequencyType", element, parametersPassedInCommand);
	    final Integer amortizationType = helper.extractIntegerNamed("amortizationType", element, parametersPassedInCommand);
	    final Integer interestType = helper.extractIntegerNamed("interestType", element, parametersPassedInCommand);
	    final Integer interestCalculationPeriodType = helper.extractIntegerNamed("interestCalculationPeriodType", element, parametersPassedInCommand);
	    
	    final LocalDate expectedDisbursementDate = helper.extractLocalDateNamed("expectedDisbursementDate", element, parametersPassedInCommand);
	    final LocalDate repaymentsStartingFromDate = helper.extractLocalDateNamed("repaymentsStartingFromDate", element, parametersPassedInCommand);
	    final LocalDate interestChargedFromDate = helper.extractLocalDateNamed("interestChargedFromDate", element, parametersPassedInCommand);
	    final LocalDate submittedOnDate = helper.extractLocalDateNamed("submittedOnDate", element, parametersPassedInCommand);
	    
	    final String submittedOnNote = helper.extractStringNamed("submittedOnNote", element, parametersPassedInCommand);
	    
        LoanChargeCommand[] charges = null;
        if (element.isJsonObject()) {
            final JsonObject topLevelJsonElement = element.getAsJsonObject();
            final String dateFormat = helper.extractDateFormatParameter(topLevelJsonElement);
            final Locale locale = helper.extractLocaleParameter(topLevelJsonElement);
            if (topLevelJsonElement.has("charges") && topLevelJsonElement.get("charges").isJsonArray()) {
                
            	parametersPassedInCommand.add("charges");
                final JsonArray array = topLevelJsonElement.get("charges").getAsJsonArray();
                charges = new LoanChargeCommand[array.size()];
                for (int i=0; i<array.size(); i++) {
                	
                    final JsonObject loanChargeElement = array.get(i).getAsJsonObject();
                    final Set<String> chargeModifiedParameters = new HashSet<String>();

                    final Long id = helper.extractLongNamed("id", loanChargeElement, chargeModifiedParameters);
                    final Long chargeId = helper.extractLongNamed("chargeId", loanChargeElement, chargeModifiedParameters);
                    final BigDecimal amount = helper.extractBigDecimalNamed("amount", loanChargeElement, locale, chargeModifiedParameters);
                    final Integer chargeTimeType = helper.extractIntegerNamed("chargeTimeType", loanChargeElement, locale, chargeModifiedParameters);
                    final Integer chargeCalculationType = helper.extractIntegerNamed("chargeCalculationType", loanChargeElement, locale, chargeModifiedParameters);
                    final LocalDate specifiedDueDate = helper.extractLocalDateNamed("specifiedDueDate", loanChargeElement, dateFormat, locale, parametersPassedInCommand);
                    
                    charges[i] = new LoanChargeCommand(chargeModifiedParameters, id, null, chargeId, amount, chargeTimeType, chargeCalculationType, specifiedDueDate);
                }
            }
        }

		return new LoanApplicationCommand(parametersPassedInCommand,
				resourceIdentifier, clientId, groupId, productId, externalId, fundId, transactionProcessingStrategyId,
				submittedOnDate, submittedOnNote, 
	    		expectedDisbursementDate, repaymentsStartingFromDate, interestChargedFromDate, 
	    		principal, interestRatePerPeriod, interestRateFrequencyType, interestType, interestCalculationPeriodType, 
	    		repaymentEvery, repaymentFrequencyType, numberOfRepayments, amortizationType, 
	    		loanTermFrequency, loanTermFrequencyType,
	    		inArrearsToleranceValue, charges,loanOfficerId);
	}

	// FIXME - KW - charges
    @Override
    public LoanChargeCommand convertJsonToLoanChargeCommand(final Long loanChargeId, final Long loanId, final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

        final Set<String> supportedParams = new HashSet<String>(
        		Arrays.asList("chargeId", "amount", "chargeTimeType", "chargeCalculationType", "specifiedDueDate", "locale", "dateFormat")
        );

        checkForUnsupportedParameters(requestMap, supportedParams);

        Set<String> modifiedParameters = new HashSet<String>();

        Long chargeId = extractLongParameter("chargeId", requestMap, modifiedParameters);
        BigDecimal amount = extractBigDecimalParameter("amount", requestMap, modifiedParameters);
        Integer chargeTimeType = extractIntegerParameter("chargeTimeType", requestMap, modifiedParameters);
        Integer chargeCalculationType = extractIntegerParameter("chargeCalculationType", requestMap, modifiedParameters);
        final LocalDate submittedOnDate = extractLocalDateParameter("specifiedDueDate", requestMap, modifiedParameters);

        return new LoanChargeCommand(modifiedParameters, loanChargeId, loanId, chargeId, amount,
                chargeTimeType, chargeCalculationType, submittedOnDate);
    }

    @Override
	public LoanStateTransitionCommand convertJsonToLoanStateTransitionCommand(final Long resourceIdentifier, final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("eventDate", "note", "locale", "dateFormat")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();

	    LocalDate eventDate = extractLocalDateParameter("eventDate", requestMap, modifiedParameters);
	    String note = extractStringParameter("note", requestMap, modifiedParameters);
	    
	    return new LoanStateTransitionCommand(resourceIdentifier, eventDate, note);
	}
	
	@Override
	public LoanTransactionCommand convertJsonToLoanTransactionCommand(final Long resourceIdentifier, final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("transactionDate", "transactionAmount", "note", "dateFormat", "locale")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();

	    LocalDate transactionDate = extractLocalDateParameter("transactionDate", requestMap, modifiedParameters);
	    BigDecimal transactionAmount = extractBigDecimalParameter("transactionAmount", requestMap, modifiedParameters);
	    String note = extractStringParameter("note", requestMap, modifiedParameters);
	    
	    return new LoanTransactionCommand(resourceIdentifier, transactionDate, transactionAmount, note);
	}
	
	@Override
	public AdjustLoanTransactionCommand convertJsonToAdjustLoanTransactionCommand(
			final Long loanId, final Long transactionId, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("transactionDate", "transactionAmount", "note", "dateFormat", "locale")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();

	    LocalDate transactionDate = extractLocalDateParameter("transactionDate", requestMap, modifiedParameters);
	    BigDecimal transactionAmount = extractBigDecimalParameter("transactionAmount", requestMap, modifiedParameters);
	    String note = extractStringParameter("note", requestMap, modifiedParameters);
	    
	    return new AdjustLoanTransactionCommand(loanId, transactionId, transactionDate, note, transactionAmount);
	}
	
	@Override
	public NoteCommand convertJsonToNoteCommand(final Long noteId, final Long clientId, final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("note")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();

	    String note = extractStringParameter("note", requestMap, modifiedParameters);
	    
	    return new NoteCommand(noteId, clientId, note);
	}
	
	@Override
	public OrganisationCurrencyCommand convertJsonToOrganisationCurrencyCommand(final String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("currencies")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();

	    // check array
	    JsonParser parser = new JsonParser();
		
		String[] currencies = null;
		JsonElement element = parser.parse(json);
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			if (object.has("currencies")) {
				modifiedParameters.add("currencies");
				JsonArray array = object.get("currencies").getAsJsonArray();
				currencies = new String[array.size()];
				for (int i=0; i<array.size(); i++) {
					currencies[i] = array.get(i).getAsString();
				}
			}
		}
	    //
	    
	    return new OrganisationCurrencyCommand(currencies);
	}

	private void checkForUnsupportedParameters(Map<String, ?> requestMap, Set<String> supportedParams) {
		List<String> unsupportedParameterList = new ArrayList<String>();
		for (String providedParameter : requestMap.keySet()) {
			if (!supportedParams.contains(providedParameter)) {
				unsupportedParameterList.add(providedParameter);
			}
		}
		
		if (!unsupportedParameterList.isEmpty()) {
			throw new UnsupportedParameterException(unsupportedParameterList);
		}
	}

	private String extractStringParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
		String paramValue = null;
		if (requestMap.containsKey(paramName)) {
			paramValue = (String) requestMap.get(paramName);
			modifiedParameters.add(paramName);
		}
		
		if (paramValue != null) {
			paramValue = paramValue.trim();
		}
		
		return paramValue;
	}
	
	private Integer extractIntegerParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
		Integer paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			paramValue = convertToInteger(valueAsString, paramName, extractLocaleValue(requestMap));
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}

	private BigDecimal extractBigDecimalParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
		BigDecimal paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			paramValue = convertFrom(valueAsString, paramName, extractLocaleValue(requestMap));
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}

	private boolean extractBooleanParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
		boolean paramValue = false;
		String paramValueAsString = null;
		if (requestMap.containsKey(paramName)) {
			paramValueAsString = (String) requestMap.get(paramName);

			if (paramValueAsString != null) {
				paramValueAsString = paramValueAsString.trim();
			}

			paramValue = Boolean.valueOf(paramValueAsString);
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}
	
	private Long extractLongParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
		Long paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			if (StringUtils.isNotBlank(valueAsString)) {
				paramValue = Long.valueOf(Double.valueOf(valueAsString).longValue());
			}
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}
	
	private LocalDate extractLocalDateParameter(final String paramName, final Map<String, ?> requestMap, final Set<String> modifiedParameters) {
		LocalDate paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			if (StringUtils.isNotBlank(valueAsString)) {
				final String dateFormat = (String) requestMap.get("dateFormat");
				final Locale locale = new Locale((String) requestMap.get("locale"));
				paramValue = convertFrom(valueAsString, paramName, dateFormat, locale);
			}
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}

	private Locale extractLocaleValue(Map<String, ?> requestMap) {
		Locale clientApplicationLocale = null;
	    String locale = null;
	    if (requestMap.containsKey("locale")) {
	    	locale = (String) requestMap.get("locale");
	    	clientApplicationLocale = localeFromString(locale);
	    }
		return clientApplicationLocale;
	}
	

	private LocalDate convertFrom(final String dateAsString,
			final String parameterName, final String dateFormat,
			final Locale clientApplicationLocale) {

		if (StringUtils.isBlank(dateFormat) || clientApplicationLocale == null) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			if (StringUtils.isBlank(dateFormat)) {
				String defaultMessage = new StringBuilder(
						"The parameter '"
								+ parameterName
								+ "' requires a 'dateFormat' parameter to be passed with it.")
						.toString();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.missing.dateFormat.parameter",
						defaultMessage, parameterName);
				dataValidationErrors.add(error);
			}
			if (clientApplicationLocale == null) {
				String defaultMessage = new StringBuilder(
						"The parameter '"
								+ parameterName
								+ "' requires a 'locale' parameter to be passed with it.")
						.toString();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.missing.locale.parameter",
						defaultMessage, parameterName);
				dataValidationErrors.add(error);
			}
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		LocalDate eventLocalDate = null;
		if (StringUtils.isNotBlank(dateAsString)) {
			try {
				// Locale locale = LocaleContextHolder.getLocale();
				eventLocalDate = DateTimeFormat
						.forPattern(dateFormat)
						.withLocale(clientApplicationLocale)
						.parseLocalDate(
								dateAsString
										.toLowerCase(clientApplicationLocale));
			} catch (IllegalArgumentException e) {
				List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.invalid.date.format", "The parameter "
								+ parameterName
								+ " is invalid based on the dateFormat: '"
								+ dateFormat + "' and locale: '"
								+ clientApplicationLocale + "' provided:",
						parameterName, dateAsString, dateFormat);
				dataValidationErrors.add(error);

				throw new PlatformApiDataValidationException(
						"validation.msg.validation.errors.exist",
						"Validation errors exist.", dataValidationErrors);
			}
		}

		return eventLocalDate;
	}

	private Integer convertToInteger(final String numericalValueFormatted,
			final String parameterName, final Locale clientApplicationLocale) {

		if (clientApplicationLocale == null) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			String defaultMessage = new StringBuilder("The parameter '"
					+ parameterName
					+ "' requires a 'locale' parameter to be passed with it.")
					.toString();
			ApiParameterError error = ApiParameterError.parameterError(
					"validation.msg.missing.locale.parameter", defaultMessage,
					parameterName);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		try {
			Integer number = null;

			if (StringUtils.isNotBlank(numericalValueFormatted)) {

				String source = numericalValueFormatted.trim();

				NumberFormat format = NumberFormat
						.getInstance(clientApplicationLocale);
				DecimalFormat df = (DecimalFormat) format;
				DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
				df.setParseBigDecimal(true);

				// http://bugs.sun.com/view_bug.do?bug_id=4510618
				char groupingSeparator = symbols.getGroupingSeparator();
				if (groupingSeparator == '\u00a0') {
					source = source.replaceAll(" ",
							Character.toString('\u00a0'));
				}

				Number parsedNumber = df.parse(source);

				double parsedNumberDouble = parsedNumber.doubleValue();
				int parsedNumberInteger = parsedNumber.intValue();

				if (source.contains(Character.toString(symbols
						.getDecimalSeparator()))) {
					throw new ParseException(source, 0);
				}

				if (!Double.valueOf(parsedNumberDouble).equals(
						Double.valueOf(Integer.valueOf(parsedNumberInteger)))) {
					throw new ParseException(source, 0);
				}

				number = parsedNumber.intValue();
			}

			return number;
		} catch (ParseException e) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			ApiParameterError error = ApiParameterError
					.parameterError(
							"validation.msg.invalid.integer.format",
							"The parameter "
									+ parameterName
									+ " has value: "
									+ numericalValueFormatted
									+ " which is invalid integer value for provided locale of ["
									+ clientApplicationLocale.toString() + "].",
							parameterName, numericalValueFormatted,
							clientApplicationLocale);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}

	private BigDecimal convertFrom(final String numericalValueFormatted,
			final String parameterName, final Locale clientApplicationLocale) {

		if (clientApplicationLocale == null) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			String defaultMessage = new StringBuilder("The parameter '"
					+ parameterName
					+ "' requires a 'locale' parameter to be passed with it.")
					.toString();
			ApiParameterError error = ApiParameterError.parameterError(
					"validation.msg.missing.locale.parameter", defaultMessage,
					parameterName);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		try {
			BigDecimal number = null;

			if (StringUtils.isNotBlank(numericalValueFormatted)) {

				String source = numericalValueFormatted.trim();

				NumberFormat format = NumberFormat
						.getNumberInstance(clientApplicationLocale);
				DecimalFormat df = (DecimalFormat) format;
				DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
				// http://bugs.sun.com/view_bug.do?bug_id=4510618
				char groupingSeparator = symbols.getGroupingSeparator();
				if (groupingSeparator == '\u00a0') {
					source = source.replaceAll(" ",
							Character.toString('\u00a0'));
				}

				NumberFormatter numberFormatter = new NumberFormatter();
				Number parsedNumber = numberFormatter.parse(source,
						clientApplicationLocale);
				number = BigDecimal.valueOf(Double.valueOf(parsedNumber
						.doubleValue()));
			}

			return number;
		} catch (ParseException e) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			ApiParameterError error = ApiParameterError
					.parameterError(
							"validation.msg.invalid.decimal.format",
							"The parameter "
									+ parameterName
									+ " has value: "
									+ numericalValueFormatted
									+ " which is invalid decimal value for provided locale of ["
									+ clientApplicationLocale.toString() + "].",
							parameterName, numericalValueFormatted,
							clientApplicationLocale);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}


	/*
	private LocalDate convertFrom(final String dateAsString, final String parameterName, final String dateFormat) {
		
		if (StringUtils.isBlank(dateFormat)) {
			
			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			String defaultMessage = new StringBuilder("The parameter '" + parameterName + "' requires a 'dateFormat' parameter to be passed with it.").toString();
			ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.dateFormat.parameter", defaultMessage, parameterName);
			dataValidationErrors.add(error);
			
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
		
		LocalDate eventLocalDate = null;
		if (StringUtils.isNotBlank(dateAsString)) {
			try {
				Locale locale = LocaleContextHolder.getLocale();
				eventLocalDate = DateTimeFormat.forPattern(dateFormat)
						.withLocale(locale)
						.parseLocalDate(dateAsString.toLowerCase(locale));
			} catch (IllegalArgumentException e) {
				List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
				ApiParameterError error = ApiParameterError
						.parameterError(
								"validation.msg.invalid.date.format",
								"The parameter "
										+ parameterName
										+ " is invalid based on the dateFormat provided:"
										+ dateFormat, parameterName,
								dateAsString, dateFormat);
				dataValidationErrors.add(error);

				throw new PlatformApiDataValidationException(
						"validation.msg.validation.errors.exist",
						"Validation errors exist.", dataValidationErrors);
			}
		}

		return eventLocalDate;
	}
	
	private Integer convertToInteger(final String numericalValueFormatted, final String parameterName, final Locale clientApplicationLocale) {
		
		if (clientApplicationLocale == null) {
			
			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			String defaultMessage = new StringBuilder("The parameter '" + parameterName + "' requires a 'locale' parameter to be passed with it.").toString();
			ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.locale.parameter", defaultMessage, parameterName);
			dataValidationErrors.add(error);
			
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
		
		try {
			Integer number = null;

			if (StringUtils.isNotBlank(numericalValueFormatted)) {
				
				String source = numericalValueFormatted.trim();
				
				NumberFormat format = NumberFormat.getInstance(clientApplicationLocale);
				DecimalFormat df = (DecimalFormat) format;
				DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
				df.setParseBigDecimal(true);
				
				// http://bugs.sun.com/view_bug.do?bug_id=4510618
				char groupingSeparator = symbols.getGroupingSeparator();
				if (groupingSeparator == '\u00a0') {
					source = source.replaceAll(" ", Character.toString('\u00a0'));
			    }
				
				Number parsedNumber = df.parse(source);
				
				double parsedNumberDouble = parsedNumber.doubleValue();
				int parsedNumberInteger = parsedNumber.intValue();
				
				if (source.contains(Character.toString(symbols.getDecimalSeparator()))) {
					throw new ParseException(source, 0);
				}
				
				if (!Double.valueOf(parsedNumberDouble).equals(Double.valueOf(Integer.valueOf(parsedNumberInteger)))) {
					throw new ParseException(source, 0);
				}
				
				number = parsedNumber.intValue();
			}

			return number;
		} catch (ParseException e) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			ApiParameterError error = ApiParameterError.parameterError(
					"validation.msg.invalid.integer.format", "The parameter "
							+ parameterName + " has value: " + numericalValueFormatted + " which is invalid integer value for provided locale of ["
							+ clientApplicationLocale.toString() + "].",
					parameterName, numericalValueFormatted,
					clientApplicationLocale);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}

	private BigDecimal convertFrom(final String numericalValueFormatted, final String parameterName, final Locale clientApplicationLocale) {

		if (clientApplicationLocale == null) {
			
			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			String defaultMessage = new StringBuilder("The parameter '" + parameterName + "' requires a 'locale' parameter to be passed with it.").toString();
			ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.locale.parameter", defaultMessage, parameterName);
			dataValidationErrors.add(error);
			
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
		
		try {
			BigDecimal number = null;
			
			if (StringUtils.isNotBlank(numericalValueFormatted)) {
				
				String source = numericalValueFormatted.trim();
				
				NumberFormat format = NumberFormat.getNumberInstance(clientApplicationLocale);
				DecimalFormat df = (DecimalFormat) format;
				DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
				// http://bugs.sun.com/view_bug.do?bug_id=4510618
				char groupingSeparator = symbols.getGroupingSeparator();
				if (groupingSeparator == '\u00a0') {
					source = source.replaceAll(" ", Character.toString('\u00a0'));
			    }
				
				NumberFormatter numberFormatter = new NumberFormatter();
				Number parsedNumber = numberFormatter.parse(source, clientApplicationLocale);
				number = BigDecimal.valueOf(Double.valueOf(parsedNumber.doubleValue()));
			}

			return number;
		} catch (ParseException e) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			ApiParameterError error = ApiParameterError.parameterError(
					"validation.msg.invalid.decimal.format", "The parameter "
							+ parameterName + " has value: " + numericalValueFormatted + " which is invalid decimal value for provided locale of ["
							+ clientApplicationLocale.toString() + "].",
					parameterName, numericalValueFormatted,
					clientApplicationLocale);
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
*/
	private Locale localeFromString(final String localeAsString) {
		
		if (StringUtils.isBlank(localeAsString)) {
			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.locale.format", "The parameter locale is invalid. It cannot be blank.", "locale");
			dataValidationErrors.add(error);

			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
		
		String languageCode = "";
		String courntryCode = "";
		String variantCode = "";
		
		String[] localeParts = localeAsString.split("_");
		
		if (localeParts != null && localeParts.length == 1) {
			languageCode = localeParts[0];
		}
		
		if (localeParts != null && localeParts.length == 2) {
			languageCode = localeParts[0];
			courntryCode = localeParts[1];
		}
		
		if (localeParts != null && localeParts.length == 3) {
			languageCode = localeParts[0];
			courntryCode = localeParts[1];
			variantCode = localeParts[2];
		}
		
		return localeFrom(languageCode, courntryCode, variantCode);
	}

	private Locale localeFrom(final String languageCode, final String courntryCode, final String variantCode) {
		
		List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		
		List<String> allowedLanguages = Arrays.asList(Locale.getISOLanguages());
		if (!allowedLanguages.contains(languageCode.toLowerCase())) {
			ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.locale.format", "The parameter locale has an invalid language value " + languageCode + " .", "locale", languageCode);
			dataValidationErrors.add(error);			
		}
		
		if (StringUtils.isNotBlank(courntryCode.toUpperCase())) {
			List<String> allowedCountries = Arrays.asList(Locale.getISOCountries());
			if (!allowedCountries.contains(courntryCode)) {
				ApiParameterError error = ApiParameterError.parameterError("validation.msg.invalid.locale.format", "The parameter locale has an invalid country value " + courntryCode + " .", "locale", courntryCode);
				dataValidationErrors.add(error);			
			}
		}
		
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
		
		return new Locale(languageCode.toLowerCase(), courntryCode.toUpperCase(), variantCode);
	}

	@Override
	public SavingProductCommand convertJsonToSavingProductCommand(final Long resourceIdentifier, final String json) {

		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

		Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale", "name", "description","currencyCode", "digitsAfterDecimal","interestRate", "minInterestRate",
				"maxInterestRate","savingsDepositAmount","savingProductType","tenureType","tenure", "frequency","interestType","interestCalculationMethod",
				"minimumBalanceForWithdrawal","isPartialDepositAllowed","isLockinPeriodAllowed","lockinPeriod","lockinPeriodType"));
		
		checkForUnsupportedParameters(requestMap, supportedParams);
		Set<String> modifiedParameters = new HashSet<String>();
		String name = extractStringParameter("name", requestMap,modifiedParameters);
		String description = extractStringParameter("description", requestMap,modifiedParameters);
		String currencyCode=extractStringParameter("currencyCode", requestMap,modifiedParameters);
		Integer digitsAfterDecimalValue = extractIntegerParameter("digitsAfterDecimal", requestMap, modifiedParameters);
		BigDecimal interestRate = extractBigDecimalParameter("interestRate", requestMap, modifiedParameters);
		BigDecimal minInterestRate = extractBigDecimalParameter("minInterestRate", requestMap, modifiedParameters);
		BigDecimal maxInterestRate = extractBigDecimalParameter("maxInterestRate", requestMap, modifiedParameters);
		BigDecimal savingsDepositAmount=extractBigDecimalParameter("savingsDepositAmount", requestMap, modifiedParameters);
		Integer savingProductType = extractIntegerParameter("savingProductType", requestMap, modifiedParameters);
		Integer tenureType = extractIntegerParameter("tenureType", requestMap, modifiedParameters);
		Integer tenure = extractIntegerParameter("tenure", requestMap, modifiedParameters);
		Integer frequency = extractIntegerParameter("frequency", requestMap, modifiedParameters);
		Integer interestType = extractIntegerParameter("interestType", requestMap, modifiedParameters);
		Integer interestCalculationMethod=extractIntegerParameter("interestCalculationMethod", requestMap, modifiedParameters);
		BigDecimal minimumBalanceForWithdrawal=extractBigDecimalParameter("minimumBalanceForWithdrawal", requestMap, modifiedParameters);
		boolean isPartialDepositAllowed = extractBooleanParameter("isPartialDepositAllowed", requestMap, modifiedParameters);
		boolean isLockinPeriodAllowed = extractBooleanParameter("isLockinPeriodAllowed", requestMap, modifiedParameters);
	    Integer lockinPeriod = extractIntegerParameter("lockinPeriod", requestMap, modifiedParameters);
	    Integer lockinPeriodType = extractIntegerParameter("lockinPeriodType", requestMap, modifiedParameters);
		

		return new SavingProductCommand(modifiedParameters, resourceIdentifier,name, description,currencyCode,digitsAfterDecimalValue,interestRate, minInterestRate,
				maxInterestRate, savingsDepositAmount,savingProductType, tenureType,tenure,frequency,interestType,interestCalculationMethod,minimumBalanceForWithdrawal,
				isPartialDepositAllowed,	isLockinPeriodAllowed,lockinPeriod,lockinPeriodType);
	}
	
	@Override
	public DepositAccountCommand convertJsonToDepositAccountCommand(final Long resourceIdentifier, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

		// preClosureInterestRate
		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("clientId", "productId", "externalId", "deposit", "maturityInterestRate", "preClosureInterestRate",
						"tenureInMonths", "interestCompoundedEvery", "interestCompoundedEveryPeriodType", "commencementDate",
						"renewalAllowed", "preClosureAllowed","interestCompoundingAllowed",
						"locale", "dateFormat","isInterestWithdrawable","isLockinPeriodAllowed","lockinPeriod","lockinPeriodType")
		);
		checkForUnsupportedParameters(requestMap, supportedParams);
		Set<String> modifiedParameters = new HashSet<String>();
		
		Long clientId = extractLongParameter("clientId", requestMap, modifiedParameters);
		Long productId = extractLongParameter("productId", requestMap, modifiedParameters);
		String externalId = extractStringParameter("externalId", requestMap,modifiedParameters);
		BigDecimal deposit=extractBigDecimalParameter("deposit", requestMap, modifiedParameters);
		BigDecimal interestRate = extractBigDecimalParameter("maturityInterestRate", requestMap, modifiedParameters);
		BigDecimal preClosureInterestRate = extractBigDecimalParameter("preClosureInterestRate", requestMap, modifiedParameters);
		Integer tenureInMonths = extractIntegerParameter("tenureInMonths", requestMap, modifiedParameters);
		
		boolean isLockinPeriodAllowed = extractBooleanParameter("isLockinPeriodAllowed", requestMap, modifiedParameters);
		Integer lockinPeriod = extractIntegerParameter("lockinPeriod", requestMap, modifiedParameters);
		Integer lockinPeriodType = extractIntegerParameter("lockinPeriodType", requestMap, modifiedParameters);
		
		Integer interestCompoundedEvery = extractIntegerParameter("interestCompoundedEvery", requestMap, modifiedParameters);
		Integer interestCompoundedEveryPeriodType = extractIntegerParameter("interestCompoundedEveryPeriodType", requestMap, modifiedParameters);
		LocalDate commencementDate = extractLocalDateParameter("commencementDate", requestMap, modifiedParameters);

		boolean renewalAllowed = extractBooleanParameter("renewalAllowed", requestMap, modifiedParameters);
		boolean preClosureAllowed = extractBooleanParameter("preClosureAllowed", requestMap, modifiedParameters);
		boolean isInterestWithdrawable = extractBooleanParameter("isInterestWithdrawable", requestMap, modifiedParameters);
		boolean interestCompoundingAllowed = extractBooleanParameter("interestCompoundingAllowed", requestMap, modifiedParameters);
		
		return new DepositAccountCommand(modifiedParameters, resourceIdentifier, clientId, productId, 
				externalId, deposit, interestRate, preClosureInterestRate, tenureInMonths,
				interestCompoundedEvery, interestCompoundedEveryPeriodType, commencementDate, renewalAllowed,
				preClosureAllowed, isInterestWithdrawable, interestCompoundingAllowed,
				isLockinPeriodAllowed,lockinPeriod,lockinPeriodType);
	}

	@Override
	public DepositProductCommand convertJsonToDepositProductCommand(final Long resourceIdentifier, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("locale", "name", "externalId", "description","currencyCode", "digitsAfterDecimal","minimumBalance","maximumBalance","tenureInMonths",
						"maturityDefaultInterestRate","maturityMinInterestRate","maturityMaxInterestRate", 
						"interestCompoundedEvery", "interestCompoundedEveryPeriodType",
						"renewalAllowed","preClosureAllowed","preClosureInterestRate","interestCompoundingAllowed","isLockinPeriodAllowed","lockinPeriod","lockinPeriodType")
				);
		checkForUnsupportedParameters(requestMap, supportedParams);
		Set<String> modifiedParameters = new HashSet<String>();
		String name = extractStringParameter("name", requestMap,modifiedParameters);
		String externalId = extractStringParameter("externalId", requestMap,modifiedParameters);
		
		String description = extractStringParameter("description", requestMap,modifiedParameters);
		String currencyCode=extractStringParameter("currencyCode", requestMap,modifiedParameters);
		Integer digitsAfterDecimalValue = extractIntegerParameter("digitsAfterDecimal", requestMap, modifiedParameters);
		BigDecimal minimumBalance=extractBigDecimalParameter("minimumBalance", requestMap, modifiedParameters);
		BigDecimal maximumBalance=extractBigDecimalParameter("maximumBalance", requestMap, modifiedParameters);
		
		Integer tenureMonths = extractIntegerParameter("tenureInMonths", requestMap, modifiedParameters);
		BigDecimal maturityDefaultInterestRate = extractBigDecimalParameter("maturityDefaultInterestRate", requestMap, modifiedParameters);
		BigDecimal maturityMinInterestRate = extractBigDecimalParameter("maturityMinInterestRate", requestMap, modifiedParameters);
		BigDecimal maturityMaxInterestRate = extractBigDecimalParameter("maturityMaxInterestRate", requestMap, modifiedParameters);
		
		Integer interestCompoundedEvery = extractIntegerParameter("interestCompoundedEvery", requestMap, modifiedParameters);
		Integer interestCompoundedEveryPeriodType = extractIntegerParameter("interestCompoundedEveryPeriodType", requestMap, modifiedParameters);
		
		boolean interestCompoundingAllowed = extractBooleanParameter("interestCompoundingAllowed", requestMap, modifiedParameters);
		
	    boolean canRenew = extractBooleanParameter("renewalAllowed", requestMap, modifiedParameters);
	    boolean canPreClose = extractBooleanParameter("preClosureAllowed", requestMap, modifiedParameters);
	    BigDecimal preClosureInterestRate = extractBigDecimalParameter("preClosureInterestRate", requestMap, modifiedParameters);
	    
	    boolean isLockinPeriodAllowed = extractBooleanParameter("isLockinPeriodAllowed", requestMap, modifiedParameters);
	    Integer lockinPeriod = extractIntegerParameter("lockinPeriod", requestMap, modifiedParameters);
	    Integer lockinPeriodType = extractIntegerParameter("lockinPeriodType", requestMap, modifiedParameters);
	    
	    
		
		return new DepositProductCommand(modifiedParameters, resourceIdentifier, externalId, 
				name, description, currencyCode, digitsAfterDecimalValue, minimumBalance,maximumBalance, 
				tenureMonths, maturityDefaultInterestRate, maturityMinInterestRate, maturityMaxInterestRate, 
				interestCompoundedEvery, interestCompoundedEveryPeriodType,
				canRenew, canPreClose, preClosureInterestRate, interestCompoundingAllowed,isLockinPeriodAllowed,lockinPeriod,lockinPeriodType);
	}

	@Override
	public DepositStateTransitionCommand convertJsonToDepositStateTransitionCommand(
			Long resourceIdentifier, String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, Object>>(){}.getType();
	    Map<String, Object> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>( Arrays.asList("eventDate", "locale", "dateFormat", "note"));
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    LocalDate eventDate = extractLocalDateParameter("eventDate", requestMap, modifiedParameters);
	    String note =extractStringParameter("note", requestMap, modifiedParameters);
	    
		return new DepositStateTransitionCommand(resourceIdentifier, eventDate, note);
	}
	
	@Override
	public DepositStateTransitionApprovalCommand convertJsonToDepositStateTransitionApprovalCommand(
			final Long resourceIdentifier, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale","commencementDate", "locale", "dateFormat", "tenureInMonths", "deposit", "interestCompoundedEveryPeriodType", "productId","interestCompoundedEvery","note","maturityInterestRate"));
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    LocalDate commencementDate = extractLocalDateParameter("commencementDate", requestMap, modifiedParameters);
	    BigDecimal deposit=extractBigDecimalParameter("deposit", requestMap, modifiedParameters);
	    BigDecimal maturityInterestRate=extractBigDecimalParameter("maturityInterestRate", requestMap, modifiedParameters);
		Integer tenureInMonths = extractIntegerParameter("tenureInMonths", requestMap, modifiedParameters);
	    Integer interestCompoundedEveryPeriodType = extractIntegerParameter("interestCompoundedEveryPeriodType", requestMap, modifiedParameters);
	    Integer interestCompoundedEvery = extractIntegerParameter("interestCompoundedEvery", requestMap, modifiedParameters);
	    Long productId = extractLongParameter("productId", requestMap, modifiedParameters);
	    String note = extractStringParameter("note", requestMap, modifiedParameters);
	    
		return new DepositStateTransitionApprovalCommand(resourceIdentifier, productId, commencementDate, tenureInMonths, deposit, interestCompoundedEveryPeriodType,interestCompoundedEvery, note,maturityInterestRate);
	}

	@Override
	public DepositAccountWithdrawalCommand convertJsonToDepositWithdrawalCommand(final Long resourceIdentifier, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(Arrays.asList("note","locale","maturesOnDate", "dateFormat"));
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    String note = extractStringParameter("note", requestMap, modifiedParameters);
	    LocalDate maturesOnDate = extractLocalDateParameter("maturesOnDate", requestMap, modifiedParameters);
	    
		return new DepositAccountWithdrawalCommand(resourceIdentifier,note,maturesOnDate);
	}

	@Override
	public DepositAccountWithdrawInterestCommand convertJsonToDepositAccountWithdrawInterestCommand(final Long resourceIdentifier, final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(Arrays.asList("amount", "note", "locale"));
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    BigDecimal amount=extractBigDecimalParameter("amount", requestMap, modifiedParameters);
	    String note = extractStringParameter("note", requestMap, modifiedParameters);
		
		return new DepositAccountWithdrawInterestCommand(resourceIdentifier, amount, note);
	}
	
	@Override
	public ClientIdentifierCommand convertJsonToClientIdentifierCommand(
			Long resourceIdentifier, Long clientId, String json) {
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    Set<String> supportedParams = new HashSet<String>(Arrays.asList("clientId", "documentTypeId", "documentKey", "description"));
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	   
	    Long documentTypeId = extractLongParameter("documentTypeId", requestMap, modifiedParameters);
	    String documentKey = extractStringParameter("documentKey", requestMap, modifiedParameters);
	    String documentDescription = extractStringParameter("description", requestMap, modifiedParameters);
	    
	    return new ClientIdentifierCommand(modifiedParameters,resourceIdentifier,clientId,documentTypeId,documentKey,documentDescription);
	}
	
	@Override
	public CodeCommand convertJsonToCodeCommand(Long resourceIdentifier,String json) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>(){}.getType();
	    Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);
	    
	    
	    Set<String> supportedParams = new HashSet<String>(
	    		Arrays.asList("name")
	    );
	    
	    checkForUnsupportedParameters(requestMap, supportedParams);
	    
	    Set<String> modifiedParameters = new HashSet<String>();
	    
	    String name = extractStringParameter("name", requestMap, modifiedParameters);
	    
		return new CodeCommand(modifiedParameters, resourceIdentifier, name);

		
	}

	

	@Override
	public SavingAccountCommand convertJsonToSavingAccountCommand(Long resourceIdentifier, String json) {

		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}
		
		Type typeOfMap = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> requestMap = gsonConverter.fromJson(json, typeOfMap);

		// currencyCode, currencyDigits
		Set<String> supportedParams = new HashSet<String>(
				Arrays.asList("clientId", "productId", "externalId", "currencyCode", "digitsAfterDecimal", "savingsDepositAmountPerPeriod", "recurringInterestRate", "savingInterestRate",
						"tenure", "commencementDate", "locale", "dateFormat","isLockinPeriodAllowed","lockinPeriod","lockinPeriodType",
						"savingProductType","tenureType", "frequency","interestType","interestCalculationMethod",
						"minimumBalanceForWithdrawal","isPartialDepositAllowed"	)
		);
		checkForUnsupportedParameters(requestMap, supportedParams);
		Set<String> modifiedParameters = new HashSet<String>();
		
		Long clientId = extractLongParameter("clientId", requestMap, modifiedParameters);
		Long productId = extractLongParameter("productId", requestMap, modifiedParameters);
		String externalId = extractStringParameter("externalId", requestMap,modifiedParameters);
		String currencyCode=extractStringParameter("currencyCode", requestMap,modifiedParameters);
		Integer digitsAfterDecimalValue = extractIntegerParameter("digitsAfterDecimal", requestMap, modifiedParameters);
		BigDecimal savingsDepositAmount=extractBigDecimalParameter("savingsDepositAmountPerPeriod", requestMap, modifiedParameters);
		BigDecimal recurringInterestRate = extractBigDecimalParameter("recurringInterestRate", requestMap, modifiedParameters);
		BigDecimal savingInterestRate = extractBigDecimalParameter("savingInterestRate", requestMap, modifiedParameters);
		Integer tenure = extractIntegerParameter("tenure", requestMap, modifiedParameters);
		
		boolean isLockinPeriodAllowed = extractBooleanParameter("isLockinPeriodAllowed", requestMap, modifiedParameters);
		Integer lockinPeriod = extractIntegerParameter("lockinPeriod", requestMap, modifiedParameters);
		Integer lockinPeriodType = extractIntegerParameter("lockinPeriodType", requestMap, modifiedParameters);
		
		LocalDate commencementDate = extractLocalDateParameter("commencementDate", requestMap, modifiedParameters);
		Integer savingProductType = extractIntegerParameter("savingProductType", requestMap, modifiedParameters);
		Integer tenureType = extractIntegerParameter("tenureType", requestMap, modifiedParameters);
		Integer frequency = extractIntegerParameter("frequency", requestMap, modifiedParameters);
		Integer interestType = extractIntegerParameter("interestType", requestMap, modifiedParameters);
		Integer interestCalculationMethod=extractIntegerParameter("interestCalculationMethod", requestMap, modifiedParameters);
		BigDecimal minimumBalanceForWithdrawal=extractBigDecimalParameter("minimumBalanceForWithdrawal", requestMap, modifiedParameters);
		boolean isPartialDepositAllowed = extractBooleanParameter("isPartialDepositAllowed", requestMap, modifiedParameters);
		
		return new SavingAccountCommand(modifiedParameters, resourceIdentifier, clientId, productId, externalId, currencyCode, digitsAfterDecimalValue,
				savingsDepositAmount, recurringInterestRate,savingInterestRate, tenure, commencementDate, savingProductType, tenureType, frequency, 
				interestType, minimumBalanceForWithdrawal, interestCalculationMethod, isLockinPeriodAllowed, isPartialDepositAllowed, lockinPeriod, lockinPeriodType);

	}

}