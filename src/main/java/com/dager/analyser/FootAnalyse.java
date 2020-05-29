package com.dager.analyser;

/**
 * FootAnalyse {@link AnalyseBuilder}
 * 具体使用模板:
 * <p>
 *     <blockquote><pre>
 *     QueryRequest request = new QueryRequest();
 *     request.setSome();
 *     RuleCompareDTO ruleDTO =
 *                 RuleCompareDTO.builder().someField.build();
 *     FootAnalyse
 *            .setRequest(request, dataSourceService::queryDataByPage)
 *            .setThread(5, 10, 10)
 *            .setRuleAndParam(ruleDTO,...rules)
 *             .setAnalyseFunction(analyseTest)
 *             .doHandle();
 *  </pre></blockquote>
 *  </p>
 *
 * @author dager
 */
public class FootAnalyse extends AnalyseBuilder {
}
