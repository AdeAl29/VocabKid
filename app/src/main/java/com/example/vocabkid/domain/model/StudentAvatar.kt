package com.example.vocabkid.domain.model

enum class StudentAvatarGroup(
    val label: String
) {
    SISWA("Siswa"),
    SISWI("Siswi")
}

enum class StudentAvatar(
    val id: String,
    val label: String,
    val contentDescription: String,
    val group: StudentAvatarGroup,
    val variant: Int
) {
    SISWA(
        id = "siswa",
        label = "Siswa 1",
        contentDescription = "Avatar siswa 1",
        group = StudentAvatarGroup.SISWA,
        variant = 1
    ),
    SISWA_02(
        id = "siswa_02",
        label = "Siswa 2",
        contentDescription = "Avatar siswa 2",
        group = StudentAvatarGroup.SISWA,
        variant = 2
    ),
    SISWA_03(
        id = "siswa_03",
        label = "Siswa 3",
        contentDescription = "Avatar siswa 3",
        group = StudentAvatarGroup.SISWA,
        variant = 3
    ),
    SISWA_04(
        id = "siswa_04",
        label = "Siswa 4",
        contentDescription = "Avatar siswa 4",
        group = StudentAvatarGroup.SISWA,
        variant = 4
    ),
    SISWA_05(
        id = "siswa_05",
        label = "Siswa 5",
        contentDescription = "Avatar siswa 5",
        group = StudentAvatarGroup.SISWA,
        variant = 5
    ),
    SISWA_06(
        id = "siswa_06",
        label = "Siswa 6",
        contentDescription = "Avatar siswa 6",
        group = StudentAvatarGroup.SISWA,
        variant = 6
    ),
    SISWA_07(
        id = "siswa_07",
        label = "Siswa 7",
        contentDescription = "Avatar siswa 7",
        group = StudentAvatarGroup.SISWA,
        variant = 7
    ),
    SISWA_08(
        id = "siswa_08",
        label = "Siswa 8",
        contentDescription = "Avatar siswa 8",
        group = StudentAvatarGroup.SISWA,
        variant = 8
    ),
    SISWA_09(
        id = "siswa_09",
        label = "Siswa 9",
        contentDescription = "Avatar siswa 9",
        group = StudentAvatarGroup.SISWA,
        variant = 9
    ),
    SISWA_10(
        id = "siswa_10",
        label = "Siswa 10",
        contentDescription = "Avatar siswa 10",
        group = StudentAvatarGroup.SISWA,
        variant = 10
    ),
    SISWI(
        id = "siswi",
        label = "Siswi 1",
        contentDescription = "Avatar siswi 1",
        group = StudentAvatarGroup.SISWI,
        variant = 1
    ),
    SISWI_02(
        id = "siswi_02",
        label = "Siswi 2",
        contentDescription = "Avatar siswi 2",
        group = StudentAvatarGroup.SISWI,
        variant = 2
    ),
    SISWI_03(
        id = "siswi_03",
        label = "Siswi 3",
        contentDescription = "Avatar siswi 3",
        group = StudentAvatarGroup.SISWI,
        variant = 3
    ),
    SISWI_04(
        id = "siswi_04",
        label = "Siswi 4",
        contentDescription = "Avatar siswi 4",
        group = StudentAvatarGroup.SISWI,
        variant = 4
    ),
    SISWI_05(
        id = "siswi_05",
        label = "Siswi 5",
        contentDescription = "Avatar siswi 5",
        group = StudentAvatarGroup.SISWI,
        variant = 5
    ),
    SISWI_06(
        id = "siswi_06",
        label = "Siswi 6",
        contentDescription = "Avatar siswi 6",
        group = StudentAvatarGroup.SISWI,
        variant = 6
    ),
    SISWI_07(
        id = "siswi_07",
        label = "Siswi 7",
        contentDescription = "Avatar siswi 7",
        group = StudentAvatarGroup.SISWI,
        variant = 7
    ),
    SISWI_08(
        id = "siswi_08",
        label = "Siswi 8",
        contentDescription = "Avatar siswi 8",
        group = StudentAvatarGroup.SISWI,
        variant = 8
    ),
    SISWI_09(
        id = "siswi_09",
        label = "Siswi 9",
        contentDescription = "Avatar siswi 9",
        group = StudentAvatarGroup.SISWI,
        variant = 9
    ),
    SISWI_10(
        id = "siswi_10",
        label = "Siswi 10",
        contentDescription = "Avatar siswi 10",
        group = StudentAvatarGroup.SISWI,
        variant = 10
    );

    companion object {
        fun fromId(id: String?): StudentAvatar {
            return entries.firstOrNull { avatar -> avatar.id == id } ?: SISWA
        }

        fun forGroup(group: StudentAvatarGroup): List<StudentAvatar> {
            return entries.filter { avatar -> avatar.group == group }
        }
    }
}
