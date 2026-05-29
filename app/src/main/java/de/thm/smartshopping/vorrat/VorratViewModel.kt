package de.thm.smartshopping.vorrat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class VorratViewModel : ViewModel() {

	var count by mutableIntStateOf(0)

	fun incrementCount() {
		count++
	}
}