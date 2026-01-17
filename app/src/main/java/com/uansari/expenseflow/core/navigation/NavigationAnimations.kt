package com.uansari.expenseflow.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

object NavigationAnimations {

    // Shared animation specs
    private const val DURATION_MEDIUM = 300
    private const val DURATION_SHORT = 200

    // Slide + Fade for main screens
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(DURATION_MEDIUM, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(DURATION_MEDIUM))
    }

    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(DURATION_MEDIUM, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(DURATION_SHORT))
    }

    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
        {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(DURATION_MEDIUM, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(DURATION_MEDIUM))
        }

    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
        {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(DURATION_MEDIUM, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(DURATION_SHORT))
        }

    // Vertical slide for dialogs/bottom sheets
    val dialogEnterTransition: EnterTransition = slideInVertically(
        initialOffsetY = { it / 2 },
        animationSpec = tween(DURATION_MEDIUM, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(DURATION_MEDIUM))

    val dialogExitTransition: ExitTransition = slideOutVertically(
        targetOffsetY = { it / 2 },
        animationSpec = tween(DURATION_SHORT, easing = FastOutLinearInEasing)
    ) + fadeOut(animationSpec = tween(DURATION_SHORT))
}