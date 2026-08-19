package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CountryDatabase
import com.example.data.model.NewsCategories
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RAYON", appName)
  }

  @Test
  fun `verify category list contains 26 categories`() {
    assertTrue(NewsCategories.ALL.size >= 26)
    assertNotNull(NewsCategories.getByName("Artificial Intelligence"))
    assertNotNull(NewsCategories.getByName("India"))
  }

  @Test
  fun `verify country database contains default countries and subdivisions`() {
    val india = CountryDatabase.getByName("India")
    assertEquals("India", india.name)
    assertTrue(india.subdivisions.contains("Tamil Nadu"))
    assertTrue(india.subdivisions.contains("Karnataka"))
  }
}
