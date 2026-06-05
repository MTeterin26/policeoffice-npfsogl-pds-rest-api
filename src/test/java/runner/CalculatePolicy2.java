package runner;

import api.Calculet;
import api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ Calculet.class, Test.class })
public class CalculatePolicy2 {
}