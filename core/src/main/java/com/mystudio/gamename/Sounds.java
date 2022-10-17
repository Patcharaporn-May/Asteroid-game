/*******************************************************************************
 * Copyright 2020 Viridian Software Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.mystudio.gamename;

import java.io.IOException;
import org.mini2Dx.core.Mdx;
import org.mini2Dx.core.audio.Sound;

public class Sounds {
    private static final String DEATH_SFX = "assets/Sounds/SoundEffects/death1.mp3";
    private static final String SHOOT_SFX = "assets/Sounds/SoundEffects/lasergun.wav";
    private static final String EXPLODE_SFX = "assets/Sounds/SoundEffects/Explosion1.mp3";

    public Sound death;
    public Sound explode;
    public Sound shoot;

    public Sounds() {
        try {
            death = Mdx.audio.newSound(Mdx.files.external(DEATH_SFX));
            explode = Mdx.audio.newSound(Mdx.files.external(EXPLODE_SFX));
            shoot = Mdx.audio.newSound(Mdx.files.external(SHOOT_SFX));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

