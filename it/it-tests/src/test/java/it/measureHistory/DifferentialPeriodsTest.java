package it.measureHistory;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarRunner;
import com.sonar.orchestrator.locator.FileLocation;
import com.sonar.orchestrator.selenium.Selenese;
import it.Category1Suite;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import util.ItUtils;
import util.selenium.SeleneseTest;

import static org.apache.commons.lang.time.DateUtils.addDays;
import static org.assertj.core.api.Assertions.assertThat;
import static util.ItUtils.formatDate;
import static util.ItUtils.projectDir;
import static util.ItUtils.setServerProperty;

public class DifferentialPeriodsTest {

  static final String PROJECT_KEY = "sample";

  @ClassRule
  public static final Orchestrator orchestrator = Category1Suite.ORCHESTRATOR;

  @Before
  public void cleanUpAnalysisData() {
    orchestrator.resetData();
  }

  @Before
  public void initPeriods() throws Exception {
    setServerProperty(orchestrator, "sonar.timemachine.period1", "previous_analysis");
    setServerProperty(orchestrator, "sonar.timemachine.period2", "previous_analysis");
    setServerProperty(orchestrator, "sonar.timemachine.period3", "previous_analysis");
  }

  @After
  public void resetPeriods() throws Exception {
    ItUtils.resetPeriods(orchestrator);
  }

  /**
   * SONAR-6787
   */
  @Test
  public void ensure_differential_period_4_and_5_defined_at_project_level_is_taken_into_account() throws Exception {
    orchestrator.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);
    setServerProperty(orchestrator, PROJECT_KEY, "sonar.timemachine.period4", "30");
    setServerProperty(orchestrator, PROJECT_KEY, "sonar.timemachine.period5", "previous_analysis");

    // Execute an analysis in the past to have a past snapshot without any issues
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "xoo", "empty");
    orchestrator.executeBuild(SonarRunner.create(projectDir("shared/xoo-sample"))
      .setProperty("sonar.projectDate", formatDate(addDays(new Date(), -60))));

    // Second analysis -> issues will be created
    orchestrator.getServer().restoreProfile(FileLocation.ofClasspath("/measureHistory/one-issue-per-line-profile.xml"));
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "xoo", "one-issue-per-line");
    orchestrator.executeBuild(SonarRunner.create(projectDir("shared/xoo-sample")));

    // New technical debt only comes from new issues
    Resource newTechnicalDebt = orchestrator.getServer().getWsClient()
      .find(ResourceQuery.createForMetrics("sample:src/main/xoo/sample/Sample.xoo", "new_technical_debt").setIncludeTrends(true));
    List<Measure> measures = newTechnicalDebt.getMeasures();
    assertThat(measures.get(0).getVariation4()).isEqualTo(17);
    assertThat(measures.get(0).getVariation5()).isEqualTo(17);

    // Third analysis, with exactly the same profile -> no new issues so no new technical debt
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "xoo", "one-issue-per-line");
    orchestrator.executeBuild(SonarRunner.create(projectDir("shared/xoo-sample")));

    newTechnicalDebt = orchestrator.getServer().getWsClient().find(
      ResourceQuery.createForMetrics("sample:src/main/xoo/sample/Sample.xoo", "new_technical_debt").setIncludeTrends(true)
      );

    // No variation => measure is purged
    assertThat(newTechnicalDebt).isNull();
  }

  /**
   * SONAR-7093
   */
  @Test
  public void ensure_leak_period_defined_at_project_level_is_taken_into_account() throws Exception {
    orchestrator.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);

    // Set a global property and a project property to ensure project property is used
    setServerProperty(orchestrator, "sonar.timemachine.period1", "previous_analysis");
    setServerProperty(orchestrator, PROJECT_KEY, "sonar.timemachine.period1", "30");

    // Execute an analysis in the past to have a past snapshot without any issues
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "xoo", "empty");
    orchestrator.executeBuild(SonarRunner.create(projectDir("shared/xoo-sample"))
      .setProperty("sonar.projectDate", formatDate(addDays(new Date(), -15))));

    // Second analysis -> issues will be created
    orchestrator.getServer().restoreProfile(FileLocation.ofClasspath("/measureHistory/one-issue-per-line-profile.xml"));
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "xoo", "one-issue-per-line");
    orchestrator.executeBuild(SonarRunner.create(projectDir("shared/xoo-sample")));

    // Third analysis -> There's no new issue from previous analysis
    orchestrator.executeBuild(SonarRunner.create(projectDir("shared/xoo-sample")));

    // Project should have 17 new issues for period 1
    Resource newTechnicalDebt = orchestrator.getServer().getWsClient()
      .find(ResourceQuery.createForMetrics(PROJECT_KEY, "violations").setIncludeTrends(true));
    List<Measure> measures = newTechnicalDebt.getMeasures();
    assertThat(measures.get(0).getVariation1()).isEqualTo(17);

    // Check on ui that it's possible to define leak period on project
    new SeleneseTest(Selenese.builder().setHtmlTestsInClasspath("define-leak-period-on-project",
      "/measureHistory/DifferentialPeriodsTest/define-leak-period-on-project.html"
    ).build()).runOn(orchestrator);
  }

  /**
   * SONAR-4700
   */
  @Test
  public void not_display_periods_selection_dropdown_on_first_analysis() {
    orchestrator.getServer().provisionProject(PROJECT_KEY, PROJECT_KEY);
    orchestrator.getServer().associateProjectToQualityProfile(PROJECT_KEY, "xoo", "empty");
    orchestrator.executeBuild(SonarRunner.create(projectDir("shared/xoo-sample")));

    // Use old way to execute Selenium because 'assertSelectOptions' action is not supported by SeleneseTest
    orchestrator.executeSelenese(Selenese.builder().setHtmlTestsInClasspath("not-display-periods-selection-dropdown-on-first-analysis",
      "/measureHistory/DifferentialPeriodsTest/not-display-periods-selection-dropdown-on-dashboard.html"
      ).build());

    orchestrator.executeBuild(SonarRunner.create(projectDir("shared/xoo-sample")));

    orchestrator.executeSelenese(Selenese.builder().setHtmlTestsInClasspath("display-periods-selection-dropdown-after-first-analysis",
      "/measureHistory/DifferentialPeriodsTest/display-periods-selection-dropdown-on-dashboard.html"
      ).build());
  }

}
