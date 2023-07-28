/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;


import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.HttpZipLocator;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.bullet.BulletAppState;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 * Example 9 - How to make walls and floors solid. This collision code uses
 * Physics and a custom Action Listener.
 *
 * @author normen, with edits by Zathras
 */
public class proyectoM extends SimpleApplication
        implements ActionListener {

    private CharacterControl player;
    final private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;

    //Temporary vectors used on each frame.
    //They here to avoid instantiating new vectors on each frame
    final private Vector3f camDir = new Vector3f();
    final private Vector3f camLeft = new Vector3f();

    public static void main(String[] args) {
        proyectoM app = new proyectoM();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        /**
         * Set up Physics
         */
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // We re-use the flyby camera for rotation, while positioning is handled by physics
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setMoveSpeed(1000);
        setUpKeys();
        setUpLight();

        // We load the scene from the zip file and adjust its size.
        assetManager.registerLocator(
                "https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/jmonkeyengine/town.zip",
                HttpZipLocator.class);
        Spatial sceneModel = assetManager.loadModel("Models/Graficación/Graficación.obj");

        sceneModel.setLocalScale(1f);

        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape
                = CollisionShapeFactory.createMeshShape(sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0f);
        sceneModel.addControl(landscape);

        // We set up collision detection for the player by creating
        // a capsule collision shape and a CharacterControl.
        // The CharacterControl offers extra settings for
        // size, step height, jumping, falling, and gravity.
        // We also put the player in its starting position.
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 0.2f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(10);
        player.setFallSpeed(10);
        player.setGravity(0f);
        player.setPhysicsLocation(new Vector3f(-10, 3, -5));

        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);

    }

    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());

        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(ColorRGBA.White);
        dl2.setDirection(new Vector3f(10.8f, -101.8f, -10.8f).normalizeLocal());

        DirectionalLight dl3 = new DirectionalLight();
        dl3.setColor(ColorRGBA.White);
        dl3.setDirection(new Vector3f(0.8f, -0.8f, -0.8f).normalizeLocal());

        DirectionalLight dl4 = new DirectionalLight();
        dl4.setColor(ColorRGBA.White);
        dl4.setDirection(new Vector3f(-10.8f, -2.8f, -2.8f).normalizeLocal());

        DirectionalLight dl5 = new DirectionalLight();
        dl5.setColor(ColorRGBA.White);
        dl5.setDirection(new Vector3f(11.8f, 12.8f, 10.8f).normalizeLocal());

        DirectionalLight dl6 = new DirectionalLight();
        dl6.setColor(ColorRGBA.White);
        dl6.setDirection(new Vector3f(-2.8f, -2.8f, -2.8f).normalizeLocal());

        //rootNode.addLight(dl6);

        rootNode.addLight(dl5);

        rootNode.addLight(dl4);
//
        rootNode.addLight(dl3);
        rootNode.addLight(dl2);

        //rootNode.addLight(dl);

        // Se define la configuración de la sombra
    }

    /**
     * We over-write some navigational key mappings here, so we can add
     * physics-controlled walking and jumping:
     */
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }

    /**
     * These are our custom actions triggered by key presses. We do not walk
     * yet, we just keep track of the direction the user pressed.
     */
    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            if (value) {
                left = true;
            } else {
                left = false;
            }
        } else if (binding.equals("Right")) {
            if (value) {
                right = true;
            } else {
                right = false;
            }
        } else if (binding.equals("Up")) {
            if (value) {
                up = true;
            } else {
                up = false;
            }
        } else if (binding.equals("Down")) {
            if (value) {
                down = true;
            } else {
                down = false;
            }
        } else if (binding.equals("Jump")) {
            player.jump();
        }
    }

    /**
     * This is the main event loop--walking happens here.We check in which
     * direction the player is walking by interpreting the camera direction
     * forward (camDir) and to the side (camLeft). The setWalkDirection()
     * command is what lets a physics-controlled player walk. We also make sure
     * here that the camera moves with player.
     *
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }
}

