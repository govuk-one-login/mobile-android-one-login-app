package uk.gov.idcheck.ui.presentation.permission.camera.errors

import javax.inject.Inject
import uk.gov.idcheck.features.api.permissions.PermissionStateHandler
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.Logger

@CameraPermissionErrorCallback
class CameraPermissionErrorGrantedCallback
@Inject
constructor(
//        private val launcher: DirectionsLauncher,
    private val logger: Logger
) : PermissionStateHandler.Granted,
    LogTagProvider {
    override fun onPermissionGranted() {
        logger.error(
            tag,
            "Permission granted after reaching camera error as no operation to perform!"
        )
    }
}
