package sl.testauto.utils

import com.eviware.soapui.impl.wsdl.teststeps.RestResponseMessageExchange
import com.eviware.soapui.model.testsuite.TestRunner
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.support.GroovyUtils
import groovy.json.*
import com.jayway.jsonpath.JsonPath

/*
* Contains JSONPATH utilities for ReadyApi
*
* @author nassemoh
*
*/

class JsonHandler {

  /* -------------------------------------------------------------------------------------------------------------- */
  /** P U B L I C
  *
  * @param jsonObject
  * @return
  * Anrop från Groovy Script steg
  */
  public static Object parseJson(String jsonObject) {
    assert jsonObject != null, "*** Specified JSON is NULL!"
    Object parsedJson = new JsonSlurper().parseText(jsonObject)
    return parsedJson
  }

  //-> Anrop från Groovy Script steg
  public static Object parseJson(Object testRunner, String testStepName) {
    Object ts = testRunner.testCase.getTestStepByName(testStepName)
    String resp = ts.getProperty("Response").value
    assert resp != null, "*** Specified JSON is NULL!"
    Object parsedJson = new JsonSlurper().parseText(resp)
    return parsedJson
  }

  //-> Anrop från Groovy Script steg
  public static Object parseJson(Object testRunner, Object testStep) {
    String resp = testStep.getProperty("Response").value
    assert resp != null, "*** Specified JSON is NULL!"
    Object parsedJson = new JsonSlurper().parseText(resp)
    return parsedJson
  }

  //-> Anrop från Groovy Script steg
  public static Object parseJson(RestResponseMessageExchange messageExchange) {
    String respContent = messageExchange.response.responseContent
    assert respContent != null, "*** Specified JSON is NULL!"
    Object parsedJson = new JsonSlurper().parseText(respContent)
    return parsedJson
  }
  /* -------------------------------------------------------------------------------------------------------------- */
  /** P U B L I C
  *
  * @param messageExchange
  * @param jsonPath
  * @param element
  * @return
  */
  public static Integer getNumberOfJsonElements(RestResponseMessageExchange messageExchange, String jsonPath, String element = "") {
    return doGetNumberOfJsonElements(parseJson(messageExchange), fixJsonPathWithElement(jsonPath, element))
  }

  //-> Anrop från Groovy Script steg
  public static Integer getNumberOfJsonElements(Object testRunner, String testStepName, String jsonPath, String element = "") {
    return doGetNumberOfJsonElements(parseJson(testRunner, testStepName), fixJsonPathWithElement(jsonPath, element))
  }

  //-> Anrop från Groovy Script steg
  public static Integer getNumberOfJsonElements(String jsonStr, String jsonPath, String element = "") {
    return doGetNumberOfJsonElements(parseJson(jsonStr), fixJsonPathWithElement(jsonPath, element))
  }

  /* -------------------------------------------------------------------------------------------------------------- */
  //-> Anrop från Groovy Script steg
  public static String getJsonValue(Object testRunner, String testStepName, String jsonPath, String element = "") {
    return doGetJsonValue(parseJson(testRunner, testStepName), fixJsonPathWithElement(jsonPath, element), false)
  }

  public static String getJsonValue(RestResponseMessageExchange messageExchange, String jsonPath, String element = "") {
    return doGetJsonValue(parseJson(messageExchange), fixJsonPathWithElement(jsonPath, element), false)
  }

  public static String getJsonValue(String jsonStr, String jsonPath, String element = "") {
    return doGetJsonValue(parseJson(jsonStr), fixJsonPathWithElement(jsonPath, element), false)
  }

  public static List getJsonValueList(Object testRunner, String testStepName, String jsonPath, String element = "", Boolean sortValueList = true) {
    return doGetJsonValue(parseJson(testRunner, testStepName), fixJsonPathWithElement(jsonPath, element), sortValueList)
  }

  //-> Anrop från Script Assertion
  public static List getJsonValueList(RestResponseMessageExchange messageExchange, String jsonPath, String element = "", Boolean sortValueList = true) {
    return doGetJsonValue(parseJson(messageExchange), fixJsonPathWithElement(jsonPath, element), sortValueList)
  }

  //-> Anrop från Script Assertion
  public static List getJsonValueList(String jsonStr, String jsonPath, String element = "", Boolean sortValueList = true) {
    return doGetJsonValue(parseJson(jsonStr), fixJsonPathWithElement(jsonPath, element), sortValueList)
  }

  //-> Anrop från Groovy Script steg
  public static Boolean isJsonValueListsEqual(Object testRunner, String testStepName, List expectedValueList, String jsonPath, String element = "", Boolean sortValueList = true) {
    List retList = JsonPath.read(parseJson(testRunner, testStepName), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
    compareLists(retList, expectedValueList, sortValueList)
  }

  //-> Anrop från Script Assertion
  public static Boolean isJsonValueListsEqual(RestResponseMessageExchange messageExchange, List expectedValueList, String jsonPath, String element = "", Boolean sortValueList = true) {
    List retList = JsonPath.read(parseJson(messageExchange), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
    compareLists(retList, expectedValueList, sortValueList)
  }

  //-> Anrop från Groovy Script steg / Script Assertion
  public static Boolean isJsonValueListsEqual(String jsonStr, List expectedValueList, String jsonPath, String element = "", Boolean sortValueList = true) {
    List retList = JsonPath.read(parseJson(jsonStr), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
    compareLists(retList, expectedValueList, sortValueList)
  }

  /* -------------------------------------------------------------------------------------------------------------- */

  //-> Anrop från Groovy Script steg / Script Assertion
  public static Boolean containsJsonValueString(Object testRunner, String testStepName, String str, String jsonPath, String element = "", Boolean caseSensitive = true) {
    List retList = getJsonValueList(testRunner, testStepName, jsonPath, element)
    containsValueListString(retList, str, caseSensitive)
  }

  public static Boolean containsJsonValueString(RestResponseMessageExchange messageExchange, String str, String jsonPath, String element = "", Boolean caseSensitive = true) {
    List retList = getJsonValueList(messageExchange, jsonPath, element)
    containsValueListString(retList, str, caseSensitive)
  }

  public static Boolean containsJsonValueString(String jsonStr, String str, String jsonPath, String element = "", Boolean caseSensitive = true) {
    List retList = getJsonValueList(jsonStr, jsonPath, element)
    containsValueListString(retList, str, caseSensitive)
  }

  /* -------------------------------------------------------------------------------------------------------------- */
  public static Boolean containsJsonValuePattern(Object testRunner, String testStepName, String pattern, String jsonPath, String element = "") {
    List retList = getJsonValueList(testRunner, testStepName, jsonPath, element)
    return matchListPattern(retList, pattern)
  }

  public static Boolean containsJsonValuePattern(RestResponseMessageExchange messageExchange, String pattern, String jsonPath, String element = "") {
    List retList = getJsonValueList(messageExchange, jsonPath, element)
    return matchListPattern(retList, pattern)
  }

  public static Boolean containsJsonValuePattern(String jsonStr, String pattern, String jsonPath, String element = "") {
    List retList = getJsonValueList(jsonStr, jsonPath, element)
    return matchListPattern(retList, pattern)
  }

  /* -------------------------------------------------------------------------------------------------------------- */
  public static Boolean containsValueListString(List valueList, String str, Boolean caseSensitive = true) {
    Boolean checkOk = false
    if (valueList) {
      valueList.each { String entry ->
        if (!caseSensitive && entry.toLowerCase().contains(str.toLowerCase())) {
          checkOk = true
          } else if (caseSensitive && entry.contains(str)) {
            checkOk = true
          }
        }
      }
      return checkOk
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /* Exempel:
    def str = "Standard cdef abcd efg"
    matchStringPattern(str, "(?i).*stand.*") //true  case insensitive
    matchStringPattern(str, "(?i).*Stand.*") //true  case insensitive
    matchStringPattern(str, ".*Stand.*") //true case sensitive
    matchStringPattern(str, "Stand.*") //false
    matchStringPattern(str, "(?i).*Stand.*abcd*efg") //true
    */
    public static Boolean matchStringPattern(String inStr, String pattern) {
      return inStr.matches(pattern)
    }

    public static Boolean matchListPattern(List inList, String pattern) {
      Boolean checkOk = false
      inList.each { String entry ->
        checkOk = matchStringPattern(entry, pattern)
        return checkOk
      }
      return checkOk
    }
    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */
    public static Boolean checkJsonPathExists(RestResponseMessageExchange messageExchange, String jsonPath, String element = "") {
      List retList = JsonPath.read(parseJson(messageExchange), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
      if (retList) {
        return true
      }
      return false
    }

    public static Boolean checkJsonPathExists(Object testRunner, String testStepName, String jsonPath, String element = "") {
      List retList = JsonPath.read(parseJson(testRunner, testStepName), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
      if (retList) {
        return true
      }
      return false
    }

    public static Boolean checkJsonPathExists(String jsonStr, String jsonPath, String element = "") {
      List retList = JsonPath.read(parseJson(jsonStr), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
      if (retList) {
        return true
      }
      return false
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    //-> Anrop från Groovy
    public static Object getJsonObject(Object testRunner, String testStepName, String jsonPath, String element = "") {
      return JsonPath.read(parseJson(testRunner, testStepName), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
    }

    //-> Anrop från Script Assertion
    public static Object getJsonObject(RestResponseMessageExchange messageExchange, String jsonPath, String element = "") {
      return JsonPath.read(parseJson(messageExchange), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
    }

    //-> Anrop från Script Assertion / Groovy Script steg
    public static Object getJsonObject(String jsonStr, String jsonPath, String element = "") {
      return JsonPath.read(parseJson(jsonStr), fixJsonPath(fixJsonPathWithElement(jsonPath, element)))
    }
    /* -------------------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                                                                                                */
    /* -------------------------------------------------------------------------------------------------------------- */


    /*------------------------------------------------------------------------------------------------------------------------------
    *  -----START------                    P R I V A T E       F U N C T I O N S
    *------------------------------------------------------------------------------------------------------------------------------*/
    private static Object doGetJsonValue(Object parsedJson, String jsonPath, Boolean sortValueList) {
      Object retVal = JsonPath.read(parsedJson, fixJsonPath(jsonPath))
      if (retVal instanceof ArrayList && sortValueList) {
        return retVal.sort()
      }
      return retVal
    }

    private static Integer doGetNumberOfJsonElements(Object parsedJson, String jsonPath) {
      Object ret = JsonPath.read(parsedJson, fixJsonPath(jsonPath))
      if (ret instanceof ArrayList) {
        return ret.size()
        } else if (ret && ret instanceof String) {
          return 1
        }
        return 0
      }

      private static String fixJsonPathWithElement(String jsonPath, String element) {
        if (element != "") {
          jsonPath = "$jsonPath.$element"
        }
        return jsonPath
      }

      private static Boolean compareLists(List currList, List refList, Boolean sortValueList = true) {
        if (sortValueList && currList.sort() == refList.sort()) {
          return true
          } else if (!sortValueList && currList == refList) {
            return true
          }
          return false
        }

        /** P R I V A T E
        *
        * @param inJsPath
        * @return
        */
        private static String fixJsonPath(String inJsPath) {
          Boolean rootSignWithDotExist = (inJsPath.substring(0,2) == "\$." || inJsPath.substring(0,2) == "\$..")
          Boolean rootSignExists = inJsPath.substring(0,1) == "\$"

          if (rootSignWithDotExist) {
            return inJsPath
            } else if (rootSignExists) {
              def root = inJsPath.substring(0,1)
              def subStr = inJsPath.substring(1, inJsPath.size())
              return "\$.$subStr"
              return subStr
              } else {
                return "\$..$inJsPath"
              }
            }

            /*------------------------------------------------------------------------------------------------------------------------------
            *   -----END------                  P R I V A T E       F U N C T I O N S
            *------------------------------------------------------------------------------------------------------------------------------*/


          }
          /*------------------------------------------------------------------------------------------------------------------------------
          *                                                   E N D
          *------------------------------------------------------------------------------------------------------------------------------*/
