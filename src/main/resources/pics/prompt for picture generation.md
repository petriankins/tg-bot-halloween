# старые хотелки
у меня есть текстовая ролевая игра в виде телеграм бота, она конфигурируется файлом. 
Я собираюсь использовать Gemini, чтобы сгенерировать к ней картинки. 
Проанализируй конфиг и помоги создать набор промптов, которые я мог бы использовать.
к сожалению в текущей архитектуре я должен положить в папку картинки с таким же номером, 
как номер сценария, чтобы она отобразилась вместе с описанием. 
Одна и та же картинка может использоваться для разных айди сценариев
мне можно применять их в одном чате друг за другом? 
сейчас есть проблема, что я применял их друг за другом, 
добавляя базовый промпт в конце, то когда я вводил "3. Морозный Угол", 
она взял картинку от "2. Общий зал Таверны (Внутри)" и просто покрыл ее льдом, 
что было довольно глупо, я бы хотел, чтобы консистентность сохранялась, 
но при этом не настолько топорно, что мы просто добавляем что-то поверх старого изображения. Может, нужная смена ракурса или зум
также я хотел бы стилистику что-то среднее между 
кельтикой, анимациями Тима Бертона и Тома Мура с довольно яркими цветами


# метапромпт
Привет! Я хочу, чтобы ты выступил в роли генератора промптов для создания изображений. Я предоставлю тебе свой сценарий текстовой ролевой игры. Твоя задача — внимательно прочитать этот сценарий и сгенерировать для меня список промптов для генерации изображений, по одному для каждой ключевой сцены, локации или важного выбора в игре.
Для КАЖДОГО промпта, который ты сгенерируешь, ты ДОЛЖЕН строго следовать этим правилам:
Язык: Все сгенерированные промпты должны быть на английском языке, так как это дает лучший результат при генерации изображений.
Содержание: Промпт должен четко описывать локацию, атмосферу и ключевые элементы сцены.
Избегание повторов: Чтобы избежать проблемы "добавления льда поверх таверны", каждый промпт должен быть полностью независимым. Если две сцены происходят в одной локации (например, "Общий зал" и "Угол в том же зале"), попробуй предложить разные ракурсы (например, wide-angle view для зала и close-up shot on a table для угла).
Стиль: В конце каждого промпта должен быть указан следующий набор стилей (это наш новый, согласованный стиль): Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration.
Отсутствие текста: Сразу после стиля, в конце каждого промпта, должно быть указано: --no text, words, letters, signature, watermark
Формат твоего ответа: Ты должен предоставить мне только список промптов, готовых к копированию. Не добавляй никаких лишних комментариев, объяснений или приветствий, кроме самого списка.
Пример того, как должен выглядеть ОДИН из сгенерированных тобой промптов: A wide-angle view of a cozy, bustling tavern interior. A large fireplace burns brightly in the center. Characters are visible as whimsical silhouettes. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark
Пример другого промпта: A close-up shot of a frosty, cobweb-covered table in a dark corner of a tavern. A single blue-glowing mushroom grows on the damp wood. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark
Если ты понял задачу, просто ответь "ГОТОВ", и я пришлю сценарий.

# промпты
A wide-angle view of a small, rustic village shrouded in thick, swirling fog under a dark night sky. A single elder figure hands a small bundle to a character silhouetted against a hut's dim doorway. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A crossroads at a forest's edge under a dead moon. To the left, a gloomy burial mound. Straight ahead, a dense forest with faint, twinkling lights. To the right, the silhouette of a ruined wooden chapel. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A close-up shot of the collapsed entrance to a stone barrow. Ancient bones and rusted weapons are scattered among the rubble. A cold wind blows from the dark interior. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

The dark interior of a burial mound. The stone walls are barely visible, leading towards a faint, eerie greenish light in the distance. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A central chamber inside a barrow, illuminated by a deathly green glow. A ghostly warrior figure in decayed armor stands guard, its eyes burning green. Behind it, a rough stone altar. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

The entrance to a whispering, ancient forest. A narrow path splits into three, vanishing into the darkness between gnarled, oversized trees. Faint, ethereal lights twinkle deep within the woods. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A magical clearing in the woods where beautiful and terrifying fae creatures dance in a wild circle. Their forms are elongated and whimsical, their movements both graceful and menacing. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A character standing alone in a dark, tangled forest, surrounded by floating, dancing orbs of pale blue and green light (will-o-wisps) that cast strange shadows. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A striking figure of Mari Lwyd, a white, formless shape wearing a large horse skull, standing on a forest path. It stares with empty eye sockets, its jaw clicking. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

The interior of a burnt-out, ruined chapel, overgrown with vines and moss. Faded, ancient murals are visible on the crumbling stone walls, depicting a forgotten ritual. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A circle of ancient, towering standing stones humming with a visible, magical energy. The air between the stones warps and shimmers, forming a portal to another world. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A surreal, timeless landscape (Annwn). Before a character stands a tall, weeping female figure, the Mistress of the land. The world around her seems to be draining of color as she cries. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A desolate, empty village covered entirely in a thick layer of frost. All houses are dark and silent. A lone character stands in the street, a ghostly rustling sound coming from behind. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

A character playing a small yew flute in the center of a glowing stone circle. A beautiful female spirit, the Mistress, smiles as the melody restores vibrant life and magic to the land. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark


## meta 2
Привет! Я хочу, чтобы ты выступил в роли генератора промптов для создания изображений. Я предоставлю тебе свой сценарий текстовой ролевой игры. Твоя задача — внимательно прочитать этот сценарий и сгенерировать для меня список промптов для генерации изображений, по одному для каждой ключевой сцены, локации или важного выбора в игре.

Для КАЖДОГО промпта, который ты сгенерируешь, ты ДОЛЖЕН строго следовать этим правилам:

Язык: Все сгенерированные промпты должны быть на английском языке.

Содержание: Промпт должен четко описывать локацию, атмосферу и ключевые элементы сцены, используя экспрессивные образы.

Избегание повторов: Каждый промпт должен быть независимым. Если две сцены происходят в одной локации, предлагай разные ракурсы (например, wide-angle view, close-up shot, low-angle perspective), чтобы избежать простого "добавления" деталей поверх старого изображения.

Стиль (Очень важно!): В конце каждого промпта должен быть указан следующий, очень специфический набор стилей: Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration.

Отсутствие текста: Сразу после стиля, в конце каждого промпта, должно быть указано: --no text, words, letters, signature, watermark

Формат твоего ответа (Очень важно!): Ты должен предоставить мне список промптов, готовых к копированию. Над каждым промптом ты должен вставить строку-комментарий, начинающуюся с // и содержащую номера сценариев (ID), к которым этот промпт подходит. Это нужно мне для сопоставления.

Пример того, как должен выглядеть твой ответ:

// Сценарии: 1, 2, 5 A wide-angle view of a cozy, bustling tavern interior. Angular, distorted wooden beams cast long shadows. A large expressionist fireplace burns brightly. Whimsical silhouettes of characters. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 3, 4 A close-up shot of a frosty, cobweb-covered table in a dark, angular corner of a tavern. A single blue-glowing mushroom grows on the damp, cracked wood. The geometry of the table is slightly broken. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарий: 6 A gnarled, spooky forest path under a full moon. The trees have sharp, angular branches. Intricate celtic patterns are carved into the visible roots. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

Если ты понял задачу, просто ответь "ГОТОВ", и я пришлю сценарий.


## prompts 2
// Сценарии: 0, 1
A wide-angle view of a gothic, mist-shrouded village during Samhain. Angular, distorted huts cast long shadows under a pale moon. An anxious elder with sharp, expressionistic features stands in a dimly lit doorway facing the inside. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 2
A low-angle, wide view of a spooky crossroads at a forest's edge under a dead moon. To the left, an ancient burial mound with broken stones. In the center, a dark forest path with glowing, whimsical lights. To the right, the angular, ruined silhouette of a wooden chapel. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 10, 12
An eye-level shot of the collapsed, angular stone entrance to a dark burial mound. Old bones and rusty, broken weapons are scattered around the entrance. Intricate celtic patterns are carved into the heavy stone lintel. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 11
A first-person perspective view from inside a dark, narrow tomb passage. The angular stone walls are close and oppressive. Far in the distance, a faint, sickly green light glows, beckoning forward. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 13, 15
A medium shot of a central tomb chamber. A spectral warrior in decayed, angular armor stands guard. His eyes glow with a dead green light. Behind him, a cracked stone altar is partially visible. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 16
A close-up shot of a cold, cracked stone altar inside a tomb. In the center of the altar is a small, dark depression. The light from the spirit warrior (off-screen) casts a green, angular shadow across it. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 14
A low-angle shot of the spirit warrior, now calm, pointing with a gauntleted hand towards a dark, hidden passage in the tomb wall, leading out to the forest. Whimsical light filters in. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 20, 23
A wide-angle view of a whispering forest interior. Gnarled, angular trees create a dense canopy. A path splits: left leads to a whimsical, laughing glow; right to a dark path with hoof prints; center leads to flickering will-o-wisp lights. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 21, 25
A high-angle, swirling perspective of a circle of beautiful and terrifying Fae dancing in a moonlit clearing. Their forms are angular and whimsical, their shadows long and distorted. The geometry of the clearing seems to bend with their dance. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 22
A path leading deeper into the angular forest, away from the Fae clearing (off-screen). In the far distance, through the gnarled trees, the tall, broken silhouettes of a stone circle are visible. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 24
An eye-level shot from the perspective of someone surrounded by dancing, whimsical will-o-wisp lights in a dark, thorny part of the expressionist forest. The lights cast shifting, angular shadows. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 30
A low-angle shot of the Mari Lwyd, a terrifying figure in a white, shapeless sheet with a massive, angular horse skull for a head. It stands in the dark forest, its empty eye sockets staring down. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 31
A medium shot of the Mari Lwyd, its horse skull head tilted, pointing a stick-like arm (hidden under the sheet) towards the path to the stone circle. The forest seems to part for the path. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 50
A wide-angle interior view of a ruined, overgrown wooden chapel. Moonlight streams through the broken, angular roof beams. On a crumbling wall, a faded mural shows expressionist figures in a ritual, with intricate celtic patterns on the border. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 40
A low-angle, wide shot of an ancient stone circle. The tall, angular stones are covered in glowing, intricate celtic patterns and seem to vibrate with energy. The center of the circle shimmers like a distorted, broken portal. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 41, 42
A wide, epic shot of a surreal, timeless landscape (Annwn). A colossal, weeping goddess figure made of light and shadow dominates the scene. Her tears fall and decay the broken, geometric ground beneath her. She has an expression of deep, expressionistic sorrow. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 101 (Плохая Концовка - Трусость)
A wide, desolate shot of the starting village, now completely empty and silent. A thick, angular frost covers all the whimsical, gothic huts. The protagonist's shadow is cast long and alone in the street. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 102 (Нейтральная Концовка)
A high-angle shot looking down on the village. The mist has receded, but the colors are muted and a dark, expressionistic cloud still lingers on the angular horizon, promising a return. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 103 (Плохая Концовка - Агрессия)
A close-up shot of a village grain store, filled with rotted, black, angular stalks. The geometry of the storage bin is broken and decayed. A ghostly, cold light emanates from the grain. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 104 (Хорошая Концовка)
A close-up shot on the face of the village elder. His expression is a mix of relief and deep, expressionistic sadness. The vibrant colors of the world around him have faded to dull, muted tones. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 105 (Плохая Концовка - Безумие)
A haunting medium shot of the protagonist, sitting as an empty, broken shell in the village. Their eyes are wide and vacant, staring at nothing, with whimsical, dark shapes swirling around their head. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 106 (Истинная Концовка)
An epic wide shot of the protagonist standing heroically in the center of the glowing stone circle, playing a whimsical flute. The world is bursting with vibrant, rich colors. In the sky, the spectral, smiling face of the Lady of the Land looks down approvingly. Style: 2D animation style, German expressionism, broken geometry, angular shapes, whimsical gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark


# meta 3
Привет! Я хочу, чтобы ты выступил в роли генератора промптов для создания изображений. Я предоставлю тебе свой сценарий текстовой ролевой игры. Твоя задача — внимательно прочитать этот сценарий и сгенерировать для меня список промптов для генерации изображений, по одному для каждой ключевой сцены, локации или важного выбора в игре.

Для КАЖДОГО промпта, который ты сгенерируешь, ты ДОЛЖЕН строго следовать этим правилам:

Язык: Все сгенерированные промпты должны быть на английском языке.

Содержание: Промпт должен четко описывать локацию, атмосферу и ключевые элементы сцены.

Избегание повторов: Каждый промпт должен быть независимым. Если две сцены происходят в одной локации, предлагай разные ракурсы (например, wide-angle view, close-up shot, low-angle perspective), чтобы избежать простого "добавления" деталей поверх старого изображения.

Стиль: В конце каждого промпта должен быть указан следующий, очень специфический набор стилей: Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration.

Отсутствие текста: Сразу после стиля, в конце каждого промпта, должно быть указано: --no text, words, letters, signature, watermark

Формат твоего ответа (Очень важно!): Ты должен предоставить мне список промптов, готовых к копированию. Над каждым промптом ты должен вставить строку-комментарий, начинающуюся с // и содержащую номера сценариев (ID), к которым этот промпт подходит. Это нужно мне для сопоставления.

Пример того, как должен выглядеть твой ответ:

// Сценарии: 1, 2, 5 A wide-angle view of a cozy, bustling tavern interior. A large fireplace burns brightly in the center. Characters are visible as whimsical silhouettes. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 3, 4 A close-up shot of a frosty, cobweb-covered table in a dark corner of a tavern. A single blue-glowing mushroom grows on the damp wood. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарий: 6 A gnarled, spooky forest path under a full moon. The trees are whimsical and slightly gothic. Intricate celtic patterns are carved into the visible roots. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

Если ты понял задачу, просто ответь "ГОТОВ", и я пришлю сценарий.

# prompts 3
Вот промпты для генерации изображений:

// Сценарии: 1
A close-up shot of a worried, whimsical village elder inside a rustic, dark hut, thick gothic fog swirling outside the window. The elder is holding out a small, hand-tied **offering bundle of oats and rowan berries** (for the 'Take the bundle' action). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 2
A wide-angle, whimsical night scene at a desolate crossroads under a dead moon. To the left, a **gothic, ancient burial barrow** with intricate celtic patterns on its stones (for 'Go to the barrow'). Straight ahead, a **dense, spooky forest** with faint, glowing lights between the gnarled trees (for 'Go into the forest'). To the right, the **skeletal, ruined frame of an old wooden chapel** (for 'Go to the chapel'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 10
A low-angle view of the collapsed stone entrance to a dark burial barrow. In the foreground, a **pile of old bones and a complete skeleton** lies amongst rusty weapons (for the 'Kick the skeleton' action). The **dark, collapsed entrance** is visible (for 'Squeeze inside' / 'Examine the entrance'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 11
A view from inside a dark, claustrophobic stone corridor of a barrow. In the distance, a **muffled greenish light** glows (for 'Go to the light'). The nearby **stone walls are rough and searchable**, with cracks and crevices, one of which has **something paper-like sticking out** (for the 'Search the walls' action). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 12
A close-up shot of the ancient, gnarled stones at the barrow's entrance. One large stone has **faint, rune-like scratches** on it (for 'Examine the scratches'). **A tattered piece of parchment is visibly stuck in a crack** between two stones (for 'Get the stuck object'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 13
A wide-angle view of the central chamber of a barrow, lit by a dead green glow. In the center stands a **spectral warrior figure in rotted armor**, its eyes glowing green (for 'Attack the figure' / 'Talk to the spirit'). Behind the warrior, a **stone altar is visible** (for 'Approach the altar'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 14
The green-glowing spectral warrior, now non-threatening, **points towards a newly revealed secret passage** in the barrow wall (for 'Go through the secret passage'). The passage is dark and framed by gnarled roots. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 15
A terrifying close-up on the **spectral warrior's head as it turns with a sickening crunch**. Its two **glowing green eyes stare directly at the viewer**, full of menace. The stone altar is slightly out of focus in the background (for 'Run to the altar'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 16
A close-up view of the ancient stone altar. In the center of the slab is a **small, empty depression** (for 'Look closer at the altar'). The menacing spectral warrior is visible in the background, watching. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 20
A wide-angle view of a whispering, gnarled forest entrance. The path splits into three: one path to the left where **faint laughter echoes** (for 'Go to laughter'), one to the right where a **strange thudding hoof-like sound** originates (for 'Go to thudding'), and one straight ahead into the deep woods where **faint, twinkling lights** dance (for 'Go to lights'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 21
A whimsical and sinister forest clearing where a **circle of beautiful and terrible Fae figures are dancing** wildly (for 'Join the dance' / 'Sneak past'). They are beckoning towards the viewer with sly smiles (for 'Offer a deal' / 'Explain who you are'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 23
A view from a narrow barrow passage opening into a dark, whimsical forest. In the distance, a **circle of stones** is visible (for 'Go to stone circle'). Closer, half-hidden **among the gnarled trees, stands a tall, white, spectral figure** (for 'Approach white figure'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 24
A disorienting, whimsical forest scene where the player is surrounded by **dancing, hypnotic, twinkling lights** (will-o'-the-wisps) (for 'Follow them further' / 'Stop and wait'). The gnarled trees are repeating, suggesting a magical trap. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 25
A close-up shot of the beautiful and terrible Fae, who have stopped their dance and are **staring intently at the viewer with curious, predatory expressions**, waiting for an offer (for 'Refuse deal'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 30
A haunting, eye-level shot of **Mari Lwyd, a tall figure draped in a white sheet, holding a horse's skull on a pole**. The skull has empty, dark eye sockets and is clacking its bone jaw (for 'Strike' / 'Stand silently' / 'Ask about stone circle'). The setting is a dark, gothic forest path. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 31
A wide shot of Mari Lwyd, the white horse-skull figure, **pointing with a skeletal hand towards a path leading to a circle of stones** (for 'Go to stone circle'). The forest is dark and whimsical. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 50
The interior of a ruined, overgrown wooden chapel. The remaining walls are covered in **faded, whimsical murals depicting an ancient ritual** with a melody, a pact, and a Lady (for 'Examine the murals'). A clear, safe spot on the floor, lit by moonlight, invites the player to rest (for 'Rest' / 'Pray to old gods'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 40
A wide-angle view of an **ancient circle of standing stones**, covered in glowing celtic patterns. The stones are **humming with power, and the air between them shimmers**, forming a swirling portal to another world (for 'Enter without protection'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 41
A surreal, timeless landscape (Annwn). In the center floats a **powerful, spectral female figure, the Lady of the Land, who is weeping profusely**, her tears draining life from the world (for 'Attack' / 'Try to comfort her'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 42
A close-up on the weeping Lady of the Land in the surreal world of Annwn. She has stopped sobbing and is **looking directly at the viewer with an expression of desperate expectation**, waiting for a song (for 'Try to sing' / 'Attack'). Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 104 (Хорошая концовка)
A bittersweet, wide-angle view of the village, now safe. The forest in the background has lost its whimsical magic and looks normal and silent. An old, sad-looking elder watches the player. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark

// Сценарии: 106 (Истинная концовка)
A joyous, vibrant scene. The spectral Lady of the Land is **smiling warmly as the player plays a whimsical yew flute**. The surreal world of Annwn is now bright and full of life. In the background, the village is celebrating the true Samhain. Style: 2D animation style, mix of Tomm Moore and Tim Burton, whimsical and slightly gothic, intricate celtic patterns, vibrant rich colors, hand-drawn look, storybook illustration. --no text, words, letters, signature, watermark