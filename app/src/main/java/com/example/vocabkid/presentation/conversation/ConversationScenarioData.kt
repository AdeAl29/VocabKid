package com.example.vocabkid.presentation.conversation

import androidx.compose.ui.graphics.Color
import com.example.vocabkid.R

data class ConversationScene(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val accentColor: Color
)

data class ConversationCharacter(
    val id: String,
    val name: String,
    val role: String,
    val avatarRes: Int
)

data class ConversationScenario(
    val id: String,
    val sceneId: String,
    val title: String,
    val description: String,
    val goal: String,
    val level: String,
    val partner: ConversationCharacter,
    val openingLines: List<ConversationLine>,
    val steps: List<ConversationStep>,
    val closingLine: ConversationLine
)

data class ConversationLine(
    val character: ConversationCharacter,
    val english: String,
    val indonesian: String
)

data class ConversationStep(
    val prompt: ConversationLine,
    val choices: List<ConversationChoice>
)

data class ConversationChoice(
    val english: String,
    val indonesian: String,
    val response: ConversationLine
)

object ConversationScenarioLibrary {
    val userCharacter = ConversationCharacter(
        id = "you",
        name = "Kamu",
        role = "English learner",
        avatarRes = R.drawable.avatar_siswa_01
    )

    private val characters = listOf(
        ConversationCharacter("alya", "Alya", "Teman kelas", R.drawable.avatar_siswi_01),
        ConversationCharacter("bima", "Bima", "Teman belajar", R.drawable.avatar_siswa_02),
        ConversationCharacter("cici", "Cici", "Teman bermain", R.drawable.avatar_siswi_02),
        ConversationCharacter("dika", "Dika", "Teman rumah", R.drawable.avatar_siswa_03),
        ConversationCharacter("eno", "Eno", "Teman proyek", R.drawable.avatar_siswa_04),
        ConversationCharacter("fira", "Fira", "Teman membaca", R.drawable.avatar_siswi_03),
        ConversationCharacter("gani", "Gani", "Teman olahraga", R.drawable.avatar_siswa_05),
        ConversationCharacter("hana", "Hana", "Teman kreatif", R.drawable.avatar_siswi_04),
        ConversationCharacter("iqbal", "Iqbal", "Teman perjalanan", R.drawable.avatar_siswa_06),
        ConversationCharacter("jihan", "Jihan", "Teman musik", R.drawable.avatar_siswi_05),
        ConversationCharacter("kevin", "Kevin", "Teman petualang", R.drawable.avatar_siswa_07),
        ConversationCharacter("lala", "Lala", "Teman cerita", R.drawable.avatar_siswi_06),
        ConversationCharacter("mila", "Mila", "Teman baik", R.drawable.avatar_siswi_07),
        ConversationCharacter("niko", "Niko", "Teman bertanya", R.drawable.avatar_siswa_08),
        ConversationCharacter("putri", "Putri", "Teman seni", R.drawable.avatar_siswi_08),
        ConversationCharacter("rafi", "Rafi", "Teman aktif", R.drawable.avatar_siswa_09),
        ConversationCharacter("sari", "Sari", "Teman teliti", R.drawable.avatar_siswi_09),
        ConversationCharacter("tata", "Tata", "Teman ceria", R.drawable.avatar_siswi_10),
        ConversationCharacter("umar", "Umar", "Teman rute", R.drawable.avatar_siswa_10),
        ConversationCharacter("vina", "Vina", "Teman belanja", R.drawable.avatar_siswi_01),
        ConversationCharacter("wira", "Wira", "Teman tim", R.drawable.avatar_siswa_02),
        ConversationCharacter("yuna", "Yuna", "Teman sains", R.drawable.avatar_siswi_02),
        ConversationCharacter("zaki", "Zaki", "Teman film", R.drawable.avatar_siswa_03),
        ConversationCharacter("rara", "Rara", "Teman hewan", R.drawable.avatar_siswi_03),
        ConversationCharacter("adam", "Adam", "Teman renang", R.drawable.avatar_siswa_04),
        ConversationCharacter("nia", "Nia", "Teman toko", R.drawable.avatar_siswi_04)
    )

    private val seeds = listOf(
        SceneSeed("school", "Sekolah", "Sapaan, alat tulis, dan meminta bantuan di kelas.", "SK", Color(0xFF4E6BE6), "at school", "di sekolah", "book", "buku", "pencil", "pensil", "read a story", "membaca cerita", "practice English", "berlatih bahasa Inggris", "teacher", "guru", "classroom", "kelas", characters[0]),
        SceneSeed("home", "Rumah", "Percakapan keluarga, kamar, dan kegiatan harian.", "RM", Color(0xFF16877A), "at home", "di rumah", "blanket", "selimut", "toy box", "kotak mainan", "clean my room", "membersihkan kamar", "help in the kitchen", "membantu di dapur", "parent", "orang tua", "living room", "ruang keluarga", characters[3]),
        SceneSeed("kitchen", "Dapur", "Meminta alat makan, makanan, dan menawarkan bantuan.", "DP", Color(0xFFFFA43A), "in the kitchen", "di dapur", "spoon", "sendok", "cup", "cangkir", "make breakfast", "membuat sarapan", "wash fruit", "mencuci buah", "parent", "orang tua", "sink", "wastafel", characters[12]),
        SceneSeed("garden", "Taman Rumah", "Tanaman, alat berkebun, dan menjaga kebersihan.", "TR", Color(0xFF42B883), "in the garden", "di taman rumah", "flower", "bunga", "watering can", "alat penyiram", "water the plants", "menyiram tanaman", "pick dry leaves", "mengambil daun kering", "gardener", "tukang kebun", "big tree", "pohon besar", characters[7]),
        SceneSeed("market", "Pasar", "Belanja buah, menanyakan harga, dan menghitung uang.", "PS", Color(0xFFE85D75), "at the market", "di pasar", "apple", "apel", "bread", "roti", "buy fruit", "membeli buah", "count the money", "menghitung uang", "seller", "penjual", "fruit stall", "kios buah", characters[19]),
        SceneSeed("restaurant", "Restoran", "Memesan makanan, minuman, dan mengucapkan terima kasih.", "RS", Color(0xFFEF7B45), "at a restaurant", "di restoran", "menu", "menu", "water", "air minum", "order noodles", "memesan mi", "ask for rice", "meminta nasi", "waiter", "pelayan", "table", "meja", characters[1]),
        SceneSeed("library", "Perpustakaan", "Mencari buku, bicara pelan, dan meminjam cerita.", "PB", Color(0xFF8B5CF6), "at the library", "di perpustakaan", "comic book", "buku komik", "library card", "kartu perpustakaan", "borrow a book", "meminjam buku", "read quietly", "membaca dengan tenang", "librarian", "pustakawan", "reading corner", "pojok baca", characters[5]),
        SceneSeed("clinic", "Klinik", "Menjelaskan kondisi badan dan menunggu giliran.", "KL", Color(0xFF0EA5E9), "at the clinic", "di klinik", "mask", "masker", "medicine", "obat", "tell my symptoms", "menceritakan gejala", "wait calmly", "menunggu dengan tenang", "nurse", "perawat", "waiting room", "ruang tunggu", characters[13]),
        SceneSeed("park", "Taman Kota", "Bermain, bergiliran, dan menjaga area umum.", "TK", Color(0xFF2F9E44), "at the park", "di taman kota", "kite", "layang-layang", "ball", "bola", "play soccer", "bermain sepak bola", "walk slowly", "berjalan pelan", "friend", "teman", "bench", "bangku", characters[6]),
        SceneSeed("playground", "Playground", "Bergiliran, mengajak teman, dan bermain aman.", "PG", Color(0xFFFFC145), "at the playground", "di playground", "swing", "ayunan", "slide", "perosotan", "take turns", "bergiliran", "help a friend", "membantu teman", "coach", "pelatih", "sand area", "area pasir", characters[2]),
        SceneSeed("bus_stop", "Halte Bus", "Menunggu bus, membaca rute, dan bertanya arah.", "HB", Color(0xFF2563EB), "at the bus stop", "di halte bus", "ticket", "tiket", "bag", "tas", "wait for the bus", "menunggu bus", "check the route", "mengecek rute", "driver", "sopir", "bus sign", "papan bus", characters[8]),
        SceneSeed("train_station", "Stasiun Kereta", "Tiket, peron, dan mengikuti pengumuman.", "ST", Color(0xFF64748B), "at the train station", "di stasiun kereta", "ticket", "tiket", "map", "peta", "find the platform", "mencari peron", "listen to the announcement", "mendengarkan pengumuman", "officer", "petugas", "platform", "peron", characters[18]),
        SceneSeed("airport", "Bandara", "Check-in, gerbang, dan menjaga barang bawaan.", "BD", Color(0xFF38BDF8), "at the airport", "di bandara", "passport", "paspor", "backpack", "ransel", "check in", "check-in", "find the gate", "mencari gerbang", "staff member", "staf", "gate", "gerbang", characters[10]),
        SceneSeed("beach", "Pantai", "Bermain pasir, menjaga barang, dan menyapa teman.", "PT", Color(0xFF06B6D4), "at the beach", "di pantai", "towel", "handuk", "hat", "topi", "build a sandcastle", "membuat istana pasir", "collect shells", "mengumpulkan kerang", "lifeguard", "penjaga pantai", "shore", "tepi laut", characters[11]),
        SceneSeed("zoo", "Kebun Binatang", "Melihat hewan, membaca peta, dan bertanya jadwal.", "KB", Color(0xFF65A30D), "at the zoo", "di kebun binatang", "map", "peta", "camera", "kamera", "see the elephants", "melihat gajah", "read the animal board", "membaca papan hewan", "keeper", "penjaga hewan", "animal board", "papan hewan", characters[23]),
        SceneSeed("museum", "Museum", "Melihat koleksi, mendengarkan pemandu, dan bertanya sopan.", "MS", Color(0xFF7C3AED), "at the museum", "di museum", "guidebook", "buku panduan", "camera", "kamera", "look at fossils", "melihat fosil", "sketch a painting", "membuat sketsa lukisan", "guide", "pemandu", "gallery", "galeri", characters[14]),
        SceneSeed("birthday_party", "Pesta Ulang Tahun", "Memberi hadiah, menyanyi, dan ngobrol dengan teman.", "UT", Color(0xFFEC4899), "at a birthday party", "di pesta ulang tahun", "gift", "hadiah", "cake", "kue", "sing a song", "menyanyikan lagu", "play a game", "bermain permainan", "host", "tuan rumah", "snack table", "meja camilan", characters[17]),
        SceneSeed("sports_field", "Lapangan Olahraga", "Pemanasan, kerja tim, dan minum air.", "LO", Color(0xFF16A34A), "on the sports field", "di lapangan olahraga", "ball", "bola", "water bottle", "botol minum", "warm up", "pemanasan", "run together", "lari bersama", "coach", "pelatih", "goal post", "gawang", characters[16]),
        SceneSeed("art_room", "Ruang Seni", "Warna, alat gambar, dan memuji karya teman.", "SN", Color(0xFFF97316), "in the art room", "di ruang seni", "brush", "kuas", "paper", "kertas", "paint a tree", "melukis pohon", "draw a house", "menggambar rumah", "art teacher", "guru seni", "paint table", "meja cat", characters[20]),
        SceneSeed("music_room", "Ruang Musik", "Ritme, lagu, dan latihan bersama.", "MU", Color(0xFF9333EA), "in the music room", "di ruang musik", "drum", "drum", "recorder", "seruling", "clap the rhythm", "menepuk ritme", "sing together", "bernyanyi bersama", "music teacher", "guru musik", "piano", "piano", characters[9]),
        SceneSeed("science_lab", "Lab Sains", "Mengamati, bertanya, dan memakai alat dengan aman.", "LS", Color(0xFF14B8A6), "in the science lab", "di lab sains", "magnet", "magnet", "glass", "gelas kaca", "observe plants", "mengamati tanaman", "mix colors", "mencampur warna", "science teacher", "guru sains", "experiment table", "meja eksperimen", characters[21]),
        SceneSeed("bookstore", "Toko Buku", "Memilih buku, bertanya harga, dan mencari rak anak.", "TB", Color(0xFFB45309), "at the bookstore", "di toko buku", "dictionary", "kamus", "storybook", "buku cerita", "choose a book", "memilih buku", "ask the price", "menanyakan harga", "cashier", "kasir", "children shelf", "rak anak", characters[25]),
        SceneSeed("cinema", "Bioskop", "Membeli tiket, mencari kursi, dan memilih camilan.", "BS", Color(0xFF334155), "at the cinema", "di bioskop", "ticket", "tiket", "popcorn", "popcorn", "find my seat", "mencari kursi", "watch the trailer", "menonton trailer", "usher", "petugas bioskop", "seat row", "baris kursi", characters[22]),
        SceneSeed("toy_store", "Toko Mainan", "Membandingkan mainan, bertanya harga, dan memilih hadiah.", "TM", Color(0xFFF59E0B), "at the toy store", "di toko mainan", "puzzle", "puzzle", "robot toy", "mainan robot", "compare toys", "membandingkan mainan", "choose a gift", "memilih hadiah", "cashier", "kasir", "toy shelf", "rak mainan", characters[15]),
        SceneSeed("swimming_pool", "Kolam Renang", "Berenang aman, alat renang, dan instruksi pelatih.", "KR", Color(0xFF0284C7), "at the swimming pool", "di kolam renang", "goggles", "kacamata renang", "towel", "handuk", "swim slowly", "berenang pelan", "practice kicking", "latihan menendang kaki", "coach", "pelatih", "pool edge", "tepi kolam", characters[24]),
        SceneSeed("pet_shop", "Pet Shop", "Merawat hewan, memilih makanan, dan bertanya sopan.", "PH", Color(0xFF84CC16), "at the pet shop", "di pet shop", "fish food", "makanan ikan", "cat toy", "mainan kucing", "choose pet food", "memilih makanan hewan", "ask about pet care", "bertanya cara merawat hewan", "shopkeeper", "penjaga toko", "fish tank", "akuarium", characters[23])
    )

    val scenes: List<ConversationScene> = seeds.map { seed ->
        ConversationScene(
            id = seed.id,
            title = seed.title,
            subtitle = seed.subtitle,
            badge = seed.badge,
            accentColor = seed.accentColor
        )
    }

    val scenarios: List<ConversationScenario> = seeds.flatMap { seed ->
        listOf(
            buildHelpScenario(seed),
            buildChoiceScenario(seed),
            buildPlanScenario(seed),
            buildDirectionScenario(seed),
            buildFeelingScenario(seed)
        )
    }

    fun scenariosForScene(sceneId: String): List<ConversationScenario> {
        return scenarios.filter { scenario -> scenario.sceneId == sceneId }
    }

    fun sceneForId(sceneId: String): ConversationScene? {
        return scenes.firstOrNull { scene -> scene.id == sceneId }
    }

    private fun buildHelpScenario(seed: SceneSeed): ConversationScenario {
        return scenario(
            seed = seed,
            suffix = "help",
            title = "Minta bantuan",
            description = "Latihan menawarkan bantuan dan mencari ${seed.itemOneIndonesian}.",
            goal = "Gunakan help, please, dan thank you.",
            level = "Mudah",
            openingEnglish = "${seed.partner.name} needs help ${seed.locationEnglish}.",
            openingIndonesian = "${seed.partner.name} perlu bantuan ${seed.locationIndonesian}.",
            steps = listOf(
                step(
                    seed.partner,
                    "Can you help me find the ${seed.itemOneEnglish}?",
                    "Bisakah kamu membantuku mencari ${seed.itemOneIndonesian}?",
                    choice(
                        "Yes, I can help you.",
                        "Ya, aku bisa membantu.",
                        seed.partner,
                        "Thank you. You are very kind.",
                        "Terima kasih. Kamu baik sekali."
                    ),
                    choice(
                        "Sure. Let us look together.",
                        "Tentu. Ayo cari bersama.",
                        seed.partner,
                        "Great idea. Looking together is easier.",
                        "Ide bagus. Mencari bersama lebih mudah."
                    )
                ),
                step(
                    seed.partner,
                    "Should we ask the ${seed.helperEnglish} or check the ${seed.destinationEnglish} first?",
                    "Sebaiknya kita bertanya kepada ${seed.helperIndonesian} atau mengecek ${seed.destinationIndonesian} dulu?",
                    choice(
                        "Let us ask the ${seed.helperEnglish}.",
                        "Ayo tanya ${seed.helperIndonesian}.",
                        seed.partner,
                        "Good. The ${seed.helperEnglish} may know.",
                        "Bagus. ${seed.helperIndonesian.replaceFirstChar { it.uppercase() }} mungkin tahu."
                    ),
                    choice(
                        "Let us check the ${seed.destinationEnglish}.",
                        "Ayo cek ${seed.destinationIndonesian}.",
                        seed.partner,
                        "Okay. The ${seed.destinationEnglish} is a smart place to start.",
                        "Oke. ${seed.destinationIndonesian.replaceFirstChar { it.uppercase() }} adalah tempat yang pintar untuk mulai."
                    )
                ),
                step(
                    seed.partner,
                    "We found it near the ${seed.destinationEnglish}. What should I say?",
                    "Kita menemukannya dekat ${seed.destinationIndonesian}. Aku harus bilang apa?",
                    choice(
                        "Please keep it safe.",
                        "Tolong simpan baik-baik.",
                        seed.partner,
                        "I will. Thanks for reminding me.",
                        "Akan aku lakukan. Terima kasih sudah mengingatkan."
                    ),
                    choice(
                        "You are welcome.",
                        "Sama-sama.",
                        seed.partner,
                        "You are a helpful friend.",
                        "Kamu teman yang suka membantu."
                    )
                )
            ),
            closingEnglish = "Nice work. We solved it politely.",
            closingIndonesian = "Kerja bagus. Kita menyelesaikannya dengan sopan."
        )
    }

    private fun buildChoiceScenario(seed: SceneSeed): ConversationScenario {
        return scenario(
            seed = seed,
            suffix = "choose",
            title = "Memilih benda",
            description = "Pilih antara ${seed.itemOneIndonesian} dan ${seed.itemTwoIndonesian}.",
            goal = "Latih I like, I need, dan because.",
            level = "Mudah",
            openingEnglish = "${seed.partner.name} wants to choose the right item ${seed.locationEnglish}.",
            openingIndonesian = "${seed.partner.name} ingin memilih benda yang tepat ${seed.locationIndonesian}.",
            steps = listOf(
                step(
                    seed.partner,
                    "I can choose the ${seed.itemOneEnglish} or the ${seed.itemTwoEnglish}. Which one is better?",
                    "Aku bisa memilih ${seed.itemOneIndonesian} atau ${seed.itemTwoIndonesian}. Mana yang lebih baik?",
                    choice(
                        "I like the ${seed.itemOneEnglish}.",
                        "Aku suka ${seed.itemOneIndonesian}.",
                        seed.partner,
                        "Good choice. The ${seed.itemOneEnglish} is useful here.",
                        "Pilihan bagus. ${seed.itemOneIndonesian.replaceFirstChar { it.uppercase() }} berguna di sini."
                    ),
                    choice(
                        "The ${seed.itemTwoEnglish} looks useful.",
                        "${seed.itemTwoIndonesian.replaceFirstChar { it.uppercase() }} terlihat berguna.",
                        seed.partner,
                        "Yes, it can help us today.",
                        "Ya, itu bisa membantu kita hari ini."
                    )
                ),
                step(
                    seed.partner,
                    "Can you tell me why?",
                    "Bisakah kamu memberi tahu alasannya?",
                    choice(
                        "Because we need it for ${seed.activityOneEnglish}.",
                        "Karena kita membutuhkannya untuk ${seed.activityOneIndonesian}.",
                        seed.partner,
                        "That reason makes sense.",
                        "Alasan itu masuk akal."
                    ),
                    choice(
                        "Because it is easy to carry.",
                        "Karena mudah dibawa.",
                        seed.partner,
                        "True. Easy things help us move faster.",
                        "Benar. Benda yang mudah membantu kita bergerak lebih cepat."
                    )
                ),
                step(
                    seed.partner,
                    "How do we ask for it politely?",
                    "Bagaimana cara memintanya dengan sopan?",
                    choice(
                        "May I have the ${seed.itemOneEnglish}, please?",
                        "Bolehkah aku meminta ${seed.itemOneIndonesian}?",
                        seed.partner,
                        "Perfect. That sounds polite.",
                        "Sempurna. Itu terdengar sopan."
                    ),
                    choice(
                        "Can I use the ${seed.itemTwoEnglish}, please?",
                        "Bolehkah aku memakai ${seed.itemTwoIndonesian}?",
                        seed.partner,
                        "Nice sentence. Please makes it friendly.",
                        "Kalimat bagus. Please membuatnya ramah."
                    )
                )
            ),
            closingEnglish = "Great choosing. You explained your answer clearly.",
            closingIndonesian = "Pilihan bagus. Kamu menjelaskan jawabanmu dengan jelas."
        )
    }

    private fun buildPlanScenario(seed: SceneSeed): ConversationScenario {
        return scenario(
            seed = seed,
            suffix = "plan",
            title = "Membuat rencana",
            description = "Susun rencana kecil untuk aktivitas ${seed.locationIndonesian}.",
            goal = "Latih first, then, dan together.",
            level = "Sedang",
            openingEnglish = "${seed.partner.name} asks you to make a short plan.",
            openingIndonesian = "${seed.partner.name} mengajakmu membuat rencana singkat.",
            steps = listOf(
                step(
                    seed.partner,
                    "We have time ${seed.locationEnglish}. What should we do first?",
                    "Kita punya waktu ${seed.locationIndonesian}. Apa yang harus kita lakukan dulu?",
                    choice(
                        "First, let us ${seed.activityOneEnglish}.",
                        "Pertama, ayo ${seed.activityOneIndonesian}.",
                        seed.partner,
                        "Great. That is a clear first step.",
                        "Bagus. Itu langkah pertama yang jelas."
                    ),
                    choice(
                        "First, let us ${seed.activityTwoEnglish}.",
                        "Pertama, ayo ${seed.activityTwoIndonesian}.",
                        seed.partner,
                        "Nice plan. We can start there.",
                        "Rencana bagus. Kita bisa mulai dari sana."
                    )
                ),
                step(
                    seed.partner,
                    "What do we need before we start?",
                    "Apa yang kita butuhkan sebelum mulai?",
                    choice(
                        "We need the ${seed.itemOneEnglish}.",
                        "Kita butuh ${seed.itemOneIndonesian}.",
                        seed.partner,
                        "Yes. I will bring it.",
                        "Ya. Aku akan membawanya."
                    ),
                    choice(
                        "We need the ${seed.itemTwoEnglish}.",
                        "Kita butuh ${seed.itemTwoIndonesian}.",
                        seed.partner,
                        "Good thinking. That will help.",
                        "Pemikiran bagus. Itu akan membantu."
                    )
                ),
                step(
                    seed.partner,
                    "How can we invite another friend?",
                    "Bagaimana kita bisa mengajak teman lain?",
                    choice(
                        "Do you want to join us?",
                        "Apakah kamu mau bergabung dengan kami?",
                        seed.partner,
                        "Friendly and clear. I like it.",
                        "Ramah dan jelas. Aku suka."
                    ),
                    choice(
                        "Come with us, please.",
                        "Ikutlah bersama kami, ya.",
                        seed.partner,
                        "That sounds warm and polite.",
                        "Itu terdengar hangat dan sopan."
                    )
                )
            ),
            closingEnglish = "Plan complete. Now the activity feels easy.",
            closingIndonesian = "Rencana selesai. Sekarang aktivitas terasa mudah."
        )
    }

    private fun buildDirectionScenario(seed: SceneSeed): ConversationScenario {
        return scenario(
            seed = seed,
            suffix = "direction",
            title = "Mencari arah",
            description = "Tanya lokasi ${seed.destinationIndonesian} dan jawab dengan sopan.",
            goal = "Latih where, over there, dan follow me.",
            level = "Sedang",
            openingEnglish = "${seed.partner.name} is not sure about the way ${seed.locationEnglish}.",
            openingIndonesian = "${seed.partner.name} belum yakin arah ${seed.locationIndonesian}.",
            steps = listOf(
                step(
                    seed.partner,
                    "Where is the ${seed.destinationEnglish}?",
                    "Di mana ${seed.destinationIndonesian}?",
                    choice(
                        "It is over there.",
                        "Itu ada di sana.",
                        seed.partner,
                        "Thanks. I can see it now.",
                        "Terima kasih. Aku bisa melihatnya sekarang."
                    ),
                    choice(
                        "Follow me, please.",
                        "Ikuti aku, ya.",
                        seed.partner,
                        "Okay, I will follow you.",
                        "Oke, aku akan mengikutimu."
                    )
                ),
                step(
                    seed.partner,
                    "Should we go slowly or quickly?",
                    "Sebaiknya kita berjalan pelan atau cepat?",
                    choice(
                        "Let us go slowly.",
                        "Ayo berjalan pelan.",
                        seed.partner,
                        "Good. Slowly is safer.",
                        "Bagus. Pelan lebih aman."
                    ),
                    choice(
                        "Let us walk together.",
                        "Ayo berjalan bersama.",
                        seed.partner,
                        "Yes. Together is better.",
                        "Ya. Bersama lebih baik."
                    )
                ),
                step(
                    seed.partner,
                    "What can I ask if I am not sure?",
                    "Apa yang bisa aku tanyakan jika aku tidak yakin?",
                    choice(
                        "Excuse me, where is the ${seed.destinationEnglish}?",
                        "Permisi, di mana ${seed.destinationIndonesian}?",
                        seed.partner,
                        "Excellent. Excuse me is very polite.",
                        "Bagus sekali. Excuse me sangat sopan."
                    ),
                    choice(
                        "Can you help me, please?",
                        "Bisakah kamu membantuku?",
                        seed.partner,
                        "That is a useful sentence anywhere.",
                        "Itu kalimat yang berguna di mana saja."
                    )
                )
            ),
            closingEnglish = "We found the way and used polite English.",
            closingIndonesian = "Kita menemukan arah dan memakai bahasa Inggris yang sopan."
        )
    }

    private fun buildFeelingScenario(seed: SceneSeed): ConversationScenario {
        return scenario(
            seed = seed,
            suffix = "feeling",
            title = "Menenangkan teman",
            description = "Respons saat teman merasa bingung atau gugup.",
            goal = "Latih feeling words dan kalimat dukungan.",
            level = "Sedang",
            openingEnglish = "${seed.partner.name} wants to talk about feelings.",
            openingIndonesian = "${seed.partner.name} ingin bicara tentang perasaannya.",
            steps = listOf(
                step(
                    seed.partner,
                    "I feel nervous ${seed.locationEnglish}.",
                    "Aku merasa gugup ${seed.locationIndonesian}.",
                    choice(
                        "It is okay. I am here.",
                        "Tidak apa-apa. Aku di sini.",
                        seed.partner,
                        "Thank you. I feel calmer.",
                        "Terima kasih. Aku merasa lebih tenang."
                    ),
                    choice(
                        "You can do it.",
                        "Kamu pasti bisa.",
                        seed.partner,
                        "That helps me feel brave.",
                        "Itu membantuku merasa berani."
                    )
                ),
                step(
                    seed.partner,
                    "Can we practice one sentence before we ${seed.activityOneEnglish}?",
                    "Bisakah kita berlatih satu kalimat sebelum kita ${seed.activityOneIndonesian}?",
                    choice(
                        "May I have the ${seed.itemOneEnglish}, please?",
                        "Bolehkah aku meminta ${seed.itemOneIndonesian}?",
                        seed.partner,
                        "Good practice. Your voice is clear.",
                        "Latihan bagus. Suaramu jelas."
                    ),
                    choice(
                        "Where is the ${seed.destinationEnglish}?",
                        "Di mana ${seed.destinationIndonesian}?",
                        seed.partner,
                        "Nice. That question is useful.",
                        "Bagus. Pertanyaan itu berguna."
                    )
                ),
                step(
                    seed.partner,
                    "How should we end our chat?",
                    "Bagaimana kita menutup chat ini?",
                    choice(
                        "See you later.",
                        "Sampai jumpa nanti.",
                        seed.partner,
                        "See you later. Thanks for talking with me.",
                        "Sampai jumpa nanti. Terima kasih sudah bicara denganku."
                    ),
                    choice(
                        "Have a nice day.",
                        "Semoga harimu menyenangkan.",
                        seed.partner,
                        "You too. That is very kind.",
                        "Kamu juga. Itu sangat baik."
                    )
                )
            ),
            closingEnglish = "Kind words can make a conversation better.",
            closingIndonesian = "Kata-kata baik bisa membuat percakapan lebih bagus."
        )
    }

    private fun scenario(
        seed: SceneSeed,
        suffix: String,
        title: String,
        description: String,
        goal: String,
        level: String,
        openingEnglish: String,
        openingIndonesian: String,
        steps: List<ConversationStep>,
        closingEnglish: String,
        closingIndonesian: String
    ): ConversationScenario {
        return ConversationScenario(
            id = "${seed.id}_$suffix",
            sceneId = seed.id,
            title = title,
            description = description,
            goal = goal,
            level = level,
            partner = seed.partner,
            openingLines = listOf(
                ConversationLine(
                    character = seed.partner,
                    english = "Hi, I am ${seed.partner.name}. We are ${seed.locationEnglish} today.",
                    indonesian = "Hai, aku ${seed.partner.name}. Hari ini kita ${seed.locationIndonesian}."
                ),
                ConversationLine(
                    character = seed.partner,
                    english = openingEnglish,
                    indonesian = openingIndonesian
                )
            ),
            steps = steps,
            closingLine = ConversationLine(
                character = seed.partner,
                english = closingEnglish,
                indonesian = closingIndonesian
            )
        )
    }

    private fun step(
        character: ConversationCharacter,
        english: String,
        indonesian: String,
        firstChoice: ConversationChoice,
        secondChoice: ConversationChoice
    ): ConversationStep {
        return ConversationStep(
            prompt = ConversationLine(character, english, indonesian),
            choices = listOf(firstChoice, secondChoice)
        )
    }

    private fun choice(
        english: String,
        indonesian: String,
        responder: ConversationCharacter,
        responseEnglish: String,
        responseIndonesian: String
    ): ConversationChoice {
        return ConversationChoice(
            english = english,
            indonesian = indonesian,
            response = ConversationLine(responder, responseEnglish, responseIndonesian)
        )
    }
}

private data class SceneSeed(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val accentColor: Color,
    val locationEnglish: String,
    val locationIndonesian: String,
    val itemOneEnglish: String,
    val itemOneIndonesian: String,
    val itemTwoEnglish: String,
    val itemTwoIndonesian: String,
    val activityOneEnglish: String,
    val activityOneIndonesian: String,
    val activityTwoEnglish: String,
    val activityTwoIndonesian: String,
    val helperEnglish: String,
    val helperIndonesian: String,
    val destinationEnglish: String,
    val destinationIndonesian: String,
    val partner: ConversationCharacter
)
