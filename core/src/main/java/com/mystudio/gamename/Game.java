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

import org.jetbrains.annotations.NotNull;
import org.mini2Dx.core.Graphics;
import org.mini2Dx.core.game.BasicGame;
import java.util.ArrayList;

public class Game extends BasicGame {
    public static final String GAME_IDENTIFIER = "com.mystudio.gamename";

    // Sprite file locations
    private static final int MAX_BULLET = 2;
    private static final int MAX_LARGE = 1;
    private static final int MAX_MEDIUM = 50;
    private static final int MAX_SMALL = 100;

    private BackGround bg;
    private Player player;
    private Bullet[] bullets = new Bullet[MAX_BULLET];
    // Used to pool our asteroids
    private Asteroid[] asteroids = new Asteroid[200];
    // any active ones we'll add here to loop through
    private ArrayList<Asteroid> activeAsteroids;
    private Score score = new Score();
    private Lives lives = new Lives();
    private UI ui;
    private Sounds myGameSounds;

    @Override
    public void initialise() {
        bg = new BackGround(ImageFilePaths.BACKGROUND);
        ui = new UI(lives.getRemainingLives(), score.getScore());
        player = new Player();
        initialiseAsteroids();
        initialiseBullets();
        myGameSounds = new Sounds();
    }

    private void initialiseBullets() {
        for (int i = 0; i < MAX_BULLET; i++) {
            bullets[i] = new Bullet(ImageFilePaths.LASER_BLUE);
        }
    }

    private void initialiseAsteroids() {
        activeAsteroids = new ArrayList<Asteroid>();

        int large = 0;
        for (; large < MAX_LARGE; large++) {
            asteroids[large] = new Asteroid(Asteroid.SIZE_LARGE_ASTEROID, true, 44);
            activeAsteroids.add(asteroids[large]);
        }

        int medium = 0;
        for (; medium < MAX_MEDIUM; medium++) {
            asteroids[large + medium] = new Asteroid(Asteroid.SIZE_MEDIUM_ASTEROID, false, 22);
        }

        int small = 0;
        for (; small < MAX_SMALL; small++) {
            asteroids[large + medium + small] =
                    new Asteroid(Asteroid.SIZE_SMALL_ASTEROID, false, 11);
        }
    }

    @Override
    public void update(float delta) {
        if(lives.getRemainingLives()!=0){ // all processes will stop if remaining lives is 0
            player.update(delta); // player rendering
            for (Asteroid asteroid : activeAsteroids) {
                asteroid.update(delta); // asteroid rendering
            }
        }else{player.isDeath();} // stop all processes
        activateBullet();

        for (int i = 0; i < MAX_BULLET; i++) {
            if (bullets[i].isActive()) {
                bullets[i].update(delta);
            }
        }

        if (player.isAlive()) {
            checkCollisions();
        } else {
            // flash
            for (Asteroid asteroid : activeAsteroids) {
                asteroid.setRandomPosition();
            }
            player.init();
        }
    }

    public void activateBullet() {
        if (!Utility.shootButtonPressed()) {
            return;
        }
        for (int i = 0; i < MAX_BULLET; i++) {
            if (bullets[i].isActive()) {
                continue;
            }
            bullets[i].init(player.getPosition(),
                    player.getFacingDirection(),
                    player.getAngle()
            );
            myGameSounds.shoot.play();
            break;
        }
    }

    public void checkCollisions() {
        checkBulletAsteroidCollisions();
        checkAsteroidPlayerCollisions();
    }

    private void checkBulletAsteroidCollisions() {
        for (int i = 0; i < MAX_BULLET; i++) {
            if (!bullets[i].isActive()) {
                continue;
            }

            for (Asteroid asteroid : activeAsteroids) {
                if (!bullets[i].getCollisionBox().intersects(asteroid.getCollisionCircle())) {
                    continue;
                }
                bullets[i].setActive(false);
                myGameSounds.explode.play();
                splitAsteroid(asteroid);
                break;
            }
        }// end for loop

    } // end checkBulletAsteroidCollisions

    private void checkAsteroidPlayerCollisions() {
        for (Asteroid asteroid : activeAsteroids) {
            if (!asteroid.getCollisionCircle().intersects(player.getPlayerCollisionCircle())) {
                continue;
            }
            player.setActive(false);
            lives.removeLife();
            myGameSounds.death.play();
            ui.set(lives.getRemainingLives(),score.getScore());
        }
    }

    private void splitAsteroid(@NotNull Asteroid asteroid) {
        if (asteroid.getSize() == Asteroid.SIZE_LARGE_ASTEROID) {
            placeAsteroid(MAX_LARGE, MAX_MEDIUM + MAX_LARGE,
                    asteroid.getCollisionCircle().getX(),
                    asteroid.getCollisionCircle().getY()
            );
            score.changeScoreByAmount(10);
        } else if (asteroid.getSize() == Asteroid.SIZE_MEDIUM_ASTEROID) {
            placeAsteroid(MAX_MEDIUM + MAX_LARGE,
                    MAX_SMALL + MAX_MEDIUM + MAX_LARGE,
                    asteroid.getCollisionCircle().getX(),
                    asteroid.getCollisionCircle().getY()
            );
            score.changeScoreByAmount(50);
        } else {
            score.changeScoreByAmount(100);

            for (int i = 0; i < MAX_LARGE; i++) { //add astroids
                asteroids[i] = new Asteroid(Asteroid.SIZE_LARGE_ASTEROID, false, 44);
                activeAsteroids.add(asteroids[i]);
                for (int j = 0; j < MAX_MEDIUM; j++) {
                    asteroids[i + j] = new Asteroid(Asteroid.SIZE_MEDIUM_ASTEROID, false, 22);
                }
            }
        }
        ui.set(lives.getRemainingLives(), score.getScore());
        asteroid.setActive(false);
        activeAsteroids.remove(asteroid);
    }

    public void placeAsteroid(int from, int to, float posX, float posY) {
        int numberOfAsteroidsSpawned = 0;
        for (int i = from; i < to; i++) {
            if (asteroids[i].isActive()) {
                continue;
            }
            asteroids[i].setPositionImmediately(posX, posY);
            activeAsteroids.add(asteroids[i]);
            asteroids[i].setActive(true);
            numberOfAsteroidsSpawned++;

            if (numberOfAsteroidsSpawned > 1) {
                break;
            }

        }
    }

    @Override
    public void render(Graphics g) {
        bg.render(g);
        for (Asteroid asteroid : activeAsteroids) {
            asteroid.render(g);
        }
        for (int i = 0; i < MAX_BULLET; i++) {
            if (!bullets[i].isActive()) {
                continue;
            }
            bullets[i].render(g);
        }
        player.render(g);

        ui.render(g);
    }

}
