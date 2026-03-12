@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.kotlinapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch


object AppColors {
    val Primary = Color(0xFFE63946)
    val PrimaryDark = Color(0xFFB71C1C)
    val Secondary = Color(0xFFF4A261)
    val BackgroundDark = Color(0xFF0D1B2A)
    val BackgroundCard = Color(0xFF1B263B)
    val SurfaceLight = Color(0xFF415A77)
    val TextPrimary = Color(0xFFE0E1DD)
    val TextSecondary = Color(0xFF778DA9)
    val Paramecia = Color(0xFF9B59B6)
    val Zoan = Color(0xFF27AE60)
    val Logia = Color(0xFF3498DB)
    val Unknown = Color(0xFF7F8C8D)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnePieceFruitsTheme {
                OnePieceFruitsApp()
            }
        }
    }
}


@Composable
fun OnePieceFruitsTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = AppColors.Primary,
        secondary = AppColors.Secondary,
        background = AppColors.BackgroundDark,
        surface = AppColors.BackgroundCard,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = AppColors.TextPrimary,
        onSurface = AppColors.TextPrimary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}


@Composable
fun OnePieceFruitsApp() {
    val fruits = remember { mutableStateListOf<Fruit>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            fruits.addAll(ApiClient.apiService.getFruits())
        } catch (e: Exception) {
            errorMessage = "Erro ao carregar frutas: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = AppColors.BackgroundDark,
        topBar = { AppTopBar() }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(AppColors.BackgroundDark)
        ) {
            when {
                isLoading -> LoadingState()
                errorMessage.isNotEmpty() -> ErrorState(errorMessage)
                fruits.isEmpty() -> EmptyState()
                else -> FruitList(fruits)
            }
        }
    }
}


@Composable
fun AppTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(AppColors.Primary, AppColors.PrimaryDark)
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .safeDrawingPadding()
    ) {
        Column {
            Text(
                text = "Devil Fruits",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "One Piece Encyclopedia",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun BoxScope.LoadingState() {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = AppColors.Primary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Carregando frutas...",
            color = AppColors.TextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
fun BoxScope.ErrorState(message: String) {
    Text(
        text = message,
        color = Color.Red,
        modifier = Modifier.align(Alignment.Center)
    )
}

@Composable
fun BoxScope.EmptyState() {
    Text(
        text = "Nenhuma fruta encontrada",
        modifier = Modifier.align(Alignment.Center),
        color = AppColors.TextSecondary
    )
}

@Composable
fun FruitList(fruits: List<Fruit>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(fruits) { fruit ->
            FruitCard(fruit)
        }
    }
}


@Composable
fun FruitCard(fruit: Fruit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = AppColors.Primary.copy(alpha = 0.3f)
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(containerColor = AppColors.BackgroundCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Imagem
                FruitImage(fruit.filename, fruit.name)

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fruit.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = fruit.roman_name,
                        fontSize = 14.sp,
                        color = AppColors.Secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FruitTypeBadge(fruit.type ?: "Desconhecido")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = fruit.description,
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
            }

            if (fruit.description.length > 100) {
                Divider(
                    color = AppColors.SurfaceLight.copy(alpha = 0.3f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fruit.technicalFile ?: "N/A",
                        fontSize = 11.sp,
                        color = AppColors.TextSecondary.copy(alpha = 0.6f)
                    )

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Ver menos" else "Ver mais",
                            tint = AppColors.Secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FruitImage(imageUrl: String?, contentDescription: String) {
    val safeUrl = imageUrl?.replace("\\", "")?.takeIf { it.startsWith("http") }
        ?: "https://via.placeholder.com/90"

    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.SurfaceLight.copy(alpha = 0.3f))
    ) {
        AsyncImage(
            model = safeUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun FruitTypeBadge(type: String) {
    val (backgroundColor, label) = when (type.lowercase()) {
        "paramecia" -> Pair(AppColors.Paramecia, "Paramecia")
        "zoan" -> Pair(AppColors.Zoan, "Zoan")
        "logia" -> Pair(AppColors.Logia, "Logia")
        else -> Pair(AppColors.Unknown, type.ifEmpty { "Desconhecido" })
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = backgroundColor
        )
    }
}