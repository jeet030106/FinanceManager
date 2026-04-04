package com.example.consego.ui.features.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.consego.R

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

@Composable
fun OnBoardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            R.drawable.onboarding1,
            "Track Your Expenses",
            "Easily manage your daily spending and stay within budget."
        ),
        OnboardingPage(
            R.drawable.onboarding2,
            "Save for the Future",
            "Set financial goals and watch your savings grow every month."
        ),
        OnboardingPage(
            R.drawable.onboarding3,
            "Secure Your Wealth",
            "Professional level insights into your bank and cash balances."
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }
    val pageData = pages[currentPage]

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF744BD7))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = pageData.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentScale = ContentScale.Fit
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = pageData.title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pageData.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    if (currentPage < pages.lastIndex) currentPage++ else onFinish()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF744BD7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (currentPage == pages.lastIndex) "Get Started" else "Next",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}