package com.example.mobilecomputingassignment.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(content: @Composable ColumnScope.() -> Unit) {
  Surface(
          modifier = Modifier.fillMaxWidth().wrapContentHeight(),
          shape = MaterialTheme.shapes.large,
          tonalElevation = 3.dp
  ) {
    Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
      // Handle indicator
      Box(
              modifier =
                      Modifier.width(32.dp)
                              .height(4.dp)
                              .align(androidx.compose.ui.Alignment.CenterHorizontally),
              contentAlignment = androidx.compose.ui.Alignment.Center
      ) {
        Surface(
                modifier = Modifier.fillMaxSize(),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        ) {}
      }
      Spacer(modifier = Modifier.height(16.dp))
      content()
    }
  }
}
