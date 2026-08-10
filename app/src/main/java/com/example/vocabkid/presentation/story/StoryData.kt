package com.example.vocabkid.presentation.story

// ─── Data Models ─────────────────────────────────────────────────────────────

data class StoryQuiz(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String = ""
)

data class StoryScene(
    val id: Int,
    val imageKeyword: String,
    val narration: String,
    val quiz: StoryQuiz? = null
)

data class VocabWord(
    val english: String,
    val indonesian: String
)

data class StoryChapter(
    val id: Int,
    val title: String,
    val titleId: String,
    val emoji: String,
    val description: String,
    val gradientStart: Long,
    val gradientEnd: Long,
    val scenes: List<StoryScene>,
    val vocabWords: List<VocabWord>
)

// ─── Story Repository (hardcoded) ────────────────────────────────────────────

object StoryData {

    val chapters: List<StoryChapter> = listOf(

        // ── Chapter 1: The Lost Kitten ────────────────────────────────────────
        StoryChapter(
            id = 1,
            title = "The Lost Kitten",
            titleId = "Anak Kucing yang Tersesat",
            emoji = "🐱",
            description = "A brave child helps a lost kitten find its way home through the forest.",
            gradientStart = 0xFFFF9A56,
            gradientEnd = 0xFFFFD89B,
            vocabWords = listOf(
                VocabWord("forest", "hutan"),
                VocabWord("lost", "tersesat"),
                VocabWord("brave", "berani"),
                VocabWord("search", "mencari"),
                VocabWord("found", "menemukan"),
                VocabWord("friend", "teman"),
                VocabWord("help", "membantu"),
                VocabWord("kind", "baik hati")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "forest path sunlight",
                    narration = "One morning, Mia was walking in the forest near her house. The trees were tall and the air smelled of flowers. Mia loved going on adventures!"
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "lost kitten alone",
                    narration = "Suddenly, Mia heard a soft sound. Meow... meow... She saw a tiny kitten sitting alone under a tree. The kitten looked lost and a little scared.",
                    quiz = StoryQuiz(
                        question = "What does \"lost\" mean?",
                        options = listOf("Berlari cepat", "Tersesat / tidak tahu jalan", "Bermain sendirian", "Tidur nyenyak"),
                        correctIndex = 1,
                        explanation = "\"Lost\" artinya tidak tahu jalan pulang atau tersesat."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "child helping cat",
                    narration = "Mia was brave. She walked closer to the kitten and said, \"Don't worry, little one. I will help you find your home!\" The kitten looked at her with big, hopeful eyes.",
                    quiz = StoryQuiz(
                        question = "What does \"brave\" mean?",
                        options = listOf("Penakut", "Malas", "Berani", "Lapar"),
                        correctIndex = 2,
                        explanation = "\"Brave\" artinya berani menghadapi sesuatu yang sulit atau menakutkan."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "searching forest trail",
                    narration = "Together, Mia and the kitten began to search for the kitten's home. They walked through the forest, calling out softly. Mia was kind and patient.",
                    quiz = StoryQuiz(
                        question = "What does \"search\" mean?",
                        options = listOf("Berlari", "Mencari", "Makan", "Tidur"),
                        correctIndex = 1,
                        explanation = "\"Search\" artinya mencari sesuatu dengan sungguh-sungguh."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "happy cat owner reunion",
                    narration = "After a while, they found a small house at the edge of the forest. An old woman came out and cried, \"My kitten!\" Mia had made a new friend that day!",
                    quiz = StoryQuiz(
                        question = "What does \"found\" mean?",
                        options = listOf("Kehilangan", "Menemukan", "Menyembunyikan", "Menunggu"),
                        correctIndex = 1,
                        explanation = "\"Found\" adalah bentuk lampau dari \"find\", artinya menemukan sesuatu."
                    )
                )
            )
        ),

        // ── Chapter 2: The River Crossing ────────────────────────────────────
        StoryChapter(
            id = 2,
            title = "The River Crossing",
            titleId = "Menyeberangi Sungai",
            emoji = "🌊",
            description = "Dika and friends must cross a wide river to reach the treasure cave.",
            gradientStart = 0xFF2196F3,
            gradientEnd = 0xFF00BCD4,
            vocabWords = listOf(
                VocabWord("river", "sungai"),
                VocabWord("bridge", "jembatan"),
                VocabWord("water", "air"),
                VocabWord("swim", "berenang"),
                VocabWord("danger", "bahaya"),
                VocabWord("together", "bersama"),
                VocabWord("cross", "menyeberangi"),
                VocabWord("safe", "aman")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "wide river nature sunlight",
                    narration = "Dika and his friends stood at the edge of a wide river. The water was clear and sparkling in the sunlight. On the other side was the famous Treasure Cave!"
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "broken old wooden bridge",
                    narration = "They found an old bridge, but it was broken in the middle. \"This is dangerous,\" said Sari. \"We cannot walk on it.\" Everyone looked worried.",
                    quiz = StoryQuiz(
                        question = "What does \"bridge\" mean?",
                        options = listOf("Perahu", "Jembatan", "Batu besar", "Pohon"),
                        correctIndex = 1,
                        explanation = "\"Bridge\" artinya jembatan, struktur untuk menyeberangi sungai atau jurang."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "children teamwork together",
                    narration = "\"Let's solve this together!\" said Dika. They collected fallen branches and made a new path across. Working together made everything possible!",
                    quiz = StoryQuiz(
                        question = "What does \"together\" mean?",
                        options = listOf("Sendiri", "Bersama-sama", "Pelan-pelan", "Cepat-cepat"),
                        correctIndex = 1,
                        explanation = "\"Together\" artinya bersama-sama, melakukan sesuatu secara bersama."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "child swimming river",
                    narration = "Budi was the best swimmer. He chose to swim across first and tied a rope to a tree on the other side. \"Be careful!\" shouted his friends from the bank.",
                    quiz = StoryQuiz(
                        question = "What does \"swim\" mean?",
                        options = listOf("Berlari", "Terbang", "Berenang", "Melompat"),
                        correctIndex = 2,
                        explanation = "\"Swim\" artinya berenang, bergerak di dalam air."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "treasure cave adventure",
                    narration = "One by one, they held the rope and crossed the river safely. Everyone cheered! They were all safe and ran toward the glittering Treasure Cave together.",
                    quiz = StoryQuiz(
                        question = "What does \"safe\" mean?",
                        options = listOf("Bahaya", "Aman / tidak dalam bahaya", "Lelah", "Takut"),
                        correctIndex = 1,
                        explanation = "\"Safe\" artinya aman, tidak dalam keadaan bahaya."
                    )
                )
            )
        ),

        // ── Chapter 3: The Secret Garden ─────────────────────────────────────
        StoryChapter(
            id = 3,
            title = "The Secret Garden",
            titleId = "Taman Tersembunyi",
            emoji = "🌺",
            description = "Lina discovers a magical hidden garden full of colorful flowers and friendly bees.",
            gradientStart = 0xFF9C27B0,
            gradientEnd = 0xFFE91E63,
            vocabWords = listOf(
                VocabWord("garden", "taman"),
                VocabWord("secret", "rahasia"),
                VocabWord("colorful", "penuh warna"),
                VocabWord("flower", "bunga"),
                VocabWord("bee", "lebah"),
                VocabWord("honey", "madu"),
                VocabWord("beautiful", "indah"),
                VocabWord("discover", "menemukan")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "old stone wall garden door",
                    narration = "Lina was exploring behind her grandmother's house when she noticed a small wooden door in an old wall. A sign said: \"Secret Garden — Enter with Kindness.\""
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "beautiful garden flowers bloom",
                    narration = "Lina opened the door and gasped. Inside was the most beautiful garden she had ever seen! Colorful flowers of every size bloomed everywhere.",
                    quiz = StoryQuiz(
                        question = "What does \"beautiful\" mean?",
                        options = listOf("Jelek / buruk", "Indah / cantik", "Besar", "Berbahaya"),
                        correctIndex = 1,
                        explanation = "\"Beautiful\" artinya indah atau cantik, sangat menyenangkan untuk dilihat."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "bee flower honey garden",
                    narration = "Tiny bees buzzed from flower to flower, collecting nectar. \"They make honey!\" whispered Lina. She watched them carefully, not wanting to disturb their work.",
                    quiz = StoryQuiz(
                        question = "What does \"bee\" mean?",
                        options = listOf("Kupu-kupu", "Lebah", "Nyamuk", "Semut"),
                        correctIndex = 1,
                        explanation = "\"Bee\" artinya lebah, serangga yang menghasilkan madu."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "colorful wildflowers field",
                    narration = "The garden was full of colorful flowers — red roses, yellow sunflowers, purple lavender, and pink tulips. Each flower had a sweet smell that made Lina smile.",
                    quiz = StoryQuiz(
                        question = "What does \"colorful\" mean?",
                        options = listOf("Berwarna gelap", "Hitam putih", "Penuh dengan berbagai warna", "Sangat kecil"),
                        correctIndex = 2,
                        explanation = "\"Colorful\" artinya memiliki banyak warna yang cerah dan beragam."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "happy girl garden discovery",
                    narration = "Lina had made an amazing discovery! She decided to keep the secret garden safe and visit every day. She left a little honey cake by the door as a thank-you gift.",
                    quiz = StoryQuiz(
                        question = "What does \"discover\" mean?",
                        options = listOf("Menyembunyikan sesuatu", "Menemukan sesuatu baru", "Menghancurkan", "Melupakan"),
                        correctIndex = 1,
                        explanation = "\"Discover\" artinya menemukan atau mengungkap sesuatu yang belum diketahui."
                    )
                )
            )
        ),

        // ── Chapter 4: Space Explorer ─────────────────────────────────────────
        StoryChapter(
            id = 4,
            title = "Space Explorer",
            titleId = "Penjelajah Luar Angkasa",
            emoji = "🚀",
            description = "Zara blasts off into outer space and discovers planets, stars, and friendly aliens!",
            gradientStart = 0xFF1A237E,
            gradientEnd = 0xFF7B1FA2,
            vocabWords = listOf(
                VocabWord("planet", "planet"),
                VocabWord("star", "bintang"),
                VocabWord("rocket", "roket"),
                VocabWord("galaxy", "galaksi"),
                VocabWord("gravity", "gravitasi"),
                VocabWord("astronaut", "astronot"),
                VocabWord("orbit", "mengorbit"),
                VocabWord("alien", "makhluk luar angkasa")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "rocket launch night sky",
                    narration = "Zara had always dreamed of going to space. One starry night, she climbed into her silver rocket, pressed the big red button, and — WHOOSH! — she blasted off into the dark, glittering sky!"
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "colorful planets solar system",
                    narration = "Zara flew past the red planet Mars and the giant ringed planet Saturn. Each planet looked different — some were hot, some cold, some stormy. The galaxy was huge and breathtaking!",
                    quiz = StoryQuiz(
                        question = "What does \"planet\" mean?",
                        options = listOf("Bintang yang bersinar", "Benda langit besar yang mengorbit bintang", "Bulan kecil", "Pesawat luar angkasa"),
                        correctIndex = 1,
                        explanation = "\"Planet\" adalah benda langit besar yang mengelilingi (mengorbit) sebuah bintang."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "astronaut floating space station",
                    narration = "Zara put on her spacesuit and floated outside the rocket. Without gravity, she drifted like a feather. \"I'm an astronaut!\" she laughed, spinning slowly among the stars.",
                    quiz = StoryQuiz(
                        question = "What does \"gravity\" mean?",
                        options = listOf("Kecepatan tinggi", "Gaya tarik bumi yang menarik benda ke bawah", "Udara di luar angkasa", "Cahaya bintang"),
                        correctIndex = 1,
                        explanation = "\"Gravity\" adalah gaya yang menarik benda-benda menuju pusat bumi atau benda langit lainnya."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "friendly alien cartoon space",
                    narration = "Near a purple moon, Zara spotted a small glowing ship. Out came a tiny green alien with three eyes and a big smile. \"Hello, Earth friend!\" it said. \"We are neighbors in this galaxy!\"",
                    quiz = StoryQuiz(
                        question = "What does \"galaxy\" mean?",
                        options = listOf("Satu bintang tunggal", "Sistem tata surya kecil", "Kumpulan miliaran bintang dan planet", "Lubang hitam di langit"),
                        correctIndex = 2,
                        explanation = "\"Galaxy\" adalah kumpulan miliaran bintang, planet, dan materi lainnya yang terikat oleh gravitasi."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "rocket landing earth sunrise",
                    narration = "After the most incredible adventure, Zara steered her rocket back toward Earth. As the blue planet grew bigger in her window, she smiled. \"Home is the most beautiful star of all,\" she whispered.",
                    quiz = StoryQuiz(
                        question = "What does \"orbit\" mean?",
                        options = listOf("Jatuh ke bumi dengan cepat", "Berputar mengelilingi benda langit lain", "Berhenti di luar angkasa", "Meledak seperti bintang"),
                        correctIndex = 1,
                        explanation = "\"Orbit\" artinya jalur atau gerakan melingkar suatu benda langit mengelilingi benda langit lain, seperti bumi mengorbit matahari."
                    )
                )
            )
        ),

        // ── Chapter 5: Ocean Depths ───────────────────────────────────────────
        StoryChapter(
            id = 5,
            title = "Ocean Depths",
            titleId = "Kedalaman Samudra",
            emoji = "🐠",
            description = "Dive into the mysterious deep ocean with Kai and discover amazing sea creatures!",
            gradientStart = 0xFF006064,
            gradientEnd = 0xFF0288D1,
            vocabWords = listOf(
                VocabWord("ocean", "samudra"),
                VocabWord("coral", "karang"),
                VocabWord("whale", "paus"),
                VocabWord("submarine", "kapal selam"),
                VocabWord("treasure", "harta karun"),
                VocabWord("deep", "dalam"),
                VocabWord("creature", "makhluk"),
                VocabWord("current", "arus")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "submarine underwater ocean surface",
                    narration = "Kai stepped into his small yellow submarine and dove beneath the waves. The sunlight faded as he went deeper. Around him, the ocean glowed with mystery and wonder."
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "colorful coral reef fish",
                    narration = "Kai discovered a magnificent coral reef! Thousands of fish in brilliant colors swam in and out. Orange clownfish, blue tangs, and silver barracudas danced through the coral like living rainbows.",
                    quiz = StoryQuiz(
                        question = "What does \"coral\" mean?",
                        options = listOf("Ikan besar di laut", "Tumbuhan laut berwarna-warni", "Struktur keras di laut yang dibuat hewan kecil", "Pasir di dasar laut"),
                        correctIndex = 2,
                        explanation = "\"Coral\" adalah struktur keras yang terbentuk dari hewan kecil bernama polip, membentuk terumbu karang yang menjadi rumah bagi banyak makhluk laut."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "giant blue whale underwater",
                    narration = "Suddenly, the water around Kai rumbled. A gigantic blue whale swam past — the largest creature on Earth! It sang a deep, haunting song that echoed through the water. Kai felt tiny but amazed.",
                    quiz = StoryQuiz(
                        question = "What does \"creature\" mean?",
                        options = listOf("Sebuah benda mati", "Makhluk hidup / hewan", "Tanaman bawah laut", "Batu karang besar"),
                        correctIndex = 1,
                        explanation = "\"Creature\" artinya makhluk hidup, terutama hewan."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "underwater treasure chest sunken ship",
                    narration = "Deep on the ocean floor, Kai spotted a sunken old ship covered in barnacles. Through the porthole, something glittered. A treasure chest! Inside were ancient golden coins and sparkling gems.",
                    quiz = StoryQuiz(
                        question = "What does \"treasure\" mean?",
                        options = listOf("Kapal yang tenggelam", "Kumpulan barang berharga / harta karun", "Batu besar di dasar laut", "Jenis ikan langka"),
                        correctIndex = 1,
                        explanation = "\"Treasure\" artinya harta karun, yaitu kumpulan barang-barang berharga."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "submarine surface ocean sunset",
                    narration = "As Kai surfaced back into the golden sunset, he looked at his photos of the whale, the coral, and the treasure. The ocean had shared its deepest secrets with him. He couldn't wait to come back.",
                    quiz = StoryQuiz(
                        question = "What does \"deep\" mean?",
                        options = listOf("Sangat dangkal", "Sangat jauh ke bawah / dalam", "Sangat lebar", "Sangat panas"),
                        correctIndex = 1,
                        explanation = "\"Deep\" artinya sangat jauh ke bawah atau memiliki jarak yang jauh dari permukaan ke dasar."
                    )
                )
            )
        ),

        // ── Chapter 6: Dragon Mountain ────────────────────────────────────────
        StoryChapter(
            id = 6,
            title = "Dragon Mountain",
            titleId = "Gunung Naga",
            emoji = "🐉",
            description = "Brave Arlo climbs a magical mountain to befriend the legendary fire dragon!",
            gradientStart = 0xFFB71C1C,
            gradientEnd = 0xFFFF6F00,
            vocabWords = listOf(
                VocabWord("mountain", "gunung"),
                VocabWord("dragon", "naga"),
                VocabWord("fire", "api"),
                VocabWord("climb", "mendaki"),
                VocabWord("legend", "legenda"),
                VocabWord("cave", "gua"),
                VocabWord("roar", "mengaum"),
                VocabWord("courage", "keberanian")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "tall mountain fog mystical",
                    narration = "At the edge of the kingdom stood Dragon Mountain, its peak hidden in swirling clouds. Everyone said a great dragon lived there. Only the bravest had ever dared to climb it."
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "boy hiking mountain trail",
                    narration = "Arlo was only ten years old, but his heart was full of courage. He packed food, a lantern, and a small golden flute, then began to climb the rocky mountain trail at dawn.",
                    quiz = StoryQuiz(
                        question = "What does \"courage\" mean?",
                        options = listOf("Rasa takut yang besar", "Keberanian untuk menghadapi bahaya", "Kemampuan berlari cepat", "Kepandaian yang luar biasa"),
                        correctIndex = 1,
                        explanation = "\"Courage\" artinya keberanian, yaitu kemampuan untuk menghadapi sesuatu yang menakutkan atau sulit."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "dark cave mountain glow",
                    narration = "Near the peak, Arlo found a massive cave glowing with orange light. The ground shook as a ROAR echoed out — so loud that pebbles bounced. Arlo took a deep breath and stepped inside.",
                    quiz = StoryQuiz(
                        question = "What does \"cave\" mean?",
                        options = listOf("Puncak gunung yang tinggi", "Lubang atau ruang di dalam batu / gunung", "Jalan berbatu yang panjang", "Sungai di atas gunung"),
                        correctIndex = 1,
                        explanation = "\"Cave\" artinya gua, yaitu ruang alami yang terbentuk di dalam bebatuan atau di bawah tanah."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "dragon fire sad lonely",
                    narration = "Inside, a massive red dragon lay curled up. But it wasn't roaring in anger — it was crying! A thorn was stuck deep in its wing. Arlo understood. The dragon wasn't scary. It was just in pain.",
                    quiz = StoryQuiz(
                        question = "What does \"roar\" mean?",
                        options = listOf("Berbisik pelan", "Bernyanyi merdu", "Mengeluarkan suara keras dan menggelegar", "Tertawa kecil"),
                        correctIndex = 2,
                        explanation = "\"Roar\" artinya mengaum atau mengeluarkan suara keras dan lantang, seperti suara singa atau naga."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "boy dragon friendship mountain",
                    narration = "Arlo gently played his golden flute to calm the dragon, then carefully removed the thorn. The dragon's eyes went wide with gratitude. It blew a tiny, warm flame — its way of saying \"thank you.\" Arlo had found a legendary friend.",
                    quiz = StoryQuiz(
                        question = "What does \"legend\" mean?",
                        options = listOf("Cerita yang baru terjadi kemarin", "Kisah atau tokoh terkenal yang diceritakan turun-temurun", "Buku pelajaran di sekolah", "Gambar di peta"),
                        correctIndex = 1,
                        explanation = "\"Legend\" artinya legenda, yaitu kisah atau tokoh yang sangat terkenal dan telah diceritakan selama generasi."
                    )
                )
            )
        ),

        // ── Chapter 7: The Time Travel Market ────────────────────────────────
        StoryChapter(
            id = 7,
            title = "The Time Travel Market",
            titleId = "Pasar Perjalanan Waktu",
            emoji = "⏰",
            description = "Nadia finds a magical clock that takes her to a colorful market from 100 years ago!",
            gradientStart = 0xFF4A148C,
            gradientEnd = 0xFF00BFA5,
            vocabWords = listOf(
                VocabWord("market", "pasar"),
                VocabWord("ancient", "kuno"),
                VocabWord("trade", "berdagang"),
                VocabWord("currency", "mata uang"),
                VocabWord("history", "sejarah"),
                VocabWord("merchant", "pedagang"),
                VocabWord("century", "abad"),
                VocabWord("journey", "perjalanan")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "antique clock shop magical",
                    narration = "Nadia found a dusty golden clock at her grandfather's attic. When she wound its key, the hands spun wildly and the room blurred. When everything stopped, she was standing in the middle of a very different street!"
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "old traditional market 1900s",
                    narration = "The market around her was bustling with merchants in old-fashioned clothes. Horse-drawn carts carried goods. Signs were written in old script. She had traveled back a whole century in time!",
                    quiz = StoryQuiz(
                        question = "What does \"century\" mean?",
                        options = listOf("Sepuluh tahun", "Seratus tahun", "Seribu tahun", "Satu juta tahun"),
                        correctIndex = 1,
                        explanation = "\"Century\" artinya seratus tahun, atau satu abad."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "merchant selling spices bazaar",
                    narration = "A friendly merchant offered Nadia a bowl of warm spiced rice. \"Trade your button for it!\" he said cheerfully. Nadia learned that before paper money, people traded objects for goods in ancient times.",
                    quiz = StoryQuiz(
                        question = "What does \"merchant\" mean?",
                        options = listOf("Pembeli di pasar", "Orang yang menjual barang / pedagang", "Penjaga toko yang tidak menjual apapun", "Pemimpin sebuah kota"),
                        correctIndex = 1,
                        explanation = "\"Merchant\" artinya pedagang, yaitu seseorang yang membeli dan menjual barang untuk mencari keuntungan."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "coins ancient currency marketplace",
                    narration = "Nadia saw people using strange coins she had never seen before. \"This is our currency,\" explained a girl her age. \"Each coin shows the face of our king.\" Nadia was fascinated by how history worked differently.",
                    quiz = StoryQuiz(
                        question = "What does \"currency\" mean?",
                        options = listOf("Benda antik yang langka", "Sistem uang yang digunakan dalam perdagangan", "Jenis pasar tertentu", "Sebuah koin emas besar"),
                        correctIndex = 1,
                        explanation = "\"Currency\" artinya mata uang, yaitu sistem uang yang digunakan oleh suatu negara atau masyarakat untuk bertransaksi."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "girl time travel clock glowing",
                    narration = "As the clock's hands began to spin again, Nadia grabbed a spice pouch as a souvenir. In a flash, she was back in the attic. History was no longer just a subject — it was a real, amazing journey she had lived.",
                    quiz = StoryQuiz(
                        question = "What does \"journey\" mean?",
                        options = listOf("Benda yang sangat berat", "Perjalanan panjang atau pengalaman", "Sebuah kata dalam kamus", "Tempat yang sangat jauh"),
                        correctIndex = 1,
                        explanation = "\"Journey\" artinya perjalanan, bisa berupa perjalanan fisik yang panjang atau pengalaman hidup yang bermakna."
                    )
                )
            )
        ),

        // ── Chapter 8: My Robot Friend ────────────────────────────────────────
        StoryChapter(
            id = 8,
            title = "My Robot Friend",
            titleId = "Teman Robotku",
            emoji = "🤖",
            description = "Felix builds a robot named Bolt and teaches it what it means to be a true friend.",
            gradientStart = 0xFF263238,
            gradientEnd = 0xFF00897B,
            vocabWords = listOf(
                VocabWord("robot", "robot"),
                VocabWord("program", "memprogram"),
                VocabWord("electricity", "listrik"),
                VocabWord("sensor", "sensor"),
                VocabWord("emotion", "emosi"),
                VocabWord("mechanical", "mekanik"),
                VocabWord("battery", "baterai"),
                VocabWord("friendship", "persahabatan")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "boy building robot garage",
                    narration = "Felix spent every weekend in his garage, surrounded by gears, wires, and blinking lights. He was building his greatest creation — a robot he would name BOLT. After months of work, he switched it on."
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "robot blinking eyes first boot",
                    narration = "BOLT's eyes flickered blue. It slowly stood up, looked at Felix, and said in a beeping voice: \"Hello. I am BOLT. I am fully charged.\" Felix cheered — the battery worked perfectly!",
                    quiz = StoryQuiz(
                        question = "What does \"battery\" mean?",
                        options = listOf("Kabel listrik panjang", "Alat penyimpan dan penyedia energi listrik", "Tombol on/off sebuah mesin", "Layar tampilan robot"),
                        correctIndex = 1,
                        explanation = "\"Battery\" artinya baterai, yaitu alat yang menyimpan energi listrik dan menyediakannya saat dibutuhkan."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "robot learning coding computer",
                    narration = "Felix programmed BOLT to help with homework and cook breakfast. But BOLT kept making mistakes — it put salt in the juice and used the wrong math formula. Programming was harder than Felix thought.",
                    quiz = StoryQuiz(
                        question = "What does \"program\" mean?",
                        options = listOf("Menghancurkan sebuah mesin", "Memberikan instruksi kepada komputer atau robot", "Membeli robot baru", "Memperbaiki kabel yang putus"),
                        correctIndex = 1,
                        explanation = "\"Program\" (sebagai kata kerja) artinya memberikan serangkaian instruksi kepada komputer atau mesin agar bisa melakukan tugas tertentu."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "robot helping child sad",
                    narration = "One day Felix was upset after losing a game. BOLT's sensors detected something different. \"Felix... are you experiencing sadness?\" BOLT asked quietly, bringing over Felix's favorite snack. Felix smiled — BOLT had learned emotion.",
                    quiz = StoryQuiz(
                        question = "What does \"sensor\" mean?",
                        options = listOf("Bagian robot yang bisa bergerak", "Alat yang mendeteksi perubahan di sekitarnya", "Layar yang menampilkan gambar", "Baut yang menahan robot"),
                        correctIndex = 1,
                        explanation = "\"Sensor\" adalah perangkat yang dapat mendeteksi perubahan fisik seperti cahaya, suhu, atau gerakan di lingkungan sekitarnya."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "boy robot best friends sunset",
                    narration = "Years passed, and BOLT became Felix's best friend — not because it was mechanical and perfect, but because it always tried, always listened, and always cared. Real friendship, Felix realized, isn't about being perfect.",
                    quiz = StoryQuiz(
                        question = "What does \"friendship\" mean?",
                        options = listOf("Persaingan antara dua orang", "Hubungan saling menyayangi antara dua orang / teman", "Kerja sama hanya untuk mendapat keuntungan", "Pertemuan singkat dengan orang asing"),
                        correctIndex = 1,
                        explanation = "\"Friendship\" artinya persahabatan, yaitu hubungan yang hangat, saling percaya, dan saling mendukung antara dua orang atau lebih."
                    )
                )
            )
        ),

        // ── Chapter 9: Jungle Expedition ─────────────────────────────────────
        StoryChapter(
            id = 9,
            title = "Jungle Expedition",
            titleId = "Ekspedisi Hutan Rimba",
            emoji = "🦁",
            description = "Young explorer Maya leads a team deep into the Amazon jungle to protect wildlife.",
            gradientStart = 0xFF1B5E20,
            gradientEnd = 0xFFF9A825,
            vocabWords = listOf(
                VocabWord("jungle", "hutan rimba"),
                VocabWord("expedition", "ekspedisi"),
                VocabWord("wildlife", "satwa liar"),
                VocabWord("protect", "melindungi"),
                VocabWord("endangered", "terancam punah"),
                VocabWord("predator", "predator"),
                VocabWord("habitat", "habitat"),
                VocabWord("survive", "bertahan hidup")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "amazon jungle dense trees mist",
                    narration = "Maya, age twelve, was the youngest member of the Wildlife Protection Team. Their mission: journey deep into the Amazon jungle and track the rare golden jaguar before poachers could find it."
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "jungle wildlife birds monkeys",
                    narration = "On the first day, the jungle came alive around them. Scarlet macaws screamed overhead, howler monkeys swung through the canopy, and a tapir waddled past their camp. The wildlife here was incredible.",
                    quiz = StoryQuiz(
                        question = "What does \"wildlife\" mean?",
                        options = listOf("Tanaman hias dalam pot", "Hewan dan tumbuhan liar yang hidup di alam bebas", "Hewan peliharaan di rumah", "Api yang membakar hutan"),
                        correctIndex = 1,
                        explanation = "\"Wildlife\" artinya satwa liar, yaitu hewan dan tumbuhan yang hidup secara alami di alam bebas tanpa campur tangan manusia."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "rare jaguar golden amazon",
                    narration = "On the third morning, Maya spotted golden paw prints in the mud. She followed them silently, heart pounding. Then — there it was! A magnificent golden jaguar, napping between two giant roots, safe in its natural habitat.",
                    quiz = StoryQuiz(
                        question = "What does \"habitat\" mean?",
                        options = listOf("Makanan utama seekor hewan", "Tempat alami di mana suatu makhluk hidup dan berkembang", "Jenis hewan yang langka", "Aturan perlindungan hewan"),
                        correctIndex = 1,
                        explanation = "\"Habitat\" adalah lingkungan alami tempat suatu organisme biasanya hidup, tumbuh, dan berkembang biak."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "endangered animal protection sign",
                    narration = "Maya's team carefully photographed the jaguar. \"It's endangered,\" whispered Dr. Ramos. \"Only fifty remain in this region.\" Maya felt a fire in her heart. She would do everything to protect these animals.",
                    quiz = StoryQuiz(
                        question = "What does \"endangered\" mean?",
                        options = listOf("Hidup dengan aman dan damai", "Dalam bahaya kepunahan / jumlahnya sangat sedikit", "Baru ditemukan oleh ilmuwan", "Hewan yang sangat berbahaya"),
                        correctIndex = 1,
                        explanation = "\"Endangered\" artinya terancam punah, yaitu suatu spesies yang jumlahnya sangat sedikit dan berisiko punah jika tidak dilindungi."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "expedition team triumph jungle",
                    narration = "Maya's report helped the government create a new protected zone for the golden jaguar. Back in school, she gave a presentation with her photos. One girl in the front row whispered: \"I want to be an explorer too.\" Maya smiled.",
                    quiz = StoryQuiz(
                        question = "What does \"protect\" mean?",
                        options = listOf("Menghancurkan sesuatu dengan sengaja", "Menjaga sesuatu agar tetap aman dari bahaya", "Membiarkan sesuatu terjadi begitu saja", "Mencari sesuatu di tempat tersembunyi"),
                        correctIndex = 1,
                        explanation = "\"Protect\" artinya melindungi, yaitu menjaga sesuatu atau seseorang agar tetap aman dari bahaya atau kerusakan."
                    )
                )
            )
        ),

        // ── Chapter 10: The Magic Library ────────────────────────────────────
        StoryChapter(
            id = 10,
            title = "The Magic Library",
            titleId = "Perpustakaan Ajaib",
            emoji = "📚",
            description = "When Sam opens a mysterious old book, the stories come alive and pull him inside!",
            gradientStart = 0xFF4E342E,
            gradientEnd = 0xFFAD1457,
            vocabWords = listOf(
                VocabWord("library", "perpustakaan"),
                VocabWord("chapter", "bab"),
                VocabWord("imagination", "imajinasi"),
                VocabWord("author", "penulis"),
                VocabWord("adventure", "petualangan"),
                VocabWord("knowledge", "pengetahuan"),
                VocabWord("character", "tokoh / karakter"),
                VocabWord("magical", "ajaib")
            ),
            scenes = listOf(
                StoryScene(
                    id = 1,
                    imageKeyword = "mysterious old library glowing books",
                    narration = "On a rainy afternoon, Sam found an old library hidden behind a waterfall in the park. The door creaked open by itself. The shelves stretched so high they disappeared into the clouds above."
                ),
                StoryScene(
                    id = 2,
                    imageKeyword = "glowing book floating pages magic",
                    narration = "One book glowed brighter than the rest. Its title shimmered: \"The Book of All Adventures.\" When Sam touched it, the pages flew open on their own and golden words swirled through the air like fireflies.",
                    quiz = StoryQuiz(
                        question = "What does \"magical\" mean?",
                        options = listOf("Biasa dan membosankan", "Sangat berat dan besar", "Penuh dengan keajaiban / seperti sihir", "Terbuat dari logam"),
                        correctIndex = 2,
                        explanation = "\"Magical\" artinya ajaib atau bersifat seperti sihir, sesuatu yang luar biasa dan tidak bisa dijelaskan dengan nalar biasa."
                    )
                ),
                StoryScene(
                    id = 3,
                    imageKeyword = "boy inside book story world",
                    narration = "In a blink, Sam was sucked into the book! He landed in a pirate ship in the middle of a stormy sea. He had become a character in the story! Around him, other characters pointed and yelled, \"The new author is here!\"",
                    quiz = StoryQuiz(
                        question = "What does \"character\" mean?",
                        options = listOf("Pengarang atau penulis cerita", "Tokoh atau peran yang ada dalam sebuah cerita", "Bab dari sebuah buku", "Jenis tulisan dalam buku"),
                        correctIndex = 1,
                        explanation = "\"Character\" dalam konteks cerita artinya tokoh atau karakter, yaitu orang atau makhluk yang berperan dalam sebuah cerita."
                    )
                ),
                StoryScene(
                    id = 4,
                    imageKeyword = "books flying imagination creative",
                    narration = "Sam realized the story would only continue if he used his imagination. He grabbed a glowing quill and wrote: \"The storm stopped. The sun came out.\" Instantly, the clouds parted! His words shaped the world around him.",
                    quiz = StoryQuiz(
                        question = "What does \"imagination\" mean?",
                        options = listOf("Kemampuan melihat hal-hal nyata", "Kemampuan menciptakan gambar atau ide dalam pikiran", "Buku yang sangat tebal", "Sebuah gambar yang indah"),
                        correctIndex = 1,
                        explanation = "\"Imagination\" artinya imajinasi, yaitu kemampuan pikiran untuk menciptakan gambaran, ide, atau konsep yang tidak ada di dunia nyata."
                    )
                ),
                StoryScene(
                    id = 5,
                    imageKeyword = "boy reading book library happy",
                    narration = "When the final chapter ended, Sam found himself back in the library, holding the now-quiet book. He understood now — every book held a whole world inside it. He picked up another one and began to read.",
                    quiz = StoryQuiz(
                        question = "What does \"knowledge\" mean?",
                        options = listOf("Rasa ingin tahu yang besar", "Informasi dan pemahaman yang diperoleh dari belajar", "Sebuah buku yang sangat mahal", "Kemampuan fisik yang kuat"),
                        correctIndex = 1,
                        explanation = "\"Knowledge\" artinya pengetahuan, yaitu informasi, fakta, dan pemahaman yang diperoleh seseorang melalui pengalaman atau pembelajaran."
                    )
                )
            )
        )
    )
}
