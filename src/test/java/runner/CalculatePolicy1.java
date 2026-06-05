package runner;

import api.Login;
import api.Calculet;
import api.Import;
import api.Issue;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ Login.class, Calculet.class, Import.class, Issue.class })
public class CalculatePolicy1 {
}