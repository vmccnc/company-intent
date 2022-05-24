package com.app.intent.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.intent.api.model.Repo
import com.app.intent.ui.components.ErrorUI
import com.app.intent.ui.components.LoadingUI
import com.app.intent.utils.height
import com.app.intent.vm.UserViewModel


@Composable
fun SearchScreen(
    vm: UserViewModel = hiltViewModel()
) {

    val tablets by vm.userDmesLive.observeAsState(listOf())
    val isLoading: Boolean by vm.isLoading.observeAsState(false)
    val isError: Boolean by vm.isError.observeAsState(false)
//    vm.loadTablets()

    val searchWidgetState by vm.searchWidgetState
    val searchTextState by vm.searchTextState

    val context = LocalContext.current

    Scaffold(
        topBar = {
            MainAppBar(
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = {
                    vm.updateSearchTextState(newValue = it)
                },
                onCloseClicked = {
                    vm.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
//                    vm.loadUsers()
                    vm.userDmesLive.value = listOf()
                },
                onSearchClicked = {
                    Log.d("Searched Text", it)
                    vm.loadUsers(it)
                },
                onSearchTriggered = {
                    vm.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                }
            )
        },
        content = {
            when {
                isLoading -> LoadingUI()
                isError -> ErrorUI()
                else -> {

                    if (tablets.isEmpty()) {
                        Text(
                            "Enter your request. Or change it",
                            Modifier
                                .fillMaxSize()
                                .wrapContentHeight(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn() {
                            items(tablets) { list ->
                                RepoItem(list) {
                                    val intent =
                                        Intent(Intent.ACTION_VIEW, Uri.parse("${it.html_url}"))
                                    context.startActivity(intent)
                                }
                            }
                        }

                    }
                }
            }
        }
    )
}


enum class SearchWidgetState {
    OPENED, CLOSED
}

@Composable
fun MainAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> DefaultAppBar(onSearchClicked = onSearchTriggered)
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

@Composable
fun DefaultAppBar(onSearchClicked: () -> Unit) =
    TopAppBar(
        title = { Text("Search page") },
        actions = {
            IconButton(
                onClick = { onSearchClicked() },
                content = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = Color.White
                    )
                })
        }
    )

@Composable
@Preview
fun DefaultAppBar_Preview() {
    DefaultAppBar(onSearchClicked = {})
}


@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.primary,
        content = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = text,
                onValueChange = { onTextChange(it) },
                placeholder = {
                    Text(
                        modifier = Modifier.alpha(ContentAlpha.medium),
                        text = "Search here...",
                        color = Color.White
                    )
                },
                textStyle = TextStyle(fontSize = MaterialTheme.typography.subtitle1.fontSize),
                singleLine = true,
                leadingIcon = {
                    IconButton(
                        modifier = Modifier
                            .alpha(ContentAlpha.medium),
                        onClick = {},
                        content = {
                            Icon(Icons.Default.Search, "Search Icon", tint = Color.Red)
                        })
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (text.isNotEmpty()) onTextChange("") else onCloseClicked()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon",
                            tint = Color.White
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchClicked(text) }),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
                )
            )
        }
    )
}


@Preview
@Composable
fun SearchAppBarPreview() {
    SearchAppBar(
        text = "Some random text",
        onTextChange = {},
        onCloseClicked = {},
        onSearchClicked = {}
    )
}


@Composable
fun RepoItem(item: Repo, clickDish: (Repo) -> Unit) =
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .clickable { clickDish(item) },
        content = {

            Column() {
                var text =item.description?.let {
                    if (it.length > 40) it.substring(0, 29).plus("..")
                    else it
                } ?: "-"


                var text1 =
                    if (item.html_url.length > 40) item.html_url.substring(0, 29).plus("..")
                    else item.html_url

                4.height()
                Text(text)
                4.height()

                Text(text1)
                4.height()
            }
        })

