package blue.starry.mitsubachi.feature.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import kotlin.math.sqrt

@Suppress("OutdatedDocumentation")
class ExampleFunctions {
  /**
   * Returns square root of [x].
   *
   * @param appFunctionContext The execution context of app function.
   * @param Double number.
   * @return Double square root of [x].
   */
  @AppFunction(isDescribedByKdoc = true)
  suspend fun squareRoot(
    @Suppress("UnusedParameter") appFunctionContext: AppFunctionContext,
    x: Double,
  ): Double {
    return sqrt(x)
  }
}
