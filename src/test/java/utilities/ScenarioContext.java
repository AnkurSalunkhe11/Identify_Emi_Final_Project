package utilities;

import io.cucumber.java.Scenario;

/**
 * Shared context to hold the current Cucumber Scenario.
 * This allows step definitions to access the scenario for attaching screenshots.
 * PicoContainer can inject this class into both Hooks and StepDefinitions.
 */
public class ScenarioContext {
    private Scenario scenario;

    public ScenarioContext() {
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public Scenario getScenario() {
        return scenario;
    }
}

