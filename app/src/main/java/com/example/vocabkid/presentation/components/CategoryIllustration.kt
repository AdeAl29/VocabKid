package com.example.vocabkid.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vocabkid.R

/**
 * Returns the drawable resource ID for a given category name, if an illustration exists.
 */
fun categoryDrawableRes(category: String): Int? {
    return when (category.trim().lowercase()) {
        "animal", "animals" -> R.drawable.illust_cat_animal
        "fruit", "fruits" -> R.drawable.illust_cat_fruit
        "vegetable", "vegetables" -> R.drawable.illust_cat_vegetable
        "food", "foods" -> R.drawable.illust_cat_food
        "drink", "drinks" -> R.drawable.illust_cat_drink
        "school" -> R.drawable.illust_cat_school
        "family" -> R.drawable.illust_cat_family
        "color", "colors" -> R.drawable.illust_cat_color
        "number", "numbers" -> R.drawable.illust_cat_number
        "action", "actions" -> R.drawable.illust_cat_action
        "body" -> R.drawable.illust_cat_body
        "clothing", "clothes" -> R.drawable.illust_cat_clothing
        "home" -> R.drawable.illust_cat_home
        "object", "objects" -> R.drawable.illust_cat_object
        "place", "places" -> R.drawable.illust_cat_place
        "nature" -> R.drawable.illust_cat_nature
        "weather" -> R.drawable.illust_cat_weather
        "time" -> R.drawable.illust_cat_time
        "feeling", "feelings" -> R.drawable.illust_cat_feeling
        "adjective", "adjectives" -> R.drawable.illust_cat_adjective
        "transportation" -> R.drawable.illust_cat_transportation
        "job", "jobs" -> R.drawable.illust_cat_job
        "position", "positions" -> R.drawable.illust_cat_position
        "expression", "expressions" -> R.drawable.illust_cat_expression
        else -> null
    }
}

/**
 * Displays a colorful cartoon illustration for the category.
 * If no illustration asset is available yet, falls back gracefully to
 * the category's theme icon on a soft tinted container.
 */
@Composable
fun CategoryIllustrationImage(
    category: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    fallbackIconSize: Dp = 48.dp
) {
    val resId = categoryDrawableRes(category)
    if (resId != null) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Ilustrasi kategori $category",
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        val containerColor = categoryThemeContainerColor(category)
        val contentColor = categoryThemeContentColor(category)
        val icon = categoryThemeIcon(category)

        Box(
            modifier = modifier
                .background(containerColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category,
                tint = contentColor,
                modifier = Modifier.size(fallbackIconSize)
            )
        }
    }
}
