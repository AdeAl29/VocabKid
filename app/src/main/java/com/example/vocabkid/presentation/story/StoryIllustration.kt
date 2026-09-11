package com.example.vocabkid.presentation.story

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.vocabkid.R

/**
 * Returns the drawable resource ID for a story scene if available.
 */
fun storySceneDrawableRes(chapterId: Int, sceneId: Int): Int? {
    return when (chapterId) {
        1 -> when (sceneId) {
            1 -> R.drawable.illust_story_c1_s1
            2 -> R.drawable.illust_story_c1_s2
            3 -> R.drawable.illust_story_c1_s3
            4 -> R.drawable.illust_story_c1_s4
            5 -> R.drawable.illust_story_c1_s5
            else -> null
        }
        2 -> when (sceneId) {
            1 -> R.drawable.illust_story_c2_s1
            2 -> R.drawable.illust_story_c2_s2
            3 -> R.drawable.illust_story_c2_s3
            4 -> R.drawable.illust_story_c2_s4
            5 -> R.drawable.illust_story_c2_s5
            else -> null
        }
        3 -> when (sceneId) {
            1 -> R.drawable.illust_story_c3_s1
            2 -> R.drawable.illust_story_c3_s2
            3 -> R.drawable.illust_story_c3_s3
            4 -> R.drawable.illust_story_c3_s4
            5 -> R.drawable.illust_story_c3_s5
            else -> null
        }
        4 -> when (sceneId) {
            1 -> R.drawable.illust_story_c4_s1
            2 -> R.drawable.illust_story_c4_s2
            3 -> R.drawable.illust_story_c4_s3
            else -> null
        }
        else -> null
    }
}

/**
 * Returns the cover/thumbnail drawable resource ID for a story chapter.
 */
fun storyChapterCoverDrawableRes(chapterId: Int, context: Context? = null): Int? {
    val staticRes = when (chapterId) {
        1 -> R.drawable.illust_story_c1_s1
        2 -> R.drawable.illust_story_c2_s1
        3 -> R.drawable.illust_story_c3_s1
        4 -> R.drawable.illust_story_c4_s1
        else -> null
    }
    if (staticRes != null) return staticRes

    if (context != null) {
        val name = "illust_story_c${chapterId}_s1"
        val id = context.resources.getIdentifier(name, "drawable", context.packageName)
        if (id != 0) return id
    }
    return null
}

/**
 * Renders an HD cartoon scene illustration if available, or falls back to
 * custom rendering (e.g. Canvas animation).
 */
@Composable
fun StorySceneIllustrationImage(
    chapterId: Int,
    sceneId: Int,
    sceneIndex: Int,
    modifier: Modifier = Modifier,
    fallbackContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val resId = remember(chapterId, sceneId, sceneIndex) {
        storySceneDrawableRes(chapterId, sceneId)
            ?: storySceneDrawableRes(chapterId, sceneIndex + 1)
            ?: run {
                val name1 = "illust_story_c${chapterId}_s${sceneId}"
                val id1 = context.resources.getIdentifier(name1, "drawable", context.packageName)
                if (id1 != 0) id1
                else {
                    val name2 = "illust_story_c${chapterId}_s${sceneIndex + 1}"
                    val id2 = context.resources.getIdentifier(name2, "drawable", context.packageName)
                    if (id2 != 0) id2 else null
                }
            }
    }

    if (resId != null) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Chapter $chapterId Scene $sceneId",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        fallbackContent()
    }
}
