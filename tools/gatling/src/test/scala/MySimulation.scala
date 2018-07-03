import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.{http, jsonPath, status}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

class MySimulation extends Simulation {

  // val noOfUsers  = 3000 // ASYNC. Ger ca 750 req/s och < 10 ms svarstid
  // val noOfUsers  = 1500 // SYNC. Ger ca 400 req/s och lite drygt 10 ms svarstid
  // val rampUpTime = 120 seconds
  // val testTime   = 180 seconds
  // val minWaitMs  = 3000 milliseconds
  // val maxWaitMs  = 5000 milliseconds
  val noOfUsers = 3000 // SYNC. Ger ca 400 req/s och lite drygt 10 ms svarstid
  val rampUpTime = 30 seconds
  val testTime   = 60 seconds
  val minWaitMs  = 3000 milliseconds
  val maxWaitMs  = 5000 milliseconds

  private val log = LoggerFactory.getLogger("composite.CompositeTestScenario")

  val conf = ConfigFactory.load

  def httpHostname = conf.getString("callista.http.hostname")

  val baseUrl = "http://" + httpHostname + ":8084"
  val requestUrl = "/async/5"
//  val requestUrl = "/5"

  val httpConf = http
    .baseURL(baseUrl)
    .acceptHeader("application/json;charset=UTF-8;q=0.9,*/*;q=0.8")
    .warmUp(baseUrl + "/")

  val myScenario: ChainBuilder =
    exec(http("Composite Test")
      .get(requestUrl)
      .check(status.is(200))
//      .check(jsonPath("$").saveAs("result"))
    )
//    .exec(session => {
//      log.debug( "Result: {}", session( "result" ).as[String] )
//      session
//    })

  setUp(
    scenario("Test Scenario")
      .during(testTime) {
        exec(myScenario).pause(minWaitMs, maxWaitMs)
      }
      .inject(rampUsers(noOfUsers) over(rampUpTime))
      .protocols(httpConf)
  )
}