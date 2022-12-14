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

import org.mini2Dx.core.Graphics;
import org.mini2Dx.core.collision.CollisionCircle;
import org.mini2Dx.core.geom.Positionable;
import org.mini2Dx.gdx.math.Vector2;

import java.awt.*;

public class Player {
    private CollisionCircle playerCollisionCircle;
    private float acceleration = 10;
    private float rotationSpeed = 150.0f;
    private boolean alive = true;
    private Vector2 accelerationVector;
    private Vector2 playerFacingDirection;
    private Physics physics;

    private GameSprite sprite;
    private Lives lives = new Lives();
    private int dupCha = 0;

    public void isDeath() { // display gameover
        sprite = new GameSprite(ImageFilePaths.GAMEOVER,
                200,
                113);
    }
    public Player() {
        playerFacingDirection = new Vector2();
        accelerationVector = new Vector2();
        playerFacingDirection.set(0, -1); // Facing up
        playerCollisionCircle = new CollisionCircle(35);
        if(dupCha==0){
            sprite = new GameSprite(ImageFilePaths.PLAYER_ONE_SPRITE,
                    GameSpritesFrameSizeInformation.PLAYER_FRAME_WIDTH,
                    GameSpritesFrameSizeInformation.PLAYER_FRAME_HEIGHT);
        }else if(dupCha==1){
            sprite = new GameSprite(ImageFilePaths.PLAYER_TWO_SPRITE,
                    GameSpritesFrameSizeInformation.PLAYER_FRAME_WIDTH,
                    GameSpritesFrameSizeInformation.PLAYER_FRAME_HEIGHT);
        }else if(dupCha==2){
            sprite = new GameSprite(ImageFilePaths.PLAYER_THREE_SPRITE,
                    GameSpritesFrameSizeInformation.PLAYER_FRAME_WIDTH,
                    GameSpritesFrameSizeInformation.PLAYER_FRAME_HEIGHT);
        }else if(dupCha==3){
            sprite = new GameSprite(ImageFilePaths.PLAYER_FOUR_SPRITE,
                    GameSpritesFrameSizeInformation.PLAYER_FRAME_WIDTH,
                    GameSpritesFrameSizeInformation.PLAYER_FRAME_HEIGHT);
        }
        playerCollisionCircle.setXY(290, 215);
        physics = new Physics(playerCollisionCircle, 0);
    }

    public void init() {
        accelerationVector.set(0, 0);
        playerFacingDirection.set(0, -1); // Facing up
        playerCollisionCircle.setXY(290, 215);
        physics.setVelocity(0, 0);
        setActive(true);
    }

    public void update(float delta) {
        rotatePlayerShip(delta);
        movementInput(delta);
        physics.update(delta);
        Utility.wrapAroundMap(playerCollisionCircle);
    }


    public void render(Graphics g) {
        // We want to centre our texture with the box collider
        sprite.getAnimation().draw(g,
                playerCollisionCircle.getRenderX() - sprite.getFrameWidth() / 2,
                playerCollisionCircle.getRenderY() - sprite.getFrameHeight() / 2);

        if (Utility.DEBUG) {
            playerCollisionCircle.draw(g);
        }
    }

    private void rotatePlayerShip(float delta) {
        if (!Utility.rightKeyPressed() && !Utility.leftKeyPressed()) {
            return;
        } else if (Utility.rightKeyPressed()) {
            playerFacingDirection.rotate((rotationSpeed * delta));
        } else if (Utility.leftKeyPressed()) {
            playerFacingDirection.rotate((-rotationSpeed * delta));
        }

        sprite.getAnimation().setRotation(playerFacingDirection.angle()
                + Utility.OFFSET_SPRITE_ROTATION);
    }

    private void movementInput(float delta) {
        if (!Utility.downKeyPressed() && !Utility.upKeyPressed()) {
        } else if (Utility.downKeyPressed()) {
            assignAccelerationVector(delta);
            physics.addForceReverse(accelerationVector);
        } else if (Utility.upKeyPressed()) {
            assignAccelerationVector(delta);
            physics.addForce(accelerationVector);
        }
        chooseCharacter();
    }

    private void chooseCharacter(){
        if(Utility.playerPressed()){
            switch (dupCha){
                case 0:
                    sprite = new GameSprite(ImageFilePaths.PLAYER_ONE_SPRITE,
                            GameSpritesFrameSizeInformation.PLAYER_FRAME_WIDTH,
                            GameSpritesFrameSizeInformation.PLAYER_FRAME_HEIGHT);
                    break;
                case 1:
                    sprite = new GameSprite(ImageFilePaths.PLAYER_TWO_SPRITE,
                            GameSpritesFrameSizeInformation.PLAYER_FRAME_WIDTH,
                            GameSpritesFrameSizeInformation.PLAYER_FRAME_HEIGHT);
                    break;
                case 2:
                    sprite = new GameSprite(ImageFilePaths.PLAYER_THREE_SPRITE,
                            GameSpritesFrameSizeInformation.PLAYER_FRAME_WIDTH,
                            GameSpritesFrameSizeInformation.PLAYER_FRAME_HEIGHT);
                    break;
                case 3:
                    sprite = new GameSprite(ImageFilePaths.PLAYER_FOUR_SPRITE,
                            GameSpritesFrameSizeInformation.PLAYER_FRAME_WIDTH,
                            GameSpritesFrameSizeInformation.PLAYER_FRAME_HEIGHT);
                    break;
                default:
            }
            dupCha++;
            dupCha%=3;
        }
    }

    private void assignAccelerationVector(float delta) {
        accelerationVector.x = (playerFacingDirection.x * acceleration * delta);
        accelerationVector.y = (playerFacingDirection.y * acceleration * delta);
    }

    public boolean isAlive() {
        return alive;
    }

    public Positionable getPosition() {
        return playerCollisionCircle;
    }

    public Vector2 getFacingDirection() {
        return playerFacingDirection;
    }

    public float getAngle() {
        return playerFacingDirection.angle();
    }

    public CollisionCircle getPlayerCollisionCircle() {
        return playerCollisionCircle;
    }

    public void setActive(boolean active) {
        alive = active;
    }
}
