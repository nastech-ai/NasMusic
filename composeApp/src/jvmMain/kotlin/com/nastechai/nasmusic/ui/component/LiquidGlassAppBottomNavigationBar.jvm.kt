package com.nastechai.nasmusic.ui.component

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nastechai.nasmusic.expect.ui.PlatformBackdrop
import com.nastechai.nasmusic.viewModel.SharedViewModel
import kotlin.reflect.KClass

@Composable
actual fun LiquidGlassAppBottomNavigationBar(
    startDestination: Any,
    navController: NavController,
    backdrop: PlatformBackdrop,
    viewModel: SharedViewModel,
    isScrolledToTop: Boolean,
    onOpenNowPlaying: () -> Unit,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit
) {
}