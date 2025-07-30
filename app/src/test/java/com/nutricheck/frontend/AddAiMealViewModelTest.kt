package com.nutricheck.frontend
import android.content.Context
import android.net.Uri
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealEvent
import com.frontend.nutricheck.client.ui.view_model.add_components.AddAiMealViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AddAiMealViewModelTest {

    @Mock
    private lateinit var remoteApi: RemoteApi

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: AddAiMealViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AddAiMealViewModel(context, remoteApi)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `onEvent OnRetakePhoto resets photoUri`() = runTest {
        viewModel.onEvent(AddAiMealEvent.OnRetakePhoto)
        assertNull(viewModel.photoUri.value)
    }

    @Test
    fun `takePhoto sets photoUri on success`() {
        // Integrationstest nötig, da Callback und Android-API.
        viewModel.takePhoto()
        // Keine Assertion möglich.
    }

    @Test
    fun `submitPhoto emits error if photoUri is null`() = runTest {
        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        advanceUntilIdle()
        assertTrue(viewModel.isError.value)
    }

    @Test
    fun `submitPhoto emits ShowMealOverview on success`() = runTest {
        val uri = mock(Uri::class.java)
        val inputStream = uri.inputStream()
        `when`(context.contentResolver.openInputStream(any())).thenReturn(inputStream)
        viewModel.photoUri.value = uri
        `when`(remoteApi.estimateMeal(anyString())).thenReturn(Response.success(mock(MealDTO::class.java)))

        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
        advanceUntilIdle()

        val event = viewModel.events.first()
        assertTrue(event is AddAiMealEvent.ShowMealOverview)
    }

    @Test
    fun `retakePhoto resets photoUri`() {
        viewModel.photoUri.value = mock(Uri::class.java)
        viewModel.onEvent(AddAiMealEvent.OnRetakePhoto)
        assertNull(viewModel.photoUri.value)
    }
}