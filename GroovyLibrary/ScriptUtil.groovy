package sl.testauto.utils

import com.eviware.soapui.impl.wsdl.teststeps.RestResponseMessageExchange
import com.eviware.soapui.model.testsuite.TestRunner
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.support.GroovyUtils

/*
* Contains Script utilities for ReadyApi
*
* @author nassemoh
*
*/

class ScriptUtil {

  private Boolean debugOn
  private Object log
  private TestRunner testRunner


  /*------------------------------------------------------------------------------------------------------------------------------------------
  METHOD: CONSTRUCTOR    S T A R T
  -------------------------------------------------------------------------------------------------------------------------------------------*/

  public ScriptUtil(Object log, TestRunner testRunner, Boolean debugOn = false) {
    this.log = log
    this.testRunner = testRunner
    this.debugOn = debugOn
  }

  /*------------------------------------------------------------------------------------------------------------------------------------------
  METHOD: E N D
  -------------------------------------------------------------------------------------------------------------------------------------------*/


  public static void setTestSuiteProps(Object log, TestRunner testRunner, String myTS, Map propMap) {
    if (propMap) {
      propMap.each() { it ->
        log.info "Set prop: {$it.key} value: {$it.value}"
        testRunner.testCase.testSuite.project.testSuites[myTS].setPropertyValue(it.key as String, it.value as String)
      }
    }
  }

  /*------------------------------------------------------------------------------------------------------------------------------------------
  METHOD: setTestStepProps / setTestStepPropsLog     S T A R T
  -------------------------------------------------------------------------------------------------------------------------------------------*/

  public void setTestStepProps(String myTestStep, Map propMap, Boolean sortPropMapOnValue = false) {
    doSetTestStepProps(this.log, this.testRunner, myTestStep, propMap, sortPropMapOnValue)
  }


  public static void setTestStepProps(Object log, TestRunner testRunner, String myTestStep, Map propMap, Boolean sortPropMapOnValue = false) {
    doSetTestStepProps(log, testRunner, myTestStep, propMap, sortPropMapOnValue)
  }

  private void doSetTestStepProps(Object log, TestRunner testRunner, String myTestStep, Map propMap, Boolean sortPropMapOnValue = false) {
    Map sortedMap = [:]
    sortedMap = propMap.sort { it.value }
    if (sortPropMapOnValue) {
      propMap = sortedMap
    }
    if (propMap) {
      propMap.each() { it ->
        testRunner.testCase.testSteps[myTestStep].setPropertyValue(it.key as String, it.value as String)
      }
      if (this.debugOn) { log.info "Test step: $myTestStep propterties updated!" }
    }
  }

  /*------------------------------------------------------------------------------------------------------------------------------------------
  METHOD: E N D
  -------------------------------------------------------------------------------------------------------------------------------------------*/

  /**
  * getAPropertyValue on soapUI project, suite, testcase level. (Note! Only within the current project/suite or calling testcase)
  * @param testRunner the testRunner object from soapUI
  * @param level the level in soapUI project
  * @param stepName the name of the step (if level is teststep/step)
  * @return the value of the property on the specified level
  */
  public static String getAPropertyValue(TestRunner testRunner, String propName, String level, String stepName = '')  {
    switch (level) {
      case 'project':
      Object project = testRunner.testCase.testSuite.getProject()
      return project.getPropertyValue(propName)
      break
      case 'testsuite': case 'suite':
      Object ts = testRunner.testCase.testSuite
      return ts.getPropertyValue(propName)
      break
      case 'testcase':
      Object tc = testRunner.testCase
      return tc.getPropertyValue(propName)
      break
      case 'teststep': case 'step':
      Object stp = testRunner.testCase.testSteps[stepName]
      return stp.getPropertyValue(propName)
      break
      default: break
    }
  }

  public static void removeTestStepProperties(Object log, TestRunner testRunner, String stepName) {
    WsdlProject project = testRunner.testCase.testSuite.getProject()
    log.info "Clear all properties for testStep: ${stepName}"
    def tstp = testRunner.testCase.testSteps[stepName]
    tstp.getProperties().keySet().each { key ->
      tstp.removeProperty(key)
    }
  }

  public static Boolean checkPropertyValue(WsdlProject project, String propName, String expectedValue) {
    return project.getPropertyValue(propName).equals(expectedValue)
  }

  public static Integer listsDifference(Object log, List refList, List retList) {
    Integer cnt = 0
    refList.each { refItem ->
      Boolean found = false
      if (refItem in retList) { found = true }
      else  { log.error "Diff found! Missing REF Item  in RET  List. Ref Item: $refItem"; cnt++ }
    }
    if (cnt > 0) { log.error "Total: $cnt missing REF item(s)" }
    return cnt
  }

  public void setTestCaseParam(String matchTcNameStr, Map propsMap) {
      doSetTestCaseParam(this.log, this.testRunner, matchTcNameStr, propsMap)
  }

  public static void setTestCaseParam(Object log, TestRunner testRunner, String matchTcNameStr, Map propsMap) {
    doSetTestCaseParam(log, testRunner, matchTcNameStr, propsMap)
  }

  private void doSetTestCaseParam(Object log, TestRunner testRunner, String matchTcNameStr, Map propsMap) {
      List tcList = testRunner.testCase.testStepList
      tcList.each { tsp ->
        Boolean tspTreated = false
        if (!tspTreated && tsp.name.contains(matchTcNameStr)) {
          doSetTestStepProps(log, testRunner, tsp.name, propsMap)
          tspTreated = true
      }
    }
  }


} /*END ScriptUtil*/

/*------------------------------------------------------------------------------------------------------------------------------
*                                                   E N D
*------------------------------------------------------------------------------------------------------------------------------*/
