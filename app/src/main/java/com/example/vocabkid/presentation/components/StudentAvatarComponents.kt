package com.example.vocabkid.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vocabkid.R
import com.example.vocabkid.domain.model.StudentAvatar

@Composable
fun StudentAvatarBadge(
    avatar: StudentAvatar,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = avatarDrawableRes(avatar)),
        contentDescription = avatar.contentDescription,
        modifier = modifier.clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun StudentAvatarPicker(
    selectedAvatar: StudentAvatar,
    onAvatarSelected: (StudentAvatar) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Pilih avatar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StudentAvatar.entries.forEach { avatar ->
                StudentAvatarOption(
                    avatar = avatar,
                    selected = selectedAvatar == avatar,
                    onClick = { onAvatarSelected(avatar) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StudentAvatarIconOption(
    avatar: StudentAvatar,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
    }

    Surface(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .semantics {
                this.selected = selected
            },
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (selected) 3.dp else 1.dp, borderColor),
        shadowElevation = if (selected) 3.dp else 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            StudentAvatarBadge(
                avatar = avatar,
                modifier = Modifier.fillMaxSize()
            )

            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(22.dp)
                )
            }
        }
    }
}

@Composable
fun StudentAvatarOption(
    avatar: StudentAvatar,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    }
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.62f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = modifier
            .heightIn(min = 124.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .semantics {
                this.selected = selected
            },
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudentAvatarBadge(
                    avatar = avatar,
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    text = avatar.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun avatarDrawableRes(avatar: StudentAvatar): Int {
    return when (avatar) {
        StudentAvatar.SISWA -> R.drawable.avatar_siswa_01
        StudentAvatar.SISWA_02 -> R.drawable.avatar_siswa_02
        StudentAvatar.SISWA_03 -> R.drawable.avatar_siswa_03
        StudentAvatar.SISWA_04 -> R.drawable.avatar_siswa_04
        StudentAvatar.SISWA_05 -> R.drawable.avatar_siswa_05
        StudentAvatar.SISWA_06 -> R.drawable.avatar_siswa_06
        StudentAvatar.SISWA_07 -> R.drawable.avatar_siswa_07
        StudentAvatar.SISWA_08 -> R.drawable.avatar_siswa_08
        StudentAvatar.SISWA_09 -> R.drawable.avatar_siswa_09
        StudentAvatar.SISWA_10 -> R.drawable.avatar_siswa_10
        StudentAvatar.SISWI -> R.drawable.avatar_siswi_01
        StudentAvatar.SISWI_02 -> R.drawable.avatar_siswi_02
        StudentAvatar.SISWI_03 -> R.drawable.avatar_siswi_03
        StudentAvatar.SISWI_04 -> R.drawable.avatar_siswi_04
        StudentAvatar.SISWI_05 -> R.drawable.avatar_siswi_05
        StudentAvatar.SISWI_06 -> R.drawable.avatar_siswi_06
        StudentAvatar.SISWI_07 -> R.drawable.avatar_siswi_07
        StudentAvatar.SISWI_08 -> R.drawable.avatar_siswi_08
        StudentAvatar.SISWI_09 -> R.drawable.avatar_siswi_09
        StudentAvatar.SISWI_10 -> R.drawable.avatar_siswi_10
    }
}
