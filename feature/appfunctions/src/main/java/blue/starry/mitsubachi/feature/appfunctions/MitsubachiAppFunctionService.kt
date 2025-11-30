package blue.starry.mitsubachi.feature.appfunctions

import android.app.appfunctions.AppFunctionException
import android.app.appfunctions.AppFunctionService
import android.app.appfunctions.ExecuteAppFunctionRequest
import android.app.appfunctions.ExecuteAppFunctionResponse
import android.content.pm.SigningInfo
import android.os.CancellationSignal
import android.os.OutcomeReceiver
import timber.log.Timber

class MitsubachiAppFunctionService : AppFunctionService() {
  override fun onExecuteFunction(
    request: ExecuteAppFunctionRequest,
    callingPackage: String,
    callingPackageSigningInfo: SigningInfo,
    cancellationSignal: CancellationSignal,
    callback: OutcomeReceiver<ExecuteAppFunctionResponse?, AppFunctionException?>,
  ) {
    Timber.d("onExecuteFunction: calling from $callingPackage: $request")
  }
}
