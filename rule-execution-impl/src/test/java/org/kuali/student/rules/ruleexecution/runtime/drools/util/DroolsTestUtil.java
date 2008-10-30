package org.kuali.student.rules.ruleexecution.runtime.drools.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.PackageBuilder;
import org.kuali.student.rules.factfinder.dto.FactResultColumnInfoDTO;
import org.kuali.student.rules.factfinder.dto.FactResultDTO;
import org.kuali.student.rules.factfinder.dto.FactResultTypeInfoDTO;
import org.kuali.student.rules.repository.drools.util.DroolsUtil;
import org.kuali.student.rules.repository.dto.RuleDTO;
import org.kuali.student.rules.repository.dto.RuleSetDTO;

public class DroolsTestUtil {

    /**
     * Gets a simple rule. 
     * First rule determines whether the minutes of the hour is even.
     * Rule takes <code>java.util.Calendar</code> as fact (package import).
     * 
     * @param ruleName Rule name
     * @return a Drools rule
     */
	private static String getSimpleRule1(String ruleName) {
        ruleName = (ruleName == null ? "HelloDroolsEven" : ruleName);
        return 
			"rule \"" + ruleName + "\"" +
			"     when" +
			"          now: Calendar()" +
			"          eval ( ( now.get(Calendar.MINUTE) % 2 == 0 ) )" +
			"     then" +
			"          insert( \"Minute is even: \" + now.get(Calendar.MINUTE) );" +
			"end";
	}
	
	/**
	 * Gets a simple rule. 
	 * Rule determines whether the minutes of the hour is odd.
	 * Rule takes <code>java.util.Calendar</code> as fact (package import).
	 *  
	 * @return a Drools' rule
	 */
	private static String getSimpleRule2(String ruleName) {
        ruleName = (ruleName == null ? "HelloDroolsOdd" : ruleName);
		return 
            "rule \"" + ruleName + "\"" +
			"     when" +
			"          now: Calendar()" +
			"          eval ( ( now.get(Calendar.MINUTE) % 2 == 1 ) )" +
			"     then" +
			"          insert( \"Minute is odd: \" + now.get(Calendar.MINUTE) );" +
			"end";
	}

	public static RuleSetDTO createRuleSet() {
    	RuleSetDTO ruleSet = new RuleSetDTO("TestName", "Test description", "DRL");
    	ruleSet.addHeader("import java.util.Calendar");
    	
    	RuleDTO rule1 = new RuleDTO("rule1", "A rule", getSimpleRule1("rule1"), "DRL");
    	RuleDTO rule2 = new RuleDTO("rule2", "A rule", getSimpleRule2("rule2"), "DRL");
    	ruleSet.addRule(rule1);
    	ruleSet.addRule(rule2);
    	ruleSet.setContent(ruleSet.buildContent());
    	return ruleSet;
    }
	
	public static byte[] createPackage(RuleSetDTO ruleSet) throws Exception {
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(new StringReader(ruleSet.getContent()));
        // Deserialize a Drools package
		return DroolsUtil.deserialize( builder.getPackage() );
	}

	public static FactResultDTO createFactResult(String[] values) {
		FactResultDTO factResult = new FactResultDTO();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		for (String item : values) {
			Map<String, String> row = new HashMap<String, String>();
			row.put("column1", item);
			resultList.add(row);
		}

		factResult.setResultList(resultList);
		return factResult;
	}

	public static FactResultTypeInfoDTO createColumnMetaData(String dataType) {
    	Map<String, FactResultColumnInfoDTO> columnsInfoMap = new HashMap<String, FactResultColumnInfoDTO>();
    	FactResultColumnInfoDTO columnInfo = new FactResultColumnInfoDTO();
    	columnInfo.setKey("column1");
    	columnInfo.setDataType(dataType);
    	columnsInfoMap.put(columnInfo.getKey(), columnInfo);
    	FactResultTypeInfoDTO typeInfo = new FactResultTypeInfoDTO();
    	typeInfo.setResultColumnsMap(columnsInfoMap);
    	return typeInfo;
    }

	public static RuleSetDTO getAverageIntersectionPropositionRuleSet() {
    	RuleSetDTO ruleSet = new RuleSetDTO("TestName", "Test description", "DRL");

		String drl = 
		"package org.kuali.student.rules.CPR101 \n" +
		"import java.util.*; \n" +
		"import java.math.BigDecimal; \n" +
		"import org.slf4j.Logger; \n" +
		"import org.slf4j.LoggerFactory; \n" +
		"import org.kuali.student.rules.internal.common.entity.*; \n" +
		"import org.kuali.student.rules.internal.common.statement.propositions.*; \n" +
		"import org.kuali.student.rules.internal.common.statement.yvf.*; \n" +
		"import org.kuali.student.rules.rulemanagement.dto.*; \n" +
		"import org.kuali.student.rules.util.FactContainer; \n" +
		"import org.kuali.student.rules.util.FactContainer.State; \n" +
		"import org.kuali.student.rules.util.CurrentDateTime; \n" +
		"import org.kuali.student.rules.internal.common.utils.BusinessRuleUtil; \n" +
		"\n" +
		"rule \"CPR101_INIT\" \n" +
		"    no-loop true \n" +
		"    lock-on-active true \n" +
		"\n" +
		"    when \n" + 
		"        container : FactContainer( id == \"CPR101\", state == FactContainer.State.INIT, prop : propositionContainer, factMap : factMap ) \n" +
		"        //Only run rule when: currentDate >= effectiveStartTime AND currentDate < effectiveEndTime \n" +
		//"        CurrentDateTime( currentDateAsLong >= 20000101120010 && currentDateAsLong < 20010101120010 ) \n" +
		"    then \n" +
		"\n" +
		"        prop.setFunctionalRuleString(\"P1*P2\"); \n" +
		"\n" +
		"        String uuidP1 = \"P1-a4f5b5b6-c7e3-4abf-abf6-f68efec26785\"; \n" +
		"        YVFAverageProposition<java.math.BigDecimal> propositionP1 = new YVFAverageProposition<java.math.BigDecimal>( \n" +
		"            uuidP1, \"P1\", ComparisonOperator.EQUAL_TO, new BigDecimal(80.0),  \n" +
		"            factMap.get( \"FactKey1\" ) ); \n" +
		"        propositionP1.apply(); \n" +
		"        prop.addProposition(propositionP1.getProposition()); \n" +
		"        insert(propositionP1.getProposition()); \n" +
		"        container.setState(FactContainer.State.DONE); \n" +
		"        update(container); \n" +
		"\n" +
		"        String uuidP2 = \"P2-a4f5b5b6-c7e3-4abf-abf6-f68efec26785\"; \n" +
		"        YVFIntersectionProposition<java.lang.Integer> propositionP2 = new YVFIntersectionProposition<java.lang.Integer>( \n" +
		"            uuidP2, \"P2\", ComparisonOperator.EQUAL_TO, new Integer(1),  \n" +
		"            factMap.get( \"CriteriaKey2\" ), factMap.get( \"FactKey2\" ) ); \n" +
		"        propositionP2.apply(); \n" +
		"        prop.addProposition(propositionP2.getProposition()); \n" +
		"        insert(propositionP2.getProposition()); \n" +
		"        container.setState(FactContainer.State.DONE); \n" +
		"        update(container); \n" +
		"end \n" +

		"rule \"CPR101\" \n" +
		"    when \n" +
		"        factContainer : FactContainer( id == \"CPR101\", state == FactContainer.State.DONE, prop : propositionContainer) \n" +
		"        P1 : Proposition( id == \"P1-a4f5b5b6-c7e3-4abf-abf6-f68efec26785\" ) \n" +
		"        P2 : Proposition( id == \"P2-a4f5b5b6-c7e3-4abf-abf6-f68efec26785\" ) \n" +
		"\n" +
		"        exists \n" +
		"        ( \n" +
		"            Proposition( id == \"P1-a4f5b5b6-c7e3-4abf-abf6-f68efec26785\" && result == true ) \n" +
		"            && \n" +
		"            Proposition( id == \"P2-a4f5b5b6-c7e3-4abf-abf6-f68efec26785\" && result == true ) \n" +
		"        ) \n" +
		"    then \n" +
		"        prop.setRuleResult(true); \n" +
		//"        retract(factContainer); \n" +
		"        retract(P1); \n" +
		"        retract(P2); \n" +
		"end \n";

    	ruleSet.setContent(drl);
    	return ruleSet;
	}
}
