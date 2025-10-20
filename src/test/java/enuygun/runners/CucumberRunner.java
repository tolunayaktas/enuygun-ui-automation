package enuygun.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"enuygun.steps"},
        plugin = {
                "pretty",
                "html:target/reports/report.html",
                "json:target/reports/cucumber.json"
        },
        tags = "@smoke"
)
public class CucumberRunner {
}