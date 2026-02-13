package uk.gov.onelogin.core.ui.components

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.onelogin.core.FragmentActivityTestCase

@RunWith(AndroidJUnit4::class)
class FlexibleTopBarTest : FragmentActivityTestCase() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Test
    fun testNullScrollBehaviour() {
        composeTestRule.setContent {
            Scaffold(
                topBar = {
                    FlexibleTopBar(
                        scrollBehavior = null,
                    ) {
                        Text(text = APP_BAR_CONTENT)
                    }
                },
            ) {
                Text(text = SCREEN_CONTENT)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Test
    fun testNonPinnedScrollBehaviour() {
        composeTestRule.setContent {
            Scaffold(
                topBar = {
                    FlexibleTopBar(
                        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                    ) {
                        Text(text = APP_BAR_CONTENT)
                    }
                },
            ) {
                Text(text = SCREEN_CONTENT)
            }
        }
    }
}

private const val APP_BAR_CONTENT = "Top app bar content"
private const val SCREEN_CONTENT = "Screen content"
