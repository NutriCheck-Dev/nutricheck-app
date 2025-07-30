package com.nutricheck.frontend
import android.content.Context
import android.net.Uri
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteApi
import com.frontend.nutricheck.client.model.data_sources.remote.RemoteRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AddAiMealViewModelTest {

    @Mock
    private lateinit var remoteRepository: RemoteRepository

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: AddAiMealViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = AddAiMealViewModel(context, remoteRepository)
        Dispatchers.setMain(testDispatcher)
    }

//    @Test
//    fun `onEvent OnRetakePhoto resets photoUri`() = runTest {
//        viewModel.onEvent(AddAiMealEvent.OnRetakePhoto)
//        assertNull(viewModel.photoUri.value)
//    }
//    @Test
//    fun `submitPhoto emits error if photoUri is null`() = runTest {
//        viewModel.onEvent(AddAiMealEvent.OnSubmitPhoto)
//        advanceUntilIdle()
//
//        val uiState = viewModel.uiState.value
//        if (uiState is BaseViewModel.UiState.Error) {
//            assertTrue(uiState.message == context.getString(R.string.error_encoding_image))
//        }
    }
