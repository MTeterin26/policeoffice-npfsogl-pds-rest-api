package runner;

import test.Login;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import test.Test;

@RunWith(Suite.class)
@Suite.SuiteClasses({ Login.class, Test.class })
public class CalculatePolicy1 {
}